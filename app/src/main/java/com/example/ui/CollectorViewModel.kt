package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.api.ItemIdentificationResult
import com.example.api.PriceSourceRegistry
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

// Achievement data class
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val iconName: String,
    val progressText: String
)

// Marketplace Offer data class
data class MarketOffer(
    val id: String,
    val userName: String,
    val userAvatarUrl: String,
    val offeredItem: String,
    val requestedItem: String,
    val description: String,
    val category: String,
    val isUserPost: Boolean = false
)

sealed interface AppraisalState {
    object Idle : AppraisalState
    object Loading : AppraisalState
    data class Success(val text: String) : AppraisalState
    data class Error(val message: String) : AppraisalState
}

sealed interface PriceUpdateState {
    object Idle : PriceUpdateState
    object Loading : PriceUpdateState
    data class Success(val newPrice: Double, val minPrice: Double, val maxPrice: Double) : PriceUpdateState
    data class Error(val message: String) : PriceUpdateState
}

sealed interface ScanUiState {
    object Idle : ScanUiState
    object Loading : ScanUiState
    data class Success(
        val result: ItemIdentificationResult,
        val capturedImageBase64: String? = null
    ) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

enum class SortOption(val title: String) {
    RECENT("Mais Recentes"),
    VALUE_HIGH("Maior Valor"),
    VALUE_LOW("Menor Valor"),
    PROFIT("Maior Valorização (%)"),
    NAME("Nome (A-Z)")
}

class CollectorViewModel(
    application: Application,
    private val repository: ItemRepository
) : AndroidViewModel(application) {

    // --- Currency Preference ---
    val selectedCurrency = MutableStateFlow(AppCurrency.BRL)

    fun setCurrency(currency: AppCurrency) {
        selectedCurrency.value = currency
    }

    // --- Filter & Search States ---
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("TODOS") // "TODOS", "Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros"
    val selectedSubCategoryFilter = MutableStateFlow("TODOS") // e.g. "Pokémon TCG", "Hot Wheels"
    val selectedRarityFilter = MutableStateFlow("TODOS")
    val selectedConditionFilter = MutableStateFlow("TODOS")
    val onlyFavoritesFilter = MutableStateFlow(false)
    val sortOption = MutableStateFlow(SortOption.RECENT)

    // --- Selected item for Details / Editing ---
    val selectedItem = MutableStateFlow<Item?>(null)
    val isEditingItem = MutableStateFlow<Item?>(null)

    // --- Multi-select for Item Comparison ---
    val comparisonSelectionMode = MutableStateFlow(false)
    val selectedComparisonIds = MutableStateFlow<Set<Int>>(emptySet())

    fun toggleComparisonMode(enabled: Boolean) {
        comparisonSelectionMode.value = enabled
        if (!enabled) selectedComparisonIds.value = emptySet()
    }

    fun toggleComparisonItem(id: Int) {
        selectedComparisonIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun clearComparisonSelection() {
        selectedComparisonIds.value = emptySet()
        comparisonSelectionMode.value = false
    }

    // --- Raw items from database ---
    private val _allItems = repository.allItems

    val allItems: StateFlow<List<Item>> = _allItems.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // --- Filtered and Sorted Items ---
    val filteredItems: StateFlow<List<Item>> = combine(
        _allItems,
        searchQuery,
        selectedCategoryFilter,
        selectedSubCategoryFilter,
        selectedRarityFilter,
        selectedConditionFilter,
        onlyFavoritesFilter,
        sortOption
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val items = args[0] as List<Item>
        val query = args[1] as String
        val category = args[2] as String
        val subCategory = args[3] as String
        val rarity = args[4] as String
        val condition = args[5] as String
        val favoritesOnly = args[6] as Boolean
        val sort = args[7] as SortOption

        val filtered = items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.collection.contains(query, ignoreCase = true) ||
                    item.subCategory.contains(query, ignoreCase = true) ||
                    item.tags.contains(query, ignoreCase = true) ||
                    item.itemNumber.contains(query, ignoreCase = true)

            val matchesCategory = category == "TODOS" || item.type == category ||
                    (category == "Trading Cards" && item.isCard) ||
                    (category == "Carrinhos / Diecast" && item.isDiecast)

            val matchesSubCategory = subCategory == "TODOS" || item.subCategory.equals(subCategory, ignoreCase = true)
            val matchesRarity = rarity == "TODOS" || item.rarity.equals(rarity, ignoreCase = true)
            val matchesCondition = condition == "TODOS" || item.condition.contains(condition, ignoreCase = true)
            val matchesFavorites = !favoritesOnly || item.isFavorite

            matchesQuery && matchesCategory && matchesSubCategory && matchesRarity && matchesCondition && matchesFavorites
        }

        when (sort) {
            SortOption.RECENT -> filtered.sortedByDescending { it.dateAdded }
            SortOption.VALUE_HIGH -> filtered.sortedByDescending { it.estimatedValue }
            SortOption.VALUE_LOW -> filtered.sortedBy { it.estimatedValue }
            SortOption.PROFIT -> filtered.sortedByDescending { it.profitPercentage }
            SortOption.NAME -> filtered.sortedBy { it.name.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Scanner & Identification State ---
    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    // Editable buffer for manual correction of identification
    val identificationCorrectionBuffer = MutableStateFlow<ItemIdentificationResult?>(null)

    fun startImageScan(bitmap: Bitmap, contextHint: String? = null) {
        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Loading
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            
            val result = GeminiClient.identifyAndPriceItemFromImage(base64, "image/jpeg", contextHint)
            identificationCorrectionBuffer.value = result
            _scanUiState.value = ScanUiState.Success(result, base64)
        }
    }

    fun startSampleScan(type: String) {
        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Loading
            val hint = if (type == "CARRINHO") "Hot Wheels Datsun 510 Wagon Super Treasure Hunt" else "Pokémon Charizard ex 151"
            val result = GeminiClient.generateSimulatedResultForFallback(hint)
            identificationCorrectionBuffer.value = result
            _scanUiState.value = ScanUiState.Success(result, null)
        }
    }

    fun updateCorrectionBuffer(updated: ItemIdentificationResult) {
        identificationCorrectionBuffer.value = updated
        val current = _scanUiState.value
        if (current is ScanUiState.Success) {
            _scanUiState.value = current.copy(result = updated)
        }
    }

    fun resetScanState() {
        _scanUiState.value = ScanUiState.Idle
        identificationCorrectionBuffer.value = null
    }

    // Save identified item to collection
    fun saveIdentifiedItemToCollection(
        identification: ItemIdentificationResult,
        quantity: Int = 1,
        purchasePrice: Double = 0.0,
        storageLocation: String = "",
        notes: String = "",
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            val offersJson = JsonParserHelper.offersToJson(identification.offers)
            val historyJson = JsonParserHelper.historyToJson(identification.priceHistory)

            val newItem = Item(
                name = identification.name,
                type = identification.category,
                subCategory = identification.subCategory,
                collection = identification.collection,
                itemNumber = identification.itemNumber,
                rarity = identification.rarity,
                variant = identification.variant,
                condition = identification.apparentCondition,
                gradingInfo = identification.gradingInfo,
                language = identification.language,
                scale = identification.scale,
                color = identification.modelColor,
                year = identification.modelYear,
                quantity = quantity,
                purchasePrice = purchasePrice,
                estimatedValue = identification.averagePrice,
                minPrice = identification.minPrice,
                maxPrice = identification.maxPrice,
                confidenceScore = identification.confidenceScore,
                imageUri = imageUri,
                storageLocation = storageLocation,
                notes = notes,
                priceOffersJson = offersJson,
                priceHistoryJson = historyJson,
                currency = "BRL"
            )

            val id = repository.insertItem(newItem)
            selectedItem.value = newItem.copy(id = id.toInt())
            resetScanState()
        }
    }

    // --- Appraisal / Expert AI Summary ---
    private val _appraisalState = MutableStateFlow<AppraisalState>(AppraisalState.Idle)
    val appraisalState: StateFlow<AppraisalState> = _appraisalState.asStateFlow()

    fun performGeminiAppraisal() {
        viewModelScope.launch {
            _appraisalState.value = AppraisalState.Loading
            val currentItems = allItems.value
            if (currentItems.isEmpty()) {
                _appraisalState.value = AppraisalState.Success(
                    "Sua coleção está vazia! Adicione algumas cartas ou carrinhos para obter uma avaliação completa."
                )
                return@launch
            }

            val builder = StringBuilder()
            currentItems.forEachIndexed { idx, item ->
                builder.append("${idx + 1}. [${item.subCategory}] ${item.name} (#${item.itemNumber}) - Coleção: ${item.collection} | Raridade: ${item.rarity} | Variante: ${item.variant} | Condição: ${item.condition} | Qtd: ${item.quantity} | Pago: R$ ${item.purchasePrice} | Est.: R$ ${item.estimatedValue}\n")
            }

            val result = GeminiClient.getAppraisal(builder.toString())
            _appraisalState.value = AppraisalState.Success(result)
        }
    }

    fun clearAppraisal() {
        _appraisalState.value = AppraisalState.Idle
    }

    // --- Price Re-evaluation with AI ---
    private val _priceUpdateState = MutableStateFlow<PriceUpdateState>(PriceUpdateState.Idle)
    val priceUpdateState: StateFlow<PriceUpdateState> = _priceUpdateState.asStateFlow()

    fun refreshItemPriceWithAI(item: Item) {
        viewModelScope.launch {
            _priceUpdateState.value = PriceUpdateState.Loading
            val prices = GeminiClient.getEstimatedValueFromAI(
                cardName = item.name,
                category = item.type,
                series = item.collection,
                rarity = item.rarity,
                variant = item.variant,
                condition = item.condition
            )

            if (prices.first > 0.0) {
                val (offers, history) = PriceSourceRegistry.generateRealisticOffersAndHistory(
                    itemName = item.name,
                    subCategory = item.subCategory,
                    rarity = item.rarity,
                    variant = item.variant,
                    condition = item.condition,
                    baseEstimatedPrice = prices.first
                )

                val updatedItem = item.copy(
                    estimatedValue = prices.first,
                    minPrice = prices.second,
                    maxPrice = prices.third,
                    priceOffersJson = JsonParserHelper.offersToJson(offers),
                    priceHistoryJson = JsonParserHelper.historyToJson(history),
                    lastPriceUpdate = System.currentTimeMillis()
                )
                repository.updateItem(updatedItem)
                selectedItem.value = updatedItem
                _priceUpdateState.value = PriceUpdateState.Success(prices.first, prices.second, prices.third)
            } else {
                _priceUpdateState.value = PriceUpdateState.Error("Não foi possível encontrar cotações atualizadas no momento.")
            }
        }
    }

    fun clearPriceUpdateState() {
        _priceUpdateState.value = PriceUpdateState.Idle
    }

    // --- CRUD and Operations ---
    fun toggleFavorite(item: Item) {
        viewModelScope.launch {
            val updated = item.copy(isFavorite = !item.isFavorite)
            repository.updateItem(updated)
            if (selectedItem.value?.id == item.id) {
                selectedItem.value = updated
            }
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
            if (selectedItem.value?.id == item.id) {
                selectedItem.value = item
            }
        }
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            val id = repository.insertItem(item)
            selectedItem.value = item.copy(id = id.toInt())
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
            if (selectedItem.value?.id == item.id) {
                selectedItem.value = null
            }
        }
    }

    fun deleteAllItems() {
        viewModelScope.launch {
            repository.deleteAllItems()
            selectedItem.value = null
        }
    }

    // --- Achievements ---
    val achievements: StateFlow<List<Achievement>> = allItems.map { items ->
        computeAchievements(items)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun computeAchievements(items: List<Item>): List<Achievement> {
        val totalCards = items.filter { it.isCard }.sumOf { it.quantity }
        val totalCars = items.filter { it.isDiecast }.sumOf { it.quantity }
        val specialCount = items.count { it.rarity.contains("Lendário", ignoreCase = true) || it.rarity.contains("Secret", ignoreCase = true) || it.variant.contains("Treasure", ignoreCase = true) }
        val totalVal = items.sumOf { it.totalEstimatedValue }
        val totalItems = items.sumOf { it.quantity }

        return listOf(
            Achievement("ach_start", "Primeiro Tesouro", "Adicionou seu primeiro item à coleção.", totalItems >= 1, "stars", "$totalItems / 1"),
            Achievement("ach_deck", "Mestre das Cartas", "Possui pelo menos 5 cartas cadastradas.", totalCards >= 5, "style", "$totalCards / 5"),
            Achievement("ach_garage", "Garagem dos Sonhos", "Possui pelo menos 5 carrinhos ou diecasts.", totalCars >= 5, "directions_car", "$totalCars / 5"),
            Achievement("ach_rare", "Caçador de Raridades", "Possui ao menos 1 item raro/especial/Super TH.", specialCount >= 1, "emoji_events", "$specialCount / 1"),
            Achievement("ach_mil", "Colecionador de Elite", "Coleção com valor total estimado superior a R$ 1.000,00.", totalVal >= 1000.0, "payments", "R$ ${String.format("%.2f", totalVal)} / R$ 1.000")
        )
    }

    // --- Marketplace / Community Trades ---
    private val _marketOffers = MutableStateFlow<List<MarketOffer>>(
        listOf(
            MarketOffer("1", "Carlos Coleções", "avatar1", "Charizard ex (151 Foil)", "'71 Datsun 510 Wagon STH", "Procuro troca pau a pau ou cartas de One Piece!", "Trading Cards"),
            MarketOffer("2", "Diecast Hunter BR", "avatar2", "Hot Wheels Nissan Skyline GT-R R34", "Black Lotus MTG", "Miniatura em cartela curta lacrada perfeita.", "Carrinhos / Diecast"),
            MarketOffer("3", "Anime Cards SP", "avatar3", "Monkey D. Luffy Manga Alt Art", "Pikachu Illustrator ou PayPal", "Card graduado PSA 10 com certificado autêntico.", "Trading Cards")
        )
    )
    val marketOffers: StateFlow<List<MarketOffer>> = _marketOffers.asStateFlow()

    fun submitMarketOffer(offered: String, requested: String, desc: String, category: String) {
        val newOffer = MarketOffer(
            id = System.currentTimeMillis().toString(),
            userName = "Você (Colecionador)",
            userAvatarUrl = "ic_avatar_user",
            offeredItem = offered,
            requestedItem = requested,
            description = desc,
            category = category,
            isUserPost = true
        )
        _marketOffers.update { listOf(newOffer) + it }
    }

    // Pre-populate with diverse starter items if clean install
    init {
        viewModelScope.launch {
            val count = _allItems.first().size
            if (count == 0) {
                populateSampleCollection()
            }
        }
    }

    private suspend fun populateSampleCollection() {
        val sample1Offers = listOf(
            PriceOffer("LigaPokémon (Brasil)", 320.00, condition = "Near Mint", listingType = "Menor Preço Ativo"),
            PriceOffer("TCGPlayer (EUA)", 345.00, condition = "Near Mint", listingType = "Média de Vendas"),
            PriceOffer("Mercado Livre", 390.00, condition = "Lacrado / Sleeve", listingType = "Anúncio Verificado")
        )
        val sample1History = listOf(
            PriceHistoryPoint("Mai/2026", 260.0),
            PriceHistoryPoint("Jun/2026", 280.0),
            PriceHistoryPoint("Jul/2026", 300.0),
            PriceHistoryPoint("Ago/2026", 320.0)
        )

        val item1 = Item(
            name = "Charizard ex",
            type = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "151/165",
            rarity = "Ultra Raro",
            variant = "Foil / Holográfico",
            condition = "Near Mint",
            language = "PT-BR",
            quantity = 1,
            purchasePrice = 180.00,
            estimatedValue = 320.00,
            minPrice = 270.00,
            maxPrice = 390.00,
            confidenceScore = 94,
            isFavorite = true,
            storageLocation = "Pasta 151 - Folha 3",
            tags = "pokémon, fogo, 151, charizard",
            notes = "Em sleeve duplo Dragon Shield e Top Loader.",
            priceOffersJson = JsonParserHelper.offersToJson(sample1Offers),
            priceHistoryJson = JsonParserHelper.historyToJson(sample1History)
        )

        val sample2Offers = listOf(
            PriceOffer("Mercado Livre Coleções", 420.00, condition = "Lacrado / Cartela Perfeita", listingType = "Anúncio Ativo"),
            PriceOffer("eBay Diecast", 450.00, condition = "Mint", listingType = "Última Venda"),
            PriceOffer("Grupos HW Brasil", 380.00, condition = "Lacrado", listingType = "Oferta de Colecionador")
        )
        val sample2History = listOf(
            PriceHistoryPoint("Mai/2026", 360.0),
            PriceHistoryPoint("Jun/2026", 390.0),
            PriceHistoryPoint("Jul/2026", 410.0),
            PriceHistoryPoint("Ago/2026", 420.0)
        )

        val item2 = Item(
            name = "'71 Datsun 510 Wagon",
            type = "Carrinhos / Diecast",
            subCategory = "Hot Wheels",
            collection = "Mainline 2024 - HW Wagons",
            itemNumber = "#142/250",
            rarity = "Super Treasure Hunt",
            variant = "Spectraflame Azul (STH)",
            condition = "Novo / Lacrado",
            scale = "1:64",
            color = "Azul Spectraflame",
            year = "2024",
            quantity = 1,
            purchasePrice = 25.00,
            estimatedValue = 420.00,
            minPrice = 380.00,
            maxPrice = 490.00,
            confidenceScore = 96,
            isFavorite = true,
            storageLocation = "Caixa Acrílica Protetora #4",
            tags = "hot wheels, sth, datsun, real riders",
            notes = "Pneus de borracha Real Riders, pintura spectraflame e logo TH na lateral.",
            priceOffersJson = JsonParserHelper.offersToJson(sample2Offers),
            priceHistoryJson = JsonParserHelper.historyToJson(sample2History)
        )

        val sample3Offers = listOf(
            PriceOffer("TCGPlayer", 95.00, condition = "Near Mint", listingType = "Market Price"),
            PriceOffer("Cardmarket (EU)", 110.00, condition = "Mint", listingType = "Média Europeia")
        )
        val sample3History = listOf(
            PriceHistoryPoint("Mai/2026", 75.0),
            PriceHistoryPoint("Jun/2026", 80.0),
            PriceHistoryPoint("Jul/2026", 90.0),
            PriceHistoryPoint("Ago/2026", 95.0)
        )

        val item3 = Item(
            name = "Monkey D. Luffy",
            type = "Trading Cards",
            subCategory = "One Piece Card Game",
            collection = "Awakening of the New Era (OP-05)",
            itemNumber = "OP05-060",
            rarity = "Super Raro",
            variant = "Alternate Art (Manga)",
            condition = "Mint",
            language = "EN",
            quantity = 1,
            purchasePrice = 60.00,
            estimatedValue = 95.00,
            minPrice = 85.00,
            maxPrice = 125.00,
            confidenceScore = 91,
            isFavorite = false,
            storageLocation = "Pasta One Piece",
            tags = "one piece, luffy, op05, manga",
            priceOffersJson = JsonParserHelper.offersToJson(sample3Offers),
            priceHistoryJson = JsonParserHelper.historyToJson(sample3History)
        )

        repository.insertItems(listOf(item1, item2, item3))
    }
}

class CollectorViewModelFactory(
    private val application: Application,
    private val repository: ItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectorViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

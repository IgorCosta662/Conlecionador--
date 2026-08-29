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
import com.example.api.ScryfallCard
import com.example.api.TcgdexCardBrief
import com.example.api.TcgdexCardDetail
import com.example.api.TcgOnlineService
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

// Batch Scan Queue Item
data class BatchScanItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val identification: ItemIdentificationResult,
    val imageBase64: String? = null,
    val savedImageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
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
        val capturedImageBase64: String? = null,
        val savedImageUri: String? = null
    ) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

enum class ViewMode(val title: String) {
    GRID("Grade"),
    LIST("Lista"),
    COMPACT("Compacto")
}

enum class SortOption(val title: String) {
    RECENT("Mais Recentes"),
    VALUE_HIGH("Maior Valor"),
    VALUE_LOW("Menor Valor"),
    PROFIT("Maior Valorização (%)"),
    NAME("Nome (A-Z)")
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val iconType: String = "info",
    val isRead: Boolean = false
)

class CollectorViewModel(
    application: Application,
    private val repository: ItemRepository
) : AndroidViewModel(application) {

    // --- Currency Preference ---
    val selectedCurrency = MutableStateFlow(AppCurrency.BRL)

    fun setCurrency(currency: AppCurrency) {
        selectedCurrency.value = currency
    }

    // --- App Mode: Modo Colecionador vs Modo Investidor ---
    val appMode = MutableStateFlow(AppMode.COLLECTOR)

    fun setAppMode(mode: AppMode) {
        appMode.value = mode
    }

    fun toggleAppMode() {
        appMode.value = if (appMode.value == AppMode.COLLECTOR) AppMode.INVESTOR else AppMode.COLLECTOR
    }

    // --- View Mode Preference (Grade / Lista / Compacto) ---
    val viewMode = MutableStateFlow(ViewMode.GRID)

    fun setViewMode(mode: ViewMode) {
        viewMode.value = mode
    }

    // --- Dialog Triggers ---
    val showGlobalSearchDialog = MutableStateFlow(false)
    val showNotificationsDialog = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)

    // --- Notifications List ---
    val notifications = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification("notif_1", "Charizard ex em Alta!", "A cotação de Charizard ex 151 subiu +15% no mercado nacional esta semana.", System.currentTimeMillis() - 3600000, "trending_up"),
            AppNotification("notif_2", "Alerta de Preço Atingido", "Nissan Skyline GT-R STH atingiu sua meta de R$ 320,00.", System.currentTimeMillis() - 7200000, "notifications_active"),
            AppNotification("notif_3", "Dica de Colecionador", "Novas listas de cartas Pokémon 151 foram catalogadas.", System.currentTimeMillis() - 86400000, "lightbulb")
        )
    )

    // --- Market Region (Brasil, EUA, Japão, Europa) ---
    val selectedMarketRegion = MutableStateFlow(MarketRegion.BRAZIL)

    fun setMarketRegion(market: MarketRegion) {
        selectedMarketRegion.value = market
    }

    // --- App Preferences & Automation Settings ---
    val autoTranslateCardEffects = MutableStateFlow(true)
    val preferOfficialWebImages = MutableStateFlow(false)
    val showCameraGrid = MutableStateFlow(true)
    val priceAlertsNotificationsEnabled = MutableStateFlow(true)
    val highPrecisionAiAppraisal = MutableStateFlow(true)

    fun setAutoTranslate(enabled: Boolean) {
        autoTranslateCardEffects.value = enabled
    }

    fun setPreferOfficialWebImages(enabled: Boolean) {
        preferOfficialWebImages.value = enabled
    }

    fun setShowCameraGrid(enabled: Boolean) {
        showCameraGrid.value = enabled
    }

    fun setPriceAlertsNotificationsEnabled(enabled: Boolean) {
        priceAlertsNotificationsEnabled.value = enabled
    }

    fun setHighPrecisionAiAppraisal(enabled: Boolean) {
        highPrecisionAiAppraisal.value = enabled
    }

    // --- Filter & Search States ---
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("TODOS")
    val selectedSubCategoryFilter = MutableStateFlow("TODOS")
    val selectedRarityFilter = MutableStateFlow("TODOS")
    val selectedConditionFilter = MutableStateFlow("TODOS")
    val selectedLanguageFilter = MutableStateFlow("TODOS")
    val onlyFavoritesFilter = MutableStateFlow(false)
    val sortOption = MutableStateFlow(SortOption.RECENT)

    // --- Selected item for Details / Editing ---
    val selectedItem = MutableStateFlow<Item?>(null)
    val isEditingItem = MutableStateFlow<Item?>(null)

    fun selectItem(item: Item) {
        selectedItem.value = item
    }

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

    // --- Batch Scanner & Inventory Fast Mode ---
    val batchScanQueue = MutableStateFlow<List<BatchScanItem>>(emptyList())

    fun addToBatchQueue(item: BatchScanItem) {
        batchScanQueue.update { it + item }
    }

    fun removeFromBatchQueue(id: String) {
        batchScanQueue.update { current -> current.filterNot { it.id == id } }
    }

    fun clearBatchQueue() {
        batchScanQueue.value = emptyList()
    }

    fun addAllBatchItemsToCollection() {
        viewModelScope.launch {
            val batch = batchScanQueue.value
            batch.forEach { item ->
                saveIdentifiedItemToCollection(
                    identification = item.identification,
                    quantity = 1,
                    purchasePrice = 0.0,
                    storageLocation = "Lote Escaneado",
                    notes = "Adicionado via scanner em lote",
                    imageUri = item.savedImageUri
                )
            }
            clearBatchQueue()
        }
    }

    // --- Raw items from database ---
    private val _allItems = repository.allItems

    val allItems: StateFlow<List<Item>> = _allItems.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Check duplicate item in collection
    fun findDuplicateItem(name: String, subCategory: String, language: String, itemNumber: String): Item? {
        val cleanName = name.trim().lowercase()
        val cleanNumber = itemNumber.trim().lowercase()
        return allItems.value.firstOrNull { existing ->
            existing.name.lowercase().contains(cleanName) || cleanName.contains(existing.name.lowercase()) &&
            (cleanNumber.isNotBlank() && existing.itemNumber.lowercase() == cleanNumber ||
             existing.subCategory.equals(subCategory, ignoreCase = true) && existing.language.equals(language, ignoreCase = true))
        }
    }

    // --- Filtered and Sorted Items (Intelligent Natural Search Engine) ---
    val filteredItems: StateFlow<List<Item>> = combine(
        _allItems,
        searchQuery,
        selectedCategoryFilter,
        selectedSubCategoryFilter,
        selectedRarityFilter,
        selectedConditionFilter,
        selectedLanguageFilter,
        onlyFavoritesFilter,
        sortOption
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val items = args[0] as List<Item>
        val rawQuery = (args[1] as String).trim()
        val query = rawQuery.lowercase()
        val category = args[2] as String
        val subCategory = args[3] as String
        val rarity = args[4] as String
        val condition = args[5] as String
        val language = args[6] as String
        val favoritesOnly = args[7] as Boolean
        val sort = args[8] as SortOption

        // Intelligent Natural Search keywords
        val isJapaneseQuery = query.contains("japones") || query.contains("japonesa") || query.contains("japão") || query.contains("jp")
        val isPortugueseQuery = query.contains("portugues") || query.contains("português") || query.contains("pt") || query.contains("brasil")
        val isEnglishQuery = query.contains("ingles") || query.contains("inglês") || query.contains("en") || query.contains("usa")
        val isSthQuery = query.contains("sth") || query.contains("super treasure") || query.contains("super th")
        val isProfitQuery = query.contains("valorizou") || query.contains("valorizaram") || query.contains("lucro") || query.contains("positivo")
        val isHighValueQuery = query.contains("acima de") || query.contains("mais de") || query.contains(">")

        var valueThreshold: Double? = null
        if (isHighValueQuery) {
            val digits = query.replace("[^0-9]".toRegex(), "").toDoubleOrNull()
            if (digits != null) valueThreshold = digits
        }

        val filtered = items.filter { item ->
            // Smart query matching
            val matchesQuery = when {
                query.isBlank() -> true
                valueThreshold != null -> item.estimatedValue >= valueThreshold
                isProfitQuery -> item.profitOrLoss > 0
                isSthQuery -> item.variant.contains("Treasure", ignoreCase = true) || item.rarity.contains("Treasure", ignoreCase = true)
                isJapaneseQuery && item.language.contains("JP", ignoreCase = true) -> true
                isPortugueseQuery && item.language.contains("PT", ignoreCase = true) -> true
                isEnglishQuery && item.language.contains("EN", ignoreCase = true) -> true
                else -> item.name.contains(query, ignoreCase = true) ||
                        item.collection.contains(query, ignoreCase = true) ||
                        item.subCategory.contains(query, ignoreCase = true) ||
                        item.tags.contains(query, ignoreCase = true) ||
                        item.itemNumber.contains(query, ignoreCase = true) ||
                        item.cardArtist.contains(query, ignoreCase = true) ||
                        item.notes.contains(query, ignoreCase = true) ||
                        item.storageLocation.contains(query, ignoreCase = true)
            }

            val matchesCategory = category == "TODOS" || item.type == category ||
                    (category == "Trading Cards" && item.isCard) ||
                    (category == "Carrinhos / Diecast" && item.isDiecast)

            val matchesSubCategory = subCategory == "TODOS" || item.subCategory.equals(subCategory, ignoreCase = true)
            val matchesRarity = rarity == "TODOS" || item.rarity.equals(rarity, ignoreCase = true)
            val matchesCondition = condition == "TODOS" || item.condition.contains(condition, ignoreCase = true)
            val matchesLanguage = language == "TODOS" || item.language.equals(language, ignoreCase = true)
            val matchesFavorites = !favoritesOnly || item.isFavorite

            matchesQuery && matchesCategory && matchesSubCategory && matchesRarity && matchesCondition && matchesLanguage && matchesFavorites
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
            val savedUri = ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), bitmap, "front")
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            val market = selectedMarketRegion.value
            val result = GeminiClient.identifyAndPriceItemFromImage(base64, "image/jpeg", contextHint, market)
            identificationCorrectionBuffer.value = result
            _scanUiState.value = ScanUiState.Success(result, base64, savedUri)
        }
    }

    fun startSampleScan(type: String) {
        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Loading
            val hint = if (type == "CARRINHO") "Hot Wheels Datsun 510 Wagon Super Treasure Hunt" else "Pokémon Charizard ex 151"
            val market = selectedMarketRegion.value
            val result = GeminiClient.generateSimulatedResultForFallback(hint, market)
            identificationCorrectionBuffer.value = result
            _scanUiState.value = ScanUiState.Success(result, null, null)
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

    fun saveBitmapToStorage(bitmap: Bitmap, prefix: String = "img"): String? {
        return ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), bitmap, prefix)
    }

    fun findPossibleDuplicate(result: ItemIdentificationResult): Item? {
        return allItems.value.firstOrNull { existing ->
            existing.name.equals(result.name, ignoreCase = true) &&
            (result.itemNumber.isBlank() || existing.itemNumber.equals(result.itemNumber, ignoreCase = true)) &&
            existing.language.equals(result.language, ignoreCase = true)
        }
    }

    // Save identified item to collection
    fun saveIdentifiedItemToCollection(
        identification: ItemIdentificationResult,
        quantity: Int = 1,
        purchasePrice: Double = 0.0,
        storageLocation: String = "",
        notes: String = "",
        imageUri: String? = null,
        backImageUri: String? = null,
        detailImages: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val offersJson = JsonParserHelper.offersToJson(identification.offers)
            val historyJson = JsonParserHelper.historyToJson(identification.priceHistory)
            val langComparisonJson = JsonParserHelper.langComparisonToJson(identification.languageComparisons)
            val conditionPricesJson = JsonParserHelper.conditionTiersToJson(identification.conditionPrices)
            val conditionAssessmentJson = JsonParserHelper.conditionAssessmentToJson(identification.conditionAssessment)

            val resolvedImageUri = imageUri ?: (_scanUiState.value as? ScanUiState.Success)?.savedImageUri

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
                cardHp = identification.cardHp,
                cardArtist = identification.cardArtist,
                cardAttacks = identification.cardAttacks,
                cardSetSymbol = identification.cardSetSymbol,
                cardOracleText = identification.cardOracleText,
                cardTranslatedEffect = identification.cardTranslatedEffect,
                quantity = quantity,
                purchasePrice = purchasePrice,
                estimatedValue = identification.averagePrice,
                minPrice = identification.minPrice,
                maxPrice = identification.maxPrice,
                confidenceScore = identification.confidenceScore,
                imageUri = resolvedImageUri,
                backImageUri = backImageUri,
                detailImagesJson = JsonParserHelper.stringListToJson(detailImages),
                storageLocation = storageLocation,
                notes = notes,
                priceOffersJson = offersJson,
                priceHistoryJson = historyJson,
                languageComparisonJson = langComparisonJson,
                conditionPricesJson = conditionPricesJson,
                conditionAssessmentJson = conditionAssessmentJson,
                authenticityStatus = identification.authenticityRisk,
                authenticityNotes = identification.authenticityNotes,
                marketRegion = identification.marketRegion.code,
                currency = "BRL"
            )

            val id = repository.insertItem(newItem)
            selectedItem.value = newItem.copy(id = id.toInt())
            resetScanState()
        }
    }

    // --- Multi-Photo Management ---
    fun updateItemBackImage(item: Item, bitmap: Bitmap) {
        viewModelScope.launch {
            val uri = ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), bitmap, "back")
            if (uri != null) {
                val updated = item.copy(backImageUri = uri)
                repository.updateItem(updated)
                selectedItem.value = updated
            }
        }
    }

    fun addItemDetailImage(item: Item, bitmap: Bitmap) {
        viewModelScope.launch {
            val uri = ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), bitmap, "detail")
            if (uri != null) {
                val current = item.getDetailImages().toMutableList()
                current.add(uri)
                val updated = item.copy(detailImagesJson = JsonParserHelper.stringListToJson(current))
                repository.updateItem(updated)
                selectedItem.value = updated
            }
        }
    }

    // --- Price Alert Setting ---
    fun setItemPriceAlert(item: Item, targetPrice: Double, enabled: Boolean) {
        viewModelScope.launch {
            val updated = item.copy(targetPriceAlert = targetPrice, isAlertEnabled = enabled)
            repository.updateItem(updated)
            selectedItem.value = updated
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
                builder.append("${idx + 1}. [${item.subCategory}] ${item.name} (#${item.itemNumber}) - Idioma: ${item.language} | Coleção: ${item.collection} | Raridade: ${item.rarity} | Variante: ${item.variant} | Condição: ${item.condition} | Qtd: ${item.quantity} | Pago: R$ ${item.purchasePrice} | Est.: R$ ${item.estimatedValue}\n")
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
            val prices = PriceSourceRegistry.generateRealisticOffersAndHistory(
                itemName = item.name,
                subCategory = item.subCategory,
                rarity = item.rarity,
                variant = item.variant,
                condition = item.condition,
                language = item.language,
                marketRegion = MarketRegion.fromCode(item.marketRegion),
                baseEstimatedPrice = item.estimatedValue
            )

            val avgPrice = prices.first.map { it.priceInBRL }.average().takeIf { !it.isNaN() } ?: item.estimatedValue
            val minP = prices.first.minOfOrNull { it.priceInBRL } ?: (avgPrice * 0.85)
            val maxP = prices.first.maxOfOrNull { it.priceInBRL } ?: (avgPrice * 1.25)
            val condTiers = PriceSourceRegistry.generateConditionPriceTiers(avgPrice)
            val langComparisons = PriceSourceRegistry.generateLanguageComparisons(item.name, avgPrice, item.language)

            val updatedItem = item.copy(
                estimatedValue = avgPrice,
                minPrice = minP,
                maxPrice = maxP,
                priceOffersJson = JsonParserHelper.offersToJson(prices.first),
                priceHistoryJson = JsonParserHelper.historyToJson(prices.second),
                conditionPricesJson = JsonParserHelper.conditionTiersToJson(condTiers),
                languageComparisonJson = JsonParserHelper.langComparisonToJson(langComparisons),
                lastPriceUpdate = System.currentTimeMillis()
            )
            repository.updateItem(updatedItem)
            selectedItem.value = updatedItem
            _priceUpdateState.value = PriceUpdateState.Success(avgPrice, minP, maxP)
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

    fun loadSampleData() {
        viewModelScope.launch {
            val sampleItems = InitialDataSeeder.getSampleItems()
            repository.insertItems(sampleItems)
        }
    }

    fun reloadCatalogWithFreshData() {
        viewModelScope.launch {
            repository.deleteAllItems()
            val freshItems = InitialDataSeeder.getInitialItems()
            repository.insertItems(freshItems)
            selectedItem.value = null
        }
    }

    fun exportJsonBackup(): String {
        val items = allItems.value
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"app\": \"Collector Pro\",\n")
        sb.append("  \"version\": \"2.6\",\n")
        sb.append("  \"exportedAt\": ${System.currentTimeMillis()},\n")
        sb.append("  \"totalItems\": ${items.size},\n")
        sb.append("  \"items\": [\n")
        items.forEachIndexed { index, it ->
            sb.append("    {\n")
            sb.append("      \"name\": \"${it.name.replace("\"", "\\\"")}\",\n")
            sb.append("      \"type\": \"${it.type}\",\n")
            sb.append("      \"subCategory\": \"${it.subCategory.replace("\"", "\\\"")}\",\n")
            sb.append("      \"collection\": \"${it.collection.replace("\"", "\\\"")}\",\n")
            sb.append("      \"itemNumber\": \"${it.itemNumber}\",\n")
            sb.append("      \"rarity\": \"${it.rarity}\",\n")
            sb.append("      \"variant\": \"${it.variant}\",\n")
            sb.append("      \"condition\": \"${it.condition}\",\n")
            sb.append("      \"language\": \"${it.language}\",\n")
            sb.append("      \"scale\": \"${it.scale}\",\n")
            sb.append("      \"color\": \"${it.color}\",\n")
            sb.append("      \"year\": \"${it.year}\",\n")
            sb.append("      \"quantity\": ${it.quantity},\n")
            sb.append("      \"purchasePrice\": ${it.purchasePrice},\n")
            sb.append("      \"estimatedValue\": ${it.estimatedValue},\n")
            sb.append("      \"minPrice\": ${it.minPrice},\n")
            sb.append("      \"maxPrice\": ${it.maxPrice},\n")
            sb.append("      \"imageUri\": ${if (it.imageUri != null) "\"${it.imageUri}\"" else "null"},\n")
            sb.append("      \"isFavorite\": ${it.isFavorite},\n")
            sb.append("      \"tags\": \"${it.tags.replace("\"", "\\\"")}\",\n")
            sb.append("      \"storageLocation\": \"${it.storageLocation.replace("\"", "\\\"")}\",\n")
            sb.append("      \"notes\": \"${it.notes.replace("\"", "\\\"").replace("\n", "\\n")}\",\n")
            sb.append("      \"cardHp\": \"${it.cardHp}\",\n")
            sb.append("      \"cardArtist\": \"${it.cardArtist.replace("\"", "\\\"")}\",\n")
            sb.append("      \"cardAttacks\": \"${it.cardAttacks.replace("\"", "\\\"")}\",\n")
            sb.append("      \"cardOracleText\": \"${it.cardOracleText.replace("\"", "\\\"").replace("\n", "\\n")}\",\n")
            sb.append("      \"cardTranslatedEffect\": \"${it.cardTranslatedEffect.replace("\"", "\\\"").replace("\n", "\\n")}\"\n")
            sb.append("    }${if (index < items.size - 1) "," else ""}\n")
        }
        sb.append("  ]\n")
        sb.append("}")
        return sb.toString()
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
    private val _marketOffers = MutableStateFlow<List<MarketOffer>>(emptyList())
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

    // --- Wishlist (Lista de Desejos) ---
    val allWishlist: StateFlow<List<WishlistItem>> = repository.allWishlist.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun insertWishlistItem(
        name: String,
        category: String,
        subCategory: String,
        targetMaxPrice: Double,
        priority: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertWishlist(
                WishlistItem(
                    name = name,
                    category = category,
                    subCategory = subCategory,
                    targetMaxPrice = targetMaxPrice,
                    priority = priority,
                    notes = notes
                )
            )
        }
    }

    fun toggleWishlistFound(item: WishlistItem) {
        viewModelScope.launch {
            repository.updateWishlist(item.copy(isFound = !item.isFound))
        }
    }

    fun deleteWishlistItem(item: WishlistItem) {
        viewModelScope.launch {
            repository.deleteWishlist(item)
        }
    }

    fun generatePortfolioReportText(currency: AppCurrency): String {
        val items = allItems.value
        val totalEst = items.sumOf { it.totalEstimatedValue }
        val totalPaid = items.sumOf { it.totalPurchasePrice }
        val profit = totalEst - totalPaid
        val profitPercent = if (totalPaid > 0) (profit / totalPaid) * 100.0 else 0.0

        val sb = StringBuilder()
        sb.append("RELATÓRIO DO PORTFÓLIO DE COLECIONÁVEIS\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Valor Total Estimado: ${currency.formatValue(totalEst)}\n")
        sb.append("Total Investido: ${currency.formatValue(totalPaid)}\n")
        sb.append("Rendimento: ${if (profit >= 0) "+" else ""}${currency.formatValue(profit)} (${String.format("%.1f", profitPercent)}%)\n")
        sb.append("Total de Itens: ${items.sumOf { it.quantity }}\n")
        sb.append("Cards TCG: ${items.filter { it.isCard }.sumOf { it.quantity }}\n")
        sb.append("Carrinhos Diecast: ${items.filter { it.isDiecast }.sumOf { it.quantity }}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")
        sb.append("CATÁLOGO DETALHADO:\n\n")

        items.forEachIndexed { index, item ->
            sb.append("${index + 1}. ${item.name} (${item.type})\n")
            if (item.language.isNotBlank() && item.language != "N/A") sb.append("   • Idioma: ${item.languageDisplayName}\n")
            if (item.collection.isNotBlank()) sb.append("   • Coleção: ${item.collection}\n")
            if (item.itemNumber.isNotBlank()) sb.append("   • Nº: ${item.itemNumber}\n")
            if (item.rarity.isNotBlank()) sb.append("   • Raridade: ${item.rarity} | Condição: ${item.condition}\n")
            sb.append("   • Quantidade: ${item.quantity} un.\n")
            sb.append("   • Cotação Atual: ${currency.formatValue(item.estimatedValue)} (Total: ${currency.formatValue(item.totalEstimatedValue)})\n")
            if (item.purchasePrice > 0) sb.append("   • Preço Pago: ${currency.formatValue(item.purchasePrice)}\n")
            if (item.storageLocation.isNotBlank()) sb.append("   • Local: ${item.storageLocation}\n")
            sb.append("\n")
        }

        return sb.toString()
    }

    // ---------------- Live Online Search (Scryfall & TCGDex APIs) ----------------
    val isOnlineSearching = MutableStateFlow(false)
    val scryfallOnlineResults = MutableStateFlow<List<ScryfallCard>>(emptyList())
    val tcgdexOnlineResults = MutableStateFlow<List<TcgdexCardBrief>>(emptyList())
    val onlineSearchMessage = MutableStateFlow<String?>(null)

    fun searchScryfallOnline(query: String) {
        if (query.isBlank()) {
            scryfallOnlineResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            isOnlineSearching.value = true
            onlineSearchMessage.value = "Buscando em Scryfall API..."
            try {
                val results = TcgOnlineService.searchMagicCards(query)
                scryfallOnlineResults.value = results
                onlineSearchMessage.value = if (results.isEmpty()) "Nenhuma carta encontrada no Scryfall." else null
            } catch (e: Exception) {
                onlineSearchMessage.value = "Erro ao consultar Scryfall: ${e.localizedMessage}"
            } finally {
                isOnlineSearching.value = false
            }
        }
    }

    fun searchTcgdexOnline(query: String) {
        if (query.isBlank()) {
            tcgdexOnlineResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            isOnlineSearching.value = true
            onlineSearchMessage.value = "Buscando em TCGDex API (PT/EN)..."
            try {
                val results = TcgOnlineService.searchPokemonCards(query)
                tcgdexOnlineResults.value = results
                onlineSearchMessage.value = if (results.isEmpty()) "Nenhuma carta encontrada no TCGDex." else null
            } catch (e: Exception) {
                onlineSearchMessage.value = "Erro ao consultar TCGDex: ${e.localizedMessage}"
            } finally {
                isOnlineSearching.value = false
            }
        }
    }

    fun clearOnlineSearchResults() {
        scryfallOnlineResults.value = emptyList()
        tcgdexOnlineResults.value = emptyList()
        onlineSearchMessage.value = null
    }

    fun importScryfallCardToCollection(
        card: ScryfallCard,
        onComplete: (Item) -> Unit = {}
    ) {
        viewModelScope.launch {
            val highResImage = card.getHighResImage()
            val estPrice = card.getEstimatedPriceBrl()
            val newItem = Item(
                name = card.name,
                type = "Card TCG",
                subCategory = "Magic: The Gathering",
                collection = card.setName.ifBlank { "Magic Modern / Standard" },
                itemNumber = card.collectorNumber,
                rarity = when (card.rarity.lowercase()) {
                    "mythic" -> "Mítica Rara"
                    "rare" -> "Rara"
                    "uncommon" -> "Incomum"
                    else -> "Comum"
                },
                condition = "Near Mint",
                language = "EN",
                estimatedValue = estPrice,
                purchasePrice = estPrice * 0.75,
                imageUri = highResImage,
                notes = "Importado via Scryfall API (${card.setName} #${card.collectorNumber})"
            )
            val id = repository.insertItem(newItem)
            onComplete(newItem.copy(id = id.toInt()))
        }
    }

    fun importTcgdexCardToCollection(
        brief: TcgdexCardBrief,
        onComplete: (Item) -> Unit = {}
    ) {
        viewModelScope.launch {
            val detail = TcgOnlineService.getPokemonCardDetail(brief.id)
            val highResImage = detail?.getHighResImage() ?: brief.getHighResImage()
            val estPrice = detail?.getEstimatedPriceBrl() ?: 15.0
            val setName = detail?.set?.name ?: "Pokémon TCG"

            val newItem = Item(
                name = detail?.name ?: brief.name,
                type = "Card TCG",
                subCategory = "Pokémon TCG",
                collection = setName,
                itemNumber = detail?.localId ?: brief.localId,
                rarity = detail?.rarity ?: "Comum",
                condition = "Near Mint",
                language = "PT-BR",
                estimatedValue = estPrice,
                purchasePrice = estPrice * 0.75,
                imageUri = highResImage,
                notes = "Importado via TCGDex API ($setName #${detail?.localId ?: brief.localId})"
            )
            val id = repository.insertItem(newItem)
            onComplete(newItem.copy(id = id.toInt()))
        }
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


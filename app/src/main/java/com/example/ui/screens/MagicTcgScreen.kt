package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.api.scryfall.ScryfallCard
import com.example.api.scryfall.ScryfallDataService
import com.example.api.scryfall.ScryfallSet
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.ViewMode
import com.example.ui.components.ItemGridCard
import com.example.ui.components.ScryfallCardDetailDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MagicScreenTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MY_COLLECTION("Minha Coleção", Icons.Default.FolderSpecial),
    SCRYFALL_EXPLORER("Scryfall Autocomplete", Icons.Default.TravelExplore),
    SCRYFALL_SETS("Sets & Edições MTG", Icons.Default.AutoAwesomeMotion)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicTcgScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(MagicScreenTab.MY_COLLECTION) }

    // Local Collection Filter States
    val mtgCards = remember(items) {
        items.filter { it.subCategory.contains("Magic", ignoreCase = true) || it.name.contains("Magic", ignoreCase = true) }
    }

    var selectedSetFilter by remember { mutableStateOf("TODOS") }
    var selectedRarityFilter by remember { mutableStateOf("TODOS") }
    var selectedFoilFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Scryfall Online Explorer States with Live Autocomplete & Fuzzy
    var scryfallQuery by remember { mutableStateOf("") }
    var scryfallResults by remember { mutableStateOf<List<ScryfallCard>>(emptyList()) }
    var liveSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var directFuzzyCard by remember { mutableStateOf<ScryfallCard?>(null) }
    var isSearchingScryfall by remember { mutableStateOf(false) }
    var isFetchingSuggestions by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Scryfall Sets States
    var scryfallSets by remember { mutableStateOf<List<ScryfallSet>>(emptyList()) }
    var isLoadingSets by remember { mutableStateOf(false) }
    var setFilterQuery by remember { mutableStateOf("") }
    var selectedSetYearFilter by remember { mutableStateOf("TODOS") }
    var activeSetForBrowsing by remember { mutableStateOf<ScryfallSet?>(null) }
    var cardsInActiveSet by remember { mutableStateOf<List<ScryfallCard>>(emptyList()) }
    var isLoadingSetCards by remember { mutableStateOf(false) }

    var selectedCardForDetails by remember { mutableStateOf<ScryfallCard?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Load Sets when Sets tab is selected
    LaunchedEffect(selectedTab) {
        if (selectedTab == MagicScreenTab.SCRYFALL_SETS && scryfallSets.isEmpty()) {
            isLoadingSets = true
            scryfallSets = ScryfallDataService.getSets()
            isLoadingSets = false
        }
    }

    val filteredList = remember(mtgCards, selectedSetFilter, selectedRarityFilter, selectedFoilFilter, searchQuery) {
        mtgCards.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.itemNumber.contains(searchQuery, ignoreCase = true) ||
                    item.collection.contains(searchQuery, ignoreCase = true)

            val matchesSet = selectedSetFilter == "TODOS" || item.collection.contains(selectedSetFilter, ignoreCase = true)
            val matchesRarity = selectedRarityFilter == "TODOS" || item.rarity.contains(selectedRarityFilter, ignoreCase = true)
            val matchesFoil = !selectedFoilFilter || item.variant.contains("Foil", ignoreCase = true)

            matchesQuery && matchesSet && matchesRarity && matchesFoil
        }
    }

    val totalCards = mtgCards.sumOf { it.quantity }
    val totalValue = mtgCards.sumOf { it.totalEstimatedValue }

    // Helper to perform full Scryfall search
    fun performFullSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            isSearchingScryfall = true
            // Run full search
            val results = ScryfallDataService.searchCardsByName(trimmed)
            scryfallResults = if (results.isNotEmpty()) {
                results
            } else {
                // Fuzzy fallback if regular search returns empty
                val fuzzy = ScryfallDataService.findCardByFuzzyName(trimmed)
                if (fuzzy != null) listOf(fuzzy) else emptyList()
            }
            isSearchingScryfall = false
        }
    }

    // Dialog for viewing rich Scryfall Card details
    if (selectedCardForDetails != null) {
        ScryfallCardDetailDialog(
            card = selectedCardForDetails!!,
            onDismiss = { selectedCardForDetails = null },
            onAddToCollection = { card ->
                viewModel.importScryfallCardToCollection(card) { savedItem ->
                    feedbackMessage = "Adicionado à coleção: ${savedItem.name}!"
                }
            },
            onAddToWishlist = { card ->
                val estPrice = card.getEstimatedPriceBrl()
                val wishItem = Item(
                    name = card.name,
                    type = "Trading Cards",
                    subCategory = "Magic: The Gathering",
                    collection = card.setName.ifBlank { "Magic: The Gathering" },
                    itemNumber = card.collectorNumber,
                    rarity = card.rarityPt,
                    condition = "Near Mint",
                    language = "EN",
                    estimatedValue = estPrice,
                    purchasePrice = estPrice * 0.75,
                    imageUri = card.getHighResImage(),
                    notes = "Desejado na Wishlist via Scryfall API (${card.setName} #${card.collectorNumber})",
                    tags = "wishlist, mtg, magic"
                )
                viewModel.insertItem(wishItem)
                feedbackMessage = "Adicionado à Wishlist: ${card.name}!"
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFEF4444))
                        Text(
                            text = if (activeSetForBrowsing != null && selectedTab == MagicScreenTab.SCRYFALL_SETS) {
                                activeSetForBrowsing!!.name
                            } else {
                                "Magic: The Gathering"
                            },
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (activeSetForBrowsing != null && selectedTab == MagicScreenTab.SCRYFALL_SETS) {
                            activeSetForBrowsing = null
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (selectedTab == MagicScreenTab.MY_COLLECTION) {
                        IconButton(onClick = {
                            val nextMode = when (currentViewMode) {
                                ViewMode.GRID -> ViewMode.LIST
                                ViewMode.LIST -> ViewMode.COMPACT
                                ViewMode.COMPACT -> ViewMode.GRID
                            }
                            viewModel.setViewMode(nextMode)
                        }) {
                            Icon(
                                when (currentViewMode) {
                                    ViewMode.GRID -> Icons.Default.GridView
                                    ViewMode.LIST -> Icons.AutoMirrored.Filled.ViewList
                                    ViewMode.COMPACT -> Icons.Default.TableRows
                                },
                                contentDescription = "Mudar Visualização"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("magic_tcg_screen")
        ) {
            // Tab Selector: Minha Coleção / Scryfall Autocomplete / Sets MTG
            PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
                MagicScreenTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            if (tab != MagicScreenTab.SCRYFALL_SETS) {
                                activeSetForBrowsing = null
                            }
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(tab.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    )
                }
            }

            // Feedback Banner
            if (feedbackMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            Text(feedbackMessage ?: "", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        IconButton(onClick = { feedbackMessage = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color(0xFF166534), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            when (selectedTab) {
                MagicScreenTab.MY_COLLECTION -> {
                    // Header Stats
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total de cards MTG:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF991B1B))
                                Text("$totalCards cards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF7F1D1D))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Valor Estimado:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF991B1B))
                                Text(currency.formatValue(totalValue), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar na coleção (carta, collector number, set...)") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedFoilFilter,
                                onClick = { selectedFoilFilter = !selectedFoilFilter },
                                label = { Text("Foil") }
                            )
                            FilterChip(
                                selected = selectedRarityFilter == "Mítica",
                                onClick = { selectedRarityFilter = if (selectedRarityFilter == "Mítica") "TODOS" else "Mítica" },
                                label = { Text("Mítica") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Nenhum card de Magic na sua coleção", color = MaterialTheme.colorScheme.outline)
                                Button(onClick = { selectedTab = MagicScreenTab.SCRYFALL_EXPLORER }) {
                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buscar no Scryfall")
                                }
                            }
                        }
                    } else {
                        when (currentViewMode) {
                            ViewMode.GRID -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(150.dp),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredList, key = { it.id }) { item ->
                                        MagicCardGridItem(item, currency) {
                                            viewModel.selectedItem.value = item
                                            navController.navigate(Routes.ITEM_DETAIL)
                                        }
                                    }
                                }
                            }
                            ViewMode.LIST, ViewMode.COMPACT -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredList, key = { it.id }) { item ->
                                        MagicCardListItem(item, currency) {
                                            viewModel.selectedItem.value = item
                                            navController.navigate(Routes.ITEM_DETAIL)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                MagicScreenTab.SCRYFALL_EXPLORER -> {
                    // Scryfall Online Search & Real-Time Autocomplete / Fuzzy Hub
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = scryfallQuery,
                            onValueChange = { newQuery ->
                                scryfallQuery = newQuery
                                searchJob?.cancel()
                                if (newQuery.trim().length >= 2) {
                                    searchJob = coroutineScope.launch {
                                        isFetchingSuggestions = true
                                        delay(150) // Fast debounce
                                        val suggestionsResult = ScryfallDataService.getLiveSuggestionsAndFuzzyCard(newQuery.trim())
                                        liveSuggestions = suggestionsResult.suggestions
                                        directFuzzyCard = suggestionsResult.directFuzzyCard
                                        isFetchingSuggestions = false

                                        // Auto-fetch full search if user pauses or has entered 3+ chars
                                        if (newQuery.trim().length >= 3) {
                                            isSearchingScryfall = true
                                            val results = ScryfallDataService.searchCardsByName(newQuery.trim())
                                            scryfallResults = if (results.isNotEmpty()) results else listOfNotNull(suggestionsResult.directFuzzyCard)
                                            isSearchingScryfall = false
                                        }
                                    }
                                } else {
                                    liveSuggestions = emptyList()
                                    directFuzzyCard = null
                                    scryfallResults = emptyList()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_scryfall_autocomplete"),
                            placeholder = { Text("Nome aproximado (ex: black lotu, sheoldred, sol ring...)") },
                            leadingIcon = {
                                if (isSearchingScryfall || isFetchingSuggestions) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF6366F1))
                                } else {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6366F1))
                                }
                            },
                            trailingIcon = {
                                if (scryfallQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        scryfallQuery = ""
                                        scryfallResults = emptyList()
                                        liveSuggestions = emptyList()
                                        directFuzzyCard = null
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Real-Time Autocomplete Suggestions Row
                        if (liveSuggestions.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = Color(0xFF6366F1),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Sugestões em tempo real (Scryfall Autocomplete):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6366F1)
                                    )
                                }
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(liveSuggestions) { suggestion ->
                                        SuggestionChip(
                                            onClick = {
                                                scryfallQuery = suggestion
                                                performFullSearch(suggestion)
                                            },
                                            label = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF6366F1))
                                                    Text(suggestion, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Real-Time Fuzzy Direct Match Preview Card
                        if (directFuzzyCard != null && scryfallQuery.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCardForDetails = directFuzzyCard },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF818CF8))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 62.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E1E2E)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = directFuzzyCard!!.getHighResImage(),
                                            contentDescription = directFuzzyCard!!.name,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Surface(
                                                color = Color(0xFF4F46E5),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.AutoAwesome,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = "Fuzzy Match",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Text(
                                                text = directFuzzyCard!!.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = "${directFuzzyCard!!.setName} #${directFuzzyCard!!.collectorNumber} • ${directFuzzyCard!!.rarityPt}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Valor Estimado: ${currency.formatValue(directFuzzyCard!!.getEstimatedPriceBrl())}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4F46E5)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.importScryfallCardToCollection(directFuzzyCard!!) { savedItem ->
                                                feedbackMessage = "Adicionado: ${savedItem.name}!"
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Adicionar", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Quick suggestion search chips when query is empty
                        if (scryfallQuery.isEmpty()) {
                            Text(
                                text = "Pesquisas populares no Scryfall:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(listOf("Black Lotus", "Sol Ring", "Force of Will", "The One Ring", "Sheoldred", "Ragavan", "Mana Crypt")) { sampleCard ->
                                    SuggestionChip(
                                        onClick = {
                                            scryfallQuery = sampleCard
                                            performFullSearch(sampleCard)
                                        },
                                        label = { Text(sampleCard, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color(0xFF6366F1)
                                    )
                                    Text(
                                        "Digite qualquer nome aproximado para sugestões instantâneas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Resultados Oficiais Scryfall (${scryfallResults.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1)
                            )

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(scryfallResults, key = { it.id }) { card ->
                                    ScryfallOnlineCardRow(
                                        card = card,
                                        currency = currency,
                                        onInspect = { selectedCardForDetails = card },
                                        onAdd = {
                                            viewModel.importScryfallCardToCollection(card) { savedItem ->
                                                feedbackMessage = "Adicionado: ${savedItem.name}!"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                MagicScreenTab.SCRYFALL_SETS -> {
                    // MTG Sets Browser inside Magic Screen
                    if (activeSetForBrowsing != null) {
                        // Viewing cards inside a selected set
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3E8FF)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(activeSetForBrowsing!!.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF581C87))
                                        Text("Set Code: [${activeSetForBrowsing!!.code.uppercase()}] • Ano: ${activeSetForBrowsing!!.releaseYear} • ${activeSetForBrowsing!!.cardCount} cartas", fontSize = 12.sp, color = Color(0xFF6B21A8))
                                    }
                                    OutlinedButton(
                                        onClick = { activeSetForBrowsing = null },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Trocar Set", fontSize = 11.sp)
                                    }
                                }
                            }

                            if (isLoadingSetCards) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF9333EA))
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(150.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(cardsInActiveSet, key = { it.id }) { card ->
                                        ScryfallSetCardGridItem(
                                            card = card,
                                            currency = currency,
                                            onInspect = { selectedCardForDetails = card },
                                            onAdd = {
                                                viewModel.importScryfallCardToCollection(card) { savedItem ->
                                                    feedbackMessage = "Adicionado: ${savedItem.name}!"
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Sets List
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = setFilterQuery,
                                onValueChange = { setFilterQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Filtrar sets MTG (ex: Bloomburrow, MH3, 2024...)") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            val availableYears = remember(scryfallSets) {
                                listOf("TODOS") + scryfallSets.map { it.releaseYear }.distinct().sortedDescending()
                            }

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(availableYears) { year ->
                                    FilterChip(
                                        selected = selectedSetYearFilter == year,
                                        onClick = { selectedSetYearFilter = year },
                                        leadingIcon = {
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                        },
                                        label = { Text(if (year == "TODOS") "Todos os Anos" else year, fontSize = 11.sp) }
                                    )
                                }
                            }

                            val filteredSets = remember(scryfallSets, setFilterQuery, selectedSetYearFilter) {
                                scryfallSets.filter { set ->
                                    val matchQuery = setFilterQuery.isBlank() ||
                                            set.name.contains(setFilterQuery, ignoreCase = true) ||
                                            set.code.contains(setFilterQuery, ignoreCase = true) ||
                                            set.releaseYear.contains(setFilterQuery, ignoreCase = true)

                                    val matchYear = selectedSetYearFilter == "TODOS" || set.releaseYear == selectedSetYearFilter
                                    matchQuery && matchYear
                                }
                            }

                            if (isLoadingSets) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredSets, key = { it.id }) { set ->
                                        ScryfallSetListItem(
                                            set = set,
                                            onBrowseSet = {
                                                activeSetForBrowsing = set
                                                isLoadingSetCards = true
                                                coroutineScope.launch {
                                                    cardsInActiveSet = ScryfallDataService.getCardsForSet(set.code)
                                                    isLoadingSetCards = false
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScryfallSetListItem(
    set: ScryfallSet,
    onBrowseSet: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBrowseSet() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = set.code.uppercase().take(4),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFFDC2626)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = set.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ano: ${set.releaseYear} • ${set.formattedSetType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "${set.cardCount} cartas no set oficial Scryfall",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6366F1)
                )
            }

            Button(
                onClick = onBrowseSet,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver Cartas", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ScryfallSetCardGridItem(
    card: ScryfallCard,
    currency: com.example.data.AppCurrency,
    onInspect: () -> Unit,
    onAdd: () -> Unit
) {
    val estPrice = card.getEstimatedPriceBrl()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.5f / 3.5f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFF1E1E2E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = card.getHighResImage(),
                    contentDescription = card.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(card.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#${card.collectorNumber} • ${card.rarityPt}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currency.formatValue(estPrice), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                    IconButton(onClick = onAdd, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Adicionar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScryfallOnlineCardRow(
    card: ScryfallCard,
    currency: com.example.data.AppCurrency,
    onInspect: () -> Unit,
    onAdd: () -> Unit
) {
    val estPrice = card.getEstimatedPriceBrl()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E1E2E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = card.getHighResImage(),
                    contentDescription = card.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${card.setName} #${card.collectorNumber} • ${card.rarityPt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "Scryfall: ${currency.formatValue(estPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onInspect) {
                    Icon(Icons.Default.Info, contentDescription = "Detalhes", tint = MaterialTheme.colorScheme.primary)
                }

                Button(
                    onClick = onAdd,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun MagicCardListItem(
    item: Item,
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF2E1065)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUri != null) {
                    AsyncImage(
                        model = item.imageUri,
                        contentDescription = item.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFEF4444))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${item.collection} #${item.itemNumber} • ${item.rarity}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Text("Condição: ${item.condition}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(currency.formatValue(item.estimatedValue), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Qtd: ${item.quantity}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun MagicCardGridItem(
    item: Item,
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    ItemGridCard(
        item = item,
        currency = currency,
        onClick = onClick
    )
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.api.scryfall.ScryfallCard
import com.example.api.scryfall.ScryfallDataService
import com.example.api.scryfall.ScryfallSet
import com.example.data.*
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.components.ScryfallCardDetailDialog
import com.example.util.OfficialCardImageHelper
import kotlinx.coroutines.launch

enum class CatalogTab(val title: String, val subtitle: String) {
    ALL_CARDS("Todas as Cartas", "Grade & Lista Completa"),
    BY_SET("Edições & Sets", "Navegador de Coleções"),
    MTG_SCRYFALL("Magic (Scryfall)", "Sets & Expansões Oficiais")
}

enum class CatalogSortOption(val title: String) {
    NUMBER("Número no Set (#)"),
    PRICE_DESC("Maior Preço"),
    PRICE_ASC("Menor Preço"),
    NAME_ASC("Nome (A-Z)"),
    YEAR_DESC("Ano mais recente")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val allCollectionItems by viewModel.allItems.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(CatalogTab.ALL_CARDS) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFranchise by remember { mutableStateOf("TODOS") }
    var selectedYear by remember { mutableStateOf("TODOS") }
    var selectedEdition by remember { mutableStateOf("TODAS") }
    var selectedRarity by remember { mutableStateOf("TODAS") }
    var selectedSort by remember { mutableStateOf(CatalogSortOption.NUMBER) }
    var isGridView by remember { mutableStateOf(true) }
    var onlyMissing by remember { mutableStateOf(false) }

    // Dialog state for fast adding to collection
    var itemToAddToCollection by remember { mutableStateOf<RealCatalogEntry?>(null) }
    var itemForDetailDialog by remember { mutableStateOf<RealCatalogEntry?>(null) }
    var selectedSetDetail by remember { mutableStateOf<CollectibleSet?>(null) }

    // Scryfall MTG Sets Tab State
    var scryfallSets by remember { mutableStateOf<List<ScryfallSet>>(emptyList()) }
    var isLoadingScryfallSets by remember { mutableStateOf(false) }
    var mtgSetQuery by remember { mutableStateOf("") }
    var mtgSelectedYear by remember { mutableStateOf("TODOS") }
    var mtgSelectedType by remember { mutableStateOf("TODOS") }
    var selectedMtgSetForBrowse by remember { mutableStateOf<ScryfallSet?>(null) }
    var cardsInSelectedMtgSet by remember { mutableStateOf<List<ScryfallCard>>(emptyList()) }
    var isLoadingMtgCards by remember { mutableStateOf(false) }
    var setCardSearchQuery by remember { mutableStateOf("") }
    var setCardRarityFilter by remember { mutableStateOf("TODAS") }
    var selectedScryfallCardForModal by remember { mutableStateOf<ScryfallCard?>(null) }
    var scryfallFeedbackBanner by remember { mutableStateOf<String?>(null) }

    // Fetch MTG sets when MTG_SCRYFALL tab is opened
    LaunchedEffect(activeTab) {
        if (activeTab == CatalogTab.MTG_SCRYFALL && scryfallSets.isEmpty()) {
            isLoadingScryfallSets = true
            scryfallSets = ScryfallDataService.getSets()
            isLoadingScryfallSets = false
        }
    }

    val allCatalogEntries = remember { RealMarketCatalog.allEntries }
    val allPopularSets = remember { SetRegistry.popularSets }

    // Dynamic Franchises from catalog
    val availableFranchises = remember(allCatalogEntries) {
        listOf("TODOS") + allCatalogEntries.map { it.subCategory }.distinct().sorted()
    }

    // Dynamic Years
    val availableYears = remember(allCatalogEntries, selectedFranchise) {
        val entries = if (selectedFranchise == "TODOS") allCatalogEntries else allCatalogEntries.filter { it.subCategory == selectedFranchise }
        listOf("TODOS") + entries.map { it.effectiveYear }.distinct().sortedDescending()
    }

    // Dynamic Editions / Sets
    val availableEditions = remember(allCatalogEntries, selectedFranchise, selectedYear) {
        var filtered = allCatalogEntries
        if (selectedFranchise != "TODOS") {
            filtered = filtered.filter { it.subCategory == selectedFranchise }
        }
        if (selectedYear != "TODOS") {
            filtered = filtered.filter { it.effectiveYear == selectedYear }
        }
        listOf("TODAS") + filtered.map { it.collection }.distinct().sorted()
    }

    // Dynamic Rarities
    val availableRarities = remember(allCatalogEntries, selectedFranchise, selectedEdition) {
        var filtered = allCatalogEntries
        if (selectedFranchise != "TODOS") filtered = filtered.filter { it.subCategory == selectedFranchise }
        if (selectedEdition != "TODAS") filtered = filtered.filter { it.collection == selectedEdition }
        listOf("TODAS") + filtered.map { it.rarity }.distinct().sorted()
    }

    // Filtered & Sorted entries
    val filteredEntries = remember(
        allCatalogEntries,
        searchQuery,
        selectedFranchise,
        selectedYear,
        selectedEdition,
        selectedRarity,
        onlyMissing,
        selectedSort,
        allCollectionItems
    ) {
        val trimmed = searchQuery.trim().lowercase()
        val list = allCatalogEntries.filter { entry ->
            val matchesSearch = trimmed.isBlank() ||
                    entry.name.lowercase().contains(trimmed) ||
                    entry.collection.lowercase().contains(trimmed) ||
                    entry.setCode.lowercase().contains(trimmed) ||
                    entry.era.lowercase().contains(trimmed) ||
                    entry.patternVariant.lowercase().contains(trimmed) ||
                    entry.itemNumber.lowercase().contains(trimmed) ||
                    entry.variant.lowercase().contains(trimmed) ||
                    entry.tags.lowercase().contains(trimmed)

            val matchesFranchise = selectedFranchise == "TODOS" || entry.subCategory.equals(selectedFranchise, ignoreCase = true)
            val matchesYear = selectedYear == "TODOS" || entry.effectiveYear == selectedYear
            val matchesEdition = selectedEdition == "TODAS" || entry.collection.equals(selectedEdition, ignoreCase = true)
            val matchesRarity = selectedRarity == "TODAS" || entry.rarity.equals(selectedRarity, ignoreCase = true)

            val isOwned = allCollectionItems.any { owned ->
                owned.name.equals(entry.name, ignoreCase = true) ||
                        (owned.collection.equals(entry.collection, ignoreCase = true) && owned.itemNumber.equals(entry.itemNumber, ignoreCase = true))
            }
            val matchesMissing = !onlyMissing || !isOwned

            matchesSearch && matchesFranchise && matchesYear && matchesEdition && matchesRarity && matchesMissing
        }

        when (selectedSort) {
            CatalogSortOption.NUMBER -> list.sortedWith(compareBy(
                { it.collection },
                { entry ->
                    val num = entry.itemNumber.split("/").firstOrNull()?.filter { it.isDigit() }?.toIntOrNull()
                    num ?: 9999
                },
                { it.name }
            ))
            CatalogSortOption.PRICE_DESC -> list.sortedByDescending { it.realMarketPriceBrl }
            CatalogSortOption.PRICE_ASC -> list.sortedBy { it.realMarketPriceBrl }
            CatalogSortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
            CatalogSortOption.YEAR_DESC -> list.sortedByDescending { it.effectiveYear }
        }
    }

    // Sets matching franchise / year
    val filteredSets = remember(allPopularSets, selectedFranchise, selectedYear, searchQuery) {
        val trimmed = searchQuery.trim().lowercase()
        allPopularSets.filter { set ->
            val matchFranchise = selectedFranchise == "TODOS" || set.franchise.equals(selectedFranchise, ignoreCase = true)
            val matchYear = selectedYear == "TODOS" || set.year == selectedYear
            val matchSearch = trimmed.isBlank() ||
                    set.name.lowercase().contains(trimmed) ||
                    set.code.lowercase().contains(trimmed) ||
                    set.era.lowercase().contains(trimmed) ||
                    set.franchise.lowercase().contains(trimmed) ||
                    set.items.any { it.name.lowercase().contains(trimmed) || it.number.lowercase().contains(trimmed) || it.setCode.lowercase().contains(trimmed) }

            matchFranchise && matchYear && matchSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Catálogo Universal de Cartas & Miniaturas", fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = "${filteredEntries.size} cartas/itens disponíveis • ${allPopularSets.size} coleções",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("btn_catalog_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (activeTab == CatalogTab.ALL_CARDS) {
                        IconButton(onClick = { isGridView = !isGridView }) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Alternar Layout"
                            )
                        }
                    }
                    IconButton(onClick = { navController.navigate(Routes.SET_CHECKLIST) }) {
                        Icon(Icons.Default.Checklist, contentDescription = "Checklists de Progresso")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("catalog_screen")
        ) {
            // Main Tabs Selector
            TabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                CatalogTab.values().forEach { tab ->
                    Tab(
                        selected = activeTab == tab,
                        onClick = { activeTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nome da carta, set, número (#001) ou raridade...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input")
                )

                // Franchise Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableFranchises) { franchise ->
                        FilterChip(
                            selected = selectedFranchise == franchise,
                            onClick = {
                                selectedFranchise = franchise
                                selectedEdition = "TODAS"
                            },
                            leadingIcon = {
                                val icon = when (franchise) {
                                    "TODOS" -> Icons.Default.Category
                                    "Pokémon TCG" -> Icons.Default.Bolt
                                    "Magic: The Gathering" -> Icons.Default.AutoAwesome
                                    "Yu-Gi-Oh!" -> Icons.Default.Style
                                    "One Piece Card Game" -> Icons.Default.DirectionsBoat
                                    "Hot Wheels" -> Icons.Default.DirectionsCar
                                    "Kaido House" -> Icons.Default.Speed
                                    "Disney Lorcana" -> Icons.Default.Stars
                                    "Moedas" -> Icons.Default.MonetizationOn
                                    else -> Icons.Default.Bookmark
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            label = {
                                val labelText = when (franchise) {
                                    "TODOS" -> "Todas Categorias"
                                    "Pokémon TCG" -> "Pokémon"
                                    "Magic: The Gathering" -> "Magic MTG"
                                    "Yu-Gi-Oh!" -> "Yu-Gi-Oh!"
                                    "One Piece Card Game" -> "One Piece"
                                    "Hot Wheels" -> "Hot Wheels"
                                    "Kaido House" -> "Kaido House"
                                    "Disney Lorcana" -> "Lorcana"
                                    "Moedas" -> "Moedas"
                                    else -> franchise
                                }
                                Text(labelText, fontSize = 12.sp, fontWeight = if (selectedFranchise == franchise) FontWeight.Bold else FontWeight.Normal)
                            }
                        )
                    }
                }

                // Sub-filter Row: Missing Only, Year, Edition, Sort
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = onlyMissing,
                        onClick = { onlyMissing = !onlyMissing },
                        label = { Text("Faltam", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                if (onlyMissing) Icons.Default.Check else Icons.Default.TrackChanges,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )

                    // Year Filter
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(availableYears) { year ->
                            FilterChip(
                                selected = selectedYear == year,
                                onClick = { selectedYear = year },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                },
                                label = { Text(if (year == "TODOS") "Anos" else year, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Editions / Sets Filter Row if multiple
                if (availableEditions.size > 2) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableEditions) { edition ->
                            FilterChip(
                                selected = selectedEdition == edition,
                                onClick = { selectedEdition = edition },
                                leadingIcon = {
                                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(12.dp))
                                },
                                label = {
                                    Text(
                                        if (edition == "TODAS") "Todas Edições" else edition,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // TAB 1: BY POPULAR COMPLETE SETS
            if (activeTab == CatalogTab.BY_SET) {
                if (filteredSets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhuma coleção encontrada com os filtros selecionados.", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSets, key = { it.id }) { set ->
                            val ownedCards = allCollectionItems.filter { item ->
                                item.collection.equals(set.name, ignoreCase = true) ||
                                        (item.subCategory.equals(set.franchise, ignoreCase = true) && set.items.any { setItem -> setItem.name.equals(item.name, ignoreCase = true) })
                            }
                            val ownedUnique = set.items.count { setCard ->
                                ownedCards.any { owned ->
                                    owned.name.equals(setCard.name, ignoreCase = true) ||
                                            owned.itemNumber.equals(setCard.number, ignoreCase = true)
                                }
                            }
                            val percent = if (set.items.isNotEmpty()) (ownedUnique.toFloat() / set.items.size.toFloat()) else 0f
                            val totalSetValue = set.items.sumOf { it.estimatedPriceBrl }

                            SetOverviewCard(
                                set = set,
                                ownedCount = ownedUnique,
                                totalCardsInSet = set.items.size,
                                completionPercent = percent,
                                totalSetValueBrl = totalSetValue,
                                currency = currency,
                                onOpenSet = {
                                    selectedEdition = set.name
                                    activeTab = CatalogTab.ALL_CARDS
                                },
                                onInspectCards = {
                                    selectedSetDetail = set
                                }
                            )
                        }
                    }
                }
            } else if (activeTab == CatalogTab.MTG_SCRYFALL) {
                // TAB 2: DEDICATED MTG SCRYFALL SETS & EXPANSIONS EXPLORER
                if (selectedMtgSetForBrowse != null) {
                    // Browsing cards within a specific Scryfall Set
                    val set = selectedMtgSetForBrowse!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Set Header Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF3E8FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC084FC))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(
                                        onClick = { selectedMtgSetForBrowse = null },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar aos Sets", tint = Color(0xFF6B21A8))
                                    }
                                    Column {
                                        Text(
                                            text = set.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF581C87),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Set: [${set.code.uppercase()}] • Ano: ${set.releaseYear} • ${set.formattedSetType}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF7E22CE)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF7E22CE)
                                ) {
                                    Text(
                                        text = "${cardsInSelectedMtgSet.size} cartas",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Search and Filter within this Set
                        OutlinedTextField(
                            value = setCardSearchQuery,
                            onValueChange = { setCardSearchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar carta no set ${set.name}...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (setCardSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { setCardSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Rarity filter chips within the set
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("TODAS", "Mítica", "Rara", "Incomum", "Comum")) { rarity ->
                                FilterChip(
                                    selected = setCardRarityFilter == rarity,
                                    onClick = { setCardRarityFilter = rarity },
                                    label = { Text(rarity, fontSize = 11.sp) }
                                )
                            }
                        }

                        val filteredCardsInSet = remember(cardsInSelectedMtgSet, setCardSearchQuery, setCardRarityFilter) {
                            val q = setCardSearchQuery.trim().lowercase()
                            cardsInSelectedMtgSet.filter { card ->
                                val matchQ = q.isBlank() ||
                                        card.name.lowercase().contains(q) ||
                                        card.collectorNumber.lowercase().contains(q) ||
                                        (card.typeLine?.lowercase()?.contains(q) == true) ||
                                        (card.oracleText?.lowercase()?.contains(q) == true)

                                val matchRarity = setCardRarityFilter == "TODAS" || card.rarityPt.equals(setCardRarityFilter, ignoreCase = true)
                                matchQ && matchRarity
                            }
                        }

                        if (isLoadingMtgCards) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator(color = Color(0xFF7E22CE))
                                    Text("Carregando cartas do Scryfall...", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else if (filteredCardsInSet.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Nenhuma carta encontrada neste set.", color = MaterialTheme.colorScheme.outline)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(150.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredCardsInSet, key = { it.id }) { card ->
                                    val estPrice = card.getEstimatedPriceBrl()
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedScryfallCardForModal = card },
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
                                                    Text(currency.formatValue(estPrice), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE))
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.importScryfallCardToCollection(card) { savedItem ->
                                                                scryfallFeedbackBanner = "Adicionado à coleção: ${savedItem.name}!"
                                                            }
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.AddCircle, contentDescription = "Adicionar", tint = Color(0xFF7E22CE), modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Displaying list of Magic Sets from Scryfall
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Feedback banner
                        if (scryfallFeedbackBanner != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(scryfallFeedbackBanner ?: "", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    IconButton(onClick = { scryfallFeedbackBanner = null }, modifier = Modifier.size(18.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color(0xFF166534), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        // MTG Sets Search Bar
                        OutlinedTextField(
                            value = mtgSetQuery,
                            onValueChange = { mtgSetQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar sets MTG (ex: Duskmourn, Modern Horizons, Fallout, 2024...)") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (mtgSetQuery.isNotEmpty()) {
                                    IconButton(onClick = { mtgSetQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Year Filters for Scryfall Sets
                        val mtgYears = remember(scryfallSets) {
                            listOf("TODOS") + scryfallSets.map { it.releaseYear }.distinct().sortedDescending()
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(mtgYears) { year ->
                                FilterChip(
                                    selected = mtgSelectedYear == year,
                                    onClick = { mtgSelectedYear = year },
                                    leadingIcon = {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp))
                                    },
                                    label = { Text(if (year == "TODOS") "Todos os Anos" else year, fontSize = 11.sp) }
                                )
                            }
                        }

                        val filteredMtgSets = remember(scryfallSets, mtgSetQuery, mtgSelectedYear) {
                            val q = mtgSetQuery.trim().lowercase()
                            scryfallSets.filter { set ->
                                val matchQuery = q.isBlank() ||
                                        set.name.lowercase().contains(q) ||
                                        set.code.lowercase().contains(q) ||
                                        set.releaseYear.lowercase().contains(q) ||
                                        set.formattedSetType.lowercase().contains(q)

                                val matchYear = mtgSelectedYear == "TODOS" || set.releaseYear == mtgSelectedYear
                                matchQuery && matchYear
                            }
                        }

                        if (isLoadingScryfallSets) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator(color = Color(0xFFEF4444))
                                    Text("Carregando catálogo de sets do Scryfall...", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else if (filteredMtgSets.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Nenhum set de Magic encontrado com os filtros.", color = MaterialTheme.colorScheme.outline)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredMtgSets, key = { it.id }) { set ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedMtgSetForBrowse = set
                                                isLoadingMtgCards = true
                                                coroutineScope.launch {
                                                    cardsInSelectedMtgSet = ScryfallDataService.getCardsForSet(set.code)
                                                    isLoadingMtgCards = false
                                                }
                                            },
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                                modifier = Modifier.size(50.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = set.code.uppercase().take(4),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 14.sp,
                                                        color = Color(0xFFDC2626)
                                                    )
                                                }
                                            }

                                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    text = set.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        color = Color(0xFF6366F1).copy(alpha = 0.15f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Default.CalendarToday,
                                                                contentDescription = null,
                                                                tint = Color(0xFF4F46E5),
                                                                modifier = Modifier.size(11.dp)
                                                            )
                                                            Text(
                                                                text = set.releaseYear,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF4F46E5)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = set.formattedSetType,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Text(
                                                    text = "${set.cardCount} cartas oficiais cadastradas no Scryfall",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    selectedMtgSetForBrowse = set
                                                    isLoadingMtgCards = true
                                                    coroutineScope.launch {
                                                        cardsInSelectedMtgSet = ScryfallDataService.getCardsForSet(set.code)
                                                        isLoadingMtgCards = false
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text("Explorar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 0: ALL CARDS ROSTER (Grid / List)
                if (filteredEntries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Nenhuma carta encontrada",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tente alterar os filtros de categoria, edição ou termo de busca.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedFranchise = "TODOS"
                                    selectedYear = "TODOS"
                                    selectedEdition = "TODAS"
                                    selectedRarity = "TODAS"
                                    onlyMissing = false
                                }
                            ) {
                                Text("Limpar Todos os Filtros")
                            }
                        }
                    }
                } else if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEntries, key = { "${it.collection}_${it.itemNumber}_${it.name}" }) { entry ->
                            val ownedQty = allCollectionItems
                                .filter { it.name.equals(entry.name, ignoreCase = true) }
                                .sumOf { it.quantity }

                            CatalogGridCard(
                                entry = entry,
                                currency = currency,
                                ownedQuantity = ownedQty,
                                onCardClick = { itemForDetailDialog = entry },
                                onAddClick = { itemToAddToCollection = entry }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEntries, key = { "${it.collection}_${it.itemNumber}_${it.name}" }) { entry ->
                            val ownedQty = allCollectionItems
                                .filter { it.name.equals(entry.name, ignoreCase = true) }
                                .sumOf { it.quantity }

                            CatalogListCard(
                                entry = entry,
                                currency = currency,
                                ownedQuantity = ownedQty,
                                onCardClick = { itemForDetailDialog = entry },
                                onAddClick = { itemToAddToCollection = entry }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal para Adicionar item do catálogo à coleção
    itemToAddToCollection?.let { entry ->
        AddCatalogItemModal(
            entry = entry,
            currency = currency,
            onDismiss = { itemToAddToCollection = null },
            onConfirm = { qty, paidPrice, cond, location, notes ->
                val officialImg = OfficialCardImageHelper.getOfficialImageUrl(entry.name, entry.subCategory, entry.collection, entry.itemNumber)
                val newItem = Item(
                    name = entry.name,
                    type = entry.category,
                    subCategory = entry.subCategory,
                    collection = entry.collection,
                    itemNumber = entry.itemNumber,
                    rarity = entry.rarity,
                    condition = cond,
                    estimatedValue = entry.realMarketPriceBrl,
                    purchasePrice = paidPrice,
                    quantity = qty,
                    storageLocation = location,
                    notes = notes.ifBlank { entry.notes },
                    tags = entry.tags,
                    variant = entry.variant,
                    language = if (entry.languageOrScale.contains("1:")) "N/A" else entry.languageOrScale,
                    scale = if (entry.languageOrScale.contains("1:")) entry.languageOrScale else "",
                    year = entry.effectiveYear,
                    imageUri = officialImg
                )
                viewModel.insertItem(newItem)
                itemToAddToCollection = null
            }
        )
    }

    // Dialog de Detalhes Completos da Carta/Carrinho do Catálogo
    itemForDetailDialog?.let { entry ->
        CatalogItemDetailDialog(
            entry = entry,
            currency = currency,
            onDismiss = { itemForDetailDialog = null },
            onAddClick = {
                itemForDetailDialog = null
                itemToAddToCollection = entry
            },
            onScanClick = {
                itemForDetailDialog = null
                viewModel.resetScanState()
                navController.navigate(Routes.SCANNER)
            }
        )
    }

    // Dialog de Inspeção Rápida de Todas as Cartas do Set
    selectedSetDetail?.let { set ->
        SetCardsRosterDialog(
            set = set,
            currency = currency,
            allCollectionItems = allCollectionItems,
            onDismiss = { selectedSetDetail = null },
            onCardClick = { cardItem ->
                val realEntry = RealCatalogEntry(
                    name = cardItem.name,
                    category = if (set.franchise == "Hot Wheels") "Carrinhos / Diecast" else if (set.franchise == "Moedas") "Moedas" else "Trading Cards",
                    subCategory = set.franchise,
                    collection = set.name,
                    itemNumber = cardItem.number,
                    rarity = cardItem.rarity,
                    variant = cardItem.variant,
                    defaultCondition = "Near Mint (NM)",
                    languageOrScale = if (set.franchise == "Hot Wheels") "1:64" else cardItem.language,
                    realMarketPriceBrl = cardItem.estimatedPriceBrl,
                    suggestedPurchasePriceBrl = cardItem.estimatedPriceBrl * 0.65,
                    defaultStorage = "Pasta ${set.name}",
                    notes = "Item oficial da coleção ${set.name} (${set.year})",
                    tags = "${set.franchise}, ${set.name}, ${cardItem.name}".lowercase(),
                    releaseYear = set.year
                )
                itemForDetailDialog = realEntry
            },
            onAddCardClick = { cardItem ->
                val realEntry = RealCatalogEntry(
                    name = cardItem.name,
                    category = if (set.franchise == "Hot Wheels") "Carrinhos / Diecast" else if (set.franchise == "Moedas") "Moedas" else "Trading Cards",
                    subCategory = set.franchise,
                    collection = set.name,
                    itemNumber = cardItem.number,
                    rarity = cardItem.rarity,
                    variant = cardItem.variant,
                    defaultCondition = "Near Mint (NM)",
                    languageOrScale = if (set.franchise == "Hot Wheels") "1:64" else cardItem.language,
                    realMarketPriceBrl = cardItem.estimatedPriceBrl,
                    suggestedPurchasePriceBrl = cardItem.estimatedPriceBrl * 0.65,
                    defaultStorage = "Pasta ${set.name}",
                    notes = "Item oficial da coleção ${set.name} (${set.year})",
                    tags = "${set.franchise}, ${set.name}, ${cardItem.name}".lowercase(),
                    releaseYear = set.year
                )
                itemToAddToCollection = realEntry
            }
        )
    }

    // Modal de Detalhes da Carta Scryfall
    selectedScryfallCardForModal?.let { scryCard ->
        ScryfallCardDetailDialog(
            card = scryCard,
            onDismiss = { selectedScryfallCardForModal = null },
            onAddToCollection = { cardToAdd ->
                viewModel.importScryfallCardToCollection(cardToAdd) { savedItem ->
                    scryfallFeedbackBanner = "Carta ${savedItem.name} adicionada à coleção!"
                }
                selectedScryfallCardForModal = null
            },
            onAddToWishlist = { cardToAdd ->
                val wishItem = Item(
                    name = cardToAdd.name,
                    type = "Trading Cards",
                    subCategory = "Magic: The Gathering",
                    collection = cardToAdd.setName,
                    itemNumber = cardToAdd.collectorNumber,
                    rarity = cardToAdd.rarityPt,
                    condition = "Near Mint",
                    estimatedValue = cardToAdd.getEstimatedPriceBrl(),
                    purchasePrice = 0.0,
                    quantity = 1,
                    storageLocation = "Wishlist",
                    notes = "Adicionado à Wishlist via Scryfall. Tipo: ${cardToAdd.typeLine ?: ""}",
                    tags = "mtg, wishlist, scryfall",
                    variant = if (cardToAdd.foil) "Foil" else "Normal",
                    language = if (cardToAdd.lang == "en") "EN" else if (cardToAdd.lang == "pt") "PT-BR" else cardToAdd.lang,
                    year = cardToAdd.releasedAt?.take(4) ?: "",
                    imageUri = cardToAdd.getHighResImage()
                )
                viewModel.insertItem(wishItem)
                scryfallFeedbackBanner = "Carta ${cardToAdd.name} adicionada à Wishlist!"
                selectedScryfallCardForModal = null
            }
        )
    }
}

@Composable
fun SetOverviewCard(
    set: CollectibleSet,
    ownedCount: Int,
    totalCardsInSet: Int,
    completionPercent: Float,
    totalSetValueBrl: Double,
    currency: AppCurrency,
    onOpenSet: () -> Unit,
    onInspectCards: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Franchise, Year & Banner Color
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(set.bannerColor))
                    )
                    Text(
                        text = set.franchise,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = set.year,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Set Title & Description
            Column {
                Text(
                    text = set.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = set.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progresso: $ownedCount / $totalCardsInSet cartas catalogadas",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(completionPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (completionPercent >= 1f) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    )
                }
                LinearProgressIndicator(
                    progress = { completionPercent.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (completionPercent >= 1f) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Set Value & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Valor Total do Set",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp
                    )
                    Text(
                        text = currency.formatValue(totalSetValueBrl),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF10B981)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onInspectCards,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Roster", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onOpenSet,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Explorar Cartas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SetCardsRosterDialog(
    set: CollectibleSet,
    currency: AppCurrency,
    allCollectionItems: List<Item>,
    onDismiss: () -> Unit,
    onCardClick: (SetCardItem) -> Unit,
    onAddCardClick: (SetCardItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = set.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Roster Oficial com todas as ${set.items.size} cartas/itens numerados",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // List of All Cards in Set
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(set.items, key = { "${it.number}_${it.name}" }) { card ->
                        val isOwned = allCollectionItems.any {
                            it.name.equals(card.name, ignoreCase = true) ||
                                    (it.collection.equals(set.name, ignoreCase = true) && it.itemNumber.equals(card.number, ignoreCase = true))
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isOwned) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCardClick(card) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isOwned) Color(0xFF10B981) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = card.number,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOwned) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = card.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${card.rarity} • ${card.variant}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 10.sp
                                    )
                                }

                                Text(
                                    text = currency.formatValue(card.estimatedPriceBrl),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )

                                IconButton(
                                    onClick = { onAddCardClick(card) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isOwned) Icons.Default.AddCircle else Icons.Default.Add,
                                        contentDescription = "Adicionar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Concluído")
                }
            }
        }
    }
}

@Composable
fun CatalogGridCard(
    entry: RealCatalogEntry,
    currency: AppCurrency,
    ownedQuantity: Int,
    onCardClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val officialUrl = remember(entry.name, entry.subCategory, entry.collection, entry.itemNumber) {
        OfficialCardImageHelper.getOfficialImageUrl(entry.name, entry.subCategory, entry.collection, entry.itemNumber)
    }

    val isCardItem = entry.category.contains("Carta", ignoreCase = true) ||
            entry.category.contains("Card", ignoreCase = true) ||
            entry.category.contains("TCG", ignoreCase = true) ||
            entry.subCategory.contains("TCG", ignoreCase = true) ||
            entry.subCategory.contains("Card", ignoreCase = true) ||
            entry.subCategory.contains("Pokémon", ignoreCase = true) ||
            entry.subCategory.contains("Pokemon", ignoreCase = true) ||
            entry.subCategory.contains("Magic", ignoreCase = true) ||
            entry.subCategory.contains("Yu-Gi-Oh", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column {
            // Image Banner with fixed 2.5:3.5 aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.5f / 3.5f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color(0xFF181824)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = officialUrl,
                    contentDescription = entry.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Year & Number Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${entry.effectiveYear} • ${entry.itemNumber.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Owned status badge
                if (ownedQuantity > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Possui ($ownedQuantity)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Info Body with aligned heights
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = entry.collection,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${entry.rarity} • ${entry.variant}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price and Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mercado",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 9.sp
                        )
                        Text(
                            text = currency.formatValue(entry.realMarketPriceBrl),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                    }

                    FilledIconButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogListCard(
    entry: RealCatalogEntry,
    currency: AppCurrency,
    ownedQuantity: Int,
    onCardClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val officialUrl = remember(entry.name, entry.subCategory, entry.collection, entry.itemNumber) {
        OfficialCardImageHelper.getOfficialImageUrl(entry.name, entry.subCategory, entry.collection, entry.itemNumber)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1E2E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = officialUrl,
                    contentDescription = entry.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (ownedQuantity > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF10B981)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "$ownedQuantity",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "${entry.collection} • ${entry.effectiveYear} • #${entry.itemNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${entry.rarity} | ${entry.variant}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Price & Add Button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currency.formatValue(entry.realMarketPriceBrl),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF10B981)
                )

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Adicionar", fontSize = 10.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatalogItemModal(
    entry: RealCatalogEntry,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, paidPrice: Double, condition: String, location: String, notes: String) -> Unit
) {
    var quantityText by remember { mutableStateOf("1") }
    var paidPriceText by remember { mutableStateOf(String.format("%.2f", entry.suggestedPurchasePriceBrl)) }
    var selectedCondition by remember { mutableStateOf(entry.defaultCondition) }
    var storageLocation by remember { mutableStateOf(entry.defaultStorage) }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Adicionar à Minha Coleção", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Preço de Compra Pago
                OutlinedTextField(
                    value = paidPriceText,
                    onValueChange = { paidPriceText = it },
                    label = { Text("Preço Pago (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quantidade
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantidade de Unidades") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Estado de Conservação
                OutlinedTextField(
                    value = selectedCondition,
                    onValueChange = { selectedCondition = it },
                    label = { Text("Estado / Condição") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Local de Armazenamento
                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Local de Armazenamento (Pasta / Caixa)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityText.toIntOrNull() ?: 1
                    val price = paidPriceText.replace(",", ".").toDoubleOrNull() ?: entry.suggestedPurchasePriceBrl
                    onConfirm(qty, price, selectedCondition, storageLocation, notesText)
                }
            ) {
                Text("Salvar na Coleção")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CatalogItemDetailDialog(
    entry: RealCatalogEntry,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit,
    onScanClick: () -> Unit
) {
    val officialUrl = remember(entry.name, entry.subCategory, entry.collection, entry.itemNumber) {
        OfficialCardImageHelper.getOfficialImageUrl(entry.name, entry.subCategory, entry.collection, entry.itemNumber)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF181825)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = officialUrl,
                        contentDescription = entry.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    )
                }

                // Name and Edition Info
                Column {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${entry.subCategory} • ${entry.collection} (${entry.effectiveYear})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Specifications
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Número:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text(entry.itemNumber.ifBlank { "N/A" }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Raridade:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text(entry.rarity, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Versão / Acabamento:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text(entry.variant, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cotação Média de Mercado:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text(
                                currency.formatValue(entry.realMarketPriceBrl),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                if (entry.notes.isNotBlank()) {
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onScanClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Escanear", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

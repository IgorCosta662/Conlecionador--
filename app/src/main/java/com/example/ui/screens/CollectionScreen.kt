package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.AppCurrency
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.SortOption
import com.example.ui.ViewMode
import com.example.ui.components.ItemCard
import com.example.ui.components.ItemGridCard

enum class OrganizationGrouping(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    BY_CATEGORY_SUB("Categoria & Marca", Icons.Default.Category),
    BY_SET("Coleção / Set", Icons.Default.Checklist),
    BY_STORAGE("Local Físico / Pasta", Icons.Default.Inventory2),
    BY_RARITY("Raridade", Icons.Default.Star),
    FLAT("Sem Agrupamento (Plano)", Icons.Default.ViewList)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selectedSubCategory by viewModel.selectedSubCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showRenewConfirmDialog by remember { mutableStateOf(false) }
    var selectedGrouping by remember { mutableStateOf(OrganizationGrouping.BY_CATEGORY_SUB) }
    var selectedSetFilter by remember { mutableStateOf("TODOS") }

    val totalEstValue = items.sumOf { it.totalEstimatedValue }
    val totalInvested = items.sumOf { it.totalPurchasePrice }
    val totalProfit = totalEstValue - totalInvested
    val profitPercentage = if (totalInvested > 0) (totalProfit / totalInvested) * 100.0 else 0.0
    val totalCount = items.sumOf { it.quantity }

    val categoryTabs = listOf(
        Triple("TODOS", "Todos", Icons.Default.Apps),
        Triple("Trading Cards", "TCG", Icons.Default.Style),
        Triple("Carrinhos / Diecast", "Diecast", Icons.Default.DirectionsCar),
        Triple("Action Figures", "Figures", Icons.Default.AccessibilityNew),
        Triple("Moedas", "Moedas", Icons.Default.MonetizationOn),
        Triple("Outros", "Outros", Icons.Default.Inventory2)
    )

    // Dynamic Subcategories & Brands based on selected category
    val availableSubCategories = remember(selectedCategory, items) {
        when (selectedCategory) {
            "Trading Cards" -> listOf(
                "TODOS" to "Todos os TCGs",
                "Pokémon TCG" to "Pokémon",
                "Magic: The Gathering" to "Magic (MTG)",
                "Yu-Gi-Oh!" to "Yu-Gi-Oh!",
                "One Piece Card Game" to "One Piece",
                "Disney Lorcana" to "Lorcana",
                "Digimon Card Game" to "Digimon",
                "Dragon Ball Super" to "Dragon Ball",
                "Star Wars Unlimited" to "Star Wars",
                "Weiss Schwarz" to "Weiss Schwarz"
            )
            "Carrinhos / Diecast" -> listOf(
                "TODOS" to "Todas as Marcas",
                "Hot Wheels" to "Hot Wheels",
                "Matchbox" to "Matchbox",
                "Mini GT" to "Mini GT",
                "Kaido House" to "Kaido House",
                "Majorette" to "Majorette",
                "Greenlight" to "Greenlight",
                "Inno64" to "Inno64",
                "Tomica" to "Tomica",
                "Auto World" to "Auto World",
                "Johnny Lightning" to "Johnny Lightning",
                "M2 Machines" to "M2 Machines"
            )
            "Action Figures" -> listOf(
                "TODOS" to "Todas",
                "Marvel Legends" to "Marvel Legends",
                "Star Wars Black Series" to "Star Wars",
                "S.H.Figuarts" to "S.H.Figuarts",
                "Funko Pop!" to "Funko Pop!",
                "NECA" to "NECA",
                "Mafex" to "Mafex",
                "Hot Toys" to "Hot Toys",
                "Bandai Gunpla" to "Gunpla / Gundam",
                "Anime / Games" to "Anime & Games"
            )
            "Moedas" -> listOf(
                "TODOS" to "Todas",
                "Moedas do Brasil (Real)" to "Brasil (Real)",
                "Moedas do Brasil (Réis / Cruzeiro)" to "Históricas BR",
                "Moedas Comemorativas" to "Comemorativas",
                "Cédulas Históricas" to "Cédulas",
                "Moedas Mundiais (Dólar / Euro / Yen)" to "Estrangeiras"
            )
            "Outros" -> listOf(
                "TODOS" to "Todos",
                "Quadrinhos & Mangás" to "Mangás & HQs",
                "Pins & Bottons" to "Pins & Bottons",
                "LEGO & Blocos" to "LEGO",
                "Video Games Retrô" to "Games Retrô",
                "Discos de Vinil & Mídia" to "Discos & Mídia"
            )
            else -> {
                val found = items.map { it.subCategory }.filter { it.isNotBlank() }.distinct()
                listOf("TODOS" to "Todos os Submenus") + found.map { it to it }
            }
        }
    }

    // Dynamic Sets for secondary filter
    val availableSetsInSub = remember(filteredItems) {
        val foundSets = filteredItems.map { it.collection.trim() }.filter { it.isNotBlank() }.distinct()
        listOf("TODOS") + foundSets
    }

    val displayFilteredItems = remember(filteredItems, selectedSetFilter) {
        if (selectedSetFilter == "TODOS") filteredItems
        else filteredItems.filter { it.collection.equals(selectedSetFilter, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Minha Coleção", fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = "${selectedCurrency.formatValue(totalEstValue)} • $totalCount itens • +${String.format("%.1f", profitPercentage)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Universal Catalog action
                    IconButton(
                        onClick = { navController.navigate(Routes.CATALOG) },
                        modifier = Modifier.testTag("btn_collection_catalog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Abrir Catálogo Universal",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Organization Menu Dialog Trigger
                    IconButton(
                        onClick = {
                            selectedGrouping = when (selectedGrouping) {
                                OrganizationGrouping.BY_CATEGORY_SUB -> OrganizationGrouping.BY_SET
                                OrganizationGrouping.BY_SET -> OrganizationGrouping.BY_STORAGE
                                OrganizationGrouping.BY_STORAGE -> OrganizationGrouping.BY_RARITY
                                OrganizationGrouping.BY_RARITY -> OrganizationGrouping.FLAT
                                OrganizationGrouping.FLAT -> OrganizationGrouping.BY_CATEGORY_SUB
                            }
                        },
                        modifier = Modifier.testTag("btn_hierarchy_mode")
                    ) {
                        Icon(
                            imageVector = selectedGrouping.icon,
                            contentDescription = "Mudar Agrupamento: ${selectedGrouping.title}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // View Mode Switcher (Grade / Lista / Compacto)
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
                                ViewMode.LIST -> Icons.Default.ViewList
                                ViewMode.COMPACT -> Icons.Default.TableRows
                            },
                            contentDescription = "Mudar Modo de Exibição"
                        )
                    }

                    // Filter Button
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros e Ordenação")
                    }

                    // Clear All action
                    IconButton(
                        onClick = { showRenewConfirmDialog = true },
                        modifier = Modifier.testTag("btn_clear_collection")
                    ) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Limpar Toda a Coleção",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("collection_screen")
        ) {
            // --- 1. MENU PRINCIPAL: CATEGORIAS EM ABAS ---
            ScrollableTabRow(
                selectedTabIndex = categoryTabs.indexOfFirst { it.first == selectedCategory }.coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {}
            ) {
                categoryTabs.forEachIndexed { index, (key, label, icon) ->
                    val isSelected = selectedCategory == key
                    Tab(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedCategoryFilter.value = key
                            viewModel.selectedSubCategoryFilter.value = "TODOS"
                            selectedSetFilter = "TODOS"
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // --- 2. SUBMENU: MARCAS / FRANQUIAS / JOGOS DINÂMICOS ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(availableSubCategories) { (subKey, label) ->
                    val isSubSelected = selectedSubCategory == subKey
                    val count = if (subKey == "TODOS") {
                        if (selectedCategory == "TODOS") items.size
                        else items.count { it.type == selectedCategory || (selectedCategory == "Trading Cards" && it.isCard) || (selectedCategory == "Carrinhos / Diecast" && it.isDiecast) }
                    } else {
                        items.count { it.subCategory.equals(subKey, ignoreCase = true) }
                    }

                    FilterChip(
                        selected = isSubSelected,
                        onClick = {
                            viewModel.selectedSubCategoryFilter.value = subKey
                            selectedSetFilter = "TODOS"
                        },
                        label = {
                            Text(
                                text = if (count > 0) "$label ($count)" else label,
                                fontSize = 12.sp,
                                fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSubSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // --- 3. BARRA DE ORGANIZAÇÃO & SUBFILTRO DE SETS ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("collection_search_field"),
                    placeholder = { Text("Filtrar por nome, série, raridade, número...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Organization Mode Selector & Summary Info Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Organization Mode Indicator pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.clickable {
                            selectedGrouping = when (selectedGrouping) {
                                OrganizationGrouping.BY_CATEGORY_SUB -> OrganizationGrouping.BY_SET
                                OrganizationGrouping.BY_SET -> OrganizationGrouping.BY_STORAGE
                                OrganizationGrouping.BY_STORAGE -> OrganizationGrouping.BY_RARITY
                                OrganizationGrouping.BY_RARITY -> OrganizationGrouping.FLAT
                                OrganizationGrouping.FLAT -> OrganizationGrouping.BY_CATEGORY_SUB
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(selectedGrouping.icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(
                                text = "Agrupado: ${selectedGrouping.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Sort order trigger
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showFilterSheet = true }
                    ) {
                        Text(
                            text = "Ord: ${sortOption.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Sub-filter for Specific Sets/Series if available
                if (availableSetsInSub.size > 2) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableSetsInSub) { setName ->
                            val isSetSelected = selectedSetFilter == setName
                            SuggestionChip(
                                onClick = { selectedSetFilter = setName },
                                label = {
                                    Text(
                                        text = if (setName == "TODOS") "Todos os Sets" else setName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSetSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSetSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }

                // Quick Navigation Chips for Pro Features
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        AssistChip(
                            onClick = { navController.navigate(Routes.SET_CHECKLIST) },
                            label = { Text("O Que Falta no Set", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { navController.navigate(Routes.STORAGE_INVENTORY) },
                            label = { Text("Pastas & Armazenamento", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { navController.navigate(Routes.DUPLICATES) },
                            label = { Text("Duplicatas (${items.count { it.quantity > 1 }})", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { navController.navigate(Routes.AI_ASSISTANT) },
                            label = { Text("Assistente IA", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }

            // --- 4. CONTEÚDO DOS ITENS ORGANIZADOS ---
            if (displayFilteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Nenhum colecionável encontrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tente alterar os filtros ou adicione novos itens pelo botão + ou Scanner.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(
                            onClick = {
                                viewModel.searchQuery.value = ""
                                viewModel.selectedCategoryFilter.value = "TODOS"
                                viewModel.selectedSubCategoryFilter.value = "TODOS"
                                selectedSetFilter = "TODOS"
                            }
                        ) {
                            Text("Limpar Filtros")
                        }
                    }
                }
            } else {
                when (selectedGrouping) {
                    OrganizationGrouping.BY_CATEGORY_SUB -> {
                        // Group by SubCategory -> Sets
                        OrganizedGroupedView(
                            items = displayFilteredItems,
                            currency = selectedCurrency,
                            viewMode = currentViewMode,
                            groupKeyProvider = { it.subCategory.ifBlank { it.type } },
                            subGroupKeyProvider = { it.collection.ifBlank { "Coleção Principal" } },
                            onItemClick = { item ->
                                viewModel.selectedItem.value = item
                                navController.navigate(Routes.ITEM_DETAIL)
                            }
                        )
                    }
                    OrganizationGrouping.BY_SET -> {
                        // Group by Set / Collection name
                        OrganizedGroupedView(
                            items = displayFilteredItems,
                            currency = selectedCurrency,
                            viewMode = currentViewMode,
                            groupKeyProvider = { it.collection.ifBlank { "Sem Coleção Definida" } },
                            subGroupKeyProvider = { it.subCategory.ifBlank { it.type } },
                            onItemClick = { item ->
                                viewModel.selectedItem.value = item
                                navController.navigate(Routes.ITEM_DETAIL)
                            }
                        )
                    }
                    OrganizationGrouping.BY_STORAGE -> {
                        // Group by Physical Storage location (Binder, Box, Shelf)
                        OrganizedGroupedView(
                            items = displayFilteredItems,
                            currency = selectedCurrency,
                            viewMode = currentViewMode,
                            groupKeyProvider = { it.storageLocation.ifBlank { "Não Atribuído / Sem Local" } },
                            subGroupKeyProvider = { it.subCategory.ifBlank { it.type } },
                            onItemClick = { item ->
                                viewModel.selectedItem.value = item
                                navController.navigate(Routes.ITEM_DETAIL)
                            }
                        )
                    }
                    OrganizationGrouping.BY_RARITY -> {
                        // Group by Rarity
                        OrganizedGroupedView(
                            items = displayFilteredItems,
                            currency = selectedCurrency,
                            viewMode = currentViewMode,
                            groupKeyProvider = { it.rarity.ifBlank { "Comum" } },
                            subGroupKeyProvider = { it.subCategory.ifBlank { it.type } },
                            onItemClick = { item ->
                                viewModel.selectedItem.value = item
                                navController.navigate(Routes.ITEM_DETAIL)
                            }
                        )
                    }
                    OrganizationGrouping.FLAT -> {
                        // Standard flat view
                        when (currentViewMode) {
                            ViewMode.GRID -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 150.dp),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(displayFilteredItems, key = { it.id }) { item ->
                                        ItemGridCard(
                                            item = item,
                                            currency = selectedCurrency,
                                            onClick = {
                                                viewModel.selectedItem.value = item
                                                navController.navigate(Routes.ITEM_DETAIL)
                                            }
                                        )
                                    }
                                }
                            }
                            ViewMode.LIST -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(displayFilteredItems, key = { it.id }) { item ->
                                        ItemListItem(
                                            item = item,
                                            currency = selectedCurrency,
                                            onClick = {
                                                viewModel.selectedItem.value = item
                                                navController.navigate(Routes.ITEM_DETAIL)
                                            }
                                        )
                                    }
                                }
                            }
                            ViewMode.COMPACT -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(displayFilteredItems, key = { it.id }) { item ->
                                        ItemCompactRow(
                                            item = item,
                                            currency = selectedCurrency,
                                            onClick = {
                                                viewModel.selectedItem.value = item
                                                navController.navigate(Routes.ITEM_DETAIL)
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

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filtros & Ordenação", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Modo de Agrupamento & Organização", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OrganizationGrouping.values().forEach { grouping ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedGrouping == grouping) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            onClick = { selectedGrouping = grouping },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(grouping.icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text(grouping.title, style = MaterialTheme.typography.bodyMedium)
                                }
                                RadioButton(
                                    selected = selectedGrouping == grouping,
                                    onClick = { selectedGrouping = grouping }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                Text("Ordenar Itens Por", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SortOption.values().forEach { opt ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sortOption == opt) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            onClick = { viewModel.sortOption.value = opt },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(opt.title, style = MaterialTheme.typography.bodyMedium)
                                RadioButton(
                                    selected = sortOption == opt,
                                    onClick = { viewModel.sortOption.value = opt }
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aplicar")
                }
            }
        }
    }

    if (showRenewConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRenewConfirmDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Limpar Toda a Coleção?") },
            text = {
                Text("Deseja apagar todos os itens salvos? Sua coleção ficará 100% limpa para você cadastrar seus próprios itens do zero.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRenewConfirmDialog = false
                        viewModel.deleteAllItems()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sim, Limpar Tudo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenewConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun OrganizedGroupedView(
    items: List<Item>,
    currency: AppCurrency,
    viewMode: ViewMode,
    groupKeyProvider: (Item) -> String,
    subGroupKeyProvider: (Item) -> String,
    onItemClick: (Item) -> Unit
) {
    val grouped = remember(items, groupKeyProvider, subGroupKeyProvider) {
        items.groupBy(groupKeyProvider)
            .mapValues { (_, groupItems) ->
                groupItems.groupBy(subGroupKeyProvider)
            }
    }

    // State for expanded/collapsed sections
    var expandedGroups by remember(grouped) {
        mutableStateOf(grouped.keys.associateWith { true })
    }

    val allExpanded = expandedGroups.values.all { it }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Controls: Expand All / Collapse All
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${grouped.size} grupos organizados (${items.sumOf { it.quantity }} itens)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                TextButton(
                    onClick = {
                        val newState = !allExpanded
                        expandedGroups = grouped.keys.associateWith { newState }
                    }
                ) {
                    Icon(
                        if (allExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (allExpanded) "Recolher Todos" else "Expandir Todos", fontSize = 12.sp)
                }
            }
        }

        grouped.forEach { (mainGroupName, subGroups) ->
            val isExpanded = expandedGroups[mainGroupName] ?: true
            val groupItems = subGroups.values.flatten()
            val groupValue = groupItems.sumOf { it.totalEstimatedValue }
            val groupCount = groupItems.sumOf { it.quantity }
            val groupProfit = groupItems.sumOf { it.profitOrLoss }

            item(key = "group_$mainGroupName") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Group Header Accordion Trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedGroups = expandedGroups.toMutableMap().apply {
                                        put(mainGroupName, !isExpanded)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = mainGroupName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "$groupCount ${if (groupCount == 1) "item" else "itens"} • ${currency.formatValue(groupValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            val arrowRotation by animateFloatAsState(
                                targetValue = if (isExpanded) 180f else 0f,
                                label = "arrow"
                            )
                            IconButton(
                                onClick = {
                                    expandedGroups = expandedGroups.toMutableMap().apply {
                                        put(mainGroupName, !isExpanded)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir/Recolher",
                                    modifier = Modifier.rotate(arrowRotation)
                                )
                            }
                        }

                        // Group Items Accordion Content
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                subGroups.forEach { (subGroupName, subCards) ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.SubdirectoryArrowRight,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.outline,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = subGroupName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "${subCards.sumOf { it.quantity }} un. (${currency.formatValue(subCards.sumOf { it.totalEstimatedValue })})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        // Subgroup Cards Rendered according to ViewMode
                                        when (viewMode) {
                                            ViewMode.GRID -> {
                                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                    subCards.chunked(2).forEach { rowCards ->
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                            rowCards.forEach { item ->
                                                                Box(modifier = Modifier.weight(1f)) {
                                                                    ItemGridCard(
                                                                        item = item,
                                                                        currency = currency,
                                                                        onClick = { onItemClick(item) }
                                                                    )
                                                                }
                                                            }
                                                            if (rowCards.size == 1) {
                                                                Spacer(modifier = Modifier.weight(1f))
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            ViewMode.LIST -> {
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    subCards.forEach { item ->
                                                        ItemListItem(
                                                            item = item,
                                                            currency = currency,
                                                            onClick = { onItemClick(item) }
                                                        )
                                                    }
                                                }
                                            }
                                            ViewMode.COMPACT -> {
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    subCards.forEach { item ->
                                                        ItemCompactRow(
                                                            item = item,
                                                            currency = currency,
                                                            onClick = { onItemClick(item) }
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
            }
        }
    }
}

@Composable
private fun ItemListItem(
    item: Item,
    currency: AppCurrency,
    onClick: () -> Unit
) {
    val isProfit = item.profitOrLoss >= 0
    val profitColor = if (isProfit) Color(0xFF16A34A) else Color(0xFFDC2626)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("item_list_row_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val (iconVector, color) = when {
                item.isCard -> Icons.Default.Style to Color(0xFF6366F1)
                item.isDiecast -> Icons.Default.DirectionsCar to Color(0xFFEF4444)
                item.type.contains("Figure", true) -> Icons.Default.SmartToy to Color(0xFF8B5CF6)
                item.type.contains("Moeda", true) -> Icons.Default.MonetizationOn to Color(0xFFF59E0B)
                else -> Icons.Default.Category to Color(0xFF10B981)
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = iconVector, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.subCategory} • ${item.collection} ${if (item.itemNumber.isNotBlank()) "#${item.itemNumber}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.rarity} | ${item.condition}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    if (item.storageLocation.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = item.storageLocation,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currency.formatValue(item.estimatedValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.quantity > 1) {
                        Text("x${item.quantity}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                    if (item.purchasePrice > 0) {
                        val sign = if (isProfit) "+" else ""
                        Text(
                            text = "$sign${String.format(java.util.Locale.US, "%.0f", item.profitPercentage)}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = profitColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCompactRow(
    item: Item,
    currency: AppCurrency,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("item_compact_row_${item.id}"),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val iconVector = when {
                item.isCard -> Icons.Default.Style
                item.isDiecast -> Icons.Default.DirectionsCar
                item.type.contains("Figure", true) -> Icons.Default.SmartToy
                item.type.contains("Moeda", true) -> Icons.Default.MonetizationOn
                else -> Icons.Default.Category
            }

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = iconVector, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.subCategory} • ${item.collection}${if (item.storageLocation.isNotBlank()) " • " + item.storageLocation else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currency.formatValue(item.estimatedValue),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (item.quantity > 1) {
                    Text(
                        text = "x${item.quantity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

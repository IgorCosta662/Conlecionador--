package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.SortOption
import com.example.ui.ViewMode
import com.example.ui.components.ItemCard

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
    var useHierarchyMode by remember { mutableStateOf(false) }
    var showRenewConfirmDialog by remember { mutableStateOf(false) }

    val totalEstValue = items.sumOf { it.totalEstimatedValue }
    val totalInvested = items.sumOf { it.totalPurchasePrice }
    val totalProfit = totalEstValue - totalInvested
    val profitPercentage = if (totalInvested > 0) (totalProfit / totalInvested) * 100.0 else 0.0
    val totalCount = items.sumOf { it.quantity }

    val categoryTabs = listOf(
        "TODOS" to "Todos",
        "Trading Cards" to "TCG",
        "Carrinhos / Diecast" to "Diecast",
        "Action Figures" to "Figures",
        "Moedas" to "Moedas",
        "Outros" to "Outros"
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
                "Digimon Card Game" to "Digimon"
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
                "Tomica" to "Tomica"
            )
            "Action Figures" -> listOf(
                "TODOS" to "Todas",
                "Marvel" to "Marvel / HQ",
                "Anime / Games" to "Anime & Games",
                "Funko" to "Funko Pop!"
            )
            "Moedas" -> listOf(
                "TODOS" to "Todas",
                "Moedas do Brasil" to "Brasil",
                "Moedas Estrangeiras" to "Estrangeiras",
                "Cédulas" to "Cédulas"
            )
            "Outros" -> listOf(
                "TODOS" to "Todos",
                "Quadrinhos & Mangás" to "Mangás & HQs",
                "Pins & Bottons" to "Pins & Bottons"
            )
            else -> {
                val found = items.map { it.subCategory }.filter { it.isNotBlank() }.distinct()
                listOf("TODOS" to "Todos") + found.map { it to it }
            }
        }
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
                    // Quick Action: Clear all items
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

                    // Toggle Hierarchy / Folder Grouping Mode
                    IconButton(
                        onClick = { useHierarchyMode = !useHierarchyMode },
                        modifier = Modifier.testTag("btn_hierarchy_mode")
                    ) {
                        Icon(
                            if (useHierarchyMode) Icons.Default.AccountTree else Icons.Default.FolderOpen,
                            contentDescription = "Hierarquia",
                            tint = if (useHierarchyMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
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
            // Category Tabs Scrollable / Row
            ScrollableTabRow(
                selectedTabIndex = categoryTabs.indexOfFirst { it.first == selectedCategory }.coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {}
            ) {
                categoryTabs.forEachIndexed { index, (key, label) ->
                    val isSelected = selectedCategory == key
                    Tab(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedCategoryFilter.value = key
                            viewModel.selectedSubCategoryFilter.value = "TODOS"
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

            // Dynamic Subcategory & Brand Chips Row
            androidx.compose.foundation.lazy.LazyRow(
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

            // Search Bar & Filter Indicators
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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

                // Results count & Sort indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredItems.size} ${if (filteredItems.size == 1) "item exibido" else "itens exibidos"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )

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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Quick Navigation Chips for Pro Features
                androidx.compose.foundation.lazy.LazyRow(
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
                            label = { Text("Inventário Físico", fontSize = 11.sp) },
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

            // Content: Either Hierarchy Groups or Normal Flat View
            if (filteredItems.isEmpty()) {
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
                            "Tente alterar os filtros ou adicione novos itens pelo scanner.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(
                            onClick = {
                                viewModel.searchQuery.value = ""
                                viewModel.selectedCategoryFilter.value = "TODOS"
                                viewModel.selectedSubCategoryFilter.value = "TODOS"
                            }
                        ) {
                            Text("Limpar Filtros")
                        }
                    }
                }
            } else if (useHierarchyMode) {
                // Hierarchical Subcategory tree view
                HierarchicalCollectionView(
                    items = filteredItems,
                    currency = selectedCurrency,
                    viewMode = currentViewMode,
                    onItemClick = { item ->
                        viewModel.selectedItem.value = item
                        navController.navigate(Routes.ITEM_DETAIL)
                    }
                )
            } else {
                // Standard Responsive View (Grid, List, Compact)
                when (currentViewMode) {
                    ViewMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                ItemCard(
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
                            items(filteredItems, key = { it.id }) { item ->
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
                            items(filteredItems, key = { it.id }) { item ->
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

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filtros Universais & Ordenação", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Ordenar Coleção Por", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
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

                HorizontalDivider()

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Concluir")
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
private fun HierarchicalCollectionView(
    items: List<Item>,
    currency: com.example.data.AppCurrency,
    viewMode: ViewMode,
    onItemClick: (Item) -> Unit
) {
    // Group items by Subcategory → Collection/Set
    val grouped = remember(items) {
        items.groupBy { it.subCategory.ifBlank { it.type } }
            .mapValues { (_, subItems) ->
                subItems.groupBy { it.collection.ifBlank { "Coleção Principal" } }
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        grouped.forEach { (subCatName, setGroups) ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = subCatName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        setGroups.forEach { (setName, setCards) ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.SubdirectoryArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "$setName (${setCards.sumOf { it.quantity }} un.)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    setCards.forEach { item ->
                                        ItemCompactRow(item = item, currency = currency, onClick = { onItemClick(item) })
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
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("item_list_row_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = iconVector, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    "${item.subCategory} • ${item.collection} ${if (item.itemNumber.isNotBlank()) "#${item.itemNumber}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    "${item.rarity} | ${item.condition} ${if (item.language.isNotBlank() && item.language != "N/A") "• [${item.language}]" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    currency.formatValue(item.estimatedValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (item.quantity > 1) {
                    Text("x${item.quantity} un.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun ItemCompactRow(
    item: Item,
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("item_compact_row_${item.id}"),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
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
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
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

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.SortOption
import com.example.ui.components.CurrencySelector
import com.example.ui.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.filteredItems.collectAsStateWithLifecycle()
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selectedSubCategory by viewModel.selectedSubCategoryFilter.collectAsStateWithLifecycle()
    val selectedRarity by viewModel.selectedRarityFilter.collectAsStateWithLifecycle()
    val selectedCondition by viewModel.selectedConditionFilter.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavoritesFilter.collectAsStateWithLifecycle()
    val currentSort by viewModel.sortOption.collectAsStateWithLifecycle()

    val isCompareMode by viewModel.comparisonSelectionMode.collectAsStateWithLifecycle()
    val selectedCompareIds by viewModel.selectedComparisonIds.collectAsStateWithLifecycle()

    var showSortMenu by remember { mutableStateOf(false) }
    var showAdvancedFiltersSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isCompareMode) "Selecionar para Comparar (${selectedCompareIds.size})" else "Minha Coleção",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (isCompareMode) {
                        TextButton(onClick = { viewModel.clearComparisonSelection() }) {
                            Text("Cancelar")
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.toggleComparisonMode(true) },
                            modifier = Modifier.testTag("start_compare_mode_button")
                        ) {
                            Icon(Icons.Default.CompareArrows, contentDescription = "Comparar")
                        }
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Outlined.FilterList, contentDescription = "Ordenar")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title) },
                                    onClick = {
                                        viewModel.sortOption.value = option
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (currentSort == option) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isCompareMode && selectedCompareIds.size >= 2) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.COMPARE) },
                    icon = { Icon(Icons.Default.Compare, contentDescription = null) },
                    text = { Text("Comparar (${selectedCompareIds.size} itens)") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("floating_compare_action")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("collection_screen")
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Buscar por nome, coleção, #número ou tags...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_input")
            )

            // Category Filter Chips Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("TODOS", "Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros")
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectedCategoryFilter.value = cat },
                        label = { Text(cat) }
                    )
                }
            }

            // Sub-filter row: Favorites Toggle & Advanced Filter Modal CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { viewModel.onlyFavoritesFilter.value = !onlyFavorites },
                        leadingIcon = {
                            Icon(
                                if (onlyFavorites) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (onlyFavorites) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("Favoritos") }
                    )

                    AssistChip(
                        onClick = { showAdvancedFiltersSheet = true },
                        leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Filtros Avançados") }
                    )
                }

                Text(
                    text = "${items.size} ${if (items.size == 1) "item" else "itens"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Items List
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum item encontrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedCategory != "TODOS") {
                                "Tente ajustar seus filtros de busca."
                            } else {
                                "Toque no scanner para adicionar seu primeiro item!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        ItemCard(
                            item = item,
                            selectedCurrency = selectedCurrency,
                            isSelectionMode = isCompareMode,
                            isSelected = selectedCompareIds.contains(item.id),
                            onSelectToggle = { viewModel.toggleComparisonItem(item.id) },
                            onClick = {
                                viewModel.selectedItem.value = item
                                navController.navigate(Routes.ITEM_DETAIL)
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(item) }
                        )
                    }
                }
            }
        }
    }

    // Modal de Filtros Avançados
    if (showAdvancedFiltersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAdvancedFiltersSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Filtros Avançados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Sub-category (TCG / Brand)
                Column {
                    Text("Jogo / Marca Específica", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val subCats = listOf("TODOS", "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game", "Hot Wheels", "Matchbox", "Tomica")
                        subCats.forEach { sc ->
                            FilterChip(
                                selected = selectedSubCategory == sc,
                                onClick = { viewModel.selectedSubCategoryFilter.value = sc },
                                label = { Text(sc, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Rarity filter
                Column {
                    Text("Raridade", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val rarities = listOf("TODOS", "Comum", "Incomum", "Raro", "Ultra Raro", "Secret Rare", "Treasure Hunt", "Super Treasure Hunt")
                        rarities.forEach { r ->
                            FilterChip(
                                selected = selectedRarity == r,
                                onClick = { viewModel.selectedRarityFilter.value = r },
                                label = { Text(r, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Condition filter
                Column {
                    Text("Condição", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val conditions = listOf("TODOS", "Novo", "Mint", "Near Mint", "Played", "Graded")
                        conditions.forEach { cond ->
                            FilterChip(
                                selected = selectedCondition == cond,
                                onClick = { viewModel.selectedConditionFilter.value = cond },
                                label = { Text(cond, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Reset and Apply Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.selectedCategoryFilter.value = "TODOS"
                            viewModel.selectedSubCategoryFilter.value = "TODOS"
                            viewModel.selectedRarityFilter.value = "TODOS"
                            viewModel.selectedConditionFilter.value = "TODOS"
                            viewModel.onlyFavoritesFilter.value = false
                            showAdvancedFiltersSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Limpar Tudo")
                    }

                    Button(
                        onClick = { showAdvancedFiltersSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Aplicar")
                    }
                }
            }
        }
    }
}

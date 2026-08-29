package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.ViewMode
import com.example.ui.components.ItemGridCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotWheelsScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()

    val hotwheelsItems = remember(items) {
        items.filter { it.subCategory.contains("Hot Wheels", ignoreCase = true) || it.name.contains("Hot Wheels", ignoreCase = true) }
    }

    var selectedYearFilter by remember { mutableStateOf("TODOS") }
    var selectedSeriesFilter by remember { mutableStateOf("TODOS") }
    var selectedSthFilter by remember { mutableStateOf(false) }
    var selectedThFilter by remember { mutableStateOf(false) }
    var selectedPackFilter by remember { mutableStateOf("TODOS") } // Lacrado vs Solto
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredList = remember(
        hotwheelsItems,
        selectedYearFilter,
        selectedSeriesFilter,
        selectedSthFilter,
        selectedThFilter,
        selectedPackFilter,
        searchQuery
    ) {
        hotwheelsItems.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.collection.contains(searchQuery, ignoreCase = true) ||
                    item.color.contains(searchQuery, ignoreCase = true) ||
                    item.itemNumber.contains(searchQuery, ignoreCase = true)

            val matchesYear = selectedYearFilter == "TODOS" || item.year == selectedYearFilter
            val matchesSeries = selectedSeriesFilter == "TODOS" || item.collection.contains(selectedSeriesFilter, ignoreCase = true)
            val matchesSth = !selectedSthFilter || item.rarity.contains("Super", ignoreCase = true) || item.variant.contains("Super", ignoreCase = true)
            val matchesTh = !selectedThFilter || item.rarity.contains("Treasure", ignoreCase = true) || item.variant.contains("Treasure", ignoreCase = true)
            val matchesPack = when (selectedPackFilter) {
                "Lacrado" -> item.condition.contains("Lacrado", ignoreCase = true) || item.condition.contains("Blister", ignoreCase = true)
                "Solto" -> item.condition.contains("Solto", ignoreCase = true) || item.condition.contains("Loose", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesYear && matchesSeries && matchesSth && matchesTh && matchesPack
        }
    }

    val totalCars = hotwheelsItems.sumOf { it.quantity }
    val totalValue = hotwheelsItems.sumOf { it.totalEstimatedValue }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFFEF4444))
                        Text("Hot Wheels", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
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
                            contentDescription = "Mudar Visualização"
                        )
                    }
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
                .testTag("hotwheels_screen")
        ) {
            // Header stats
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEE2E2)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF991B1B))
                        Text(
                            "$totalCars carrinhos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF7F1D1D)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor da coleção:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF991B1B))
                        Text(
                            currency.formatValue(totalValue),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }

            // Quick Filters Row
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar modelo (Skyline, Datsun...), ano, série...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedSthFilter,
                        onClick = { selectedSthFilter = !selectedSthFilter },
                        label = { Text("STH (Super)") }
                    )
                    FilterChip(
                        selected = selectedThFilter,
                        onClick = { selectedThFilter = !selectedThFilter },
                        label = { Text("TH (Treasure)") }
                    )
                    FilterChip(
                        selected = selectedPackFilter == "Lacrado",
                        onClick = { selectedPackFilter = if (selectedPackFilter == "Lacrado") "TODOS" else "Lacrado" },
                        label = { Text("Lacrado") }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum carrinho Hot Wheels encontrado com os filtros", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                when (currentViewMode) {
                    ViewMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList, key = { it.id }) { item ->
                                HotWheelsGridCard(
                                    item = item,
                                    currency = currency,
                                    onClick = {
                                        viewModel.selectedItem.value = item
                                        navController.navigate(Routes.ITEM_DETAIL)
                                    }
                                )
                            }
                        }
                    }
                    ViewMode.LIST, ViewMode.COMPACT -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredList, key = { it.id }) { item ->
                                HotWheelsListCard(
                                    item = item,
                                    currency = currency,
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
                Text("Filtros Avançados Hot Wheels", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Ano de Lançamento", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS", "2026", "2025", "2024", "2023").forEach { yr ->
                        FilterChip(
                            selected = selectedYearFilter == yr,
                            onClick = { selectedYearFilter = yr },
                            label = { Text(yr) }
                        )
                    }
                }

                Text("Série / Linha", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS", "Mainline", "Boulevard", "Car Culture", "RLC").forEach { s ->
                        FilterChip(
                            selected = selectedSeriesFilter == s,
                            onClick = { selectedSeriesFilter = s },
                            label = { Text(s) }
                        )
                    }
                }

                Text("Estado da Embalagem", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS", "Lacrado", "Solto").forEach { pack ->
                        FilterChip(
                            selected = selectedPackFilter == pack,
                            onClick = { selectedPackFilter = pack },
                            label = { Text(pack) }
                        )
                    }
                }

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aplicar Filtros (${filteredList.size} itens)")
                }
            }
        }
    }
}

@Composable
fun HotWheelsGridCard(
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

@Composable
fun HotWheelsListCard(
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
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    "Hot Wheels • ${item.year} • ${item.collection} • ${item.condition}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${item.rarity} | ${item.scale.ifBlank { "1:64" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFEF4444)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    currency.formatValue(item.estimatedValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Qtd: ${item.quantity}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

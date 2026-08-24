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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicTcgScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()

    val mtgCards = remember(items) {
        items.filter { it.subCategory.contains("Magic", ignoreCase = true) || it.name.contains("Magic", ignoreCase = true) }
    }

    var selectedSetFilter by remember { mutableStateOf("TODOS") }
    var selectedRarityFilter by remember { mutableStateOf("TODOS") }
    var selectedFoilFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFEF4444))
                        Text("Magic: The Gathering", fontWeight = FontWeight.Bold)
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
                    placeholder = { Text("Buscar carta, collector number, set...") },
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
                    Text("Nenhum card de Magic encontrado", color = MaterialTheme.colorScheme.outline)
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
                                PokemonCardListItem(item, currency) {
                                    viewModel.selectedItem.value = item
                                    navController.navigate(Routes.ITEM_DETAIL)
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
private fun MagicCardGridItem(
    item: Item,
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFF2E1065)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUri),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(36.dp))
                        Text("MTG", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${item.collection} • ${item.itemNumber}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currency.formatValue(item.estimatedValue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Text(item.rarity, style = MaterialTheme.typography.labelSmall, color = Color(0xFFEF4444))
                }
            }
        }
    }
}

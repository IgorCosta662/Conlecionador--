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
fun ActionFiguresScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()

    val figureItems = remember(items) {
        items.filter {
            it.type.contains("Figure", ignoreCase = true) ||
            it.type.contains("Boneco", ignoreCase = true) ||
            it.subCategory.contains("Marvel", ignoreCase = true) ||
            it.subCategory.contains("DC", ignoreCase = true) ||
            it.subCategory.contains("Star Wars", ignoreCase = true) ||
            it.subCategory.contains("Funko", ignoreCase = true) ||
            it.subCategory.contains("Anime", ignoreCase = true)
        }
    }

    var selectedSubCat by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(figureItems, selectedSubCat, searchQuery) {
        figureItems.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.subCategory.contains(searchQuery, ignoreCase = true) ||
                    item.collection.contains(searchQuery, ignoreCase = true)

            val matchesSub = selectedSubCat == "TODOS" || item.subCategory.contains(selectedSubCat, ignoreCase = true)

            matchesQuery && matchesSub
        }
    }

    val totalCount = figureItems.sumOf { it.quantity }
    val totalValue = figureItems.sumOf { it.totalEstimatedValue }

    val subCategories = listOf("TODOS", "Marvel", "DC", "Star Wars", "Anime", "Games", "Funko", "Outros")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF8B5CF6))
                        Text("Action Figures & Estátuas", fontWeight = FontWeight.Bold)
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
                .testTag("action_figures_screen")
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF3E8FF)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total em Figures:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6B21A8))
                        Text("$totalCount peças", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF581C87))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor Estimado:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6B21A8))
                        Text(currency.formatValue(totalValue), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF9333EA))
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar personagem, série, escala...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subCategories.take(4).forEach { sub ->
                        FilterChip(
                            selected = selectedSubCat == sub,
                            onClick = { selectedSubCat = sub },
                            label = { Text(sub) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma Action Figure encontrada", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        ActionFigureGridCard(item, currency) {
                            viewModel.selectedItem.value = item
                            navController.navigate(Routes.ITEM_DETAIL)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionFigureGridCard(
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
                    .height(140.dp)
                    .background(Color(0xFF3B0764)),
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
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(36.dp))
                        Text(item.subCategory, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${item.subCategory} • ${if (item.scale.isNotBlank()) item.scale else item.condition}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currency.formatValue(item.estimatedValue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    if (item.quantity > 1) {
                        Text("x${item.quantity}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

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
fun YugiohTcgScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()

    val yugiohCards = remember(items) {
        items.filter { it.subCategory.contains("Yu-Gi-Oh", ignoreCase = true) || it.name.contains("Yu-Gi-Oh", ignoreCase = true) }
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(yugiohCards, searchQuery) {
        yugiohCards.filter { item ->
            searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.itemNumber.contains(searchQuery, ignoreCase = true) ||
                    item.collection.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalCards = yugiohCards.sumOf { it.quantity }
    val totalValue = yugiohCards.sumOf { it.totalEstimatedValue }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFF3B82F6))
                        Text("Yu-Gi-Oh! TCG", fontWeight = FontWeight.Bold)
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
                .testTag("yugioh_tcg_screen")
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFDBEAFE)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total de cards Yu-Gi-Oh:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1E40AF))
                        Text("$totalCards cartas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E3A8A))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor Estimado:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1E40AF))
                        Text(currency.formatValue(totalValue), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Buscar carta por código (ex: RA01), nome...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum card de Yu-Gi-Oh! encontrado", color = MaterialTheme.colorScheme.outline)
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
                        ItemGridCard(
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

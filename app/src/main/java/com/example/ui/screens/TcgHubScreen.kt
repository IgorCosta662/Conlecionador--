package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.ItemCard

data class TcgCategoryInfo(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TcgHubScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    val tcgCards = remember(items) { items.filter { it.isCard } }
    val totalCount = tcgCards.sumOf { it.quantity }
    val totalValue = tcgCards.sumOf { it.totalEstimatedValue }
    val mostValuable = tcgCards.maxByOrNull { it.estimatedValue }

    val tcgCategories = listOf(
        TcgCategoryInfo("pokemon", "Pokémon TCG", Icons.Default.Style, Color(0xFFFBBF24), Routes.POKEMON_TCG),
        TcgCategoryInfo("magic", "Magic: The Gathering", Icons.Default.Style, Color(0xFFEF4444), Routes.MAGIC_TCG),
        TcgCategoryInfo("yugioh", "Yu-Gi-Oh!", Icons.Default.Style, Color(0xFF3B82F6), Routes.YUGIOH_TCG),
        TcgCategoryInfo("onepiece", "One Piece Card Game", Icons.Default.Style, Color(0xFF8B5CF6)),
        TcgCategoryInfo("digimon", "Digimon Card Game", Icons.Default.Style, Color(0xFF10B981)),
        TcgCategoryInfo("lorcana", "Disney Lorcana", Icons.Default.Style, Color(0xFFF97316)),
        TcgCategoryInfo("other_tcg", "Outros TCG", Icons.Default.Category, Color(0xFF6B7280))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Trading Card Games", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.CATALOG) }) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Ver Catálogo Completo")
                    }
                    IconButton(onClick = {
                        viewModel.selectedCategoryFilter.value = "Trading Cards"
                        navController.navigate(Routes.COLLECTION)
                    }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar Coleção")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("tcg_hub_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Overview Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total em Cards TCG", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    text = currency.formatValue(totalValue),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$totalCount cards",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (mostValuable != null) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectedItem.value = mostValuable
                                        navController.navigate(Routes.ITEM_DETAIL)
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Card Mais Valioso:", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${mostValuable.name} (${mostValuable.subCategory})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    currency.formatValue(mostValuable.estimatedValue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }

            // Categories Section
            item {
                Text(
                    "Selecione o Card Game",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(tcgCategories, key = { it.id }) { cat ->
                val catItems = remember(tcgCards) {
                    tcgCards.filter { it.subCategory.contains(cat.name.substringBefore(" "), ignoreCase = true) }
                }
                val count = catItems.sumOf { it.quantity }
                val value = catItems.sumOf { it.totalEstimatedValue }
                val topCard = catItems.maxByOrNull { it.estimatedValue }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (cat.route != null) {
                                navController.navigate(cat.route)
                            } else {
                                viewModel.selectedCategoryFilter.value = "Trading Cards"
                                viewModel.selectedSubCategoryFilter.value = cat.name
                                navController.navigate(Routes.COLLECTION)
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(cat.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(26.dp))
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$count cards • ${currency.formatValue(value)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (topCard != null) {
                                Text(
                                    text = "Top: ${topCard.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Abrir",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Recent Added Cards
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Últimos Cards Adicionados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        viewModel.selectedCategoryFilter.value = "Trading Cards"
                        navController.navigate(Routes.COLLECTION)
                    }) {
                        Text("Ver Todos")
                    }
                }
            }

            item {
                val recentCards = remember(tcgCards) { tcgCards.sortedByDescending { it.dateAdded }.take(5) }
                if (recentCards.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum card cadastrado ainda", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentCards, key = { it.id }) { item ->
                            Box(modifier = Modifier.width(170.dp)) {
                                ItemCard(
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
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

data class DiecastBrandInfo(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiecastHubScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    val diecastList = remember(items) { items.filter { it.isDiecast } }
    val totalCount = diecastList.sumOf { it.quantity }
    val totalValue = diecastList.sumOf { it.totalEstimatedValue }
    val mostValuable = diecastList.maxByOrNull { it.estimatedValue }

    val brands = listOf(
        DiecastBrandInfo("hw", "Hot Wheels", Icons.Default.DirectionsCar, Color(0xFFEF4444), Routes.HOTWHEELS),
        DiecastBrandInfo("mb", "Matchbox", Icons.Default.DirectionsCar, Color(0xFF3B82F6)),
        DiecastBrandInfo("minigt", "Mini GT", Icons.Default.DirectionsCar, Color(0xFFD97706)),
        DiecastBrandInfo("kaidohouse", "Kaido House", Icons.Default.DirectionsCar, Color(0xFF8B5CF6)),
        DiecastBrandInfo("inno64", "Inno64", Icons.Default.DirectionsCar, Color(0xFFE11D48)),
        DiecastBrandInfo("tomica", "Tomica", Icons.Default.DirectionsCar, Color(0xFF10B981)),
        DiecastBrandInfo("majorette", "Majorette", Icons.Default.DirectionsCar, Color(0xFF84CC16)),
        DiecastBrandInfo("greenlight", "GreenLight", Icons.Default.DirectionsCar, Color(0xFF059669)),
        DiecastBrandInfo("autoworld", "Auto World", Icons.Default.DirectionsCar, Color(0xFF7C3AED)),
        DiecastBrandInfo("johnny", "Johnny Lightning", Icons.Default.DirectionsCar, Color(0xFFF59E0B)),
        DiecastBrandInfo("other_diecast", "Outras Marcas", Icons.Default.Category, Color(0xFF64748B))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Diecast & Miniaturas", fontWeight = FontWeight.Bold)
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
                        viewModel.selectedCategoryFilter.value = "Carrinhos / Diecast"
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
                .testTag("diecast_hub_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total em Garagem / Diecast", style = MaterialTheme.typography.labelMedium, color = Color(0xFF991B1B))
                                Text(
                                    text = currency.formatValue(totalValue),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$totalCount miniaturas",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }

                        if (mostValuable != null) {
                            HorizontalDivider(color = Color(0xFFDC2626).copy(alpha = 0.2f))
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
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Miniatura Mais Valiosa:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF991B1B))
                                    Text(
                                        "${mostValuable.name} (${mostValuable.subCategory})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF450A0A),
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    currency.formatValue(mostValuable.estimatedValue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }

            // Brands Section
            item {
                Text(
                    "Marcas & Fabricantes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(brands, key = { it.id }) { brand ->
                val brandItems = remember(diecastList) {
                    diecastList.filter { it.subCategory.contains(brand.name.substringBefore(" "), ignoreCase = true) || it.name.contains(brand.name.substringBefore(" "), ignoreCase = true) }
                }
                val count = brandItems.sumOf { it.quantity }
                val value = brandItems.sumOf { it.totalEstimatedValue }
                val topItem = brandItems.maxByOrNull { it.estimatedValue }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (brand.route != null) {
                                navController.navigate(brand.route)
                            } else {
                                viewModel.selectedCategoryFilter.value = "Carrinhos / Diecast"
                                viewModel.selectedSubCategoryFilter.value = brand.name
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
                                .background(brand.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = brand.icon, contentDescription = null, tint = brand.color, modifier = Modifier.size(26.dp))
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = brand.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$count itens • ${currency.formatValue(value)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (topItem != null) {
                                Text(
                                    text = "Top: ${topItem.name}",
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

            // Recent Added Diecasts
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Últimas Miniaturas Adicionadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        viewModel.selectedCategoryFilter.value = "Carrinhos / Diecast"
                        navController.navigate(Routes.COLLECTION)
                    }) {
                        Text("Ver Todas")
                    }
                }
            }

            item {
                val recentDiecast = remember(diecastList) { diecastList.sortedByDescending { it.dateAdded }.take(5) }
                if (recentDiecast.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhuma miniatura cadastrada ainda", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentDiecast, key = { it.id }) { item ->
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

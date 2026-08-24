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
import androidx.compose.material.icons.outlined.*
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
import com.example.data.AppCurrency
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.components.*

data class CategoryOverview(
    val id: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val count: Int,
    val color: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val appMode by viewModel.appMode.collectAsStateWithLifecycle()
    val showGlobalSearch by viewModel.showGlobalSearchDialog.collectAsStateWithLifecycle()
    val showNotifications by viewModel.showNotificationsDialog.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettingsDialog.collectAsStateWithLifecycle()

    var showRoiDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val totalEstimatedValue = items.sumOf { it.totalEstimatedValue }
    val totalPurchasePrice = items.sumOf { it.totalPurchasePrice }
    val totalProfit = totalEstimatedValue - totalPurchasePrice
    val totalItems = items.sumOf { it.quantity }
    val mostValuableItem = items.maxByOrNull { it.estimatedValue }

    val tcgCount = items.filter { it.isCard }.sumOf { it.quantity }
    val diecastCount = items.filter { it.isDiecast }.sumOf { it.quantity }
    val figuresCount = items.filter { it.type.contains("Figure", true) || it.subCategory.contains("Marvel", true) || it.subCategory.contains("Funko", true) }.sumOf { it.quantity }
    val coinsCount = items.filter { it.type.contains("Moeda", true) }.sumOf { it.quantity }
    val otherCount = items.filter { !it.isCard && !it.isDiecast && !it.type.contains("Figure", true) && !it.type.contains("Moeda", true) }.sumOf { it.quantity }

    val categories = listOf(
        CategoryOverview("tcg", "TCG", Icons.Default.Style, tcgCount, Color(0xFF6366F1), Routes.TCG_HUB),
        CategoryOverview("diecast", "Diecast", Icons.Default.DirectionsCar, diecastCount, Color(0xFFEF4444), Routes.DIECAST_HUB),
        CategoryOverview("figures", "Action Figures", Icons.Default.SmartToy, figuresCount, Color(0xFF8B5CF6), Routes.ACTION_FIGURES),
        CategoryOverview("coins", "Moedas", Icons.Default.MonetizationOn, coinsCount, Color(0xFFF59E0B), Routes.COINS),
        CategoryOverview("collectibles", "Colecionáveis", Icons.Default.Extension, otherCount, Color(0xFFEC4899), Routes.OTHER_COLLECTIBLES),
        CategoryOverview("others", "Outros", Icons.Default.Category, otherCount, Color(0xFF10B981), Routes.OTHER_COLLECTIBLES)
    )

    val topGainers = remember(items) {
        items.filter { it.profitOrLoss > 0 }.sortedByDescending { it.profitPercentage }.take(4)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- 1. CABEÇALHO ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // User Avatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Minha Coleção",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (appMode == com.example.data.AppMode.COLLECTOR) "Foco: Completar Sets & Organização" else "Foco: Valorização & Patrimônio",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { navController.navigate(Routes.AI_ASSISTANT) },
                                modifier = Modifier.testTag("btn_ai_assistant")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Assistente IA", tint = MaterialTheme.colorScheme.primary)
                            }

                            IconButton(
                                onClick = { viewModel.showNotificationsDialog.value = true },
                                modifier = Modifier.testTag("btn_notifications")
                            ) {
                                BadgedBox(badge = { Badge { Text("3") } }) {
                                    Icon(Icons.Outlined.Notifications, contentDescription = "Notificações")
                                }
                            }

                            IconButton(
                                onClick = { viewModel.showSettingsDialog.value = true },
                                modifier = Modifier.testTag("btn_settings")
                            ) {
                                Icon(Icons.Outlined.Settings, contentDescription = "Configurações")
                            }
                        }
                    }

                    // Mode Switcher: Modo Colecionador vs Modo Investidor
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        SegmentedButton(
                            selected = appMode == com.example.data.AppMode.COLLECTOR,
                            onClick = { viewModel.setAppMode(com.example.data.AppMode.COLLECTOR) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            icon = { Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        ) {
                            Text("Modo Colecionador", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        SegmentedButton(
                            selected = appMode == com.example.data.AppMode.INVESTOR,
                            onClick = { viewModel.setAppMode(com.example.data.AppMode.INVESTOR) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            icon = { Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        ) {
                            Text("Modo Investidor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Global Search Bar Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showGlobalSearchDialog.value = true }
                        .testTag("global_search_bar_trigger"),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Pesquisar cartas, Hot Wheels, moedas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // --- 2. RESUMO (CARDS DE ESTATÍSTICA) ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Linha 1: Valor Total e Valorização
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Valor Total
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_total_value"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                                Text(
                                    "Valor total",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = selectedCurrency.formatValue(totalEstimatedValue),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Valorização
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_profit"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF065F46), modifier = Modifier.size(18.dp))
                                Text(
                                    "Valorização",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF065F46)
                                )
                            }
                            Text(
                                text = "${if (totalProfit >= 0) "+" else ""}${selectedCurrency.formatValue(totalProfit)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }

                // Linha 2: Itens e Item mais valioso
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Itens
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_total_items"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                Text(
                                    "Itens",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$totalItems",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Item mais valioso
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (mostValuableItem != null) {
                                    viewModel.selectedItem.value = mostValuableItem
                                    navController.navigate(Routes.ITEM_DETAIL)
                                }
                            }
                            .testTag("card_top_valuable"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(18.dp))
                                Text(
                                    "Mais valioso",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Text(
                                text = selectedCurrency.formatValue(mostValuableItem?.estimatedValue ?: 0.0),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. RECURSOS PRO DE COLEÇÃO & INVESTIMENTO ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (appMode == com.example.data.AppMode.COLLECTOR) "Ferramentas do Colecionador" else "Painel do Investidor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gestão Avançada",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Grid of 6 Pro Tools
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // O Que Falta (Checklist)
                    QuickActionButton(
                        icon = Icons.Default.Checklist,
                        title = "O Que Falta?",
                        subtitle = "Checklist por Set",
                        containerColor = Color(0xFFEFF6FF),
                        contentColor = Color(0xFF1D4ED8),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_set_checklist",
                        onClick = { navController.navigate(Routes.SET_CHECKLIST) }
                    )

                    // Inventário Físico
                    QuickActionButton(
                        icon = Icons.Default.Inventory2,
                        title = "Inventário Físico",
                        subtitle = "Pastas & Caixas",
                        containerColor = Color(0xFFF0FDF4),
                        contentColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_storage_inventory",
                        onClick = { navController.navigate(Routes.STORAGE_INVENTORY) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Duplicatas
                    QuickActionButton(
                        icon = Icons.Default.ContentCopy,
                        title = "Duplicatas",
                        subtitle = "Controle de Cópias",
                        containerColor = Color(0xFFFAF5FF),
                        contentColor = Color(0xFF7E22CE),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_duplicates",
                        onClick = { navController.navigate(Routes.DUPLICATES) }
                    )

                    // Assistente IA
                    QuickActionButton(
                        icon = Icons.Default.AutoAwesome,
                        title = "Assistente IA",
                        subtitle = "Perguntas da Coleção",
                        containerColor = Color(0xFFFFFBEB),
                        contentColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_ai_assistant_hub",
                        onClick = { navController.navigate(Routes.AI_ASSISTANT) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Alertas & Preço Suspeito
                    QuickActionButton(
                        icon = Icons.Default.NotificationsActive,
                        title = "Alertas de Preço",
                        subtitle = "Detector Suspeito",
                        containerColor = Color(0xFFFEF2F2),
                        contentColor = Color(0xFFB91C1C),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_price_alerts",
                        onClick = { navController.navigate(Routes.PRICE_ALERTS) }
                    )

                    // Segurança & Backup
                    QuickActionButton(
                        icon = Icons.Default.Security,
                        title = "Patrimônio & Backup",
                        subtitle = "PIN, CSV & PDF",
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Color(0xFF334155),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_security_backup",
                        onClick = { navController.navigate(Routes.SECURITY_BACKUP) }
                    )
                }
            }
        }

        // --- 4. AÇÕES RÁPIDAS (BOTÕES GRANDES) ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Ações Rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.CameraAlt,
                        title = "Escanear",
                        subtitle = "Identificar com IA",
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_quick_scan",
                        onClick = {
                            viewModel.resetScanState()
                            navController.navigate(Routes.SCANNER)
                        }
                    )

                    QuickActionButton(
                        icon = Icons.Default.Add,
                        title = "Adicionar",
                        subtitle = "Manual por Categoria",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_quick_add",
                        onClick = {
                            navController.navigate(Routes.ADD_ITEM)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Search,
                        title = "Pesquisar",
                        subtitle = "Busca no Acervo",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_quick_search",
                        onClick = {
                            viewModel.showGlobalSearchDialog.value = true
                        }
                    )

                    QuickActionButton(
                        icon = Icons.Default.CollectionsBookmark,
                        title = "Ver Coleção",
                        subtitle = "Todos os $totalItems itens",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_quick_collection",
                        onClick = {
                            viewModel.selectedCategoryFilter.value = "TODOS"
                            navController.navigate(Routes.COLLECTION)
                        }
                    )
                }
            }
        }

        // --- 4. CATEGORIAS ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categorias da Coleção",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Visualização Isolada",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Grid 2x3 de categorias
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    categories.chunked(2).forEach { rowCats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowCats.forEach { cat ->
                                CategoryCard(
                                    category = cat,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        navController.navigate(cat.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 5. TOP MAIORES VALORIZAÇÕES ---
        if (topGainers.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Maiores Valorizações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate(Routes.STATISTICS) }) {
                            Text("Ver Estatísticas")
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(topGainers, key = { it.id }) { item ->
                            Box(modifier = Modifier.width(170.dp)) {
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
                }
            }
        }
    }

    // Dialogs
    if (showGlobalSearch) {
        GlobalSearchDialog(
            viewModel = viewModel,
            navController = navController,
            onDismiss = { viewModel.showGlobalSearchDialog.value = false }
        )
    }

    if (showNotifications) {
        NotificationsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showNotificationsDialog.value = false }
        )
    }

    if (showSettings) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showSettingsDialog.value = false },
            onOpenExport = { showExportDialog = true }
        )
    }

    if (showExportDialog) {
        ExportReportDialog(
            reportText = viewModel.generatePortfolioReportText(selectedCurrency),
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryOverview,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("cat_card_${category.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${category.count} itens",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

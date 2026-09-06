package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*

object Routes {
    const val DASHBOARD = "dashboard"
    const val COLLECTION = "collection"
    const val SCANNER = "scanner"
    const val SCAN_RESULT = "scan_result"
    const val ITEM_DETAIL = "item_detail"
    const val COMPARE = "compare"
    const val ADD_ITEM = "add_item"
    const val MARKETPLACE = "marketplace"
    const val ACHIEVEMENTS = "achievements"
    const val WISHLIST = "wishlist"
    const val SET_CHECKLIST = "set_checklist"
    const val STORAGE_INVENTORY = "storage_inventory"
    const val DUPLICATES = "duplicates"
    const val AI_ASSISTANT = "ai_assistant"
    const val PRICE_ALERTS = "price_alerts"
    const val SECURITY_BACKUP = "security_backup"
    const val CATALOG = "catalog"
    const val DECKS = "decks"
    const val DECK_BUILDER = "deck_builder"

    // Dedicated Category & Stats Hubs
    const val TCG_HUB = "tcg_hub"
    const val POKEMON_TCG = "pokemon_tcg"
    const val MAGIC_TCG = "magic_tcg"
    const val YUGIOH_TCG = "yugioh_tcg"
    const val DIECAST_HUB = "diecast_hub"
    const val HOTWHEELS = "hotwheels"
    const val ACTION_FIGURES = "action_figures"
    const val COINS = "coins"
    const val OTHER_COLLECTIBLES = "other_collectibles"
    const val STATISTICS = "statistics"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectorApp(viewModel: CollectorViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.DASHBOARD

    val showBottomBar = currentRoute in listOf(
        Routes.DASHBOARD,
        Routes.COLLECTION,
        Routes.SCANNER,
        Routes.MARKETPLACE,
        Routes.STATISTICS
    )

    fun navigateToTab(targetRoute: String) {
        if (targetRoute != Routes.SCANNER) {
            viewModel.resetScanState()
        }
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                        label = { Text("Início") },
                        selected = currentRoute == Routes.DASHBOARD,
                        alwaysShowLabel = true,
                        onClick = { navigateToTab(Routes.DASHBOARD) },
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Coleção") },
                        label = { Text("Coleção") },
                        selected = currentRoute == Routes.COLLECTION,
                        alwaysShowLabel = true,
                        onClick = { navigateToTab(Routes.COLLECTION) },
                        modifier = Modifier.testTag("nav_collection")
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Escanear",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Scanner") },
                        selected = currentRoute == Routes.SCANNER,
                        alwaysShowLabel = true,
                        onClick = { navigateToTab(Routes.SCANNER) },
                        modifier = Modifier.testTag("nav_scanner")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Mercado") },
                        label = { Text("Mercado") },
                        selected = currentRoute == Routes.MARKETPLACE,
                        alwaysShowLabel = true,
                        onClick = { navigateToTab(Routes.MARKETPLACE) },
                        modifier = Modifier.testTag("nav_marketplace")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Estatísticas") },
                        label = { Text("Estatísticas") },
                        selected = currentRoute == Routes.STATISTICS,
                        alwaysShowLabel = true,
                        onClick = { navigateToTab(Routes.STATISTICS) },
                        modifier = Modifier.testTag("nav_statistics")
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(viewModel, navController)
            }
            composable(Routes.COLLECTION) {
                CollectionScreen(viewModel, navController)
            }
            composable(Routes.SCANNER) {
                ScannerScreen(viewModel, navController)
            }
            composable(Routes.SCAN_RESULT) {
                ScanResultScreen(viewModel, navController)
            }
            composable(Routes.ITEM_DETAIL) {
                ItemDetailScreen(viewModel, navController)
            }
            composable(Routes.COMPARE) {
                CompareScreen(viewModel, navController)
            }
            composable(Routes.ADD_ITEM) {
                AddItemScreen(viewModel) {
                    navController.popBackStack()
                }
            }
            composable(Routes.MARKETPLACE) {
                MarketplaceScreen(viewModel)
            }
            composable(Routes.ACHIEVEMENTS) {
                AchievementsScreen(viewModel)
            }
            composable(Routes.WISHLIST) {
                WishlistScreen(viewModel, navController)
            }
            composable(Routes.SET_CHECKLIST) {
                SetChecklistScreen(viewModel, navController)
            }
            composable(Routes.STORAGE_INVENTORY) {
                StorageInventoryScreen(viewModel, navController)
            }
            composable(Routes.DUPLICATES) {
                DuplicatesScreen(viewModel, navController)
            }
            composable(Routes.AI_ASSISTANT) {
                AiAssistantScreen(viewModel, navController)
            }
            composable(Routes.PRICE_ALERTS) {
                PriceAlertsScreen(viewModel, navController)
            }
            composable(Routes.SECURITY_BACKUP) {
                SecurityBackupScreen(viewModel, navController)
            }
            composable(Routes.CATALOG) {
                CatalogScreen(viewModel, navController)
            }
            composable(Routes.DECKS) {
                DecksListScreen(viewModel, navController)
            }
            composable(Routes.DECK_BUILDER) {
                DeckDetailScreen(viewModel, navController)
            }

            // Category Specific Pages
            composable(Routes.TCG_HUB) {
                TcgHubScreen(viewModel, navController)
            }
            composable(Routes.POKEMON_TCG) {
                PokemonTcgScreen(viewModel, navController)
            }
            composable(Routes.MAGIC_TCG) {
                MagicTcgScreen(viewModel, navController)
            }
            composable(Routes.YUGIOH_TCG) {
                YugiohTcgScreen(viewModel, navController)
            }
            composable(Routes.DIECAST_HUB) {
                DiecastHubScreen(viewModel, navController)
            }
            composable(Routes.HOTWHEELS) {
                HotWheelsScreen(viewModel, navController)
            }
            composable(Routes.ACTION_FIGURES) {
                ActionFiguresScreen(viewModel, navController)
            }
            composable(Routes.COINS) {
                CoinsScreen(viewModel, navController)
            }
            composable(Routes.OTHER_COLLECTIBLES) {
                OtherCollectiblesScreen(viewModel, navController)
            }
            composable(Routes.STATISTICS) {
                StatisticsScreen(viewModel, navController)
            }
        }
    }
}

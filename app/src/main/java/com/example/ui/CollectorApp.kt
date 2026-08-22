package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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
        Routes.MARKETPLACE,
        Routes.ACHIEVEMENTS
    )

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
                        onClick = {
                            if (currentRoute != Routes.DASHBOARD) {
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Coleção") },
                        label = { Text("Coleção") },
                        selected = currentRoute == Routes.COLLECTION,
                        onClick = {
                            if (currentRoute != Routes.COLLECTION) {
                                navController.navigate(Routes.COLLECTION) {
                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.testTag("nav_collection")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Escanear") },
                        label = { Text("Scanner") },
                        selected = currentRoute == Routes.SCANNER,
                        onClick = {
                            viewModel.resetScanState()
                            navController.navigate(Routes.SCANNER)
                        },
                        modifier = Modifier.testTag("nav_scanner")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Trocas") },
                        label = { Text("Trocas") },
                        selected = currentRoute == Routes.MARKETPLACE,
                        onClick = {
                            if (currentRoute != Routes.MARKETPLACE) {
                                navController.navigate(Routes.MARKETPLACE) {
                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.testTag("nav_marketplace")
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Conquistas") },
                        label = { Text("Conquistas") },
                        selected = currentRoute == Routes.ACHIEVEMENTS,
                        onClick = {
                            if (currentRoute != Routes.ACHIEVEMENTS) {
                                navController.navigate(Routes.ACHIEVEMENTS) {
                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.testTag("nav_achievements")
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
        }
    }
}

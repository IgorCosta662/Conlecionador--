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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageInventoryScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val allItems by viewModel.allItems.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    // Group items by storage location
    val storageGroups = remember(allItems) {
        allItems.groupBy { item ->
            if (item.storageLocation.isNotBlank()) item.storageLocation.trim() else "Não Atribuído"
        }.toList().sortedByDescending { it.second.size }
    }

    val defaultContainer = storageGroups.firstOrNull()?.first ?: "Todos"
    var selectedContainer by remember(storageGroups) { mutableStateOf("TODOS") }
    var itemToEditLocation by remember { mutableStateOf<Item?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val displayedItems = remember(allItems, selectedContainer) {
        if (selectedContainer == "TODOS") {
            allItems
        } else if (selectedContainer == "SEM_LOCAL") {
            allItems.filter { it.storageLocation.isBlank() }
        } else {
            allItems.filter { it.storageLocation.equals(selectedContainer, ignoreCase = true) }
        }
    }

    val totalItemsCount = allItems.sumOf { it.quantity }
    val assignedCount = allItems.filter { it.storageLocation.isNotBlank() }.sumOf { it.quantity }
    val unassignedCount = (totalItemsCount - assignedCount).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Inventário Físico", fontWeight = FontWeight.Bold)
                        Text("Pastas, Caixas, Estantes e Slots", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ORGANIZAÇÃO FÍSICA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$assignedCount de $totalItemsCount itens localizados",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { if (totalItemsCount > 0) (assignedCount.toFloat() / totalItemsCount.toFloat()).coerceIn(0f, 1f) else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Text(
                            text = "Saiba exatamente em qual pasta, caixa, página ou gaveta cada item da sua coleção está guardado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Storage Containers Filters
            item {
                Text("Compartimentos & Locais:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedContainer == "TODOS",
                            onClick = { selectedContainer = "TODOS" },
                            label = { Text("Todos (${allItems.size})") },
                            leadingIcon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    if (unassignedCount > 0) {
                        item {
                            FilterChip(
                                selected = selectedContainer == "SEM_LOCAL",
                                onClick = { selectedContainer = "SEM_LOCAL" },
                                label = { Text("Sem Local ($unassignedCount)") },
                                leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }

                    items(storageGroups.filter { it.first != "Não Atribuído" }) { (loc, itemsInLoc) ->
                        val isSelected = selectedContainer == loc
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedContainer = loc },
                            label = { Text("$loc (${itemsInLoc.size})") },
                            leadingIcon = {
                                Icon(
                                    if (loc.contains("Pasta", ignoreCase = true) || loc.contains("Binder", ignoreCase = true)) {
                                        Icons.Default.Folder
                                    } else if (loc.contains("Caixa", ignoreCase = true) || loc.contains("Box", ignoreCase = true)) {
                                        Icons.Default.Archive
                                    } else {
                                        Icons.Default.Place
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedContainer == "TODOS") "Todos os Itens no Inventário" else "Itens em: $selectedContainer",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${displayedItems.size} itens",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Items List with physical coordinates
            items(displayedItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.selectItem(item)
                            navController.navigate(Routes.ITEM_DETAIL)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (item.isCard) Icons.Default.Style else if (item.isDiecast) Icons.Default.DirectionsCar else Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.subCategory} • Qtd: ${item.quantity}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Storage badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (item.storageLocation.isNotBlank()) MaterialTheme.colorScheme.primary else Color(0xFFEAB308)
                                )
                                Text(
                                    text = if (item.storageLocation.isNotBlank()) item.storageLocation else "Sem localização definida",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.storageLocation.isNotBlank()) MaterialTheme.colorScheme.primary else Color(0xFFEAB308)
                                )
                            }
                        }

                        // Edit Location Button
                        IconButton(onClick = {
                            itemToEditLocation = item
                            showEditDialog = true
                        }) {
                            Icon(Icons.Default.EditLocation, contentDescription = "Editar Localização", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Quick Edit Location Dialog
    if (showEditDialog && itemToEditLocation != null) {
        var newLocation by remember { mutableStateOf(itemToEditLocation?.storageLocation ?: "") }
        var quickType by remember { mutableStateOf("Pasta") }
        var quickPage by remember { mutableStateOf("") }
        var quickSlot by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Definir Localização Física", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = itemToEditLocation?.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = newLocation,
                        onValueChange = { newLocation = it },
                        label = { Text("Local / Compartimento") },
                        placeholder = { Text("Ex: Pasta Mewtwo - Pág 3, Slot 2 ou Caixa HW 01") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Sugestões Rápidas:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Binder 01", "Binder 02", "Caixa Principal", "Vitrine").forEach { suggest ->
                            AssistChip(
                                onClick = { newLocation = suggest },
                                label = { Text(suggest, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    itemToEditLocation?.let { current ->
                        viewModel.updateItem(current.copy(storageLocation = newLocation.trim()))
                    }
                    showEditDialog = false
                }) {
                    Text("Salvar Local")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

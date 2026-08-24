package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.AppCurrency
import com.example.data.WishlistItem
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val wishlist by viewModel.allWishlist.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lista de Desejos & Santo Graal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${wishlist.count { !it.isFound }} itens em caça",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("wishlist_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_wishlist_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Adicionar à Wishlist", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_wishlist")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Desejo")
            }
        }
    ) { padding ->
        if (wishlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sua Lista de Desejos está vazia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cadastre as cartas raras ou carrinhos STH dos sonhos que você quer encontrar no mercado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Adicionar Item dos Sonhos")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Top banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Radar, contentDescription = null, tint = Color.White)
                            }
                            Column {
                                Text(
                                    text = "Radar de Caça Ativo",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Ao escanear ou encontrar seu item no mercado, marque como adquirido para adicioná-lo à sua coleção.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                items(wishlist, key = { it.id }) { wishItem ->
                    WishlistCard(
                        item = wishItem,
                        currency = currency,
                        onToggleFound = { viewModel.toggleWishlistFound(wishItem) },
                        onDelete = { viewModel.deleteWishlistItem(wishItem) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddWishlistDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, cat, subCat, targetPrice, priority, notes ->
                    viewModel.insertWishlistItem(name, cat, subCat, targetPrice, priority, notes)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun WishlistCard(
    item: WishlistItem,
    currency: AppCurrency,
    onToggleFound: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isFound) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isFound) NeonEmerald.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(item.priority, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when {
                                    item.priority.contains("Santo Graal", ignoreCase = true) -> NeonRose.copy(alpha = 0.2f)
                                    item.priority.contains("Alta", ignoreCase = true) -> NeonAmber.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                },
                                labelColor = when {
                                    item.priority.contains("Santo Graal", ignoreCase = true) -> NeonRose
                                    item.priority.contains("Alta", ignoreCase = true) -> NeonAmber
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                }
                            ),
                            border = null
                        )

                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isFound) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )

                    if (item.subCategory.isNotBlank()) {
                        Text(
                            text = item.subCategory,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                }
            }

            if (item.notes.isNotBlank()) {
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Preço Alvo Máximo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        if (item.targetMaxPrice > 0) currency.formatValue(item.targetMaxPrice) else "Qualquer valor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonEmerald
                    )
                }

                Button(
                    onClick = onToggleFound,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isFound) NeonEmerald else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (item.isFound) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        if (item.isFound) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (item.isFound) "Adquirido!" else "Marcar Encontrado", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddWishlistDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, cat: String, subCat: String, targetPrice: Double, priority: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Trading Cards") }
    var subCategory by remember { mutableStateOf("") }
    var targetPriceStr by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Alta") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros")
    val priorities = listOf("Máxima (Santo Graal)", "Alta", "Média", "Baixa")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Item Desejado", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Item / Card / Carrinho") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = category == "Trading Cards",
                        onClick = { category = "Trading Cards" },
                        label = { Text("Card TCG", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = category == "Carrinhos / Diecast",
                        onClick = { category = "Carrinhos / Diecast" },
                        label = { Text("Carrinho", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = category == "Outros",
                        onClick = { category = "Outros" },
                        label = { Text("Outro", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = subCategory,
                    onValueChange = { subCategory = it },
                    label = { Text("Coleção / Marca (Ex: Pokémon 151, Hot Wheels STH)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetPriceStr,
                    onValueChange = { targetPriceStr = it },
                    label = { Text("Preço Alvo Máximo (R$)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority
                Text("Prioridade:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    priorities.take(3).forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(if (p.contains("Santo Graal")) "Santo Graal" else p, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações (Ex: PSA 9, Cartela Lacrada)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val price = targetPriceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        onConfirm(name, category, subCategory, price, priority, notes)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Salvar Desejo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

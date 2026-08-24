package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.PriceAlert
import com.example.ui.CollectorViewModel
import com.example.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceAlertsScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val allItems by viewModel.allItems.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    var alerts by remember {
        mutableStateOf(
            listOf(
                PriceAlert(
                    itemName = "Charizard ex #006/165 (151)",
                    itemCategory = "Pokémon TCG",
                    targetMinPrice = 160.0,
                    targetMaxPrice = 250.0,
                    currentPrice = 185.0,
                    isEnabled = true,
                    isTriggered = false
                ),
                PriceAlert(
                    itemName = "Nissan Skyline GT-R R34 STH",
                    itemCategory = "Hot Wheels",
                    targetMinPrice = 280.0,
                    targetMaxPrice = 350.0,
                    currentPrice = 320.0,
                    isEnabled = true,
                    isTriggered = true,
                    triggerMessage = "Atingiu teto de valorização (R$ 320,00)"
                ),
                PriceAlert(
                    itemName = "Sheoldred, the Apocalypse",
                    itemCategory = "Magic: The Gathering",
                    targetMinPrice = 350.0,
                    targetMaxPrice = 450.0,
                    currentPrice = 420.0,
                    isEnabled = true,
                    isTriggered = false
                )
            )
        )
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedItemForAlert by remember { mutableStateOf(allItems.firstOrNull()) }
    var minPriceInput by remember { mutableStateOf("") }
    var maxPriceInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Alertas & Oportunidades", fontWeight = FontWeight.Bold)
                        Text("Monitoramento de Mercado & Preços Suspeitos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.AddAlert, contentDescription = "Criar Alerta")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo Alerta de Preço") }
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
            // Suspicious Price & Fake Detection Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "DETECTOR DE PREÇO SUSPEITO",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Proteção contra golpes e anúncios falsificados",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }

                        Text(
                            text = "Quando uma oferta estiver >50% abaixo da média de mercado (ex: Carta de R$ 300 anunciada por R$ 45), o aplicativo alertará os possíveis motivos:\n• Carta ou carrinho falsificado\n• Estado muito desgastado (Damaged/Heavy Played)\n• Variante comum sendo vendida como Foil/Rare\n• Erro no cadastro do anúncio",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            // Active Alerts Section
            item {
                Text(
                    text = "Seus Alertas de Preço Ativos (${alerts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(alerts) { alert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (alert.isTriggered) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (alert.isTriggered) Color(0xFF16A34A) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.itemCategory,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = alert.itemName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Switch(
                                checked = alert.isEnabled,
                                onCheckedChange = { isChecked ->
                                    alerts = alerts.map { if (it.id == alert.id) it.copy(isEnabled = isChecked) else it }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Avisar se Baixar de", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(currency.formatValue(alert.targetMinPrice), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                            Column {
                                Text("Preço Atual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(currency.formatValue(alert.currentPrice), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Avisar se Subir de", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(currency.formatValue(alert.targetMaxPrice), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                        }

                        if (alert.isTriggered && alert.triggerMessage.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                Text(alert.triggerMessage, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            }

            // Stores Comparison Matrix
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Lojas & Marketplaces Monitorados:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cotações integradas em tempo real com identificação de menor preço:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("LigaPokémon", "Mercado Especializado TCG Nacional", "Ativo"),
                            Triple("LigaMagic", "Maior marketplace de MTG do Brasil", "Ativo"),
                            Triple("Mercado Livre", "Cards, Carrinhos e Colecionáveis", "Ativo"),
                            Triple("TCGPlayer", "Referência global em cotação de cards", "Ativo (US)"),
                            Triple("Cardmarket", "Referência europeia de TCGs", "Ativo (EU)"),
                            Triple("Mercari Japan", "Cards originais japoneses e raridades", "Ativo (JP)")
                        ).forEach { (store, desc, status) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(store, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                                AssistChip(onClick = {}, label = { Text(status, fontSize = 10.sp) })
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }

    // Create Alert Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Criar Alerta de Preço", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Defina as metas de compra ou venda para ser notificado:", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = minPriceInput,
                        onValueChange = { minPriceInput = it },
                        label = { Text("Avisar quando baixar de (R$)") },
                        placeholder = { Text("Ex: 100.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = maxPriceInput,
                        onValueChange = { maxPriceInput = it },
                        label = { Text("Avisar quando subir de (R$)") },
                        placeholder = { Text("Ex: 300.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val min = minPriceInput.toDoubleOrNull() ?: 50.0
                    val max = maxPriceInput.toDoubleOrNull() ?: 200.0
                    val newAlert = PriceAlert(
                        itemName = selectedItemForAlert?.name ?: "Novo Colecionável",
                        itemCategory = selectedItemForAlert?.subCategory ?: "Cards",
                        targetMinPrice = min,
                        targetMaxPrice = max,
                        currentPrice = selectedItemForAlert?.estimatedValue ?: 100.0,
                        isEnabled = true
                    )
                    alerts = alerts + newAlert
                    showCreateDialog = false
                    minPriceInput = ""
                    maxPriceInput = ""
                }) {
                    Text("Criar Alerta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

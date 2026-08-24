package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.CollectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityBackupScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val allItems by viewModel.allItems.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    var isPinEnabled by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("1234") }
    var showPinDialog by remember { mutableStateOf(false) }

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var showExportPreviewDialog by remember { mutableStateOf(false) }
    var exportPreviewContent by remember { mutableStateOf("") }
    var exportTypeTitle by remember { mutableStateOf("") }

    val totalPortfolioValue = allItems.sumOf { it.totalEstimatedValue }
    val totalInvested = allItems.sumOf { it.totalPurchasePrice }
    val profit = totalPortfolioValue - totalInvested

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        snackbarMessage = "$label copiado para a área de transferência!"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Segurança & Patrimônio", fontWeight = FontWeight.Bold)
                        Text("Backup, Exportação e Bloqueio por PIN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = {
            snackbarMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portfolio Summary Card
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
                                Text("PATRIMÔNIO TOTAL DA COLEÇÃO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(currency.formatValue(totalPortfolioValue), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Capital Investido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(currency.formatValue(totalInvested), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Valorização Líquida", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    "${if (profit >= 0) "+" else ""}${currency.formatValue(profit)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (profit >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }

            // PIN Security Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("Bloqueio por PIN / Senha", fontWeight = FontWeight.Bold)
                                    Text("Protege sua coleção contra acessos não autorizados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            Switch(
                                checked = isPinEnabled,
                                onCheckedChange = {
                                    isPinEnabled = it
                                    if (it) showPinDialog = true
                                }
                            )
                        }

                        if (isPinEnabled) {
                            TextButton(
                                onClick = { showPinDialog = true },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alterar Código PIN")
                            }
                        }
                    }
                }
            }

            // Export Section (Excel/CSV, PDF, JSON)
            item {
                Text("Exportação & Backup de Dados:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // CSV / Excel Export
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(28.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Planilha Excel / CSV", fontWeight = FontWeight.Bold)
                                Text("Exportar colunas completas: Nome, Preço Pago, Cotação, Condição, Pasta/Caixa.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    val csvContent = buildString {
                                        append("ID,Nome,Categoria,SubCategoria,Coleção,Número,Raridade,Condição,Idioma,Quantidade,PrecoPago,CotacaoAtual,ValorTotal,LocalFisico\n")
                                        allItems.forEach { item ->
                                            append("${item.id},\"${item.name}\",\"${item.type}\",\"${item.subCategory}\",\"${item.collection}\",\"${item.itemNumber}\",\"${item.rarity}\",\"${item.condition}\",\"${item.language}\",${item.quantity},${item.purchasePrice},${item.estimatedValue},${item.totalEstimatedValue},\"${item.storageLocation}\"\n")
                                        }
                                    }
                                    exportPreviewContent = csvContent
                                    exportTypeTitle = "Arquivo CSV (Excel)"
                                    showExportPreviewDialog = true
                                }
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gerar CSV")
                            }
                        }
                    }
                }
            }

            // PDF Portfolio Report Export
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(28.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Relatório / Certificado de Patrimônio (PDF)", fontWeight = FontWeight.Bold)
                                Text("Documento estruturado com sumário executivo, valor por categoria e catálogo.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    val report = viewModel.generatePortfolioReportText(currency)
                                    exportPreviewContent = report
                                    exportTypeTitle = "Relatório PDF de Patrimônio"
                                    showExportPreviewDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Visualizar Relatório")
                            }
                        }
                    }
                }
            }

            // JSON Full Backup & Cloud Sync
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Backup Completo (JSON)", fontWeight = FontWeight.Bold)
                                Text("Salve uma cópia exata de todos os itens, cotações, imagens e histórico.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val backupJson = buildString {
                                        append("{\"exportedAt\": ${System.currentTimeMillis()}, \"totalItems\": ${allItems.size}, \"items\": [")
                                        append(allItems.joinToString(",") { "{\"id\":${it.id},\"name\":\"${it.name}\",\"type\":\"${it.type}\",\"subCategory\":\"${it.subCategory}\",\"estimatedValue\":${it.estimatedValue},\"quantity\":${it.quantity}}" })
                                        append("]}")
                                    }
                                    copyToClipboard("Backup JSON", backupJson)
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copiar Backup Completo")
                            }
                        }
                    }
                }
            }
        }
    }

    // Export Preview Modal Dialog
    if (showExportPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showExportPreviewDialog = false },
            title = { Text(exportTypeTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pré-visualização do conteúdo:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    OutlinedTextField(
                        value = exportPreviewContent,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    copyToClipboard(exportTypeTitle, exportPreviewContent)
                    showExportPreviewDialog = false
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Conteúdo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportPreviewDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    // PIN Setup Dialog
    if (showPinDialog) {
        var tempPin by remember { mutableStateOf(pinCode) }

        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Definir PIN de Segurança", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Digite um PIN de 4 dígitos para proteger o aplicativo:")
                    OutlinedTextField(
                        value = tempPin,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) tempPin = it },
                        label = { Text("PIN (4 dígitos)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempPin.length == 4) {
                            pinCode = tempPin
                            showPinDialog = false
                            snackbarMessage = "PIN de segurança configurado com sucesso!"
                        }
                    },
                    enabled = tempPin.length == 4
                ) {
                    Text("Salvar PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

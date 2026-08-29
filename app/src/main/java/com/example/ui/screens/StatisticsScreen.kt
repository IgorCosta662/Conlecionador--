package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.components.CategoryDistributionChart
import com.example.ui.components.PriceEvolutionChart
import com.example.util.CollectionExporter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var showExportModal by remember { mutableStateOf(false) }
    var selectedExportFormat by remember { mutableStateOf("PDF") } // "PDF" or "EXCEL"
    var isGeneratingExport by remember { mutableStateOf(false) }

    val totalItems = items.sumOf { it.quantity }
    val totalEstValue = items.sumOf { it.totalEstimatedValue }
    val totalInvested = items.sumOf { it.totalPurchasePrice }
    val profit = totalEstValue - totalInvested
    val profitPercentage = if (totalInvested > 0) (profit / totalInvested) * 100.0 else 0.0

    val topItems = remember(items) {
        items.sortedByDescending { it.estimatedValue }.take(5)
    }

    val tcgValue = items.filter { it.isCard }.sumOf { it.totalEstimatedValue }
    val diecastValue = items.filter { it.isDiecast }.sumOf { it.totalEstimatedValue }
    val figuresValue = items.filter { it.type.contains("Figure", true) || it.subCategory.contains("Marvel", true) }.sumOf { it.totalEstimatedValue }
    val coinsValue = items.filter { it.type.contains("Moeda", true) }.sumOf { it.totalEstimatedValue }
    val othersValue = items.filter { !it.isCard && !it.isDiecast && !it.type.contains("Figure", true) && !it.type.contains("Moeda", true) }.sumOf { it.totalEstimatedValue }

    val langGroups = remember(items) {
        items.groupBy { it.language.ifBlank { "N/A" } }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }
            .toList()
            .sortedByDescending { it.second }
    }

    fun handleExport(format: String) {
        if (items.isEmpty()) {
            Toast.makeText(context, "A coleção está vazia para exportação.", Toast.LENGTH_SHORT).show()
            return
        }
        isGeneratingExport = true
        coroutineScope.launch {
            try {
                if (format == "PDF") {
                    val pdfFile = CollectionExporter.exportToPdf(context, items, currency)
                    CollectionExporter.shareFile(
                        context = context,
                        file = pdfFile,
                        mimeType = "application/pdf",
                        title = "Relatório de Patrimônio da Coleção (PDF)"
                    )
                    snackbarHostState.showSnackbar("Relatório PDF gerado com sucesso!")
                } else {
                    val csvFile = CollectionExporter.exportToExcelCsv(context, items, currency)
                    CollectionExporter.shareFile(
                        context = context,
                        file = csvFile,
                        mimeType = "text/csv",
                        title = "Planilha da Coleção Completa (Excel)"
                    )
                    snackbarHostState.showSnackbar("Planilha Excel/CSV gerada com sucesso!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erro ao gerar arquivo: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                isGeneratingExport = false
                showExportModal = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Estatísticas da Coleção", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = {
                            selectedExportFormat = "PDF"
                            showExportModal = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exportar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("statistics_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Portfolio Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Patrimônio Total em Colecionáveis", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = currency.formatValue(totalEstValue),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Investido", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    currency.formatValue(totalInvested),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Valorização Acumulada", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${if (profit >= 0) "+" else ""}${currency.formatValue(profit)} (${String.format("%.1f", profitPercentage)}%)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (profit >= 0) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Export & Personal Control Section
            item {
                Text(
                    "Exportação & Controle Pessoal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Baixe resumos completos e relatórios da sua coleção para inventário pessoal, seguro ou análise financeira:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // PDF Export Button
                            OutlinedCard(
                                onClick = {
                                    selectedExportFormat = "PDF"
                                    showExportModal = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        "Relatório PDF",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "Documento formal com sumário executivo e catálogo para impressão.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        lineHeight = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            "Baixar PDF",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFEF4444)
                                        )
                                        Icon(
                                            Icons.Default.FileDownload,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Excel / CSV Export Button
                            OutlinedCard(
                                onClick = {
                                    selectedExportFormat = "EXCEL"
                                    showExportModal = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.TableChart,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        "Planilha Excel",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "Arquivo CSV com todas as colunas de preços, cotações e localização.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        lineHeight = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            "Baixar Excel",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF10B981)
                                        )
                                        Icon(
                                            Icons.Default.FileDownload,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Charts Section
            item {
                Text(
                    "Distribuição por Categoria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CategoryDistributionChart(items = items)

                        HorizontalDivider()

                        // Category Value Breakdown
                        CategoryValueRow("Trading Card Games (TCG)", tcgValue, totalEstValue, currency, Color(0xFF6366F1))
                        CategoryValueRow("Diecast / Carrinhos", diecastValue, totalEstValue, currency, Color(0xFFEF4444))
                        CategoryValueRow("Action Figures", figuresValue, totalEstValue, currency, Color(0xFF8B5CF6))
                        CategoryValueRow("Moedas & Numismática", coinsValue, totalEstValue, currency, Color(0xFFF59E0B))
                        CategoryValueRow("Outros Colecionáveis", othersValue, totalEstValue, currency, Color(0xFF10B981))
                    }
                }
            }

            // Price Evolution
            item {
                Text(
                    "Evolução de Valor da Coleção",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Histórico de Valorização (Últimos 6 meses)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        val samplePortfolioHistory = remember(totalEstValue, totalInvested) {
                            val base = if (totalInvested > 0) totalInvested else 1000.0
                            val current = if (totalEstValue > 0) totalEstValue else base * 1.3
                            val step = (current - base) / 5.0
                            listOf(
                                com.example.data.PriceHistoryPoint("Nov", (base).coerceAtLeast(10.0)),
                                com.example.data.PriceHistoryPoint("Dez", (base + step * 1).coerceAtLeast(10.0)),
                                com.example.data.PriceHistoryPoint("Jan", (base + step * 2).coerceAtLeast(10.0)),
                                com.example.data.PriceHistoryPoint("Fev", (base + step * 3).coerceAtLeast(10.0)),
                                com.example.data.PriceHistoryPoint("Mar", (base + step * 4).coerceAtLeast(10.0)),
                                com.example.data.PriceHistoryPoint("Hoje", current.coerceAtLeast(10.0))
                            )
                        }
                        PriceEvolutionChart(
                            history = samplePortfolioHistory,
                            selectedCurrency = currency
                        )
                    }
                }
            }

            // Top 5 Most Valuable Items
            item {
                Text(
                    "Top 5 Itens Mais Valiosos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        topItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (index == 0) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (index == 0) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${item.subCategory} • ${item.collection}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        maxLines = 1
                                    )
                                }

                                Text(
                                    text = currency.formatValue(item.estimatedValue),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                            if (index < topItems.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }

            // Language Breakdown
            item {
                Text(
                    "Distribuição por Idioma",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        langGroups.forEach { (lang, count) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(lang, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("$count itens", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }

    // Export Options Modal Dialog / BottomSheet
    if (showExportModal) {
        AlertDialog(
            onDismissRequest = { if (!isGeneratingExport) showExportModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (selectedExportFormat == "PDF") Icons.Default.PictureAsPdf else Icons.Default.TableChart,
                        contentDescription = null,
                        tint = if (selectedExportFormat == "PDF") Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                    Text(
                        if (selectedExportFormat == "PDF") "Exportar Relatório PDF" else "Exportar Planilha Excel",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Format Switcher Tabs
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = selectedExportFormat == "PDF",
                            onClick = { selectedExportFormat = "PDF" },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("PDF (.pdf)")
                        }
                        SegmentedButton(
                            selected = selectedExportFormat == "EXCEL",
                            onClick = { selectedExportFormat = "EXCEL" },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("Excel (.csv)")
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Itens a exportar:", style = MaterialTheme.typography.bodySmall)
                                Text("${items.size} registros ($totalItems unidades)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Patrimônio total:", style = MaterialTheme.typography.bodySmall)
                                Text(currency.formatValue(totalEstValue), fontWeight = FontWeight.Bold, color = Color(0xFF10B981), style = MaterialTheme.typography.bodySmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Formato de saída:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    if (selectedExportFormat == "PDF") "Documento A4 Diagramado" else "Planilha CSV (UTF-8)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Text(
                        if (selectedExportFormat == "PDF")
                            "O arquivo PDF contém o cabeçalho executivo, gráfico de rentabilidade e o inventário completo pronto para impressão ou envio por e-mail."
                        else
                            "A planilha gerada possui codificação UTF-8 compatível com Microsoft Excel, Google Planilhas e LibreOffice com todas as colunas de compra, cotação e locais de armazenamento.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    if (isGeneratingExport) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Processando arquivo...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { handleExport(selectedExportFormat) },
                    enabled = !isGeneratingExport && items.isNotEmpty()
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Baixar / Compartilhar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val previewText = if (selectedExportFormat == "PDF") {
                            viewModel.generatePortfolioReportText(currency)
                        } else {
                            buildString {
                                append("ID;Nome;Categoria;SubCategoria;Coleção;Quantidade;PrecoPago;Cotacao;ValorTotal;Local\n")
                                items.forEach {
                                    append("${it.id};\"${it.name}\";\"${it.type}\";\"${it.subCategory}\";\"${it.collection}\";${it.quantity};${it.purchasePrice};${it.estimatedValue};${it.totalEstimatedValue};\"${it.storageLocation}\"\n")
                                }
                            }
                        }
                        CollectionExporter.copyToClipboard(context, "Resumo da Coleção", previewText)
                        showExportModal = false
                    },
                    enabled = !isGeneratingExport
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copiar Texto")
                }
            }
        )
    }
}

@Composable
private fun CategoryValueRow(
    title: String,
    value: Double,
    total: Double,
    currency: com.example.data.AppCurrency,
    accentColor: Color
) {
    val percentage = if (total > 0) (value / total) * 100.0 else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(
                "${currency.formatValue(value)} (${String.format("%.1f", percentage)}%)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
        LinearProgressIndicator(
            progress = { (percentage / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = accentColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

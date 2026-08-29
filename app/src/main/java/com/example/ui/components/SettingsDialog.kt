package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppCurrency
import com.example.data.AppMode
import com.example.data.MarketRegion
import com.example.ui.CollectorViewModel
import com.example.ui.ViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    viewModel: CollectorViewModel,
    onDismiss: () -> Unit,
    onOpenExport: () -> Unit
) {
    val context = LocalContext.current
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val selectedMarket by viewModel.selectedMarketRegion.collectAsStateWithLifecycle()
    val appMode by viewModel.appMode.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val autoTranslate by viewModel.autoTranslateCardEffects.collectAsStateWithLifecycle()
    val preferOfficialImages by viewModel.preferOfficialWebImages.collectAsStateWithLifecycle()
    val showCameraGrid by viewModel.showCameraGrid.collectAsStateWithLifecycle()
    val priceAlertsEnabled by viewModel.priceAlertsNotificationsEnabled.collectAsStateWithLifecycle()
    val highPrecisionAi by viewModel.highPrecisionAiAppraisal.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showSampleConfirm by remember { mutableStateOf(false) }
    var showBackupJsonDialog by remember { mutableStateOf(false) }
    var backupJsonText by remember { mutableStateOf("") }

    val tabs = listOf(
        SettingsTabItem("Geral & Visual", Icons.Default.Tune),
        SettingsTabItem("Scanner & IA", Icons.Default.AutoAwesome),
        SettingsTabItem("Dados & Backup", Icons.Default.Storage),
        SettingsTabItem("Sobre & Status", Icons.Default.Info)
    )

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .testTag("settings_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header com Título e Botão Fechar
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Configurações",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Preferências & Gerenciamento",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_settings_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    }
                }

                // Abas de Navegação das Configurações
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)) }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                // Conteúdo da Aba Selecionada
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    when (selectedTab) {
                        0 -> GeneralVisualSettingsTab(
                            viewModel = viewModel,
                            selectedCurrency = selectedCurrency,
                            appMode = appMode,
                            viewMode = viewMode,
                            selectedMarket = selectedMarket
                        )
                        1 -> ScannerAiSettingsTab(
                            autoTranslate = autoTranslate,
                            preferOfficialImages = preferOfficialImages,
                            showCameraGrid = showCameraGrid,
                            priceAlertsEnabled = priceAlertsEnabled,
                            highPrecisionAi = highPrecisionAi,
                            onToggleAutoTranslate = { viewModel.setAutoTranslate(it) },
                            onTogglePreferOfficialImages = { viewModel.setPreferOfficialWebImages(it) },
                            onToggleShowCameraGrid = { viewModel.setShowCameraGrid(it) },
                            onTogglePriceAlerts = { viewModel.setPriceAlertsNotificationsEnabled(it) },
                            onToggleHighPrecisionAi = { viewModel.setHighPrecisionAiAppraisal(it) }
                        )
                        2 -> DataBackupSettingsTab(
                            itemsCount = allItems.size,
                            onOpenExportReport = {
                                onDismiss()
                                onOpenExport()
                            },
                            onGenerateJsonBackup = {
                                backupJsonText = viewModel.exportJsonBackup()
                                showBackupJsonDialog = true
                            },
                            onLoadSampleData = { showSampleConfirm = true },
                            onResetCollection = { showResetConfirm = true }
                        )
                        3 -> AboutDiagnosticSettingsTab(
                            totalItems = allItems.sumOf { it.quantity },
                            totalPortfolioValue = allItems.sumOf { it.totalEstimatedValue },
                            selectedCurrency = selectedCurrency
                        )
                    }
                }

                // Rodapé com Ação Rápida de Fechar
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Collector Pro v2.6",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            Text("Pronto")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de Confirmação: Carregar Amostras
    if (showSampleConfirm) {
        AlertDialog(
            onDismissRequest = { showSampleConfirm = false },
            icon = { Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Carregar Dados de Exemplo?") },
            text = {
                Text("Isso adicionará itens de demonstração altamente detalhados (Pokémon Charizard 151, Magic The One Ring, Hot Wheels STH, Yu-Gi-Oh e Moedas) à sua coleção para teste das ferramentas de análise e cotação.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSampleConfirm = false
                        viewModel.loadSampleData()
                        Toast.makeText(context, "Coleção de demonstração carregada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Carregar Exemplos")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSampleConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Confirmação: Zerar Coleção
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Limpar Toda a Coleção?") },
            text = {
                Text("Esta ação apagará permanentemente todos os itens salvos no banco de dados local. A coleção ficará 100% zerada.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        viewModel.deleteAllItems()
                        Toast.makeText(context, "Coleção zerada com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmar e Zerar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Backup JSON
    if (showBackupJsonDialog) {
        AlertDialog(
            onDismissRequest = { showBackupJsonDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Backup, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Backup JSON da Coleção", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Arquivo JSON estruturado com todos os itens, cotações e detalhes catalogados:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = backupJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        copyToClipboard("Backup JSON", backupJsonText)
                        showBackupJsonDialog = false
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupJsonDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

private data class SettingsTabItem(
    val title: String,
    val icon: ImageVector
)

// ----------------------------------------------------
// TAB 0: Geral & Visual
// ----------------------------------------------------
@Composable
private fun GeneralVisualSettingsTab(
    viewModel: CollectorViewModel,
    selectedCurrency: AppCurrency,
    appMode: AppMode,
    viewMode: ViewMode,
    selectedMarket: MarketRegion
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Moeda Principal
        SettingsSectionCard(
            title = "Moeda de Exibição",
            subtitle = "Define o câmbio e a moeda em todo o aplicativo",
            icon = Icons.Default.Payments
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppCurrency.values().forEach { curr ->
                    val isSelected = selectedCurrency == curr
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCurrency(curr) },
                        label = {
                            Text(
                                text = curr.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Modo do Aplicativo (Colecionador vs Investidor)
        SettingsSectionCard(
            title = "Modo do Aplicativo",
            subtitle = "Alterna o foco de navegação da interface",
            icon = Icons.Default.Explore
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppMode.values().forEach { mode ->
                    val isSelected = appMode == mode
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        onClick = { viewModel.setAppMode(mode) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    if (mode == AppMode.COLLECTOR) Icons.Default.Collections else Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (mode == AppMode.COLLECTOR) "Colecionador" else "Investidor",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = if (mode == AppMode.COLLECTOR) "Checklists & Pastas" else "Lucro & Cotações",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // 3. Estilo de Visualização dos Itens
        SettingsSectionCard(
            title = "Estilo de Visualização da Lista",
            subtitle = "Como os itens são renderizados na coleção",
            icon = Icons.Default.ViewAgenda
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewMode.values().forEach { mode ->
                    val isSelected = viewMode == mode
                    val icon = when (mode) {
                        ViewMode.GRID -> Icons.Default.GridView
                        ViewMode.LIST -> Icons.Default.FormatListBulleted
                        ViewMode.COMPACT -> Icons.Default.ViewAgenda
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setViewMode(mode) },
                        label = { Text(mode.title) },
                        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Mercado Regional Preferencial de Cotação
        SettingsSectionCard(
            title = "Mercado Preferencial de Cotação",
            subtitle = "Fonte e região prioritária para busca de preços",
            icon = Icons.Default.Public
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MarketRegion.values().forEach { region ->
                    val isSelected = selectedMarket == region
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                        onClick = { viewModel.setMarketRegion(region) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = region.code,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = region.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = region.primarySource,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setMarketRegion(region) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 1: Scanner & IA
// ----------------------------------------------------
@Composable
private fun ScannerAiSettingsTab(
    autoTranslate: Boolean,
    preferOfficialImages: Boolean,
    showCameraGrid: Boolean,
    priceAlertsEnabled: Boolean,
    highPrecisionAi: Boolean,
    onToggleAutoTranslate: (Boolean) -> Unit,
    onTogglePreferOfficialImages: (Boolean) -> Unit,
    onToggleShowCameraGrid: (Boolean) -> Unit,
    onTogglePriceAlerts: (Boolean) -> Unit,
    onToggleHighPrecisionAi: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tradução Automática
        SettingsSwitchCard(
            title = "Tradução Automática de Cartas",
            description = "Traduz regras, ataques e habilidades em inglês para Português (PT-BR) oficial.",
            icon = Icons.Default.Translate,
            checked = autoTranslate,
            onCheckedChange = onToggleAutoTranslate
        )

        // Resolução de Imagens Oficiais
        SettingsSwitchCard(
            title = "Buscar Imagem Oficial da Web",
            description = "Conecta à Scryfall e TCGdex para usar ilustrações em alta definição das cartas.",
            icon = Icons.Default.Image,
            checked = preferOfficialImages,
            onCheckedChange = onTogglePreferOfficialImages
        )

        // Grade da Câmera
        SettingsSwitchCard(
            title = "Grade Guia de Enquadramento",
            description = "Exibe linhas de proporção na câmera para capturar cartas e miniaturas sem distorção.",
            icon = Icons.Default.CenterFocusStrong,
            checked = showCameraGrid,
            onCheckedChange = onToggleShowCameraGrid
        )

        // Avaliação Pericial por IA
        SettingsSwitchCard(
            title = "Análise Pericial & Autenticidade",
            description = "Executa verificação visual de textura, microimpressão e desgaste com Gemini AI.",
            icon = Icons.Default.VerifiedUser,
            checked = highPrecisionAi,
            onCheckedChange = onToggleHighPrecisionAi
        )

        // Notificações de Preços
        SettingsSwitchCard(
            title = "Alertas Inteligentes de Mercado",
            description = "Gera notificações automáticas para subidas e quedas expressivas de cotação.",
            icon = Icons.Default.NotificationsActive,
            checked = priceAlertsEnabled,
            onCheckedChange = onTogglePriceAlerts
        )
    }
}

// ----------------------------------------------------
// TAB 2: Dados & Backup
// ----------------------------------------------------
@Composable
private fun DataBackupSettingsTab(
    itemsCount: Int,
    onOpenExportReport: () -> Unit,
    onGenerateJsonBackup: () -> Unit,
    onLoadSampleData: () -> Unit,
    onResetCollection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Relatório de Patrimônio
        SettingsActionCard(
            title = "Gerar Relatório de Patrimônio",
            description = "Exporta resumo financeiro com lucros, histórico e lista completa para compartilhamento.",
            icon = Icons.Default.Share,
            buttonText = "Abrir Relatório",
            onClick = onOpenExportReport
        )

        // Backup Completo JSON
        SettingsActionCard(
            title = "Backup Completo (JSON)",
            description = "Gera cópia digital completa de todos os itens cadastrados, preços e observações.",
            icon = Icons.Default.Backup,
            buttonText = "Exportar JSON",
            onClick = onGenerateJsonBackup
        )

        // Carregar Exemplos da Coleção
        SettingsActionCard(
            title = "Carregar Itens de Demonstração",
            description = "Adiciona 5 itens raros com cotações e regras completas para testar as ferramentas do app.",
            icon = Icons.Default.CloudDownload,
            buttonText = "Carregar Exemplos",
            onClick = onLoadSampleData
        )

        // Limpar / Zerar Coleção
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Zerar Toda a Coleção",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Atualmente você possui $itemsCount item(ns) cadastrado(s).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = onResetCollection,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Limpar Todos os Itens")
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: Sobre & Diagnóstico
// ----------------------------------------------------
@Composable
private fun AboutDiagnosticSettingsTab(
    totalItems: Int,
    totalPortfolioValue: Double,
    selectedCurrency: AppCurrency
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Resumo do Portfólio Local
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ESTATÍSTICAS DA INSTALAÇÃO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Itens Catalogados", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("$totalItems unidades", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Patrimônio Estimado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(selectedCurrency.formatValue(totalPortfolioValue), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Status dos Serviços e APIs
        SettingsSectionCard(
            title = "Conectividade & Motores de IA",
            subtitle = "Serviços integrados em operação",
            icon = Icons.Default.CloudDone
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ApiStatusItem("Google Gemini 2.5 Flash", "Visão Computacional & OCR de Alta Precisão", true)
                ApiStatusItem("Scryfall REST API", "Catálogo & Imagens Magic: The Gathering", true)
                ApiStatusItem("TCGdex & PokéAPI", "Catálogo & Imagens Pokémon TCG", true)
                ApiStatusItem("Banco de Dados Room", "Persistência Local Offline Segura (SQLite)", true)
            }
        }

        // Informações da Aplicação
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Collector Pro", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(
                    "Hub completo para gestão, escaneamento por IA e avaliação patrimonial de colecionáveis (Cards Pokémon, Magic, Yu-Gi-Oh, Diecast Hot Wheels, Moedas e Figuras).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Versão: 2.6.0 • Build de Produção", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

// ----------------------------------------------------
// Reusable UI Components
// ----------------------------------------------------
@Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
            content()
        }
    }
}

@Composable
private fun SettingsSwitchCard(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingsActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun ApiStatusItem(
    name: String,
    description: String,
    isOperational: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isOperational) Color(0xFF16A34A).copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isOperational) Color(0xFF16A34A) else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isOperational) "Ativo" else "Offline",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOperational) Color(0xFF16A34A) else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

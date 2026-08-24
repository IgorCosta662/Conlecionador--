package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.api.ItemIdentificationResult
import com.example.data.*
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.ScanUiState
import com.example.ui.components.ConfidenceMeter
import com.example.ui.components.CurrencySelector
import com.example.ui.components.PriceEvolutionChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val scanUiState by viewModel.scanUiState.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val buffer by viewModel.identificationCorrectionBuffer.collectAsStateWithLifecycle()
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()

    var showCorrectionDialog by remember { mutableStateOf(false) }
    var showAddToCollectionDialog by remember { mutableStateOf(false) }

    // Multi-photo support
    var backImageUri by remember { mutableStateOf<String?>(null) }
    val detailImages = remember { mutableStateListOf<String>() }

    val backCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val uri = viewModel.saveBitmapToStorage(bitmap, "back_${System.currentTimeMillis()}.jpg")
            backImageUri = uri
        }
    }

    val detailCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val uri = viewModel.saveBitmapToStorage(bitmap, "detail_${System.currentTimeMillis()}.jpg")
            if (uri != null) detailImages.add(uri)
        }
    }

    if (scanUiState !is ScanUiState.Success || buffer == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val successState = scanUiState as ScanUiState.Success
    val itemResult = buffer ?: successState.result

    // Duplicate detection check
    val duplicateItem = remember(itemResult, allItems) {
        viewModel.findPossibleDuplicate(itemResult)
    }

    // Decode base64 image if captured
    val imageBitmap = remember(successState.capturedImageBase64) {
        successState.capturedImageBase64?.let { base64Str ->
            try {
                val bytes = Base64.decode(base64Str, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado da Identificação", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetScanState()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    CurrencySelector(
                        selectedCurrency = selectedCurrency,
                        onCurrencySelected = { viewModel.setCurrency(it) }
                    )
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showCorrectionDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("correct_identification_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Corrigir Dados", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { showAddToCollectionDialog = true },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                            .testTag("add_to_collection_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Adicionar à Coleção", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("scan_result_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // DUPLICATE WARNING BANNER (If already in collection)
            if (duplicateItem != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Column {
                                Text(
                                    text = "Item Já Existente na Coleção!",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = "Você já possui ${duplicateItem.quantity} un. deste item em \"${duplicateItem.storageLocation.ifBlank { "Coleção Principal" }}\".",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // 1. FOTO DO ITEM & IDENTIFICAÇÃO BÁSICA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Image banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Foto do Item Identificado",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (itemResult.category.contains("Card", ignoreCase = true)) Icons.Default.Style else Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Item Identificado por IA",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Multi-photo adders (Verso + Detalhes)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { backCameraLauncher.launch(null) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.FlipToBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (backImageUri != null) "Verso Salvo" else "+ Foto Verso", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { detailCameraLauncher.launch(null) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (detailImages.isNotEmpty()) "${detailImages.size} Detalhes" else "+ Detalhes", fontSize = 11.sp)
                            }
                        }

                        // Identification Details
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = itemResult.subCategory.ifBlank { itemResult.category },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (itemResult.itemNumber.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = itemResult.itemNumber,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = itemResult.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )

                            if (itemResult.collection.isNotBlank()) {
                                Text(
                                    text = itemResult.collection,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Badges (Rarity, Variant, Language Flag)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(itemResult.rarity, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                                )
                                if (itemResult.variant.isNotBlank() && itemResult.variant != "Normal") {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(itemResult.variant, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFFFE082).copy(alpha = 0.4f),
                                            labelColor = Color(0xFFE65100)
                                        )
                                    )
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(itemResult.languageDisplayName, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // 2. PREÇO ESTIMADO DE MERCADO (Médio, Mínimo, Máximo)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "PREÇO ESTIMADO DE MERCADO (${itemResult.marketRegion.displayName})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Preço Médio
                            PriceColumn(
                                label = "Preço Médio",
                                value = selectedCurrency.format(itemResult.averagePrice),
                                isHighlight = true,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Mais Barato
                            PriceColumn(
                                label = "Mais Barato",
                                value = selectedCurrency.format(itemResult.minPrice),
                                isHighlight = false,
                                color = Color(0xFF2E7D32)
                            )

                            // Mais Caro
                            PriceColumn(
                                label = "Mais Caro",
                                value = selectedCurrency.format(itemResult.maxPrice),
                                isHighlight = false,
                                color = Color(0xFFC62828)
                            )
                        }

                        if (itemResult.marketTrendComment.isNotBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            Text(
                                text = itemResult.marketTrendComment,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. AVALIAÇÃO DE CONDIÇÃO POR IA & ESTIMATIVA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ESTIMATIVA DE CONDIÇÃO (IA)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = itemResult.apparentCondition,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Detailed criteria
                        val assess = itemResult.conditionAssessment
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ConditionCriterionRow("Cantos (Corners)", assess.corners)
                            ConditionCriterionRow("Bordas (Edges)", assess.edges)
                            ConditionCriterionRow("Superfície (Surface)", assess.scratches)
                            ConditionCriterionRow("Centralização (Centering)", assess.centering)
                            ConditionCriterionRow("Estrutura (Curvatura)", assess.bends)
                        }

                        // Condition Tiers price guide
                        if (itemResult.conditionPrices.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Valores por Grau de Conservação:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(itemResult.conditionPrices) { tier ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(tier.condition, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                selectedCurrency.format(tier.priceBrl),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Mandatory Legal & Valuation Disclaimer
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Esta análise é apenas uma estimativa visual por IA e não substitui uma avaliação profissional por empresas de graduação (PSA, BGS, CGC).",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. COMPARAÇÃO DE PREÇO POR IDIOMA (PT-BR vs EN vs JP)
            if (itemResult.languageComparisons.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "COTAÇÃO COMPARATIVA POR IDIOMA",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Os preços nunca são misturados; cada tiragem possui liquidez própria.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            itemResult.languageComparisons.forEach { langComp ->
                                val isCurrent = langComp.languageCode.equals(itemResult.language, ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                        Column {
                                            Text(
                                                text = "${langComp.languageName}${if (isCurrent) " (Identificado)" else ""}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = "${langComp.languageCode} • Mín: ${selectedCurrency.format(langComp.minPriceBrl)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Text(
                                        text = selectedCurrency.format(langComp.averagePriceBrl),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. CONFIANÇA DA IDENTIFICAÇÃO
            item {
                ConfidenceMeter(score = itemResult.confidenceScore)
            }

            // 6. OFERTAS DISPONÍVEIS POR FONTE
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "OFERTAS ENCONTRADAS POR FONTE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Valores verificados para a condição e variante específica.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (itemResult.offers.isEmpty()) {
                            Text(
                                text = "Não foi possível encontrar dados suficientes para calcular um preço confiável.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            itemResult.offers.forEachIndexed { idx, offer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = offer.storeName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${offer.condition} • ${offer.listingType}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = selectedCurrency.format(offer.priceInBRL),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (idx < itemResult.offers.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }

            // 7. HISTÓRICO DE PREÇOS GRÁFICO
            item {
                PriceEvolutionChart(
                    history = itemResult.priceHistory,
                    selectedCurrency = selectedCurrency
                )
            }
        }
    }

    // Modal para "Corrigir Identificação"
    if (showCorrectionDialog) {
        CorrectionModal(
            initialResult = itemResult,
            onDismiss = { showCorrectionDialog = false },
            onConfirm = { updated ->
                viewModel.updateCorrectionBuffer(updated)
                showCorrectionDialog = false
            }
        )
    }

    // Modal para "Adicionar à Coleção"
    if (showAddToCollectionDialog) {
        AddToCollectionModal(
            itemResult = itemResult,
            selectedCurrency = selectedCurrency,
            onDismiss = { showAddToCollectionDialog = false },
            onConfirm = { qty, paidPrice, location, notes ->
                viewModel.saveIdentifiedItemToCollection(
                    identification = itemResult,
                    quantity = qty,
                    purchasePrice = paidPrice,
                    storageLocation = location,
                    notes = notes,
                    imageUri = successState.savedImageUri,
                    backImageUri = backImageUri,
                    detailImages = detailImages.toList()
                )
                showAddToCollectionDialog = false
                navController.navigate(Routes.COLLECTION) {
                    popUpTo(Routes.DASHBOARD)
                }
            }
        )
    }
}

@Composable
private fun ConditionCriterionRow(label: String, score: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(
            text = score,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PriceColumn(
    label: String,
    value: String,
    isHighlight: Boolean,
    color: Color
) {
    Column(horizontalAlignment = if (isHighlight) Alignment.Start else Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorrectionModal(
    initialResult: ItemIdentificationResult,
    onDismiss: () -> Unit,
    onConfirm: (ItemIdentificationResult) -> Unit
) {
    var name by remember { mutableStateOf(initialResult.name) }
    var subCategory by remember { mutableStateOf(initialResult.subCategory) }
    var collection by remember { mutableStateOf(initialResult.collection) }
    var itemNumber by remember { mutableStateOf(initialResult.itemNumber) }
    var rarity by remember { mutableStateOf(initialResult.rarity) }
    var variant by remember { mutableStateOf(initialResult.variant) }
    var language by remember { mutableStateOf(initialResult.language) }
    var condition by remember { mutableStateOf(initialResult.apparentCondition) }
    var averagePrice by remember { mutableStateOf(initialResult.averagePrice.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Corrigir Dados do Item", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Item / Carta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = subCategory,
                        onValueChange = { subCategory = it },
                        label = { Text("Jogo / Marca (Ex: Pokémon, Hot Wheels)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = collection,
                        onValueChange = { collection = it },
                        label = { Text("Coleção / Expansão / Série") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = itemNumber,
                        onValueChange = { itemNumber = it },
                        label = { Text("Número (#151/165, #142/250)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("Idioma (PT-BR, EN, JP)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = rarity,
                        onValueChange = { rarity = it },
                        label = { Text("Raridade") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = variant,
                        onValueChange = { variant = it },
                        label = { Text("Variante (Foil, Super TH, Normal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it },
                        label = { Text("Condição (Near Mint, Lacrado, Usado)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = averagePrice,
                        onValueChange = { averagePrice = it },
                        label = { Text("Preço Médio Estimado (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val parsedPrice = averagePrice.replace(",", ".").toDoubleOrNull() ?: initialResult.averagePrice
                val updated = initialResult.copy(
                    name = name,
                    subCategory = subCategory,
                    collection = collection,
                    itemNumber = itemNumber,
                    language = language,
                    rarity = rarity,
                    variant = variant,
                    apparentCondition = condition,
                    averagePrice = parsedPrice,
                    minPrice = parsedPrice * 0.85,
                    maxPrice = parsedPrice * 1.25
                )
                onConfirm(updated)
            }) {
                Text("Salvar Correções")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddToCollectionModal(
    itemResult: ItemIdentificationResult,
    selectedCurrency: AppCurrency,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, purchasePrice: Double, location: String, notes: String) -> Unit
) {
    var quantityText by remember { mutableStateOf("1") }
    var purchasePriceText by remember { mutableStateOf(String.format(java.util.Locale.US, "%.2f", itemResult.averagePrice * 0.7)) }
    var storageLocation by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar à Coleção", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "${itemResult.languageFlag} ${itemResult.name} (${itemResult.subCategory})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Valor estimado: ${selectedCurrency.format(itemResult.averagePrice)} (${itemResult.languageDisplayName})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantidade") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Preço Pago (em R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = storageLocation,
                        onValueChange = { storageLocation = it },
                        label = { Text("Local Onde Está Guardado") },
                        placeholder = { Text("Ex: Pasta Charizard - Pág 3, Gaveta 2") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações") },
                        placeholder = { Text("Ex: Sleeves duplos, Top Loader") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = quantityText.toIntOrNull() ?: 1
                val price = purchasePriceText.replace(",", ".").toDoubleOrNull() ?: 0.0
                onConfirm(qty, price, storageLocation, notes)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

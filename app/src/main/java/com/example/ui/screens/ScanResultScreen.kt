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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.api.ItemIdentificationResult
import com.example.data.*
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.ScanUiState
import com.example.ui.components.ConfidenceMeter
import com.example.ui.components.CurrencySelector
import com.example.ui.components.OfficialCardPrintSelectorModal
import com.example.ui.components.PriceEvolutionChart
import com.example.util.CardEffectTranslator
import com.example.util.OfficialCardImageHelper
import kotlinx.coroutines.launch

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

    // Official Web Card Image toggle - Defaults to true for crystal clear scans from official sites
    var preferOfficialImage by remember { mutableStateOf(true) }
    var showPrintSelectorModal by remember { mutableStateOf(false) }
    var showCatalogSelectorModal by remember { mutableStateOf(false) }
    var isSearchingWebImage by remember { mutableStateOf(false) }
    var isTranslatingEffect by remember { mutableStateOf(false) }
    var showEditEffectDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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

    var customOfficialImageUrl by remember(itemResult.name, itemResult.officialImageUrl) {
        mutableStateOf(itemResult.officialImageUrl.ifBlank { null })
    }

    // Auto-fetch high-resolution official image directly from official APIs (Scryfall/Gatherer/TCGDex) on scan completion
    LaunchedEffect(itemResult.name, itemResult.subCategory, itemResult.collection, itemResult.itemNumber) {
        if (customOfficialImageUrl.isNullOrBlank()) {
            isSearchingWebImage = true
            try {
                val liveUrl = OfficialCardImageHelper.searchOfficialImageOnline(
                    itemResult.name,
                    itemResult.subCategory,
                    itemResult.collection,
                    itemResult.itemNumber
                )
                if (!liveUrl.isNullOrBlank()) {
                    customOfficialImageUrl = liveUrl
                }
            } catch (_: Exception) {}
            isSearchingWebImage = false
        }
    }

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
                        val officialUrl = remember(itemResult.name, itemResult.subCategory, itemResult.collection, itemResult.itemNumber) {
                            OfficialCardImageHelper.getOfficialImageUrl(
                                name = itemResult.name,
                                subCategory = itemResult.subCategory,
                                collection = itemResult.collection,
                                itemNumber = itemResult.itemNumber
                            )
                        }

                        val effectiveOfficialUrl = customOfficialImageUrl ?: officialUrl

                        // Image banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
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
                            if (preferOfficialImage && effectiveOfficialUrl.isNotBlank()) {
                                AsyncImage(
                                    model = effectiveOfficialUrl,
                                    contentDescription = "Foto Oficial da Web",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                )
                            } else if (imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Foto do Item Capturada",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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

                            // Badge indicator on top-right of image
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    if (isSearchingWebImage) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            strokeWidth = 1.5.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Buscando Scryfall...",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (preferOfficialImage) Icons.Default.AutoAwesome else Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (preferOfficialImage) "Oficial HD (Scryfall)" else "Foto Capturada",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        // Informative banner when official image is active
                        if (preferOfficialImage && effectiveOfficialUrl.isNotBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Scan oficial de alta definição (TCG / Print oficial).",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 11.sp
                                        )
                                    }

                                    FilledTonalButton(
                                        onClick = { showPrintSelectorModal = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Trocar Print", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Toggle Buttons (Sua Foto vs Foto Oficial HD + Verso + Detalhes)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (preferOfficialImage) {
                                Button(
                                    onClick = { preferOfficialImage = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Minha Foto",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                FilledTonalButton(
                                    onClick = { showPrintSelectorModal = true },
                                    modifier = Modifier.weight(1.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Collections,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Mudar Edição",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        preferOfficialImage = true
                                        if (customOfficialImageUrl == null) {
                                            coroutineScope.launch {
                                                isSearchingWebImage = true
                                                val liveUrl = OfficialCardImageHelper.searchOfficialImageOnline(
                                                    itemResult.name,
                                                    itemResult.subCategory,
                                                    itemResult.collection,
                                                    itemResult.itemNumber
                                                )
                                                if (!liveUrl.isNullOrBlank()) {
                                                    customOfficialImageUrl = liveUrl
                                                }
                                                isSearchingWebImage = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Foto Oficial HD",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        preferOfficialImage = true
                                        showPrintSelectorModal = true
                                    },
                                    modifier = Modifier.weight(0.9f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Prints", fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = { backCameraLauncher.launch(null) },
                                modifier = Modifier.weight(0.85f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.FlipToBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (backImageUri != null) "Verso OK" else "+ Verso", fontSize = 10.5.sp)
                            }

                            OutlinedButton(
                                onClick = { detailCameraLauncher.launch(null) },
                                modifier = Modifier.weight(0.85f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (detailImages.isNotEmpty()) "${detailImages.size} Det." else "+ Detalhes", fontSize = 10.5.sp)
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

                            // Quick Correction / Direct Catalog Switcher
                            OutlinedButton(
                                onClick = { showCatalogSelectorModal = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Não é esta carta? Trocar no Catálogo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

            // 4.5 O QUE A CARTA FAZ (TRADUÇÃO PT-BR)
            if (itemResult.category == "Trading Cards" || itemResult.cardTranslatedEffect.isNotBlank() || itemResult.cardOracleText.isNotBlank() || itemResult.cardAttacks.isNotBlank() || itemResult.cardHp.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "O QUE A CARTA FAZ (PT-BR)",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    FilledTonalButton(
                                        onClick = { showEditEffectDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Editar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    TextButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                isTranslatingEffect = true
                                                try {
                                                    val result = CardEffectTranslator.searchCardEffectsOnline(
                                                        name = itemResult.name,
                                                        subCategory = itemResult.subCategory,
                                                        collection = itemResult.collection,
                                                        itemNumber = itemResult.itemNumber
                                                    )
                                                    val updated = itemResult.copy(
                                                        cardTranslatedEffect = result.translatedEffect.ifBlank {
                                                            if (itemResult.cardOracleText.isNotBlank()) {
                                                                CardEffectTranslator.translateToPortuguese(itemResult.cardOracleText, itemResult.subCategory)
                                                            } else itemResult.cardTranslatedEffect
                                                        },
                                                        cardOracleText = if (result.originalText.isNotBlank()) result.originalText else itemResult.cardOracleText,
                                                        cardAttacks = if (result.cardAttacks.isNotBlank()) result.cardAttacks else itemResult.cardAttacks,
                                                        cardHp = if (result.cardHp.isNotBlank()) result.cardHp else itemResult.cardHp,
                                                        cardArtist = if (result.cardArtist.isNotBlank()) result.cardArtist else itemResult.cardArtist
                                                    )
                                                    viewModel.updateCorrectionBuffer(updated)
                                                } catch (_: Exception) {
                                                } finally {
                                                    isTranslatingEffect = false
                                                }
                                            }
                                        },
                                        enabled = !isTranslatingEffect,
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        if (isTranslatingEffect) {
                                            CircularProgressIndicator(modifier = Modifier.size(13.dp), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Traduzindo...", fontSize = 11.sp)
                                        } else {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Atualizar IA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Badges for HP, Attacks, Artist
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (itemResult.cardHp.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = itemResult.cardHp,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (itemResult.cardArtist.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "Ilustrador: ${itemResult.cardArtist}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (itemResult.cardAttacks.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = itemResult.cardAttacks,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            val effectText = if (itemResult.cardTranslatedEffect.isNotBlank()) {
                                itemResult.cardTranslatedEffect
                            } else if (itemResult.cardOracleText.isNotBlank()) {
                                CardEffectTranslator.translateToPortuguese(itemResult.cardOracleText, itemResult.subCategory)
                            } else {
                                "Efeitos da carta serão traduzidos automaticamente ao consultar Scryfall / TCGDex. Clique em 'Traduzir / Atualizar' para buscar agora."
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = effectText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            if (itemResult.cardOracleText.isNotBlank() && itemResult.cardOracleText != itemResult.cardTranslatedEffect) {
                                Text(
                                    text = "Texto Original (Inglês):\n${itemResult.cardOracleText}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
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

    // Modal para "Editar Efeitos / Tradução Manualmente"
    if (showEditEffectDialog) {
        CardEffectEditModal(
            initialTranslatedEffect = itemResult.cardTranslatedEffect,
            initialOracleText = itemResult.cardOracleText,
            initialAttacks = itemResult.cardAttacks,
            initialHp = itemResult.cardHp,
            initialArtist = itemResult.cardArtist,
            subCategory = itemResult.subCategory,
            onDismiss = { showEditEffectDialog = false },
            onConfirm = { translated, oracle, attacks, hp, artist ->
                val updated = itemResult.copy(
                    cardTranslatedEffect = translated,
                    cardOracleText = oracle,
                    cardAttacks = attacks,
                    cardHp = hp,
                    cardArtist = artist
                )
                viewModel.updateCorrectionBuffer(updated)
                showEditEffectDialog = false
            }
        )
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
        val officialUrl = remember(itemResult.name, itemResult.subCategory, itemResult.collection, itemResult.itemNumber) {
            OfficialCardImageHelper.getOfficialImageUrl(
                name = itemResult.name,
                subCategory = itemResult.subCategory,
                collection = itemResult.collection,
                itemNumber = itemResult.itemNumber
            )
        }

        AddToCollectionModal(
            itemResult = itemResult,
            selectedCurrency = selectedCurrency,
            onDismiss = { showAddToCollectionDialog = false },
            onConfirm = { qty, paidPrice, location, notes ->
                val chosenOfficial = customOfficialImageUrl ?: officialUrl
                val finalImageUri = if (preferOfficialImage && chosenOfficial.isNotBlank()) chosenOfficial else successState.savedImageUri

                val extraDetailImages = detailImages.toMutableList()
                if (preferOfficialImage && !successState.savedImageUri.isNullOrBlank() && !extraDetailImages.contains(successState.savedImageUri)) {
                    extraDetailImages.add(0, successState.savedImageUri)
                }

                viewModel.saveIdentifiedItemToCollection(
                    identification = itemResult,
                    quantity = qty,
                    purchasePrice = paidPrice,
                    storageLocation = location,
                    notes = notes,
                    imageUri = finalImageUri,
                    backImageUri = backImageUri,
                    detailImages = extraDetailImages.toList()
                )
                showAddToCollectionDialog = false
                navController.navigate(Routes.COLLECTION) {
                    popUpTo(Routes.DASHBOARD)
                }
            }
        )
    }

    // Modal para Selecionar Print / Edição Oficial da Carta
    if (showPrintSelectorModal) {
        val currentEffective = customOfficialImageUrl ?: OfficialCardImageHelper.getOfficialImageUrl(
            name = itemResult.name,
            subCategory = itemResult.subCategory,
            collection = itemResult.collection,
            itemNumber = itemResult.itemNumber
        )
        OfficialCardPrintSelectorModal(
            initialName = itemResult.name,
            subCategory = itemResult.subCategory,
            collection = itemResult.collection,
            itemNumber = itemResult.itemNumber,
            currentImageUrl = currentEffective,
            onDismiss = { showPrintSelectorModal = false },
            onSelectPrint = { selectedUrl ->
                customOfficialImageUrl = selectedUrl
                preferOfficialImage = true
                showPrintSelectorModal = false
            }
        )
    }

    // Modal para Selecionar Carta / Item Correto do Catálogo Completo
    if (showCatalogSelectorModal) {
        CatalogItemSelectorModal(
            currentCategory = itemResult.category,
            currentSubCategory = itemResult.subCategory,
            onDismiss = { showCatalogSelectorModal = false },
            onSelectEntry = { selectedEntry ->
                viewModel.applyCatalogMatchToResult(selectedEntry)
                preferOfficialImage = true
                customOfficialImageUrl = OfficialCardImageHelper.getOfficialImageUrl(
                    selectedEntry.name,
                    selectedEntry.subCategory,
                    selectedEntry.collection,
                    selectedEntry.itemNumber
                )
                showCatalogSelectorModal = false
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
    var cardTranslatedEffect by remember { mutableStateOf(initialResult.cardTranslatedEffect) }
    var cardOracleText by remember { mutableStateOf(initialResult.cardOracleText) }

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
                item {
                    OutlinedTextField(
                        value = cardTranslatedEffect,
                        onValueChange = { cardTranslatedEffect = it },
                        label = { Text("O que a carta faz (Tradução PT-BR)") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = cardOracleText,
                        onValueChange = { cardOracleText = it },
                        label = { Text("Texto Original (Inglês)") },
                        minLines = 2,
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
                    maxPrice = parsedPrice * 1.25,
                    cardTranslatedEffect = cardTranslatedEffect,
                    cardOracleText = cardOracleText
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
                    val parsedQty = quantityText.toIntOrNull() ?: 1
                    val parsedPrice = purchasePriceText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val totalEstimated = itemResult.averagePrice * parsedQty
                    val totalPaid = parsedPrice * parsedQty

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Número de Cópias (Quantidade)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (parsedQty == 4) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "Playset Completo",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = {
                                        val current = quantityText.toIntOrNull() ?: 1
                                        if (current > 1) quantityText = (current - 1).toString()
                                    },
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Diminuir cópia")
                                }

                                OutlinedTextField(
                                    value = quantityText,
                                    onValueChange = { quantityText = it },
                                    label = { Text("Cópias") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )

                                FilledTonalIconButton(
                                    onClick = {
                                        val current = quantityText.toIntOrNull() ?: 1
                                        quantityText = (current + 1).toString()
                                    },
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumentar cópia")
                                }
                            }

                            // Quick preset chips for copies (1x, 2x, 3x, 4x Playset, 5x, 10x)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val presets = listOf(
                                    1 to "1x (Un.)",
                                    2 to "2x",
                                    3 to "3x",
                                    4 to "4x (Playset)",
                                    5 to "5x",
                                    10 to "10x"
                                )
                                items(presets) { (count, label) ->
                                    FilterChip(
                                        selected = (quantityText.toIntOrNull() == count),
                                        onClick = { quantityText = count.toString() },
                                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                                    )
                                }
                            }

                            if (parsedQty > 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Valor Total (${parsedQty}x):",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = selectedCurrency.format(totalEstimated),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEffectEditModal(
    initialTranslatedEffect: String,
    initialOracleText: String,
    initialAttacks: String,
    initialHp: String,
    initialArtist: String,
    subCategory: String,
    onDismiss: () -> Unit,
    onConfirm: (translated: String, oracle: String, attacks: String, hp: String, artist: String) -> Unit
) {
    var translatedEffect by remember { mutableStateOf(initialTranslatedEffect) }
    var oracleText by remember { mutableStateOf(initialOracleText) }
    var attacks by remember { mutableStateOf(initialAttacks) }
    var hp by remember { mutableStateOf(initialHp) }
    var artist by remember { mutableStateOf(initialArtist) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Editar Efeitos & Tradução", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Personalize ou adicione manualmente o texto em português, regras e atributos da carta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = translatedEffect,
                        onValueChange = { translatedEffect = it },
                        label = { Text("O que a carta faz (Tradução PT-BR)") },
                        placeholder = { Text("Ex: Quando este card entrar no campo de batalha, compre 2 cards...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        supportingText = { Text("Texto que aparecerá como efeito principal em português") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = oracleText,
                        onValueChange = { oracleText = it },
                        label = { Text("Texto Original em Inglês (Oracle / Regras)") },
                        placeholder = { Text("Ex: When this card enters the battlefield, draw 2 cards...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilledTonalButton(
                            onClick = {
                                if (oracleText.isNotBlank()) {
                                    translatedEffect = CardEffectTranslator.translateToPortuguese(oracleText, subCategory)
                                }
                            },
                            enabled = oracleText.isNotBlank(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Traduzir EN➔PT Automaticamente", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = attacks,
                        onValueChange = { attacks = it },
                        label = { Text("Ataques / Habilidades Especiais") },
                        placeholder = { Text("Ex: Choque do Trovão: 50 | Investida: 20") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hp,
                            onValueChange = { hp = it },
                            label = { Text("HP / Pontos") },
                            placeholder = { Text("Ex: HP 120 ou 4/4") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = artist,
                            onValueChange = { artist = it },
                            label = { Text("Ilustrador") },
                            placeholder = { Text("Ex: Ken Sugimori") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(translatedEffect.trim(), oracleText.trim(), attacks.trim(), hp.trim(), artist.trim())
                }
            ) {
                Text("Salvar Tradução")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogItemSelectorModal(
    currentCategory: String,
    currentSubCategory: String,
    onDismiss: () -> Unit,
    onSelectEntry: (RealCatalogEntry) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember {
        mutableStateOf(
            if (currentSubCategory.contains("Pokémon", ignoreCase = true) || currentCategory.contains("Pokémon", ignoreCase = true)) {
                "Pokémon TCG"
            } else if (currentSubCategory.contains("Magic", ignoreCase = true)) {
                "Magic MTG"
            } else if (currentSubCategory.contains("Hot Wheels", ignoreCase = true) || currentCategory.contains("Diecast", ignoreCase = true)) {
                "Hot Wheels"
            } else {
                "Todos"
            }
        )
    }

    val filteredEntries = remember(searchQuery, selectedFilter) {
        val query = searchQuery.trim().lowercase()
        RealMarketCatalog.allEntries.filter { entry ->
            val matchesCategory = when (selectedFilter) {
                "Pokémon TCG" -> entry.subCategory.contains("Pokémon", ignoreCase = true) || entry.category.contains("Pokémon", ignoreCase = true)
                "Magic MTG" -> entry.subCategory.contains("Magic", ignoreCase = true) || entry.category.contains("Magic", ignoreCase = true)
                "Hot Wheels" -> entry.subCategory.contains("Hot Wheels", ignoreCase = true) || entry.category.contains("Diecast", ignoreCase = true)
                "Yu-Gi-Oh!" -> entry.subCategory.contains("Yu-Gi-Oh", ignoreCase = true)
                "One Piece" -> entry.subCategory.contains("One Piece", ignoreCase = true)
                else -> true
            }

            val matchesQuery = query.isBlank() ||
                entry.name.lowercase().contains(query) ||
                entry.collection.lowercase().contains(query) ||
                entry.itemNumber.lowercase().contains(query) ||
                entry.subCategory.lowercase().contains(query)

            matchesCategory && matchesQuery
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Selecionar do Catálogo", fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
                Text(
                    text = "Escolha o Pokémon ou carta correta da lista verificada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp, max = 500.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nome (ex: Pikachu, Blastoise)...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Franchise filter tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterTabs = listOf("Todos", "Pokémon TCG", "Magic MTG", "Hot Wheels", "Yu-Gi-Oh!", "One Piece")
                    items(filterTabs) { tab ->
                        FilterChip(
                            selected = selectedFilter == tab,
                            onClick = { selectedFilter = tab },
                            label = { Text(tab, fontSize = 11.sp, fontWeight = if (selectedFilter == tab) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Results list
                if (filteredEntries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhum item encontrado para '$searchQuery'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredEntries) { entry ->
                            Card(
                                onClick = { onSelectEntry(entry) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val entryImgUrl = remember(entry.name) {
                                        OfficialCardImageHelper.getOfficialImageUrl(entry.name, entry.subCategory, entry.collection, entry.itemNumber)
                                    }

                                    if (entryImgUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = entryImgUrl,
                                            contentDescription = entry.name,
                                            modifier = Modifier
                                                .size(54.dp, 75.dp)
                                                .clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp, 75.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Style, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = entry.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2
                                        )
                                        Text(
                                            text = "${entry.subCategory} • ${entry.collection} (${entry.itemNumber})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = entry.rarity,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Text(
                                                text = "R$ ${"%.2f".format(entry.realMarketPriceBrl)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Selecionar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

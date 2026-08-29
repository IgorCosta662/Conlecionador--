package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.PriceUpdateState
import com.example.ui.Routes
import com.example.ui.components.CurrencySelector
import com.example.ui.components.PriceEvolutionChart
import com.example.ui.components.SlabShowcaseDialog
import com.example.util.CardEffectTranslator
import com.example.util.OfficialCardImageHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val priceUpdateState by viewModel.priceUpdateState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSlabShowcase by remember { mutableStateOf(false) }
    var showPriceAlertDialog by remember { mutableStateOf(false) }
    var isUpdatingOfficialImage by remember { mutableStateOf(false) }
    var isTranslatingCardEffect by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val backCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null && selectedItem != null) {
            viewModel.updateItemBackImage(selectedItem!!, bitmap)
        }
    }

    val detailCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null && selectedItem != null) {
            viewModel.addItemDetailImage(selectedItem!!, bitmap)
        }
    }

    if (selectedItem == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val item = selectedItem!!
    val isProfit = item.profitOrLoss >= 0
    val profitColor = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828)

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val lastUpdateStr = remember(item.lastPriceUpdate) {
        dateFormat.format(Date(item.lastPriceUpdate))
    }

    val conditionAssessment = remember(item.conditionAssessmentJson) {
        item.getConditionAssessment()
    }
    val conditionTiers = remember(item.conditionPricesJson) {
        item.getConditionPriceTiers()
    }
    val langComparisons = remember(item.languageComparisonJson) {
        item.getLanguageComparisonList()
    }
    val detailImages = remember(item.detailImagesJson) {
        item.getDetailImages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showPriceAlertDialog = true }) {
                        Icon(
                            imageVector = if (item.isAlertEnabled) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                            contentDescription = "Alerta de Preço",
                            tint = if (item.isAlertEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(item) }) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (item.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        viewModel.isEditingItem.value = item
                        navController.navigate(Routes.ADD_ITEM)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("item_detail_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. HERO PHOTO & MULTI-PHOTO GALLERY
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Main Photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .background(
                                    Brush.verticalGradient(
                                        if (item.isCard) listOf(Color(0xFF2E1C4E), Color(0xFF140D24))
                                        else listOf(Color(0xFF421515), Color(0xFF1C0A0A))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val resolvedImageUrl = if (!item.imageUri.isNullOrBlank()) {
                                item.imageUri
                            } else {
                                OfficialCardImageHelper.getOfficialImageUrl(item.name, item.subCategory, item.collection, item.itemNumber)
                            }

                            if (!resolvedImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = resolvedImageUrl,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            } else {
                                Icon(
                                    imageVector = if (item.isCard) Icons.Default.Style else Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(72.dp)
                                )
                            }
                        }

                        // Multi-photo strip (Verso, Detalhes adicionais)
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Fotos do Item", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    TextButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                isUpdatingOfficialImage = true
                                                val resolved = OfficialCardImageHelper.searchOfficialImageOnline(
                                                    name = item.name,
                                                    subCategory = item.subCategory,
                                                    collection = item.collection,
                                                    itemNumber = item.itemNumber
                                                ) ?: OfficialCardImageHelper.getOfficialImageUrl(item.name, item.subCategory, item.collection, item.itemNumber)
                                                val updated = item.copy(imageUri = resolved)
                                                viewModel.updateItem(updated)
                                                isUpdatingOfficialImage = false
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isUpdatingOfficialImage) "Buscando..." else "Foto Oficial HD", fontSize = 11.sp)
                                    }

                                    TextButton(
                                        onClick = { backCameraLauncher.launch(null) },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.FlipToBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (item.backImageUri != null) "Verso" else "+ Verso", fontSize = 11.sp)
                                    }
                                    TextButton(
                                        onClick = { detailCameraLauncher.launch(null) },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Detalhe", fontSize = 11.sp)
                                    }
                                }
                            }

                            // Secondary photos row
                            val extraPhotos = buildList {
                                item.backImageUri?.let { add("Verso" to it) }
                                detailImages.forEachIndexed { i, uri -> add("Detalhe #${i + 1}" to uri) }
                            }

                            if (extraPhotos.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    items(extraPhotos) { (label, uri) ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            AsyncImage(
                                                model = uri,
                                                contentDescription = label,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                            )
                                            Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Item Identifiers
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.subCategory.ifBlank { item.type },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (item.itemNumber.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = item.itemNumber,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )

                            if (item.collection.isNotBlank()) {
                                Text(
                                    text = item.collection,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Badges (Rarity, Variant, Condition, Language Flag)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(item.rarity, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                                )
                                if (item.variant.isNotBlank() && item.variant != "Normal") {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(item.variant, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFFFE082).copy(alpha = 0.4f),
                                            labelColor = Color(0xFFE65100)
                                        )
                                    )
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(item.languageDisplayName, fontSize = 11.sp) }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Slab 3D Holographic Showcase Trigger Button
                            Button(
                                onClick = { showSlabShowcase = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir Vitrine Slab 3D & Efeito Foil", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. VALUATION & PRICE METRICS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COTAÇÃO & VALORIZAÇÃO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            CurrencySelector(
                                selectedCurrency = selectedCurrency,
                                onCurrencySelected = { viewModel.setCurrency(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selectedCurrency.format(item.estimatedValue),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Menor: ${selectedCurrency.format(item.minPrice)} • Maior: ${selectedCurrency.format(item.maxPrice)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Preço Pago (un.)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(selectedCurrency.format(item.purchasePrice), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Quantidade", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${item.quantity} un.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Lucro / Valorização", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val sign = if (isProfit) "+" else ""
                                Text(
                                    text = "$sign${selectedCurrency.format(item.profitOrLoss)} ($sign${String.format(Locale.US, "%.1f", item.profitPercentage)}%)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = profitColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // AI Re-Price Button
                        Button(
                            onClick = { viewModel.refreshItemPriceWithAI(item) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("refresh_price_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (priceUpdateState is PriceUpdateState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Buscando cotações em tempo real...")
                            } else {
                                Icon(Icons.Default.AutoGraph, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Atualizar Preço com IA")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Última atualização: $lastUpdateStr",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // 3. AVALIAÇÃO DE CONDIÇÃO DETALHADA & AVISO LEGAL
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
                                text = "ESTADO DE CONSERVAÇÃO (IA)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = item.condition,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cantos (Corners)", style = MaterialTheme.typography.bodySmall)
                                Text(conditionAssessment.corners, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Bordas (Edges)", style = MaterialTheme.typography.bodySmall)
                                Text(conditionAssessment.edges, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Superfície (Surface)", style = MaterialTheme.typography.bodySmall)
                                Text(conditionAssessment.scratches, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Centralização (Centering)", style = MaterialTheme.typography.bodySmall)
                                Text(conditionAssessment.centering, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (conditionTiers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Matriz de Preços por Estado:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(conditionTiers) { tier ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(tier.condition, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                            Text(selectedCurrency.format(tier.priceBrl), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
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
                                    text = "Esta análise é apenas uma estimativa e não substitui uma avaliação profissional.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. COTAÇÃO POR IDIOMA (PT-BR vs EN vs JP)
            if (langComparisons.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("COTAÇÃO ISOLADA POR IDIOMA", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            langComparisons.forEach { langComp ->
                                val isCurrent = langComp.languageCode.equals(item.language, ignoreCase = true)
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
                                                text = "${langComp.languageName}${if (isCurrent) " (Seu item)" else ""}",
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

            // 4.5 EFEITOS & HABILIDADES DA CARTA (TRADUÇÃO PT-BR)
            if (item.type == "Trading Cards" || item.cardTranslatedEffect.isNotBlank() || item.cardOracleText.isNotBlank() || item.cardAttacks.isNotBlank() || item.cardHp.isNotBlank()) {
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

                                TextButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            isTranslatingCardEffect = true
                                            try {
                                                val result = CardEffectTranslator.searchCardEffectsOnline(
                                                    name = item.name,
                                                    subCategory = item.subCategory,
                                                    collection = item.collection,
                                                    itemNumber = item.itemNumber
                                                )
                                                val updated = item.copy(
                                                    cardTranslatedEffect = result.translatedEffect.ifBlank {
                                                        if (item.cardOracleText.isNotBlank()) {
                                                            CardEffectTranslator.translateToPortuguese(item.cardOracleText, item.subCategory)
                                                        } else item.cardTranslatedEffect
                                                    },
                                                    cardOracleText = if (result.originalText.isNotBlank()) result.originalText else item.cardOracleText,
                                                    cardAttacks = if (result.cardAttacks.isNotBlank()) result.cardAttacks else item.cardAttacks,
                                                    cardHp = if (result.cardHp.isNotBlank()) result.cardHp else item.cardHp,
                                                    cardArtist = if (result.cardArtist.isNotBlank()) result.cardArtist else item.cardArtist
                                                )
                                                viewModel.updateItem(updated)
                                            } catch (_: Exception) {
                                            } finally {
                                                isTranslatingCardEffect = false
                                            }
                                        }
                                    },
                                    enabled = !isTranslatingCardEffect
                                ) {
                                    if (isTranslatingCardEffect) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Traduzindo...", fontSize = 11.sp)
                                    } else {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Traduzir / Atualizar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Badges for HP, Attacks, Artist
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (item.cardHp.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = item.cardHp,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (item.cardArtist.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "Ilustrador: ${item.cardArtist}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (item.cardAttacks.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = item.cardAttacks,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Translated Card Effect / Oracle text
                            val effectText = if (item.cardTranslatedEffect.isNotBlank()) {
                                item.cardTranslatedEffect
                            } else if (item.cardOracleText.isNotBlank()) {
                                CardEffectTranslator.translateToPortuguese(item.cardOracleText, item.subCategory)
                            } else {
                                "Nenhum texto de efeito cadastrado ainda. Clique em 'Traduzir / Atualizar' para buscar os efeitos e traduzir para o português."
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

                            if (item.cardOracleText.isNotBlank() && item.cardOracleText != item.cardTranslatedEffect) {
                                Text(
                                    text = "Texto Original (Inglês):\n${item.cardOracleText}",
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

            // 5. STORAGE LOCATION & NOTES
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "LOCALIZAÇÃO & OBSERVAÇÕES",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (item.storageLocation.isNotBlank()) item.storageLocation else "Local não informado (ex: Pasta 1, Gaveta 2)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (item.storageLocation.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (item.notes.isNotBlank()) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 6. PRICE EVOLUTION CHART
            item {
                PriceEvolutionChart(
                    history = item.getHistoryList(),
                    selectedCurrency = selectedCurrency
                )
            }

            // 6.5 CONSENSO MULTIMERCADO & NORMALIZAÇÃO CAMBIAL
            item {
                val crossReport = item.getCrossReferencedReport() ?: com.example.api.PriceSourceRegistry.generateCrossReferencedMarketPricing(
                    itemName = item.name,
                    subCategory = item.subCategory,
                    rarity = item.rarity,
                    variant = item.variant,
                    condition = item.condition,
                    language = item.language,
                    baseEstimatedPrice = item.estimatedValue
                ).third

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                            Column {
                                Text(
                                    text = "CONSENSO MULTIMERCADO & CAMBIO",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "LigaMagic vs Mercados Internacionais",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "${crossReport.stabilityScore}% Estabilidade",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Parity / Discrepancy Note
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = crossReport.discrepancyNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Quotes Table Breakdown
                        if (crossReport.quotes.isNotEmpty()) {
                            Text(
                                text = "Fontes Cruzadas & Normalização (${selectedCurrency.code}):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            crossReport.quotes.forEach { quote ->
                                val origSymbol = when (quote.originalCurrency) {
                                    "USD" -> "$"
                                    "EUR" -> "€"
                                    "JPY" -> "¥"
                                    "GBP" -> "£"
                                    else -> "R$"
                                }
                                val origFormatted = if (quote.originalCurrency == "JPY") {
                                    "$origSymbol ${String.format(Locale.US, "%.0f", quote.originalPrice)}"
                                } else {
                                    "$origSymbol ${String.format(Locale.US, "%.2f", quote.originalPrice)}"
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (quote.isDomesticSource) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        ) {
                                            Text(
                                                text = quote.sourceRegion,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Column {
                                            Text(quote.sourceName, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = "Orig: $origFormatted • Peso: ${quote.weightPercentage}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = selectedCurrency.format(quote.normalizedPriceBrl),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (quote.isDomesticSource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. STORE OFFERS LIST
            item {
                val offers = item.getOffersList()
                if (offers.isNotEmpty()) {
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
                                text = "OFERTAS VERIFICADAS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            offers.forEachIndexed { idx, offer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(offer.storeName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text("${offer.condition} • ${offer.listingType}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = selectedCurrency.format(offer.priceInBRL),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (idx < offers.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Item?") },
            text = { Text("Deseja realmente remover '${item.name}' da sua coleção?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItem(item)
                        showDeleteDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showSlabShowcase) {
        SlabShowcaseDialog(
            item = item,
            currency = selectedCurrency,
            onDismiss = { showSlabShowcase = false }
        )
    }

    // Price Target Alert Dialog
    if (showPriceAlertDialog) {
        var alertTargetText by remember { mutableStateOf(if (item.targetPriceAlert > 0) item.targetPriceAlert.toString() else "") }
        var isAlertOn by remember { mutableStateOf(item.isAlertEnabled) }

        AlertDialog(
            onDismissRequest = { showPriceAlertDialog = false },
            title = { Text("Alerta de Preço Alvo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Defina um valor alvo para receber notificações quando este item atingir a cotação desejada.")
                    OutlinedTextField(
                        value = alertTargetText,
                        onValueChange = { alertTargetText = it },
                        label = { Text("Preço Alvo (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ativar Alerta")
                        Switch(
                            checked = isAlertOn,
                            onCheckedChange = { isAlertOn = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val target = alertTargetText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    viewModel.setItemPriceAlert(item, target, isAlertOn)
                    showPriceAlertDialog = false
                }) {
                    Text("Salvar Alerta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPriceAlertDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

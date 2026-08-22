package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.AppCurrency
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

    var showCorrectionDialog by remember { mutableStateOf(false) }
    var showAddToCollectionDialog by remember { mutableStateOf(false) }

    if (scanUiState !is ScanUiState.Success || buffer == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val successState = scanUiState as ScanUiState.Success
    val itemResult = buffer ?: successState.result

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
                                .height(200.dp)
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

                            // Badges (Rarity, Variant, Condition, Language)
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
                                        label = { Text("✨ ${itemResult.variant}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFFFE082).copy(alpha = 0.4f),
                                            labelColor = Color(0xFFE65100)
                                        )
                                    )
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(itemResult.apparentCondition, fontSize = 11.sp) }
                                )
                                if (itemResult.language != "N/A") {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(itemResult.language, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. 💰 PREÇO ESTIMADO (Médio, Mais barato, Mais caro)
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
                                text = "💰 PREÇO ESTIMADO DE MERCADO",
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
                                text = "💡 ${itemResult.marketTrendComment}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. 📊 CONFIANÇA DA IDENTIFICAÇÃO
            item {
                ConfidenceMeter(score = itemResult.confidenceScore)
            }

            // 4. 🛒 OFERTAS DISPONÍVEIS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🛒 OFERTAS ENCONTRADAS POR FONTE",
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

            // 5. HISTÓRICO DE PREÇOS GRÁFICO
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
                    notes = notes
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
                        text = "${itemResult.name} (${itemResult.subCategory})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Valor estimado: ${selectedCurrency.format(itemResult.averagePrice)}",
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

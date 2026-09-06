package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.MarketRegion
import com.example.ui.BatchScanItem
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.ui.ScanUiState

enum class ScannerMode(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SINGLE("Scanner Completo", Icons.Default.CameraAlt),
    BATCH("Modo Lote", Icons.Default.Inventory2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scanUiState by viewModel.scanUiState.collectAsStateWithLifecycle()
    val batchQueue by viewModel.batchScanQueue.collectAsStateWithLifecycle()
    val selectedMarket by viewModel.selectedMarketRegion.collectAsStateWithLifecycle()

    var activeMode by remember { mutableStateOf(ScannerMode.SINGLE) }
    var contextHintText by remember { mutableStateOf("") }
    var showHintDialog by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.startImageScan(bitmap, contextHintText.ifBlank { null })
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.startImageScan(bitmap, contextHintText.ifBlank { null })
            } catch (e: Exception) {
                // handle error gracefully
            }
        }
    }

    // Handle scan success depending on mode
    LaunchedEffect(scanUiState) {
        if (scanUiState is ScanUiState.Success) {
            val success = scanUiState as ScanUiState.Success
            if (activeMode == ScannerMode.BATCH) {
                viewModel.addToBatchQueue(
                    BatchScanItem(
                        identification = success.result,
                        imageBase64 = success.capturedImageBase64,
                        savedImageUri = success.savedImageUri
                    )
                )
                viewModel.resetScanState()
            } else {
                navController.navigate(Routes.SCAN_RESULT)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Scanner Inteligente", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Mercado: ${selectedMarket.flag} ${selectedMarket.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showHintDialog = true }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Dicas do Scanner")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("scanner_screen")
        ) {
            if (scanUiState is ScanUiState.Loading) {
                // Automated End-to-End Processing HUD with step animation
                ScanningProgressHUD()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Controls: Mode Switcher & Market Selector
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Scanner Mode Tabs
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            ScannerMode.values().forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = activeMode == mode,
                                    onClick = { activeMode = mode },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ScannerMode.values().size),
                                    icon = { Icon(mode.icon, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                ) {
                                    Text(mode.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Market Selector Chips (BR, US, JP, EU)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mercado:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                MarketRegion.values().forEach { market ->
                                    FilterChip(
                                        selected = selectedMarket == market,
                                        onClick = { viewModel.setMarketRegion(market) },
                                        label = { Text(market.code, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                        }

                        // Category Focus Selector (Auto, Pokémon, Magic, Yu-Gi-Oh, Hot Wheels)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Foco IA:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                val options = listOf(
                                    "" to "Auto (IA)",
                                    "Pokémon TCG" to "Pokémon TCG",
                                    "Magic: The Gathering MTG" to "Magic MTG",
                                    "Yu-Gi-Oh!" to "Yu-Gi-Oh!",
                                    "One Piece Card Game" to "One Piece",
                                    "Hot Wheels Diecast" to "Hot Wheels"
                                )
                                items(options) { (hintKey, label) ->
                                    FilterChip(
                                        selected = contextHintText == hintKey,
                                        onClick = {
                                            contextHintText = if (contextHintText == hintKey) "" else hintKey
                                        },
                                        label = { Text(label, fontSize = 11.sp, fontWeight = if (contextHintText == hintKey) FontWeight.Bold else FontWeight.Normal) }
                                    )
                                }
                            }
                        }

                        // Secondary Quick Pokemon Chips (Shown when Pokemon TCG is active)
                        AnimatedVisibility(visible = contextHintText.contains("Pokémon", ignoreCase = true)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pokémon:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    val pokeNames = listOf("Pikachu", "Bulbasaur", "Charmander", "Squirtle", "Blastoise", "Venusaur", "Mewtwo", "Gengar", "Umbreon", "Charizard")
                                    items(pokeNames) { pName ->
                                        SuggestionChip(
                                            onClick = {
                                                contextHintText = "Pokémon TCG $pName"
                                            },
                                            label = { Text(pName, fontSize = 10.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = if (contextHintText.contains(pName, ignoreCase = true)) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Scanner Viewfinder with Card / Diecast Framing Target
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanner Reticle Guidelines
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Posicione a carta ou miniatura aqui",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Identificação de nome, idioma, raridade e condição",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Corner indicator badges
                        Text(
                            text = "AUTO-ALINHAMENTO ATIVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 10.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    // Batch Queue Strip (Shown when in BATCH mode and queue has items)
                    AnimatedVisibility(visible = activeMode == ScannerMode.BATCH && batchQueue.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Fila de Lote (${batchQueue.size} itens)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    TextButton(onClick = { viewModel.clearBatchQueue() }) {
                                        Text("Limpar", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(batchQueue) { item ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            tonalElevation = 2.dp
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${item.identification.languageFlag} ${item.identification.name}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                IconButton(
                                                    onClick = { viewModel.removeFromBatchQueue(item.id) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remover", modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        viewModel.addAllBatchItemsToCollection()
                                        navController.navigate(Routes.COLLECTION) {
                                            popUpTo(Routes.DASHBOARD)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Adicionar Todos (${batchQueue.size}) à Coleção")
                                }
                            }
                        }
                    }

                    // Action Buttons (Camera / Gallery / Demo Presets)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("camera_scan_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (activeMode == ScannerMode.BATCH) "Fotografar Próximo Item (Lote)" else "Tirar Foto & Identificar",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("gallery_scan_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Galeria", fontSize = 12.sp)
                            }

                            FilledTonalButton(
                                onClick = { viewModel.startSampleScan("CARTA") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("sample_pokemon_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ex. Card", fontSize = 12.sp)
                            }

                            FilledTonalButton(
                                onClick = { viewModel.startSampleScan("CARRINHO") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("sample_car_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ex. Carrinho", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Hint Dialog
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = { Text("Como obter a melhor identificação", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Dicas para cartas (TCG):", fontWeight = FontWeight.Bold)
                    Text("• Centralize o nome, número (#xxx/xxx) e símbolo da coleção na foto.")
                    Text("• Evite reflexos de luz direta para que a IA detecte se a carta é Foil/Holo.")
                    Text("• A IA detectará automaticamente o idioma (Português, Inglês, Japonês, etc.).")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Dicas para Diecast (Hot Wheels):", fontWeight = FontWeight.Bold)
                    Text("• Mantenha visível a pintura, rodas e texto da base/cartela para identificar Super Treasure Hunts.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun ScanningProgressHUD() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha))
                .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Analisando com IA...",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Step checklist
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            ScanStepRow(text = "1. Imagem original armazenada", isDone = true)
            ScanStepRow(text = "2. Identificando coleção & variante", isDone = true)
            ScanStepRow(text = "3. Detectando idioma da tiragem", isDone = true)
            ScanStepRow(text = "4. Avaliando condição visual", isDone = true)
            ScanStepRow(text = "5. Isolando preços específicos", isDone = false)
        }
    }
}

@Composable
private fun ScanStepRow(text: String, isDone: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
            contentDescription = null,
            tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


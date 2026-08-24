package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.CollectorViewModel
import java.util.Locale

data class ItemPreset(
    val title: String,
    val name: String,
    val category: String,
    val subCategory: String,
    val collection: String,
    val itemNumber: String,
    val rarity: String,
    val variant: String,
    val condition: String,
    val languageOrScale: String,
    val defaultPaid: Double,
    val defaultEstValue: Double,
    val defaultStorage: String,
    val defaultNotes: String,
    val tags: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: CollectorViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val editingItem by viewModel.isEditingItem.collectAsStateWithLifecycle()
    val isEditMode = editingItem != null

    // Main Categories Menu
    val mainCategories = listOf(
        Triple("Trading Cards", "🃏 TCG (Cartas)", Color(0xFF6366F1)),
        Triple("Carrinhos / Diecast", "🏎️ Diecast (Carros)", Color(0xFFEF4444)),
        Triple("Action Figures", "🦸 Action Figures", Color(0xFF8B5CF6)),
        Triple("Moedas", "🪙 Moedas & Cédulas", Color(0xFFF59E0B)),
        Triple("Outros", "📦 Outros Colecionáveis", Color(0xFF10B981))
    )

    var category by remember(editingItem) { mutableStateOf(editingItem?.type ?: "Trading Cards") }
    var subCategory by remember(editingItem) { mutableStateOf(editingItem?.subCategory ?: "Pokémon TCG") }

    // Subcategories / Brands Submenu mapping
    val subCategoryOptions = remember(category) {
        when (category) {
            "Trading Cards" -> listOf(
                "Pokémon TCG",
                "Magic: The Gathering",
                "Yu-Gi-Oh!",
                "One Piece Card Game",
                "Disney Lorcana",
                "Digimon Card Game",
                "Dragon Ball Super",
                "Star Wars Unlimited",
                "Weiss Schwarz",
                "Marvel Champions"
            )
            "Carrinhos / Diecast" -> listOf(
                "Hot Wheels",
                "Matchbox",
                "Mini GT",
                "Kaido House",
                "Inno64",
                "Tomica",
                "Majorette",
                "Greenlight",
                "Auto World",
                "Johnny Lightning",
                "M2 Machines",
                "Tarmac Works",
                "Sparky"
            )
            "Action Figures" -> listOf(
                "Marvel Legends",
                "Star Wars Black Series",
                "S.H.Figuarts",
                "Funko Pop!",
                "NECA",
                "Mafex",
                "Hot Toys",
                "Bandai Gunpla",
                "McFarlane Toys",
                "Anime / Games"
            )
            "Moedas" -> listOf(
                "Moedas do Brasil (Real)",
                "Moedas do Brasil (Réis / Cruzeiro)",
                "Moedas Comemorativas",
                "Cédulas Históricas",
                "Moedas Mundiais (Dólar / Euro / Yen)",
                "Moedas Antigas / Numismática"
            )
            else -> listOf(
                "Quadrinhos & Mangás",
                "Pins & Bottons",
                "LEGO & Blocos",
                "Video Games Retrô",
                "Discos de Vinil & Mídia",
                "Autógrafos & Memorabilia",
                "Outros Colecionáveis"
            )
        }
    }

    var name by remember(editingItem) { mutableStateOf(editingItem?.name ?: "") }
    var collection by remember(editingItem) { mutableStateOf(editingItem?.collection ?: "") }
    var itemNumber by remember(editingItem) { mutableStateOf(editingItem?.itemNumber ?: "") }
    var rarity by remember(editingItem) { mutableStateOf(editingItem?.rarity ?: "Comum") }
    var variant by remember(editingItem) { mutableStateOf(editingItem?.variant ?: "Normal") }
    var condition by remember(editingItem) { mutableStateOf(editingItem?.condition ?: "Near Mint") }
    var language by remember(editingItem) { mutableStateOf(editingItem?.language ?: if (category == "Carrinhos / Diecast") "1:64" else "PT-BR") }
    var quantityText by remember(editingItem) { mutableStateOf(editingItem?.quantity?.toString() ?: "1") }
    var purchasePriceText by remember(editingItem) { mutableStateOf(editingItem?.purchasePrice?.toString() ?: "0.00") }
    var estimatedValueText by remember(editingItem) { mutableStateOf(editingItem?.estimatedValue?.toString() ?: "0.00") }
    var storageLocation by remember(editingItem) { mutableStateOf(editingItem?.storageLocation ?: "") }
    var notes by remember(editingItem) { mutableStateOf(editingItem?.notes ?: "") }
    var tags by remember(editingItem) { mutableStateOf(editingItem?.tags ?: "") }
    var imageUri by remember(editingItem) { mutableStateOf(editingItem?.imageUri) }

    // Live autocomplete & catalog state
    var showLiveSuggestions by remember { mutableStateOf(true) }
    var showCatalogSheet by remember { mutableStateOf(false) }
    var autoFilledFeedback by remember { mutableStateOf<String?>(null) }

    // Live search in real market catalog
    val liveMatches = remember(name, showLiveSuggestions) {
        if (showLiveSuggestions && name.trim().length >= 2) {
            RealMarketCatalog.search(name, maxResults = 5)
        } else {
            emptyList()
        }
    }

    // Fast apply real catalog entry
    fun applyRealCatalogEntry(entry: RealCatalogEntry) {
        name = entry.name
        category = entry.category
        subCategory = entry.subCategory
        collection = entry.collection
        itemNumber = entry.itemNumber
        rarity = entry.rarity
        variant = entry.variant
        condition = entry.defaultCondition
        language = entry.languageOrScale
        purchasePriceText = String.format(Locale.US, "%.2f", entry.suggestedPurchasePriceBrl)
        estimatedValueText = String.format(Locale.US, "%.2f", entry.realMarketPriceBrl)
        storageLocation = entry.defaultStorage
        notes = entry.notes
        tags = entry.tags
        showLiveSuggestions = false
        autoFilledFeedback = "✨ Preenchido automaticamente: ${entry.name} • Cotação Real: R$ ${String.format(Locale.US, "%.2f", entry.realMarketPriceBrl)}"
    }

    // Popular Presets for One-Tap Fast Fill
    val smartPresets = remember(category, subCategory) {
        listOf(
            ItemPreset(
                title = "Charizard ex 151 (SIR)",
                name = "Charizard ex (Special Illustration Rare)",
                category = "Trading Cards",
                subCategory = "Pokémon TCG",
                collection = "Scarlet & Violet 151",
                itemNumber = "199/165",
                rarity = "Special Illustration Rare",
                variant = "Foil Holográfico Texturizado",
                condition = "Near Mint (NM)",
                languageOrScale = "PT-BR",
                defaultPaid = 500.0,
                defaultEstValue = 850.0,
                defaultStorage = "Pasta 151 - Pág 1 (TopLoader)",
                defaultNotes = "Tirado no booster pack da box Ultra Premium. Centralização excelente.",
                tags = "pokemon, charizard, sir, 151, fogo, ultra rara"
            ),
            ItemPreset(
                title = "Pikachu 151 Ilustração",
                name = "Pikachu (Illustration Rare)",
                category = "Trading Cards",
                subCategory = "Pokémon TCG",
                collection = "Scarlet & Violet 151",
                itemNumber = "173/165",
                rarity = "Illustration Rare",
                variant = "Foil / Holográfico",
                condition = "Near Mint (NM)",
                languageOrScale = "PT-BR",
                defaultPaid = 90.0,
                defaultEstValue = 190.0,
                defaultStorage = "Pasta 151 - Pág 1",
                defaultNotes = "Ilustração secreta passeando pela cidade.",
                tags = "pokemon, pikachu, eletrico, 151, ir"
            ),
            ItemPreset(
                title = "The One Ring (MTG)",
                name = "The One Ring (Extended Borderless Foil)",
                category = "Trading Cards",
                subCategory = "Magic: The Gathering",
                collection = "The Lord of the Rings: Tales of Middle-earth",
                itemNumber = "#0246",
                rarity = "Mítica Rara",
                variant = "Foil Extended Art",
                condition = "Near Mint (NM)",
                languageOrScale = "EN",
                defaultPaid = 350.0,
                defaultEstValue = 620.0,
                defaultStorage = "Pasta Luxo MTG - Pág 1",
                defaultNotes = "Indestrutível. Staple nos formatos Modern e Commander.",
                tags = "mtg, lord of the rings, ring, commander, modern"
            ),
            ItemPreset(
                title = "Blue-Eyes 25th (Yu-Gi-Oh)",
                name = "Blue-Eyes White Dragon (Quarter Century Secret Rare)",
                category = "Trading Cards",
                subCategory = "Yu-Gi-Oh!",
                collection = "25th Anniversary Rarity Collection",
                itemNumber = "RA01-EN001",
                rarity = "Quarter Century Secret Rare",
                variant = "25th Logo Foil",
                condition = "Near Mint (NM)",
                languageOrScale = "EN",
                defaultPaid = 200.0,
                defaultEstValue = 380.0,
                defaultStorage = "TopLoader YuGiOh Case",
                defaultNotes = "Gravação com logo comemorativo do 25º aniversário da franquia.",
                tags = "yugioh, blue eyes, 25th, kaiba, dragao, qcr"
            ),
            ItemPreset(
                title = "Luffy Manga Rare (One Piece)",
                name = "Monkey.D.Luffy (Manga Rare Alternate Art Gear 5)",
                category = "Trading Cards",
                subCategory = "One Piece Card Game",
                collection = "Awakening of the New Era [OP-05]",
                itemNumber = "OP05-060",
                rarity = "Secret Rare Manga",
                variant = "Manga Alternate Art Foil",
                condition = "Near Mint (NM)",
                languageOrScale = "JP",
                defaultPaid = 4500.0,
                defaultEstValue = 7800.0,
                defaultStorage = "Cofre Graduado / Slab Magnético",
                defaultNotes = "Fundo com painéis originais do mangá desenhados por Eiichiro Oda. Gear 5.",
                tags = "one piece, luffy, manga rare, gear 5, op05"
            ),
            ItemPreset(
                title = "Skyline R34 Super TH",
                name = "Nissan Skyline GT-R (BNR34) Super Treasure Hunt",
                category = "Carrinhos / Diecast",
                subCategory = "Hot Wheels",
                collection = "Mainline Factory Sealed 2025",
                itemNumber = "#142/250",
                rarity = "Super Treasure Hunt (STH)",
                variant = "Spectraflame Bayside Blue + Real Riders",
                condition = "Lacrado no Blister (Mint)",
                languageOrScale = "1:64",
                defaultPaid = 150.0,
                defaultEstValue = 380.0,
                defaultStorage = "Protetor Acrílico Blister #1",
                defaultNotes = "Super Treasure Hunt oficial com pneus de borracha Real Riders e pintura Spectraflame.",
                tags = "hotwheels, skyline, sth, super treasure hunt, nissan, jdm"
            ),
            ItemPreset(
                title = "Moeda 1 Real DH 1998",
                name = "Moeda 1 Real Declaração Universal dos Direitos Humanos 1998",
                category = "Moedas",
                subCategory = "Moedas do Brasil (Real)",
                collection = "Moedas Comemorativas do Real",
                itemNumber = "DH-1998",
                rarity = "Raríssima (Tiragem 600 mil)",
                variant = "Cuproníquel / Alpaca",
                condition = "Flor de Cunho (FC)",
                languageOrScale = "Brasil - 1998",
                defaultPaid = 250.0,
                defaultEstValue = 450.0,
                defaultStorage = "Cápsula Acrílica Selada",
                defaultNotes = "A moeda mais rara da primeira família do Real (tiragem de apenas 600 mil unidades).",
                tags = "moeda, real, direitos humanos, 1998, rara, flor de cunho"
            )
        )
    }

    // Quick Suggestions Chips for Fields
    val collectionSuggestions = remember(category, subCategory) {
        when {
            subCategory.contains("Pokémon", true) -> listOf("Scarlet & Violet 151", "Paldea Evolved", "Crown Zenith", "Twilight Masquerade", "Obsidian Flames", "Base Set 1999", "Evolving Skies", "Surging Sparks")
            subCategory.contains("Magic", true) -> listOf("Modern Horizons 3", "Lord of the Rings: Tales of Middle-earth", "Bloomburrow", "Commander Legends", "Dominaria United", "Kamigawa Neon Dynasty")
            subCategory.contains("Yu-Gi-Oh", true) -> listOf("25th Anniversary Rarity Collection", "Phantom Nightmare", "Age of Overlord", "Legend of Blue Eyes", "Battles of Legend")
            subCategory.contains("One Piece", true) -> listOf("Awakening of the New Era [OP-05]", "Romance Dawn [OP-01]", "Wings of the Captain [OP-06]", "500 Years in the Future [OP-07]")
            subCategory.contains("Lorcana", true) -> listOf("The First Chapter", "Rise of the Floodborn", "Into the Inklands", "Ursula's Return")
            subCategory.contains("Hot Wheels", true) -> listOf("Mainline 2025", "Car Culture: Boulevard", "Fast & Furious", "Team Transport", "Red Line Club (RLC)", "Premium Retro Entertainment")
            subCategory.contains("Matchbox", true) -> listOf("Mainline Matchbox", "Matchbox Moving Parts", "Super Chase", "Collectors Series 70th")
            subCategory.contains("Mini GT", true) -> listOf("Mini GT Regular", "Kaido House x Mini GT", "MiJo Exclusives")
            subCategory.contains("Marvel", true) -> listOf("Marvel Legends Retro Card", "Marvel Studios Infinity Saga", "Marvel Comics 85th", "Spider-Man Retro")
            subCategory.contains("Star Wars", true) -> listOf("Black Series 6\"", "Black Series 40th Anniversary", "Archive Collection", "Vintage Collection")
            subCategory.contains("Funko", true) -> listOf("Animation", "Marvel", "Star Wars", "Television", "Movies", "Games")
            subCategory.contains("Brasil", true) -> listOf("Moedas do Real (1994-2025)", "Comemorativas do Real", "Réis Império", "Cruzeiro / Cruzado")
            else -> listOf("Série 2025", "Edição Especial", "Coleção Principal", "Vintage 90s")
        }
    }

    val raritySuggestions = remember(category) {
        when (category) {
            "Trading Cards" -> listOf("Comum", "Incomum", "Rara Holo", "Ultra Rara", "Secret Rare", "Special Art (SIR)", "Illustration Rare", "Mítica Rara", "Quarter Century", "Manga Rare", "Enchanted")
            "Carrinhos / Diecast" -> listOf("Básico / Mainline", "Super Treasure Hunt (STH)", "Treasure Hunt (TH)", "Premium Metal/Metal", "Red Line Club (RLC)", "Chase 1:24 / Raw", "Super Chase")
            "Action Figures" -> listOf("Regular", "Exclusivo / SDCC", "Chase 1:6", "Edição Especial", "Glow in the Dark", "Importação Japonesa")
            "Moedas" -> listOf("Comum de Circulação", "Comemorativa", "Flor de Cunho (FC)", "Soberba (SOB)", "Muito Bem Conservada (MBC)", "Prata / Ouro")
            else -> listOf("Comum", "Edição Especial", "Limitado", "Colecionador", "Raro")
        }
    }

    val conditionSuggestions = remember(category) {
        if (category == "Carrinhos / Diecast") {
            listOf("Lacrado no Blister (Mint)", "Cartela Perfeita (Near Mint)", "Blister com Detalhe", "Solto Impecável (Loose Mint)", "Solto com Marcas (Loose Played)")
        } else if (category == "Trading Cards") {
            listOf("Near Mint (NM)", "Lightly Played (LP)", "Moderately Played (MP)", "Heavily Played (HP)", "Damaged (DMG)", "Graded PSA/BGS 10", "Graded 9/9.5")
        } else if (category == "Action Figures") {
            listOf("Lacrado na Caixa (MIB)", "Caixa com Detalhes", "Exposto em Vitrine (Mint)", "Solto Completo", "Solto Incompleto")
        } else if (category == "Moedas") {
            listOf("Flor de Cunho (FC)", "Soberba (SOB)", "Muito Bem Conservada (MBC)", "Bem Conservada (BC)", "Regular (R)")
        } else {
            listOf("Novo / Lacrado", "Excelente Estado", "Bom Estado", "Usado / Com Marcas")
        }
    }

    val storageSuggestions = listOf(
        "Pasta 151 - Pág 1 (TopLoader)",
        "Pasta Principal - Pág 2",
        "TopLoader Case",
        "Slab Acrílico Graduado",
        "Protetor Acrílico Blister #1",
        "Vitrine de Vidro Sala",
        "Gaveta Colecionáveis #1",
        "Caixa Organizadora #1",
        "Cofre Numismático"
    )

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedPath = ImageStorageHelper.saveBitmapToInternalStorage(context, bitmap)
            imageUri = savedPath
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
                val savedPath = ImageStorageHelper.saveBitmapToInternalStorage(context, bitmap)
                imageUri = savedPath
            } catch (e: Exception) {
                // Ignore failure
            }
        }
    }

    fun applyPreset(preset: ItemPreset) {
        name = preset.name
        category = preset.category
        subCategory = preset.subCategory
        collection = preset.collection
        itemNumber = preset.itemNumber
        rarity = preset.rarity
        variant = preset.variant
        condition = preset.condition
        language = preset.languageOrScale
        purchasePriceText = String.format(Locale.US, "%.2f", preset.defaultPaid)
        estimatedValueText = String.format(Locale.US, "%.2f", preset.defaultEstValue)
        storageLocation = preset.defaultStorage
        notes = preset.defaultNotes
        tags = preset.tags
        showLiveSuggestions = false
        autoFilledFeedback = "✨ Preenchido: ${preset.name} (R$ ${String.format(Locale.US, "%.2f", preset.defaultEstValue)})"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isEditMode) "Editar Item" else "Cadastrar Novo Item",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Auto-Preenchimento & Cotações Reais",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.isEditingItem.value = null
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCatalogSheet = true },
                        modifier = Modifier.testTag("btn_open_real_catalog")
                    ) {
                        Icon(
                            Icons.Default.ManageSearch,
                            contentDescription = "Buscar Catálogo Real",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("add_item_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- FEEDBACK DE AUTO-PREENCHIMENTO ---
            if (autoFilledFeedback != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                                Text(
                                    text = autoFilledFeedback!!,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = { autoFilledFeedback = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // --- 1. BANNER DE AUTO-PREENCHIMENTO & CATÁLOGO REAL ---
            if (!isEditMode) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Busca Rápida de Cartas & Preços Reais",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                TextButton(
                                    onClick = { showCatalogSheet = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ver Todas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "Digite o nome da carta ou escolha um modelo abaixo para preencher TODOS os dados e o valor de mercado real em 1 toque:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Quick trending chip carousel
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(smartPresets) { preset ->
                                    ElevatedSuggestionChip(
                                        onClick = { applyPreset(preset) },
                                        label = {
                                            Column {
                                                Text(preset.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("R$ ${String.format(Locale.US, "%.2f", preset.defaultEstValue)}", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                Icons.Default.AutoFixHigh,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. MENU PRINCIPAL: CATEGORIA DO COLECIONÁVEL ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Menu Principal: Tipo de Colecionável *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mainCategories.forEach { (catKey, catLabel, catColor) ->
                            val isSelected = category == catKey
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    category = catKey
                                    // Set default subcategory for the category
                                    subCategory = when (catKey) {
                                        "Trading Cards" -> "Pokémon TCG"
                                        "Carrinhos / Diecast" -> "Hot Wheels"
                                        "Action Figures" -> "Marvel Legends"
                                        "Moedas" -> "Moedas do Brasil (Real)"
                                        else -> "Quadrinhos & Mangás"
                                    }
                                    if (catKey == "Carrinhos / Diecast") language = "1:64"
                                },
                                label = {
                                    Text(
                                        text = catLabel,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = catColor.copy(alpha = 0.2f),
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }

            // --- 3. SUBMENU: FRANQUIA / MARCA / LINHA ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "2. Submenu: Jogo / Marca / Série *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subCategoryOptions) { subCat ->
                            val isSelected = subCategory == subCat
                            FilterChip(
                                selected = isSelected,
                                onClick = { subCategory = subCat },
                                label = {
                                    Text(
                                        text = subCat,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // --- 4. FOTO PRINCIPAL DO ITEM ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Foto do Item",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        if (imageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Foto do Item",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                                IconButton(
                                    onClick = { imageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remover",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { cameraLauncher.launch(null) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Câmera")
                                }
                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Galeria")
                                }
                            }
                        }
                    }
                }
            }

            // --- 5. NOME PRINCIPAL DO ITEM + AUTOCOMPLETAÇÃO INTELIGENTE EM TEMPO REAL ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            showLiveSuggestions = true
                        },
                        label = { Text("Nome da Carta / Item / Carrinho *") },
                        placeholder = { Text("Ex: Charizard, Pikachu, Blue-Eyes, Skyline, Luffy...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_item_name"),
                        singleLine = true,
                        trailingIcon = {
                            if (name.isNotEmpty()) {
                                IconButton(onClick = {
                                    name = ""
                                    showLiveSuggestions = false
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpar")
                                }
                            }
                        }
                    )

                    // LIVE AUTOCOMPLETE DROP-DOWN / CARDS
                    AnimatedVisibility(
                        visible = liveMatches.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary
                            )
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Cartas Reais Encontradas (${liveMatches.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "Toque para Auto-Preencher",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                liveMatches.forEach { match ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { applyRealCatalogEntry(match) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = match.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${match.subCategory} • ${match.collection} #${match.itemNumber}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "${match.rarity} • ${match.variant}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Surface(
                                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                                                ) {
                                                    Text(
                                                        text = "R$ ${String.format(Locale.US, "%.2f", match.realMarketPriceBrl)}",
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF10B981),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }

                                                FilledTonalButton(
                                                    onClick = { applyRealCatalogEntry(match) },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(28.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Preencher", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 6. COLEÇÃO & NÚMERO COM SUGESTÕES ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = collection,
                            onValueChange = { collection = it },
                            label = { Text("Coleção / Série / Set") },
                            placeholder = { Text("Ex: 151, Mainline 2025") },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_collection"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = itemNumber,
                            onValueChange = { itemNumber = it },
                            label = { Text("Número / Código") },
                            placeholder = { Text("#199/165") },
                            modifier = Modifier
                                .weight(0.8f)
                                .testTag("input_number"),
                            singleLine = true
                        )
                    }

                    // Dynamic collection set suggestions
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(collectionSuggestions) { set ->
                            SuggestionChip(
                                onClick = { collection = set },
                                label = { Text(set, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // --- 7. RARIDADE & VARIANTE COM CHIPS SUGESTIVOS ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rarity,
                            onValueChange = { rarity = it },
                            label = { Text("Raridade") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_rarity"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = variant,
                            onValueChange = { variant = it },
                            label = { Text("Variante") },
                            placeholder = { Text("Foil, STH, Chase...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_variant"),
                            singleLine = true
                        )
                    }

                    // Sugestões de raridade em chips rápidos
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(raritySuggestions) { r ->
                            SuggestionChip(
                                onClick = { rarity = r },
                                label = { Text(r, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // --- 8. ESTADO DE CONSERVAÇÃO & IDIOMA/ESCALA ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = condition,
                            onValueChange = { condition = it },
                            label = { Text("Estado de Conservação") },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_condition"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = language,
                            onValueChange = { language = it },
                            label = { Text(if (category == "Carrinhos / Diecast") "Escala" else "Idioma") },
                            placeholder = { Text(if (category == "Carrinhos / Diecast") "1:64" else "PT-BR") },
                            modifier = Modifier
                                .weight(0.8f)
                                .testTag("input_language"),
                            singleLine = true
                        )
                    }

                    // Sugestões de condição em chips rápidos
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(conditionSuggestions) { cond ->
                            SuggestionChip(
                                onClick = { condition = cond },
                                label = { Text(cond, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // --- 9. QUANTIDADE & PREÇOS (COMPRA E MERCADO) ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantidade") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("input_quantity"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Preço Pago (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("input_purchase_price"),
                        singleLine = true
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = estimatedValueText,
                    onValueChange = { estimatedValueText = it },
                    label = { Text("Valor Estimado de Mercado (R$) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_estimated_value"),
                    singleLine = true,
                    supportingText = { Text("Cotação média real atual em marketplaces especializados (Liga, TCGPlayer, eBay, ML)") }
                )
            }

            // --- 10. LOCALIZAÇÃO FÍSICA NO INVENTÁRIO COM SUGESTÕES ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = storageLocation,
                        onValueChange = { storageLocation = it },
                        label = { Text("Local Onde Está Guardado (Físico)") },
                        placeholder = { Text("Ex: Pasta 151 - Pág 1, Caixa Hot Wheels #2...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_storage_location"),
                        singleLine = true
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(storageSuggestions) { loc ->
                            SuggestionChip(
                                onClick = { storageLocation = loc },
                                label = { Text(loc, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // --- 11. TAGS & OBSERVAÇÕES ---
            item {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags de Busca (separadas por vírgula)") },
                    placeholder = { Text("Ex: pokémon, fogo, raro, troca, rlc") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas & Observações do Colecionador") },
                    placeholder = { Text("Detalhes de aquisição, centralização, histórico...") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_notes")
                )
            }

            // --- 12. BOTÃO DE CONFIRMAÇÃO ---
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val qty = quantityText.toIntOrNull() ?: 1
                        val paid = purchasePriceText.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val est = estimatedValueText.replace(",", ".").toDoubleOrNull() ?: 0.0

                        if (isEditMode && editingItem != null) {
                            val updated = editingItem!!.copy(
                                name = name,
                                type = category,
                                subCategory = subCategory,
                                collection = collection,
                                itemNumber = itemNumber,
                                rarity = rarity,
                                variant = variant,
                                condition = condition,
                                language = language,
                                quantity = qty,
                                purchasePrice = paid,
                                estimatedValue = est,
                                storageLocation = storageLocation,
                                notes = notes,
                                tags = tags,
                                imageUri = imageUri
                            )
                            viewModel.updateItem(updated)
                        } else {
                            val (offers, history) = com.example.api.PriceSourceRegistry.generateRealisticOffersAndHistory(
                                itemName = name,
                                subCategory = subCategory,
                                rarity = rarity,
                                variant = variant,
                                condition = condition,
                                baseEstimatedPrice = est
                            )
                            val newItem = Item(
                                name = name,
                                type = category,
                                subCategory = subCategory,
                                collection = collection,
                                itemNumber = itemNumber,
                                rarity = rarity,
                                variant = variant,
                                condition = condition,
                                language = language,
                                quantity = qty,
                                purchasePrice = paid,
                                estimatedValue = est,
                                minPrice = est * 0.85,
                                maxPrice = est * 1.25,
                                storageLocation = storageLocation,
                                notes = notes,
                                tags = tags,
                                imageUri = imageUri,
                                priceOffersJson = JsonParserHelper.offersToJson(offers),
                                priceHistoryJson = JsonParserHelper.historyToJson(history)
                            )
                            viewModel.insertItem(newItem)
                        }
                        viewModel.isEditingItem.value = null
                        onNavigateBack()
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_item_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        if (isEditMode) Icons.Default.Save else Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditMode) "Salvar Alterações" else "Cadastrar Item na Coleção",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // --- BOTTOM SHEET: CATÁLOGO GERAL DE CARTAS & COTAÇÕES REAIS ---
    if (showCatalogSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCatalogSheet = false },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            var sheetSearchQuery by remember { mutableStateOf("") }
            var sheetSelectedCategory by remember { mutableStateOf("Todas") }

            val sheetCategories = listOf("Todas", "Pokémon TCG", "Magic", "Yu-Gi-Oh", "One Piece", "Lorcana", "Hot Wheels", "Moedas")

            val filteredEntries = remember(sheetSearchQuery, sheetSelectedCategory) {
                RealMarketCatalog.allEntries.filter { entry ->
                    val matchesCategory = when (sheetSelectedCategory) {
                        "Todas" -> true
                        "Pokémon TCG" -> entry.subCategory.contains("Pokémon", ignoreCase = true)
                        "Magic" -> entry.subCategory.contains("Magic", ignoreCase = true)
                        "Yu-Gi-Oh" -> entry.subCategory.contains("Yu-Gi-Oh", ignoreCase = true)
                        "One Piece" -> entry.subCategory.contains("One Piece", ignoreCase = true)
                        "Lorcana" -> entry.subCategory.contains("Lorcana", ignoreCase = true)
                        "Hot Wheels" -> entry.subCategory.contains("Hot Wheels", ignoreCase = true) || entry.category.contains("Diecast", ignoreCase = true)
                        "Moedas" -> entry.category.contains("Moedas", ignoreCase = true)
                        else -> true
                    }
                    val matchesQuery = sheetSearchQuery.isBlank() ||
                            entry.name.contains(sheetSearchQuery, ignoreCase = true) ||
                            entry.collection.contains(sheetSearchQuery, ignoreCase = true) ||
                            entry.itemNumber.contains(sheetSearchQuery, ignoreCase = true) ||
                            entry.tags.contains(sheetSearchQuery, ignoreCase = true)

                    matchesCategory && matchesQuery
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Catálogo com Cotações Reais",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${filteredEntries.size} itens autênticos verificados",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showCatalogSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = sheetSearchQuery,
                    onValueChange = { sheetSearchQuery = it },
                    placeholder = { Text("Buscar por nome, coleção, número (#199/165)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (sheetSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { sheetSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sheetCategories) { cat ->
                        FilterChip(
                            selected = sheetSelectedCategory == cat,
                            onClick = { sheetSelectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }

                // List of Real Catalog Entries
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredEntries) { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    applyRealCatalogEntry(entry)
                                    showCatalogSheet = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${entry.subCategory} • ${entry.collection} (${entry.itemNumber})",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${entry.rarity} • ${entry.variant}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                                    ) {
                                        Text(
                                            text = "R$ ${String.format(Locale.US, "%.2f", entry.realMarketPriceBrl)}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF10B981),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = "Cotação Real",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

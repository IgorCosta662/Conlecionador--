package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.data.ImageStorageHelper
import com.example.data.Item
import com.example.data.JsonParserHelper
import com.example.ui.CollectorViewModel

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

    // Popular Presets for One-Tap Fast Fill
    val smartPresets = remember(category, subCategory) {
        listOf(
            // TCG Presets
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
                name = "Pikachu (Secret Illustration Rare)",
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
                tags = "pokemon, pikachu, eletrico, 151"
            ),
            ItemPreset(
                title = "The One Ring (MTG)",
                name = "The One Ring (Foil Borderless)",
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
                tags = "yugioh, blue eyes, 25th, kaiba, dragao"
            ),
            ItemPreset(
                title = "Luffy Manga Rare (One Piece)",
                name = "Monkey.D.Luffy (Manga Rare Alternate Art)",
                category = "Trading Cards",
                subCategory = "One Piece Card Game",
                collection = "Awakening of the New Era [OP-05]",
                itemNumber = "OP05-060",
                rarity = "Secret Rare Manga",
                variant = "Manga Alternate Art Foil",
                condition = "Near Mint (NM)",
                languageOrScale = "JP",
                defaultPaid = 450.0,
                defaultEstValue = 750.0,
                defaultStorage = "Slab Magnético One Piece",
                defaultNotes = "Fundo com painéis originais do mangá desenhados por Eiichiro Oda.",
                tags = "one piece, luffy, manga rare, gear 5"
            ),
            // Diecast Presets
            ItemPreset(
                title = "Skyline R34 Super TH",
                name = "Nissan Skyline GT-R (BNR34) Super Treasure Hunt",
                category = "Carrinhos / Diecast",
                subCategory = "Hot Wheels",
                collection = "Mainline Factory Sealed 2025",
                itemNumber = "#142/250",
                rarity = "Super Treasure Hunt (STH)",
                variant = "Spectraflame Azul + Real Riders",
                condition = "Lacrado no Blister (Mint)",
                languageOrScale = "1:64",
                defaultPaid = 18.0,
                defaultEstValue = 320.0,
                defaultStorage = "Protetor Acrílico Blister #1",
                defaultNotes = "Pneus de borracha Real Riders e pintura Spectraflame Bayside Blue.",
                tags = "hotwheels, sth, super treasure hunt, skyline, nissan, jdm"
            ),
            ItemPreset(
                title = "Porsche 911 GT3 Boulevard",
                name = "Porsche 911 GT3 RS (Car Culture Boulevard)",
                category = "Carrinhos / Diecast",
                subCategory = "Hot Wheels",
                collection = "Car Culture: Boulevard",
                itemNumber = "#78",
                rarity = "Premium Metal/Metal",
                variant = "Real Riders + Metal Base",
                condition = "Lacrado no Blister (Mint)",
                languageOrScale = "1:64",
                defaultPaid = 45.0,
                defaultEstValue = 95.0,
                defaultStorage = "Gaveta Colecionáveis #2",
                defaultNotes = "Base 100% metal fundido e faróis em tampografia detalhada.",
                tags = "hotwheels, boulevard, premium, porsche, 911"
            ),
            ItemPreset(
                title = "Kaido House Datsun 510 Chase",
                name = "Kaido House Datsun 510 Pro Street (Raw Metal Chase)",
                category = "Carrinhos / Diecast",
                subCategory = "Kaido House",
                collection = "Kaido House x Mini GT",
                itemNumber = "KHMG042-CHASE",
                rarity = "Chase 1:24",
                variant = "Metal Cru Polido (Raw Metal Chase)",
                condition = "Novo na Caixa Lacrada (MIB)",
                languageOrScale = "1:64",
                defaultPaid = 140.0,
                defaultEstValue = 380.0,
                defaultStorage = "Vitrine Miniaturas #1",
                defaultNotes = "Design assinado por Jun Imai. Versão secreta Chase.",
                tags = "kaido house, minigt, chase, datsun, 510, raw"
            ),
            ItemPreset(
                title = "Matchbox Dodge D-200 Super Chase",
                name = "Matchbox 1968 Dodge D-200 4x4 (Super Chase)",
                category = "Carrinhos / Diecast",
                subCategory = "Matchbox",
                collection = "Matchbox Super Chase 2024",
                itemNumber = "#04/100",
                rarity = "Super Chase",
                variant = "Pneus Borracha + Pintura Especial",
                condition = "Lacrado no Blister",
                languageOrScale = "1:64",
                defaultPaid = 16.0,
                defaultEstValue = 180.0,
                defaultStorage = "Protetor Acrílico #3",
                defaultNotes = "Rara versão Super Chase de linha da Matchbox.",
                tags = "matchbox, super chase, dodge, 4x4"
            ),
            // Action Figures & Moedas
            ItemPreset(
                title = "Homem-Aranha Retro Marvel",
                name = "Homem-Aranha (Marvel Legends Retro Toy Biz)",
                category = "Action Figures",
                subCategory = "Marvel Legends",
                collection = "Marvel Legends Retro Card",
                itemNumber = "F0228",
                rarity = "Edição Especial Retro",
                variant = "Cartela Vintage Toy Biz",
                condition = "Lacrado na Cartela (Mint)",
                languageOrScale = "1:12 (6 polegadas)",
                defaultPaid = 160.0,
                defaultEstValue = 260.0,
                defaultStorage = "Nicho Parede Quarto",
                defaultNotes = "Mais de 30 pontos de articulação com mãos extras e teias intercambiáveis.",
                tags = "marvel, spiderman, hasbro, legends, retro"
            ),
            ItemPreset(
                title = "Funko Luffy Gear 5 Chase",
                name = "Funko Pop! Luffy Gear 5 #1607 (Glow in the Dark Chase)",
                category = "Action Figures",
                subCategory = "Funko Pop!",
                collection = "One Piece Animation",
                itemNumber = "#1607 Chase",
                rarity = "Chase 1:6",
                variant = "Brilha no Escuro (GITD)",
                condition = "Novo com Protetor Rígido UV",
                languageOrScale = "4 polegadas",
                defaultPaid = 190.0,
                defaultEstValue = 340.0,
                defaultStorage = "Estante Funko Sala",
                defaultNotes = "Selo Chase oficial da Funko com protetor Pop Shield UV.",
                tags = "funko, pop, one piece, luffy, gear 5, gitd, chase"
            ),
            ItemPreset(
                title = "Moeda 1 Real 50 Anos BCB",
                name = "Moeda 1 Real 50 Anos Banco Central (BCB)",
                category = "Moedas",
                subCategory = "Moedas do Brasil (Real)",
                collection = "Moedas Comemorativas do Real",
                itemNumber = "BCB-50-2015",
                rarity = "Comemorativa Escassa",
                variant = "Bimetálica Flor de Cunho",
                condition = "Flor de Cunho (FC)",
                languageOrScale = "Brasil - 2015",
                defaultPaid = 15.0,
                defaultEstValue = 65.0,
                defaultStorage = "Cápsula Acrílica / Álbum Numismático",
                defaultNotes = "Tiragem de 50 milhões de unidades em estado de conservação impecável Flor de Cunho.",
                tags = "moeda, real, bcb, 50 anos, flor de cunho"
            )
        )
    }

    // Quick Suggestions Chips for Fields
    val raritySuggestions = remember(category) {
        when (category) {
            "Trading Cards" -> listOf("Comum", "Incomum", "Rara Holo", "Ultra Rara", "Secret Rare", "Special Art (SIR)", "Illustration Rare", "Mítica Rara", "Quarter Century", "Manga Rare")
            "Carrinhos / Diecast" -> listOf("Básico / Mainline", "Super Treasure Hunt (STH)", "Treasure Hunt (TH)", "Premium Metal/Metal", "Red Line Club (RLC)", "Chase 1:24 / Raw", "Super Chase", "Edição Limitada")
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
        "Pasta 1 - Pág 1",
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
        purchasePriceText = String.format("%.2f", preset.defaultPaid).replace(",", ".")
        estimatedValueText = String.format("%.2f", preset.defaultEstValue).replace(",", ".")
        storageLocation = preset.defaultStorage
        notes = preset.defaultNotes
        tags = preset.tags
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
                            text = "Menus, Submenus & Preenchimento Inteligente",
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
            // --- 1. MODELOS DE PREENCHIMENTO RÁPIDO (ONE-TAP PRESETS) ---
            if (!isEditMode) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
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
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Modelos & Preenchimento Rápido",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Text(
                                    text = "1 Toque",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "Clique em um modelo para preencher todos os dados instantaneamente:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(smartPresets) { preset ->
                                    ElevatedSuggestionChip(
                                        onClick = { applyPreset(preset) },
                                        label = {
                                            Text(
                                                preset.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                if (preset.category == "Trading Cards") Icons.Default.Style
                                                else if (preset.category == "Carrinhos / Diecast") Icons.Default.DirectionsCar
                                                else if (preset.category == "Action Figures") Icons.Default.SmartToy
                                                else Icons.Default.MonetizationOn,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. MENU PRINCIPAL: SELEÇÃO DE CATEGORIA ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Menu Principal: Categoria do Item *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(mainCategories) { (catKey, catLabel, catColor) ->
                            val isSelected = category == catKey
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    category = catKey
                                    // Reset subcategory to first available in new category
                                    val newSubs = when (catKey) {
                                        "Trading Cards" -> "Pokémon TCG"
                                        "Carrinhos / Diecast" -> "Hot Wheels"
                                        "Action Figures" -> "Marvel Legends"
                                        "Moedas" -> "Moedas do Brasil (Real)"
                                        else -> "Quadrinhos & Mangás"
                                    }
                                    subCategory = newSubs
                                    if (catKey == "Carrinhos / Diecast") {
                                        language = "1:64"
                                    } else if (catKey == "Trading Cards") {
                                        language = "PT-BR"
                                    }
                                },
                                label = {
                                    Text(
                                        catLabel,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
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

            // --- 3. SUBMENU: JOGO / MARCA / FABRICANTE ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Submenu: Jogo / Marca / Fabricante *",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${subCategoryOptions.size} opções",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subCategoryOptions) { subOption ->
                            val isSelected = subCategory.equals(subOption, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { subCategory = subOption },
                                label = {
                                    Text(
                                        subOption,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }

                    // Campo de texto livre caso queira digitar subcategoria customizada
                    OutlinedTextField(
                        value = subCategory,
                        onValueChange = { subCategory = it },
                        label = { Text("Subcategoria Personalizada") },
                        placeholder = { Text("Ex: Pokémon TCG, Hot Wheels, Matchbox...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_subcategory"),
                        singleLine = true
                    )
                }
            }

            // --- 4. FOTO DO ITEM ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!imageUri.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Foto do Item",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { cameraLauncher.launch(null) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Trocar", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Galeria", fontSize = 12.sp)
                                }
                                TextButton(
                                    onClick = { imageUri = null },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remover")
                                }
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Adicionar Foto ou Card",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
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

            // --- 5. NOME PRINCIPAL DO ITEM ---
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Item / Carta / Carrinho *") },
                    placeholder = { Text("Ex: Charizard ex, Nissan Skyline GT-R BNR34...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_item_name"),
                    singleLine = true
                )
            }

            // --- 6. COLEÇÃO & NÚMERO ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = collection,
                        onValueChange = { collection = it },
                        label = { Text("Coleção / Série") },
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

            // --- 8. CONDIÇÃO & IDIOMA / ESCALA COM CHIPS SUGESTIVOS ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = condition,
                            onValueChange = { condition = it },
                            label = { Text("Estado de Conservação") },
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("input_condition"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = language,
                            onValueChange = { language = it },
                            label = { Text("Idioma / Escala") },
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("input_language"),
                            singleLine = true
                        )
                    }

                    // Sugestões de condição
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(conditionSuggestions) { c ->
                            SuggestionChip(
                                onClick = { condition = c },
                                label = { Text(c, fontSize = 11.sp) }
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
                    supportingText = { Text("Cotação média atual em marketplaces especializados") }
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
}

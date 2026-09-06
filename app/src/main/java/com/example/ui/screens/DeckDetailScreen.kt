package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.CollectorViewModel
import com.example.util.DeckCalculator
import com.example.util.OfficialCardImageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val activeDeckWithCards by viewModel.activeDeckWithCards.collectAsStateWithLifecycle()
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddCardModal by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showSampleHandModal by remember { mutableStateOf(false) }
    var showEditDeckDialog by remember { mutableStateOf(false) }

    if (activeDeckWithCards == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Deck não encontrado") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Selecione um deck na lista para visualizar.")
            }
        }
        return
    }

    val deckWithCards = activeDeckWithCards!!
    val deck = deckWithCards.deck
    val cards = deckWithCards.cards

    val analysis = remember(deck, cards, allItems) {
        DeckCalculator.analyzeDeck(deck, cards, allItems)
    }

    val rule = remember(deck.game, deck.format) {
        FormatRegistry.getRule(deck.game, deck.format)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = deck.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${deck.game} • ${deck.format}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSampleHandModal = true },
                        modifier = Modifier.testTag("btn_sample_hand")
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = "Mão Inicial", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.testTag("btn_export_deck")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Exportar Decklist")
                    }
                    IconButton(onClick = { showEditDeckDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurar Deck")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddCardModal = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Adicionar Carta") },
                modifier = Modifier.testTag("fab_add_card_to_deck")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("deck_detail_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === 1. Resumo de Cálculos do Deck ===
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Linha 1: Status de Validade do Formato
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
                                    imageVector = if (analysis.isLegalityValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (analysis.isLegalityValid) Color(0xFF16A34A) else Color(0xFFEA580C),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (analysis.isLegalityValid) "Deck Válido no Formato" else "Ajustes Necessários",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (analysis.isLegalityValid) Color(0xFF16A34A) else Color(0xFFEA580C)
                                )
                            }

                            Text(
                                text = "${analysis.totalCardsCount} / ${analysis.targetCountText}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Barra de Progresso do Tamanho do Deck
                        val progress = (analysis.totalCardsCount.toFloat() / rule.minCards.toFloat()).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (analysis.isCountValid) Color(0xFF16A34A) else Color(0xFFEA580C)
                        )

                        // Avisos de Legalidade se houver
                        if (analysis.countWarning != null || analysis.legalityIssues.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    analysis.countWarning?.let {
                                        Text(text = "• $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB45309), fontSize = 12.sp)
                                    }
                                    analysis.legalityIssues.forEach { issue ->
                                        Text(text = "• $issue", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB45309), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // === CALCULADORA DE CARTAS QUE VOCÊ TEM ===
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
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
                                        Icon(Icons.Default.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Text(
                                            "Cartas na Sua Coleção",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (analysis.completionPercentage == 100) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = "${analysis.completionPercentage}% Completo",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (analysis.completionPercentage == 100) Color(0xFF15803D) else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Você possui ${analysis.ownedCardsCount} de ${analysis.totalCardsCount} cartas deste deck",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                // Matriz de Valores
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Valor do Deck", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(currency.formatValue(analysis.totalDeckMarketValue), fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 13.sp)
                                    }
                                    Column {
                                        Text("Valor em Posse", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(currency.formatValue(analysis.ownedMarketValue), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Custo Faltante", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            if (analysis.missingCardsCount > 0) currency.formatValue(analysis.missingEstimatedCost) else "R$ 0,00",
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (analysis.missingCardsCount > 0) MaterialTheme.colorScheme.error else Color(0xFF15803D),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // === 2. Abas de Conteúdo ===
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Deck (${analysis.totalCardsCount})", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Faltantes", fontSize = 12.sp)
                                if (analysis.missingCardsCount > 0) {
                                    Badge { Text("${analysis.missingCardsCount}") }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Distribuição", fontSize = 12.sp) }
                    )
                }
            }

            // === 3. Conteúdo da Aba ===
            when (selectedTab) {
                0 -> {
                    // Deck Cards Grouped by Type
                    if (cards.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.LibraryAdd, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text("Deck Vazio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Adicione cartas da sua coleção ou busque no catálogo verificado.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(onClick = { showAddCardModal = true }) {
                                        Text("Adicionar Primeira Carta")
                                    }
                                }
                            }
                        }
                    } else {
                        val grouped = cards.groupBy { it.cardType }
                        grouped.forEach { (type, typeCards) ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$type (${typeCards.sumOf { it.quantity }})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            items(typeCards) { card ->
                                DeckCardRow(
                                    card = card,
                                    allItems = allItems,
                                    currency = currency,
                                    onIncrement = { viewModel.updateDeckCardQuantity(card, card.quantity + 1) },
                                    onDecrement = { viewModel.updateDeckCardQuantity(card, card.quantity - 1) },
                                    onRemove = { viewModel.removeDeckCard(card) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Missing Cards Tab
                    if (analysis.missingCards.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7).copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(44.dp))
                                    Text("Deck 100% em Posse!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    Text(
                                        "Você já possui todas as cartas necessárias deste deck na sua coleção cadastrada.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Lista de Compras / Faltantes",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            "${analysis.missingCardsCount} cartas a adquirir",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        currency.formatValue(analysis.missingEstimatedCost),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        items(analysis.missingCards) { missingCard ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("${missingCard.quantity}x")
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(missingCard.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${missingCard.collection} (${missingCard.itemNumber}) • ${missingCard.rarity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            currency.formatValue(missingCard.estimatedPriceBrl * missingCard.quantity),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        TextButton(
                                            onClick = {
                                                viewModel.insertWishlistItem(
                                                    name = missingCard.name,
                                                    category = "Trading Cards",
                                                    subCategory = deck.game,
                                                    targetMaxPrice = missingCard.estimatedPriceBrl,
                                                    priority = "ALTA",
                                                    notes = "Faltante para o deck ${deck.name} (${missingCard.collection} #${missingCard.itemNumber})"
                                                )
                                                Toast.makeText(context, "${missingCard.name} adicionado à Wishlist!", Toast.LENGTH_SHORT).show()
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Wishlist", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Distribution Tab
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Composição por Tipo de Carta", fontWeight = FontWeight.Bold)
                                analysis.cardTypeDistribution.forEach { (type, count) ->
                                    val pct = if (analysis.totalCardsCount > 0) (count.toFloat() / analysis.totalCardsCount.toFloat()) else 0f
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(type, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                            Text("$count cartas (${(pct * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        LinearProgressIndicator(
                                            progress = { pct },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Modal para Adicionar Cartas (Da Coleção ou do Catálogo)
    if (showAddCardModal) {
        AddCardToDeckModal(
            deck = deck,
            userItems = allItems,
            onDismiss = { showAddCardModal = false },
            onAddFromCollection = { item, qty ->
                viewModel.addCardFromCollectionToDeck(deck.id, item, qty)
                showAddCardModal = false
            },
            onAddFromCatalog = { entry, qty ->
                viewModel.addCardFromCatalogToDeck(deck.id, entry, qty)
                showAddCardModal = false
            }
        )
    }

    // Modal de Exportar Decklist
    if (showExportDialog) {
        val decklistText = remember(deck, cards) {
            DeckCalculator.exportDecklistText(deck, cards)
        }

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exportar Decklist", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Texto padrão formatado para torneios e simuladores (PTCGL, MTG Arena, YGOPRO):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = decklistText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Decklist", decklistText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Decklist copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Texto")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    // Modal de Mão Inicial (Sample Hand 7 cartas)
    if (showSampleHandModal) {
        SampleHandModal(
            cards = cards,
            onDismiss = { showSampleHandModal = false }
        )
    }

    // Modal de Editar Detalhes do Deck
    if (showEditDeckDialog) {
        EditDeckDetailsDialog(
            deck = deck,
            onDismiss = { showEditDeckDialog = false },
            onSave = { name, format, commander, desc, color ->
                viewModel.updateDeckDetails(deck, name, format, commander, desc, color)
                showEditDeckDialog = false
            }
        )
    }
}

@Composable
fun DeckCardRow(
    card: DeckCard,
    allItems: List<Item>,
    currency: AppCurrency,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    val matchingOwned = remember(card.name, allItems) {
        allItems.filter { it.isCard && (it.name.equals(card.name, true) || it.name.contains(card.name, true)) }
            .sumOf { it.quantity }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val imgUrl = remember(card.name, card.imageUrl) {
                if (card.imageUrl.isNotBlank()) card.imageUrl
                else OfficialCardImageHelper.getOfficialImageUrl(card.name, card.cardType, card.collection, card.itemNumber)
            }

            if (imgUrl.isNotBlank()) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = card.name,
                    modifier = Modifier
                        .size(46.dp, 64.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp, 64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Style, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${card.cardType}${if (card.subType.isNotBlank()) " • ${card.subType}" else ""} • ${card.rarity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (card.isBasicEnergyOrLand) {
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Text("Recurso", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    } else if (matchingOwned >= card.quantity) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                            Text("Na Coleção (${matchingOwned})", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEE2E2)) {
                            Text("Falta ${card.quantity - matchingOwned}", fontSize = 10.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }

                    Text(
                        currency.formatValue(card.estimatedPriceBrl * card.quantity),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            // Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Diminuir", modifier = Modifier.size(16.dp))
                }

                Text(
                    text = "${card.quantity}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )

                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardToDeckModal(
    deck: Deck,
    userItems: List<Item>,
    onDismiss: () -> Unit,
    onAddFromCollection: (Item, Int) -> Unit,
    onAddFromCatalog: (RealCatalogEntry, Int) -> Unit
) {
    var selectedSource by remember { mutableStateOf(0) } // 0: Minha Coleção, 1: Catálogo Completo
    var searchQuery by remember { mutableStateOf("") }

    val collectionCards = remember(userItems, searchQuery, deck.game) {
        val query = searchQuery.trim().lowercase()
        userItems.filter { item ->
            item.isCard &&
            (query.isBlank() || item.name.lowercase().contains(query) || item.collection.lowercase().contains(query))
        }
    }

    val catalogCards = remember(searchQuery, deck.game) {
        val query = searchQuery.trim().lowercase()
        RealMarketCatalog.allEntries.filter { entry ->
            val matchesGame = when {
                deck.game.contains("Pokémon", true) -> entry.subCategory.contains("Pokémon", true)
                deck.game.contains("Magic", true) -> entry.subCategory.contains("Magic", true)
                deck.game.contains("Yu-Gi-Oh", true) -> entry.subCategory.contains("Yu-Gi-Oh", true)
                deck.game.contains("One Piece", true) -> entry.subCategory.contains("One Piece", true)
                else -> true
            }
            matchesGame && (query.isBlank() || entry.name.lowercase().contains(query) || entry.collection.lowercase().contains(query))
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
                    Text("Adicionar Carta ao Deck", fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
                TabRow(selectedTabIndex = selectedSource) {
                    Tab(
                        selected = selectedSource == 0,
                        onClick = { selectedSource = 0 },
                        text = { Text("Da Minha Coleção (${collectionCards.size})", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedSource == 1,
                        onClick = { selectedSource = 1 },
                        text = { Text("Do Catálogo Geral", fontSize = 12.sp) }
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp, max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nome da carta...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (selectedSource == 0) {
                    if (collectionCards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (searchQuery.isBlank()) "Nenhuma carta cadastrada na sua coleção ainda."
                                else "Nenhuma carta encontrada para '$searchQuery'.",
                                style = MaterialTheme.typography.bodySmall,
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
                            items(collectionCards) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${item.subCategory} • Possui ${item.quantity} cópias",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Button(
                                            onClick = { onAddFromCollection(item, 1) },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Adicionar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (catalogCards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Nenhuma carta do catálogo encontrada para '$searchQuery'.",
                                style = MaterialTheme.typography.bodySmall,
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
                            items(catalogCards) { entry ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(entry.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${entry.collection} (${entry.itemNumber}) • R$ ${"%.2f".format(entry.realMarketPriceBrl)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Button(
                                            onClick = { onAddFromCatalog(entry, 1) },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Adicionar")
                                        }
                                    }
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

@Composable
fun SampleHandModal(
    cards: List<DeckCard>,
    onDismiss: () -> Unit
) {
    // Flatten cards by quantity
    val fullDeck = remember(cards) {
        val list = mutableListOf<DeckCard>()
        cards.forEach { card ->
            repeat(card.quantity) {
                list.add(card)
            }
        }
        list
    }

    var drawnHand by remember { mutableStateOf<List<DeckCard>>(emptyList()) }

    fun drawSeven() {
        val shuffled = fullDeck.shuffled()
        drawnHand = shuffled.take(7)
    }

    LaunchedEffect(Unit) {
        drawSeven()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Casino, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Simulador de Mão Inicial", fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp, max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Mão de 7 cartas compradas aleatoriamente do seu deck:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (fullDeck.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("Adicione cartas ao deck para testar a mão inicial.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(drawnHand) { card ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Style, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(card.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${card.cardType} • ${card.rarity}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { drawSeven() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mulligan (Comprar 7)")
                    }
                    OutlinedButton(
                        onClick = {
                            if (drawnHand.size < fullDeck.size) {
                                val remaining = fullDeck - drawnHand.toSet()
                                if (remaining.isNotEmpty()) {
                                    drawnHand = drawnHand + remaining.shuffled().first()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Comprar +1")
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

@Composable
fun EditDeckDetailsDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onSave: (name: String, format: String, commander: String, desc: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf(deck.name) }
    var selectedFormat by remember { mutableStateOf(deck.format) }
    var commander by remember { mutableStateOf(deck.commanderOrLeader) }
    var description by remember { mutableStateOf(deck.description) }

    val formats = remember(deck.game) { FormatRegistry.getFormatsForGame(deck.game) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurações do Deck", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Deck") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Formato de Jogo:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(formats) { f ->
                        FilterChip(
                            selected = selectedFormat == f.name,
                            onClick = { selectedFormat = f.name },
                            label = { Text(f.name, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = commander,
                    onValueChange = { commander = it },
                    label = { Text("Comandante / Líder") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Estratégia / Notas") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedFormat, commander, description, deck.themeColorHex) },
                enabled = name.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

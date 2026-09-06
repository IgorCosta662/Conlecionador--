package com.example.ui.screens

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
import com.example.ui.Routes
import com.example.util.DeckCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksListScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val allDecksWithCards by viewModel.allDecksWithCards.collectAsStateWithLifecycle()
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var selectedGameFilter by remember { mutableStateOf("Todos") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showTemplatesDialog by remember { mutableStateOf(false) }

    val gameFilters = listOf("Todos", "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game")

    val filteredDecks = remember(allDecksWithCards, selectedGameFilter) {
        if (selectedGameFilter == "Todos") {
            allDecksWithCards
        } else {
            allDecksWithCards.filter { it.deck.game.contains(selectedGameFilter, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.DashboardCustomize, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Deck Builder & Formatos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                "Monte decks, valide regras e calcule suas cartas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showTemplatesDialog = true }) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Importar Modelo", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Criar Novo Deck") },
                modifier = Modifier.testTag("fab_create_deck")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("decks_list_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Hero Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    )
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
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = Color.White)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Calculadora de Decks & Coleção",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Cruza as cartas do seu inventário físico com listas de torneio para calcular % de completude e custo faltante.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showTemplatesDialog = true },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ver Decks Prontos", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { showCreateDialog = true },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Criar do Zero", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Game Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(gameFilters) { filter ->
                        FilterChip(
                            selected = selectedGameFilter == filter,
                            onClick = { selectedGameFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) }
                        )
                    }
                }
            }

            if (filteredDecks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Style,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Text(
                                "Nenhum deck encontrado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Crie um novo deck do zero ou carregue um formato competitivo pronto!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(onClick = { showTemplatesDialog = true }) {
                                Icon(Icons.Default.LibraryAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Carregar Decks Recomendados")
                            }
                        }
                    }
                }
            } else {
                items(filteredDecks) { deckWithCards ->
                    val analysis = remember(deckWithCards, allItems) {
                        DeckCalculator.analyzeDeck(deckWithCards.deck, deckWithCards.cards, allItems)
                    }

                    DeckCardItem(
                        deckWithCards = deckWithCards,
                        analysis = analysis,
                        currency = currency,
                        onClick = {
                            viewModel.selectDeck(deckWithCards.deck.id)
                            navController.navigate(Routes.DECK_BUILDER)
                        },
                        onDelete = {
                            viewModel.deleteDeck(deckWithCards.deck.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    if (showCreateDialog) {
        CreateDeckDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, game, format, commander, desc, color ->
                viewModel.createDeck(name, game, format, commander, desc, color) {
                    showCreateDialog = false
                    navController.navigate(Routes.DECK_BUILDER)
                }
            }
        )
    }

    if (showTemplatesDialog) {
        StarterDecksModal(
            onDismiss = { showTemplatesDialog = false },
            onSelectTemplate = { template ->
                viewModel.createDeckFromTemplate(template) {
                    showTemplatesDialog = false
                    navController.navigate(Routes.DECK_BUILDER)
                }
            }
        )
    }
}

@Composable
fun DeckCardItem(
    deckWithCards: DeckWithCards,
    analysis: DeckInventoryAnalysis,
    currency: AppCurrency,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val deck = deckWithCards.deck
    var showMenu by remember { mutableStateOf(false) }

    val accentColor = remember(deck.themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(deck.themeColorHex))
        } catch (_: Exception) {
            Color(0xFF3B82F6)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("deck_item_${deck.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Color accent bar on top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title and badges row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = deck.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "${deck.game} • ${deck.format}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opções")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Abrir & Editar") },
                                onClick = {
                                    showMenu = false
                                    onClick()
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Excluir Deck", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }

                if (deck.commanderOrLeader.isNotBlank()) {
                    Surface(
                        color = accentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Comandante / Líder: ${deck.commanderOrLeader}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }

                // Cards Count & Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total de Cartas: ${analysis.totalCardsCount} (${analysis.targetCountText})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        if (analysis.isLegalityValid) {
                            Text(
                                text = "✓ Válido no Formato",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        } else {
                            Text(
                                text = "Ajustes Necessários",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEA580C)
                            )
                        }
                    }

                    val rule = FormatRegistry.getRule(deck.game, deck.format)
                    val cardProgress = (analysis.totalCardsCount.toFloat() / rule.minCards.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { cardProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (analysis.isLegalityValid) Color(0xFF16A34A) else Color(0xFFEA580C)
                    )
                }

                // Inventory & Collection Calculation Highlight
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                                text = "Na sua coleção:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${analysis.ownedCardsCount}/${analysis.totalCardsCount} cartas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (analysis.completionPercentage == 100) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "${analysis.completionPercentage}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (analysis.completionPercentage == 100) Color(0xFF15803D) else Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Valor de Mercado:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currency.formatValue(analysis.totalDeckMarketValue),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32)
                            )
                            if (analysis.missingCardsCount > 0) {
                                Text(
                                    text = "Falta: ${currency.formatValue(analysis.missingEstimatedCost)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, game: String, format: String, commander: String, desc: String, color: String) -> Unit
) {
    var deckName by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf("Pokémon TCG") }
    var selectedFormat by remember { mutableStateOf("Padrão (Standard)") }
    var commanderOrLeader by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#3B82F6") }

    val availableFormats = remember(selectedGame) {
        FormatRegistry.getFormatsForGame(selectedGame)
    }

    LaunchedEffect(selectedGame) {
        selectedFormat = availableFormats.firstOrNull()?.name ?: "Padrão"
    }

    val currentRule = remember(selectedGame, selectedFormat) {
        FormatRegistry.getRule(selectedGame, selectedFormat)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criar Novo Deck", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Nome do Deck") },
                    placeholder = { Text("Ex: Charizard ex Tera, Burn Mono Red") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Game selection
                Text("Jogo / Franquia:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                val games = listOf("Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(games) { g ->
                        FilterChip(
                            selected = selectedGame == g,
                            onClick = { selectedGame = g },
                            label = { Text(g, fontSize = 11.sp) }
                        )
                    }
                }

                // Format selection
                Text("Formato de Jogo:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(availableFormats) { f ->
                        FilterChip(
                            selected = selectedFormat == f.name,
                            onClick = { selectedFormat = f.name },
                            label = { Text(f.name, fontSize = 11.sp) }
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Regra do Formato: ${currentRule.description}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                if (currentRule.hasCommanderOrLeader) {
                    OutlinedTextField(
                        value = commanderOrLeader,
                        onValueChange = { commanderOrLeader = it },
                        label = { Text("Nome do Comandante / Líder") },
                        placeholder = { Text("Ex: The Thing, Ben Grimm ou Luffy") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Estratégia / Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (deckName.isNotBlank()) {
                        onCreate(deckName.trim(), selectedGame, selectedFormat, commanderOrLeader.trim(), description.trim(), selectedColor)
                    }
                },
                enabled = deckName.isNotBlank()
            ) {
                Text("Criar Deck")
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
fun StarterDecksModal(
    onDismiss: () -> Unit,
    onSelectTemplate: (Pair<Deck, List<DeckCard>>) -> Unit
) {
    val templates = remember { DeckTemplates.getStarterDecks() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Decks Recomendados", fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
                Text(
                    "Importe listas de torneios de vários formatos para estudar regras e calcular sua coleção",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp, max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(templates) { template ->
                    val deck = template.first
                    val cards = template.second
                    Card(
                        onClick = { onSelectTemplate(template) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (deck.coverImageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = deck.coverImageUrl,
                                    contentDescription = deck.name,
                                    modifier = Modifier
                                        .size(50.dp, 70.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp, 70.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Style, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deck.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${deck.game} • ${deck.format}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${cards.sumOf { it.quantity }} cartas • Custo estimado: R$ ${"%.2f".format(cards.sumOf { it.estimatedPriceBrl * it.quantity })}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = deck.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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

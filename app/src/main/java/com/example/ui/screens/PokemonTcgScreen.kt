package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.util.OfficialCardImageHelper
import com.example.ui.ViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonTcgScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val currentViewMode by viewModel.viewMode.collectAsStateWithLifecycle()

    val pokemonCards = remember(items) {
        items.filter { it.subCategory.contains("Pokémon", ignoreCase = true) || it.name.contains("Pokémon", ignoreCase = true) }
    }

    var selectedSetFilter by remember { mutableStateOf("TODOS") }
    var selectedRarityFilter by remember { mutableStateOf("TODOS") }
    var selectedLanguageFilter by remember { mutableStateOf("TODOS") }
    var selectedVariantFilter by remember { mutableStateOf("TODOS") }
    var selectedConditionFilter by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredList = remember(
        pokemonCards,
        selectedSetFilter,
        selectedRarityFilter,
        selectedLanguageFilter,
        selectedVariantFilter,
        selectedConditionFilter,
        searchQuery
    ) {
        pokemonCards.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.itemNumber.contains(searchQuery, ignoreCase = true) ||
                    item.collection.contains(searchQuery, ignoreCase = true)

            val matchesSet = selectedSetFilter == "TODOS" || item.collection.contains(selectedSetFilter, ignoreCase = true)
            val matchesRarity = selectedRarityFilter == "TODOS" || item.rarity.contains(selectedRarityFilter, ignoreCase = true)
            val matchesLanguage = selectedLanguageFilter == "TODOS" || item.language.equals(selectedLanguageFilter, ignoreCase = true)
            val matchesVariant = when (selectedVariantFilter) {
                "TODOS" -> true
                "Holo" -> item.variant.contains("Holo", ignoreCase = true) || item.variant.contains("Foil", ignoreCase = true)
                "Reverse Holo" -> item.variant.contains("Reverse", ignoreCase = true)
                "Promo" -> item.variant.contains("Promo", ignoreCase = true) || item.rarity.contains("Promo", ignoreCase = true)
                else -> item.variant.contains(selectedVariantFilter, ignoreCase = true)
            }
            val matchesCondition = selectedConditionFilter == "TODOS" || item.condition.contains(selectedConditionFilter, ignoreCase = true)

            matchesQuery && matchesSet && matchesRarity && matchesLanguage && matchesVariant && matchesCondition
        }
    }

    val totalCards = pokemonCards.sumOf { it.quantity }
    val totalValue = pokemonCards.sumOf { it.totalEstimatedValue }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFFBBF24))
                        Text("Pokémon TCG", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // View Mode Switcher
                    IconButton(onClick = {
                        val nextMode = when (currentViewMode) {
                            ViewMode.GRID -> ViewMode.LIST
                            ViewMode.LIST -> ViewMode.COMPACT
                            ViewMode.COMPACT -> ViewMode.GRID
                        }
                        viewModel.setViewMode(nextMode)
                    }) {
                        Icon(
                            when (currentViewMode) {
                                ViewMode.GRID -> Icons.Default.GridView
                                ViewMode.LIST -> Icons.Default.ViewList
                                ViewMode.COMPACT -> Icons.Default.TableRows
                            },
                            contentDescription = "Mudar Visualização"
                        )
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("pokemon_tcg_screen")
        ) {
            // Header stats
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF3C7)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total de cartas:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF92400E))
                        Text(
                            "$totalCards cartas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF78350F)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor da coleção:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF92400E))
                        Text(
                            currency.formatValue(totalValue),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            // Search Bar & Filter Chips
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar nome, número (#151), coleção...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Filter Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedVariantFilter == "Holo",
                        onClick = { selectedVariantFilter = if (selectedVariantFilter == "Holo") "TODOS" else "Holo" },
                        label = { Text("Holo") }
                    )
                    FilterChip(
                        selected = selectedVariantFilter == "Reverse Holo",
                        onClick = { selectedVariantFilter = if (selectedVariantFilter == "Reverse Holo") "TODOS" else "Reverse Holo" },
                        label = { Text("Reverse Holo") }
                    )
                    FilterChip(
                        selected = selectedVariantFilter == "Promo",
                        onClick = { selectedVariantFilter = if (selectedVariantFilter == "Promo") "TODOS" else "Promo" },
                        label = { Text("Promo") }
                    )
                }
            }

            // Content Grid / List
            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
                        Text("Nenhuma carta encontrada com os filtros selecionados", color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                when (currentViewMode) {
                    ViewMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList, key = { it.id }) { item ->
                                PokemonCardGridItem(
                                    item = item,
                                    currency = currency,
                                    onClick = {
                                        viewModel.selectedItem.value = item
                                        navController.navigate(Routes.ITEM_DETAIL)
                                    }
                                )
                            }
                        }
                    }
                    ViewMode.LIST, ViewMode.COMPACT -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredList, key = { it.id }) { item ->
                                PokemonCardListItem(
                                    item = item,
                                    currency = currency,
                                    onClick = {
                                        viewModel.selectedItem.value = item
                                        navController.navigate(Routes.ITEM_DETAIL)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filtros Avançados Pokémon", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Idioma da Carta", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS", "PT-BR", "EN", "JP").forEach { lang ->
                        FilterChip(
                            selected = selectedLanguageFilter == lang,
                            onClick = { selectedLanguageFilter = lang },
                            label = { Text(if (lang == "TODOS") "Todos" else lang) }
                        )
                    }
                }

                Text("Coleção / Expansão", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS", "151", "Paldea", "Obsidian", "Crown Zenith").forEach { set ->
                        FilterChip(
                            selected = selectedSetFilter == set,
                            onClick = { selectedSetFilter = set },
                            label = { Text(set) }
                        )
                    }
                }

                Text("Condição", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS", "Mint", "Near Mint", "Played").forEach { cond ->
                        FilterChip(
                            selected = selectedConditionFilter == cond,
                            onClick = { selectedConditionFilter = cond },
                            label = { Text(cond) }
                        )
                    }
                }

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aplicar Filtros (${filteredList.size} cartas)")
                }
            }
        }
    }
}

@Composable
fun PokemonCardGridItem(
    item: Item,
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    val resolvedImageUrl = remember(item.imageUri, item.name, item.subCategory, item.collection, item.itemNumber) {
        if (!item.imageUri.isNullOrBlank()) {
            item.imageUri
        } else {
            OfficialCardImageHelper.getOfficialImageUrl(item.name, item.subCategory, item.collection, item.itemNumber)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("pokemon_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                if (!resolvedImageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(resolvedImageUrl),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(36.dp))
                        Text(item.subCategory, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    }
                }

                // Number badge
                if (item.itemNumber.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = item.itemNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.languageFlag} ${item.languageDisplayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.rarity,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currency.formatValue(item.estimatedValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (item.quantity > 1) {
                        Text(
                            text = "x${item.quantity}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonCardListItem(
    item: Item,
    currency: com.example.data.AppCurrency,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    "${item.collection} • #${item.itemNumber} • [${item.language}]",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${item.rarity} | ${item.condition}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD97706)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    currency.formatValue(item.estimatedValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Qtd: ${item.quantity}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

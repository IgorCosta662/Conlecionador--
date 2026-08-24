package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import com.example.util.OfficialCardImageHelper

enum class ChecklistFilter(val title: String) {
    ALL("Todas"),
    MISSING("Faltam na Coleção"),
    OWNED("Já Possuo")
}

enum class MissingSortOption(val title: String) {
    NUMBER("Número do Set"),
    PRICE_LOW("Mais Baratas"),
    PRICE_HIGH("Mais Caras"),
    RARITY("Mais Raras")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetChecklistScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val allItems by viewModel.allItems.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val sets = SetRegistry.popularSets

    var selectedSetId by remember { mutableStateOf(sets.first().id) }
    var selectedFilter by remember { mutableStateOf(ChecklistFilter.MISSING) }
    var selectedSort by remember { mutableStateOf(MissingSortOption.NUMBER) }
    var searchQuery by remember { mutableStateOf("") }

    var selectedFranchiseFilter by remember { mutableStateOf("TODOS") }
    var selectedEraFilter by remember { mutableStateOf("TODAS") }

    val currentSet = sets.firstOrNull { it.id == selectedSetId } ?: sets.first()

    val franchises = remember(sets) {
        listOf("TODOS") + sets.map { it.franchise }.distinct().sorted()
    }

    val eras = remember(sets, selectedFranchiseFilter) {
        val filtered = if (selectedFranchiseFilter == "TODOS") sets else sets.filter { it.franchise == selectedFranchiseFilter }
        listOf("TODAS") + filtered.map { if (it.era.isNotBlank()) it.era else it.year }.distinct().sortedDescending()
    }

    val availableSetsForPicker = remember(sets, selectedFranchiseFilter, selectedEraFilter, searchQuery) {
        sets.filter { set ->
            val matchFranchise = selectedFranchiseFilter == "TODOS" || set.franchise == selectedFranchiseFilter
            val matchEra = selectedEraFilter == "TODAS" || set.era == selectedEraFilter || set.year == selectedEraFilter
            matchFranchise && matchEra
        }
    }

    // Calculate progress for current set
    val ownedInSet = remember(allItems, currentSet) {
        allItems.filter { item ->
            item.collection.contains(currentSet.name, ignoreCase = true) ||
            currentSet.name.contains(item.collection, ignoreCase = true) ||
            (currentSet.code.isNotBlank() && item.collection.contains(currentSet.code, ignoreCase = true)) ||
            (currentSet.code.isNotBlank() && item.notes.contains(currentSet.code, ignoreCase = true)) ||
            currentSet.items.any { s -> s.name.equals(item.name, ignoreCase = true) }
        }
    }

    val ownedNames = remember(ownedInSet) {
        ownedInSet.map { it.name.lowercase().trim() }.toSet()
    }

    val ownedNumbers = remember(ownedInSet) {
        ownedInSet.mapNotNull { if (it.itemNumber.isNotBlank()) it.itemNumber.lowercase().trim() else null }.toSet()
    }

    fun isCardOwned(card: SetCardItem): Boolean {
        val cardNumClean = card.number.lowercase().trim()
        val cardNameClean = card.name.lowercase().trim()
        return ownedNumbers.contains(cardNumClean) ||
               ownedInSet.any { it.name.lowercase().contains(cardNameClean) || cardNameClean.contains(it.name.lowercase()) }
    }

    val ownedCount = currentSet.items.count { isCardOwned(it) }
    val totalCount = currentSet.totalItems
    val missingCount = (totalCount - ownedCount).coerceAtLeast(0)
    val progressPercent = if (totalCount > 0) (ownedCount.toDouble() / totalCount.toDouble()) * 100.0 else 0.0

    // Missing items calculation
    val missingCards = currentSet.items.filterNot { isCardOwned(it) }
    val estimatedCostToComplete = missingCards.sumOf { it.estimatedPriceBrl }

    // Filter and sort items to display
    val displayCards = remember(currentSet, selectedFilter, selectedSort, searchQuery, ownedInSet) {
        var list = when (selectedFilter) {
            ChecklistFilter.ALL -> currentSet.items
            ChecklistFilter.MISSING -> currentSet.items.filterNot { isCardOwned(it) }
            ChecklistFilter.OWNED -> currentSet.items.filter { isCardOwned(it) }
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase().trim()
            list = list.filter { it.name.lowercase().contains(q) || it.number.lowercase().contains(q) || it.rarity.lowercase().contains(q) }
        }

        when (selectedSort) {
            MissingSortOption.NUMBER -> list
            MissingSortOption.PRICE_LOW -> list.sortedBy { it.estimatedPriceBrl }
            MissingSortOption.PRICE_HIGH -> list.sortedByDescending { it.estimatedPriceBrl }
            MissingSortOption.RARITY -> list.sortedByDescending { it.rarity }
        }
    }

    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("O Que Falta na Coleção", fontWeight = FontWeight.Bold)
                        Text("Checklist Inteligente de Sets", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.WISHLIST) }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Lista de Desejos")
                    }
                }
            )
        },
        snackbarHost = {
            snackbarMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Set Selector Chips & Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Filtrar por Franquia & Era / Ano:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Franchise Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(franchises) { franchise ->
                            FilterChip(
                                selected = selectedFranchiseFilter == franchise,
                                onClick = {
                                    selectedFranchiseFilter = franchise
                                    selectedEraFilter = "TODAS"
                                    val firstMatch = sets.firstOrNull { franchise == "TODOS" || it.franchise == franchise }
                                    if (firstMatch != null) selectedSetId = firstMatch.id
                                },
                                label = { Text(if (franchise == "TODOS") "🌟 Todas Franquias" else franchise, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Era Row if available
                    if (eras.size > 2) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(eras) { era ->
                                FilterChip(
                                    selected = selectedEraFilter == era,
                                    onClick = {
                                        selectedEraFilter = era
                                        val firstMatch = availableSetsForPicker.firstOrNull { era == "TODAS" || it.era == era || it.year == era }
                                        if (firstMatch != null) selectedSetId = firstMatch.id
                                    },
                                    label = { Text(if (era == "TODAS") "📅 Todas as Eras" else "📅 $era", fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Text(
                        text = "2. Escolha o Set / Coleção (${availableSetsForPicker.size} disponíveis):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableSetsForPicker) { set ->
                            val isSelected = set.id == selectedSetId
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSetId = set.id },
                                label = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (set.code.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = set.code,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = set.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        when (set.iconCategory) {
                                            "diecast" -> Icons.Default.DirectionsCar
                                            "pokemon" -> Icons.Default.Style
                                            "magic" -> Icons.Default.AutoAwesome
                                            else -> Icons.Default.Category
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Set Header Card with Completion and Cost
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(currentSet.bannerColor).copy(alpha = 0.12f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(currentSet.bannerColor).copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSet.franchise,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(currentSet.bannerColor),
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = currentSet.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ano: ${currentSet.year} • ${currentSet.totalItems} itens totais",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentSet.bannerColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${progressPercent.toInt()}%",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (progressPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(currentSet.bannerColor),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Possui", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("$ownedCount / $totalCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                            Column {
                                Text("Faltam", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("$missingCount itens", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Custo p/ Completar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    currency.formatValue(estimatedCostToComplete),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Text(
                            text = currentSet.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Filter & Search Controls
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar nome, número (#001) ou raridade...") },
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

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        ChecklistFilter.values().forEachIndexed { index, filter ->
                            SegmentedButton(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = ChecklistFilter.values().size)
                            ) {
                                Text(
                                    when (filter) {
                                        ChecklistFilter.ALL -> "Todas (${currentSet.items.size})"
                                        ChecklistFilter.MISSING -> "Faltam ($missingCount)"
                                        ChecklistFilter.OWNED -> "Tenho ($ownedCount)"
                                    },
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Sort row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ordenar por:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MissingSortOption.values().forEach { sort ->
                                FilterChip(
                                    selected = selectedSort == sort,
                                    onClick = { selectedSort = sort },
                                    label = { Text(sort.title, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Cards Checklist List
            items(displayCards) { card ->
                val isOwned = isCardOwned(card)
                val ownedItem = ownedInSet.firstOrNull {
                    it.itemNumber.equals(card.number, ignoreCase = true) ||
                    it.name.equals(card.name, ignoreCase = true)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            if (ownedItem != null) {
                                viewModel.selectItem(ownedItem)
                                navController.navigate(Routes.ITEM_DETAIL)
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOwned) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isOwned) Color(0xFF16A34A).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Checkbox or Status Icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isOwned) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isOwned) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isOwned) "Possui" else "Falta",
                                tint = if (isOwned) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Card / Pokémon Image Preview Thumbnail
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 58.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E1E2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            val cardImageUrl = OfficialCardImageHelper.getOfficialImageUrl(
                                name = card.name,
                                subCategory = currentSet.franchise,
                                collection = currentSet.name,
                                itemNumber = card.number
                            )
                            AsyncImage(
                                model = cardImageUrl,
                                contentDescription = card.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(2.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = if (card.setCode.isNotBlank()) "${card.setCode} ${card.number}" else card.number,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = card.rarity,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                if (card.patternVariant != "Standard" && card.patternVariant.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = card.patternVariant,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFD97706),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = card.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (isOwned && ownedItem != null) {
                                Text(
                                    text = "Na coleção: ${ownedItem.condition} • ${ownedItem.storageLocation.ifBlank { "Sem local físico" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF16A34A)
                                )
                            } else {
                                Text(
                                    text = "Cotação estimada: ${currency.formatValue(card.estimatedPriceBrl)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Action Button
                        if (!isOwned) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val newItem = Item(
                                            name = card.name,
                                            type = if (currentSet.franchise.contains("Hot Wheels")) "Carrinhos / Diecast" else "Trading Cards",
                                            subCategory = currentSet.franchise,
                                            collection = currentSet.name,
                                            itemNumber = card.number,
                                            rarity = card.rarity,
                                            condition = "Near Mint (NM)",
                                            estimatedValue = card.estimatedPriceBrl,
                                            purchasePrice = card.estimatedPriceBrl * 0.7,
                                            quantity = 1,
                                            storageLocation = "Pasta ${currentSet.name}",
                                            notes = "Adicionado pelo Checklist oficial ${currentSet.name} [${currentSet.code}].",
                                            tags = "${currentSet.franchise}, ${currentSet.name}, ${card.name}, ${card.patternVariant}".lowercase(),
                                            variant = card.variant,
                                            language = card.language,
                                            year = currentSet.year,
                                            imageUri = OfficialCardImageHelper.getOfficialImageUrl(card.name, currentSet.franchise, currentSet.name, card.number)
                                        )
                                        viewModel.insertItem(newItem)
                                        snackbarMessage = "✓ ${card.name} adicionado à sua coleção!"
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AddCircle,
                                        contentDescription = "Adicionar à Coleção",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                FilledTonalButton(
                                    onClick = {
                                        viewModel.insertWishlistItem(
                                            name = "${card.name} (${card.number})",
                                            category = if (currentSet.franchise.contains("Hot Wheels")) "Diecast" else "Cards",
                                            subCategory = currentSet.name,
                                            targetMaxPrice = card.estimatedPriceBrl,
                                            priority = if (card.estimatedPriceBrl > 100) "Alta" else "Média",
                                            notes = "Adicionado pelo checklist do set ${currentSet.name}"
                                        )
                                        snackbarMessage = "${card.name} adicionado à Lista de Desejos!"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Desejo", fontSize = 10.sp)
                                }
                            }
                        } else {
                            IconButton(onClick = {
                                if (ownedItem != null) {
                                    viewModel.selectItem(ownedItem)
                                    navController.navigate(Routes.ITEM_DETAIL)
                                }
                            }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Ver Detalhes", tint = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}

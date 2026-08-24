package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.api.ScryfallCard
import com.example.api.TcgdexCardBrief
import com.example.data.Item
import com.example.ui.CollectorViewModel
import com.example.ui.Routes

enum class SearchScope(val title: String) {
    LOCAL("Minha Coleção"),
    TCGDEX("Pokémon (TCGDex API)"),
    SCRYFALL("Magic (Scryfall API)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
    viewModel: CollectorViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    var searchInput by remember { mutableStateOf("") }
    var selectedScope by remember { mutableStateOf(SearchScope.LOCAL) }

    val isSearchingOnline by viewModel.isOnlineSearching.collectAsStateWithLifecycle()
    val scryfallResults by viewModel.scryfallOnlineResults.collectAsStateWithLifecycle()
    val tcgdexResults by viewModel.tcgdexOnlineResults.collectAsStateWithLifecycle()
    val searchMessage by viewModel.onlineSearchMessage.collectAsStateWithLifecycle()

    var importedFeedback by remember { mutableStateOf<String?>(null) }

    val localFiltered = remember(items, searchInput) {
        val q = searchInput.trim().lowercase()
        if (q.isBlank()) {
            emptyList()
        } else {
            items.filter { item ->
                item.name.lowercase().contains(q) ||
                item.subCategory.lowercase().contains(q) ||
                item.collection.lowercase().contains(q) ||
                item.itemNumber.lowercase().contains(q) ||
                item.rarity.lowercase().contains(q) ||
                item.type.lowercase().contains(q)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .testTag("global_search_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.TravelExplore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Busca Global & APIs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Scope Tab selector
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SearchScope.entries.forEachIndexed { index, scope ->
                        SegmentedButton(
                            selected = selectedScope == scope,
                            onClick = {
                                selectedScope = scope
                                if (searchInput.isNotBlank()) {
                                    when (scope) {
                                        SearchScope.TCGDEX -> viewModel.searchTcgdexOnline(searchInput)
                                        SearchScope.SCRYFALL -> viewModel.searchScryfallOnline(searchInput)
                                        SearchScope.LOCAL -> {}
                                    }
                                }
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = SearchScope.entries.size)
                        ) {
                            Text(scope.title, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        if (it.length >= 3) {
                            when (selectedScope) {
                                SearchScope.TCGDEX -> viewModel.searchTcgdexOnline(it)
                                SearchScope.SCRYFALL -> viewModel.searchScryfallOnline(it)
                                SearchScope.LOCAL -> {}
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input"),
                    placeholder = {
                        Text(
                            when (selectedScope) {
                                SearchScope.LOCAL -> "Buscar na sua coleção..."
                                SearchScope.TCGDEX -> "Buscar Pokémon em TCGDex (ex: Pikachu, Charizard)..."
                                SearchScope.SCRYFALL -> "Buscar Magic em Scryfall (ex: Black Lotus, Sol Ring)..."
                            }
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (isSearchingOnline) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else if (searchInput.isNotEmpty()) {
                            IconButton(onClick = {
                                searchInput = ""
                                viewModel.clearOnlineSearchResults()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                // Feedback banner
                if (importedFeedback != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            Text(importedFeedback ?: "", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content according to scope
                when (selectedScope) {
                    SearchScope.LOCAL -> {
                        if (searchInput.isBlank()) {
                            EmptySearchPlaceholder("Digite para pesquisar em toda a sua coleção")
                        } else if (localFiltered.isEmpty()) {
                            EmptySearchPlaceholder("Nenhum colecionável encontrado com '$searchInput'")
                        } else {
                            Text(
                                text = "Resultados na coleção (${localFiltered.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(localFiltered, key = { it.id }) { item ->
                                    SearchResultRow(
                                        item = item,
                                        currency = currency,
                                        onClick = {
                                            viewModel.selectedItem.value = item
                                            onDismiss()
                                            navController.navigate(Routes.ITEM_DETAIL)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SearchScope.TCGDEX -> {
                        if (searchInput.isBlank()) {
                            EmptySearchPlaceholder("Pesquise online em tempo real no banco oficial de Pokémon TCG (TCGDex SDK/API)")
                        } else if (tcgdexResults.isEmpty() && !isSearchingOnline) {
                            EmptySearchPlaceholder(searchMessage ?: "Nenhuma carta de Pokémon encontrada no TCGDex")
                        } else {
                            Text(
                                text = "Cartas Oficiais TCGDex API (${tcgdexResults.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEAB308)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(tcgdexResults, key = { it.id }) { card ->
                                    TcgdexResultRow(
                                        card = card,
                                        onImport = {
                                            viewModel.importTcgdexCardToCollection(card) { savedItem ->
                                                importedFeedback = "Adicionado à coleção: ${savedItem.name}!"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SearchScope.SCRYFALL -> {
                        if (searchInput.isBlank()) {
                            EmptySearchPlaceholder("Pesquise online em tempo real no banco oficial de Magic: The Gathering (Scryfall API)")
                        } else if (scryfallResults.isEmpty() && !isSearchingOnline) {
                            EmptySearchPlaceholder(searchMessage ?: "Nenhuma carta de Magic encontrada no Scryfall")
                        } else {
                            Text(
                                text = "Cartas Oficiais Scryfall API (${scryfallResults.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(scryfallResults, key = { it.id }) { card ->
                                    ScryfallResultRow(
                                        card = card,
                                        currency = currency,
                                        onImport = {
                                            viewModel.importScryfallCardToCollection(card) { savedItem ->
                                                importedFeedback = "Adicionado à coleção: ${savedItem.name}!"
                                            }
                                        }
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

@Composable
private fun EmptySearchPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun TcgdexResultRow(
    card: TcgdexCardBrief,
    onImport: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    .size(width = 44.dp, height = 58.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E1E2E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = card.getHighResImage(),
                    contentDescription = card.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "ID: ${card.id} • Nº #${card.localId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "Fonte: TCGDex API (Oficial)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFEAB308)
                )
            }

            Button(
                onClick = onImport,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ScryfallResultRow(
    card: ScryfallCard,
    currency: com.example.data.AppCurrency,
    onImport: () -> Unit
) {
    val estPrice = card.getEstimatedPriceBrl()
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    .size(width = 44.dp, height = 58.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E1E2E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = card.getHighResImage(),
                    contentDescription = card.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${card.setName} #${card.collectorNumber} • ${card.rarity.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "Scryfall: ${currency.formatValue(estPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
            }

            Button(
                onClick = onImport,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SearchResultRow(
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val (iconVector, color) = when {
                item.isCard -> Icons.Default.Style to Color(0xFF6366F1)
                item.isDiecast -> Icons.Default.DirectionsCar to Color(0xFFEF4444)
                item.type.contains("Figure", true) -> Icons.Default.SmartToy to Color(0xFF8B5CF6)
                item.type.contains("Moeda", true) -> Icons.Default.MonetizationOn to Color(0xFFF59E0B)
                else -> Icons.Default.Category to Color(0xFF10B981)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = iconVector, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${item.subCategory} • ${item.collection} ${if (item.itemNumber.isNotBlank()) "#${item.itemNumber}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currency.formatValue(item.estimatedValue),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Qtd: ${item.quantity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

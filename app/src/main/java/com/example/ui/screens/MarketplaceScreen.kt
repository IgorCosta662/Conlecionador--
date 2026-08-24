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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CollectorViewModel
import com.example.ui.MarketOffer

data class MarketOpportunity(
    val id: String,
    val title: String,
    val category: String,
    val currentPrice: Double,
    val marketAverage: Double,
    val discountPercent: Int,
    val trend: String,
    val source: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(viewModel: CollectorViewModel) {
    val offers by viewModel.marketOffers.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateOfferDialog by remember { mutableStateOf(false) }

    val opportunities = remember {
        listOf(
            MarketOpportunity(
                id = "opp1",
                title = "Charizard ex #199/165 (Special Illustration Rare)",
                category = "Pokémon TCG",
                currentPrice = 640.0,
                marketAverage = 850.0,
                discountPercent = 25,
                trend = "Em alta (+14% este mês)",
                source = "LigaPokemon",
                icon = Icons.Default.Style
            ),
            MarketOpportunity(
                id = "opp2",
                title = "Hot Wheels Nissan Skyline GT-R R34 (STH 2024)",
                category = "Hot Wheels",
                currentPrice = 320.0,
                marketAverage = 450.0,
                discountPercent = 29,
                trend = "Valorizando rápido",
                source = "Mercado Livre",
                icon = Icons.Default.DirectionsCar
            ),
            MarketOpportunity(
                id = "opp3",
                title = "The One Ring (Foil #001) Tales of Middle-earth",
                category = "Magic: The Gathering",
                currentPrice = 420.0,
                marketAverage = 580.0,
                discountPercent = 27,
                trend = "Item Raro",
                source = "LigaMagic",
                icon = Icons.Default.Style
            ),
            MarketOpportunity(
                id = "opp4",
                title = "Moeda 2000 Réis 1932 Vicentina (Prata 500)",
                category = "Numismática",
                currentPrice = 110.0,
                marketAverage = 160.0,
                discountPercent = 31,
                trend = "Abaixo do mercado",
                source = "Catálogo Numismático",
                icon = Icons.Default.MonetizationOn
            ),
            MarketOpportunity(
                id = "opp5",
                title = "Iron Man Mark 85 Hot Toys 1/6 Diecast",
                category = "Action Figures",
                currentPrice = 1800.0,
                marketAverage = 2400.0,
                discountPercent = 25,
                trend = "Oferta Relâmpago",
                source = "eBay BR",
                icon = Icons.Default.SmartToy
            )
        )
    }

    val filteredOpps = remember(searchQuery, selectedTab, opportunities) {
        opportunities.filter {
            searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val tabs = listOf("Em Alta", "Abaixo da Média", "Mais Valiosos", "Mural de Trocas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Mercado & Oportunidades", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 3) {
                FloatingActionButton(
                    onClick = { showCreateOfferDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_trade_offer_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Publicar Troca")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("marketplace_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Market Alert Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "Radar de Oportunidades Ativo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Monitorando preços na LigaPokemon, LigaMagic, Mercado Livre e eBay.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar cotação em tempo real...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Navigation Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            if (selectedTab < 3) {
                items(filteredOpps, key = { it.id }) { opp ->
                    OpportunityCard(opp = opp, currency = currency)
                }
            } else {
                // Community Trades (Mural de Trocas)
                if (offers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                                Text("Nenhuma proposta no momento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Toque no botão '+' abaixo para publicar seu anúncio de troca ou venda.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                } else {
                    items(offers, key = { it.id }) { offer ->
                        TradeOfferCard(offer = offer)
                    }
                }
            }
        }
    }
}

@Composable
private fun OpportunityCard(
    opp: MarketOpportunity,
    currency: com.example.data.AppCurrency
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = opp.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(opp.category, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFD1FAE5)
                ) {
                    Text(
                        text = "-${opp.discountPercent}% abaixo",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = opp.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Preço Encontrado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = currency.formatValue(opp.currentPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = currency.formatValue(opp.marketAverage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                }

                Text(
                    text = "Fonte: ${opp.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun TradeOfferCard(offer: MarketOffer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(offer.userName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(offer.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                }
            }

            Text("Oferece: ${offer.offeredItem}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Busca: ${offer.requestedItem}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            if (offer.description.isNotBlank()) {
                Text(offer.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.navigation.NavController
import com.example.data.AiChatMessage
import com.example.data.Item
import com.example.data.SetRegistry
import com.example.ui.CollectorViewModel
import com.example.ui.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: CollectorViewModel,
    navController: NavController
) {
    val allItems by viewModel.allItems.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }

    // Chat history state
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                AiChatMessage(
                    sender = "assistant",
                    message = "Olá! Eu sou seu Assistente Inteligente de Coleção. Conheço todos os seus itens cadastrados, cotações, localização física e conjuntos. Como posso te ajudar hoje?"
                )
            )
        )
    }

    val suggestedQuestions = listOf(
        "Qual é minha carta mais valiosa?",
        "Quanto vale minha coleção por categoria?",
        "Quais cartas faltam para completar 151?",
        "Quais itens mais valorizaram?",
        "Tenho cartas duplicadas para troca?",
        "Itens que valem mais de R$ 300",
        "Onde estão guardados meus itens?"
    )

    fun answerQuery(prompt: String) {
        val q = prompt.lowercase().trim()
        val userMsg = AiChatMessage(sender = "user", message = prompt)
        chatMessages = chatMessages + userMsg

        // Compute dynamic AI answer from actual collection data
        val (responseMsg, relatedIds, actionRoute) = when {
            q.contains("mais valios") || q.contains("top 1") || q.contains("mais cara") -> {
                val mostValuable = allItems.maxByOrNull { it.estimatedValue }
                if (mostValuable != null) {
                    val top3 = allItems.sortedByDescending { it.estimatedValue }.take(3)
                    val text = "Sua carta/item mais valioso é **${mostValuable.name}** (${mostValuable.subCategory}), avaliado atualmente em **${currency.formatValue(mostValuable.estimatedValue)}**.\n\n" +
                               "Top 3 itens mais valiosos da sua coleção:\n" +
                               top3.mapIndexed { idx, it -> "${idx + 1}. ${it.name} (${currency.formatValue(it.estimatedValue)})" }.joinToString("\n")
                    Triple(text, top3.map { it.id }, Routes.COLLECTION)
                } else {
                    Triple("Sua coleção ainda não possui itens cadastrados. Escaneie uma carta ou carrinho para começar!", emptyList(), Routes.SCANNER)
                }
            }

            q.contains("quanto vale") || q.contains("categoria") || q.contains("patrimonio") || q.contains("total") -> {
                val totalValue = allItems.sumOf { it.totalEstimatedValue }
                val totalInvested = allItems.sumOf { it.totalPurchasePrice }
                val profit = totalValue - totalInvested
                val pokemonVal = allItems.filter { it.subCategory.contains("Pokémon", ignoreCase = true) }.sumOf { it.totalEstimatedValue }
                val hwVal = allItems.filter { it.isDiecast }.sumOf { it.totalEstimatedValue }
                val magicVal = allItems.filter { it.subCategory.contains("Magic", ignoreCase = true) }.sumOf { it.totalEstimatedValue }
                val yugiohVal = allItems.filter { it.subCategory.contains("Yu-Gi-Oh", ignoreCase = true) }.sumOf { it.totalEstimatedValue }

                val text = "Seu patrimônio total em colecionáveis é de **${currency.formatValue(totalValue)}** (investimento de ${currency.formatValue(totalInvested)}, rendimento de ${if (profit >= 0) "+" else ""}${currency.formatValue(profit)}).\n\n" +
                           "Divisão por categoria:\n" +
                           "• Pokémon TCG: ${currency.formatValue(pokemonVal)}\n" +
                           "• Hot Wheels / Diecast: ${currency.formatValue(hwVal)}\n" +
                           "• Magic: The Gathering: ${currency.formatValue(magicVal)}\n" +
                           "• Yu-Gi-Oh!: ${currency.formatValue(yugiohVal)}"
                Triple(text, emptyList(), Routes.STATISTICS)
            }

            q.contains("151") || q.contains("faltam") || q.contains("completar") -> {
                val set151 = SetRegistry.popularSets.first { it.id == "pokemon_151" }
                val owned = allItems.filter { it.collection.contains("151", ignoreCase = true) || it.name.contains("Charizard", ignoreCase = true) || it.name.contains("Pikachu", ignoreCase = true) }
                val missingCount = (set151.totalItems - owned.size).coerceAtLeast(0)
                val text = "No conjunto **${set151.name}** (165 cartas no total):\n\n" +
                           "• Você possui: **${owned.size} cartas**\n" +
                           "• Faltam: **$missingCount cartas** para completar\n" +
                           "• Custo estimado para finalizar o set: **${currency.formatValue(missingCount * 22.50)}**\n\n" +
                           "Abra o Checklist para ver a lista detalhada e ordenar pelas mais baratas ou mais raras."
                Triple(text, owned.map { it.id }, Routes.COLLECTION)
            }

            q.contains("valoriz") || q.contains("lucro") || q.contains("subiu") || q.contains("alta") -> {
                val profitableItems = allItems.filter { it.purchasePrice > 0 }.sortedByDescending { it.profitPercentage }
                if (profitableItems.isNotEmpty()) {
                    val best = profitableItems.first()
                    val text = "O item que mais valorizou foi **${best.name}** com alta de **+${String.format("%.1f", best.profitPercentage)}%** (Preço pago: ${currency.formatValue(best.purchasePrice)} → Atual: ${currency.formatValue(best.estimatedValue)}).\n\n" +
                               "Top altas da coleção:\n" +
                               profitableItems.take(3).joinToString("\n") { "• ${it.name}: +${String.format("%.1f", it.profitPercentage)}% (+${currency.formatValue(it.profitOrLoss)})" }
                    Triple(text, profitableItems.take(3).map { it.id }, Routes.COLLECTION)
                } else {
                    Triple("Para calcular a valorização percentual precisa, adicione o preço de compra ao cadastrar ou editar seus itens.", emptyList(), null)
                }
            }

            q.contains("duplicad") || q.contains("troca") || q.contains("repetid") -> {
                val dupes = allItems.filter { it.quantity > 1 }
                val totalDupeUnits = dupes.sumOf { it.quantity - 1 }
                val totalDupeVal = dupes.sumOf { (it.quantity - 1) * it.estimatedValue }
                if (dupes.isNotEmpty()) {
                    val text = "Você possui **$totalDupeUnits cópias excedentes** distribuídas em **${dupes.size} itens diferentes**, totalizando **${currency.formatValue(totalDupeVal)}** em duplicatas disponíveis para troca ou venda no Marketplace."
                    Triple(text, dupes.map { it.id }, Routes.MARKETPLACE)
                } else {
                    Triple("Você não possui nenhuma carta ou carrinho duplicado no momento! Todos os seus itens estão com 1 única cópia.", emptyList(), null)
                }
            }

            q.contains("300") || q.contains("500") || q.contains("caros") -> {
                val expensive = allItems.filter { it.estimatedValue >= 300.0 }.sortedByDescending { it.estimatedValue }
                if (expensive.isNotEmpty()) {
                    val text = "Você possui **${expensive.size} itens** com valor igual ou superior a R$ 300:\n\n" +
                               expensive.joinToString("\n") { "• ${it.name} (${it.subCategory}) — ${currency.formatValue(it.estimatedValue)}" }
                    Triple(text, expensive.map { it.id }, Routes.COLLECTION)
                } else {
                    Triple("No momento nenhum item da sua coleção individualmente ultrapassa esse valor. Seu item de maior valor é ${allItems.maxByOrNull { it.estimatedValue }?.name ?: "N/A"}.", emptyList(), null)
                }
            }

            q.contains("onde") || q.contains("local") || q.contains("pasta") || q.contains("caixa") -> {
                val withLocation = allItems.filter { it.storageLocation.isNotBlank() }
                val locations = withLocation.groupBy { it.storageLocation }
                val text = "Seus itens estão organizados em **${locations.size} locais físicos**:\n\n" +
                           locations.entries.take(4).joinToString("\n") { "• ${it.key}: ${it.value.size} itens" } +
                           if (allItems.size - withLocation.size > 0) "\n\nExistem ${(allItems.size - withLocation.size)} itens sem localização física atribuída." else ""
                Triple(text, emptyList(), null)
            }

            else -> {
                val text = "Analisei sua coleção de **${allItems.size} itens** avaliada em **${currency.formatValue(allItems.sumOf { it.totalEstimatedValue })}**.\n\n" +
                           "Você pode me perguntar sobre cotações, cartas mais raras, checklists de coleções incompletas, duplicatas para troca ou organização física em pastas e caixas!"
                Triple(text, emptyList(), null)
            }
        }

        val assistantMsg = AiChatMessage(
            sender = "assistant",
            message = responseMsg,
            relatedItemIds = relatedIds,
            isActionable = actionRoute != null,
            actionRoute = actionRoute
        )
        chatMessages = chatMessages + assistantMsg

        coroutineScope.launch {
            listState.animateScrollToItem((chatMessages.size - 1).coerceAtLeast(0))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Assistente da Coleção", fontWeight = FontWeight.Bold)
                            Text("IA treinada com seus dados reais", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Suggested chips row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestedQuestions) { question ->
                            SuggestionChip(
                                onClick = { answerQuery(question) },
                                label = { Text(question, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Pergunte algo sobre sua coleção...") },
                            modifier = Modifier.weight(1f),
                            maxLines = 3,
                            shape = RoundedCornerShape(24.dp)
                        )

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val text = inputText
                                    inputText = ""
                                    answerQuery(text)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chatMessages) { msg ->
                val isAssistant = msg.sender == "assistant"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAssistant) Arrangement.Start else Arrangement.End
                ) {
                    if (isAssistant) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        modifier = Modifier.widthIn(max = 300.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isAssistant) 4.dp else 16.dp,
                            bottomEnd = if (isAssistant) 16.dp else 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAssistant) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = msg.message,
                                color = if (isAssistant) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Quick Action Button if actionable
                            if (isAssistant && msg.isActionable && msg.actionRoute != null) {
                                Button(
                                    onClick = { navController.navigate(msg.actionRoute) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ver na Coleção / Ferramenta", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

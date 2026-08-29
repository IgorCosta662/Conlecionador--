package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.util.OfficialCardImageHelper
import com.example.util.OfficialCardPrintOption
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialCardPrintSelectorModal(
    initialName: String,
    subCategory: String,
    collection: String = "",
    itemNumber: String = "",
    currentImageUrl: String? = null,
    onDismiss: () -> Unit,
    onSelectPrint: (imageUrl: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialName) }
    var isLoading by remember { mutableStateOf(false) }
    var printOptions by remember { mutableStateOf<List<OfficialCardPrintOption>>(emptyList()) }
    var customUrlInput by remember { mutableStateOf("") }
    var showCustomUrlSection by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Initial search on launch
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            printOptions = OfficialCardImageHelper.searchOfficialCardPrintOptions(
                name = initialName,
                subCategory = subCategory,
                collection = collection,
                itemNumber = itemNumber
            )
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        coroutineScope.launch {
            isLoading = true
            try {
                printOptions = OfficialCardImageHelper.searchOfficialCardPrintOptions(
                    name = query.trim(),
                    subCategory = subCategory,
                    collection = collection,
                    itemNumber = itemNumber
                )
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Trocar Print / Edição Oficial",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Escolha o scan/print real da sua carta",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Pesquisar Carta / Edição") },
                    placeholder = { Text("Ex: Charizard 151, Pikachu VMAX...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpar")
                                }
                            }
                            IconButton(onClick = { performSearch(searchQuery) }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { performSearch(searchQuery) })
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle for custom URL input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (printOptions.isNotEmpty()) "${printOptions.size} versões encontradas" else "Nenhuma versão encontrada",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    TextButton(
                        onClick = { showCustomUrlSection = !showCustomUrlSection },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (showCustomUrlSection) Icons.Default.ExpandLess else Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showCustomUrlSection) "Ocultar URL" else "Colar Link Direto",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showCustomUrlSection) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = customUrlInput,
                                onValueChange = { customUrlInput = it },
                                label = { Text("URL da Imagem / Print da Carta") },
                                placeholder = { Text("https://...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (customUrlInput.isNotBlank()) {
                                        onSelectPrint(customUrlInput.trim())
                                    }
                                },
                                enabled = customUrlInput.isNotBlank(),
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Usar Esta Imagem")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Content area (Grid of Card Prints)
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Buscando prints e scans oficiais...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (printOptions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Nenhum print encontrado para \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Tente buscar apenas pelo nome do Pokémon (ex: \"Charizard\" ou \"Pikachu\") ou insira o link direto.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(printOptions, key = { "${it.id}_${it.imageUrl}" }) { print ->
                            val isCurrent = currentImageUrl == print.imageUrl
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectPrint(print.imageUrl) }
                                    .border(
                                        width = if (isCurrent) 2.5.dp else 1.dp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(print.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = print.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )

                                        if (isCurrent) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
                                                modifier = Modifier.align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "Atual",
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = print.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (print.setName.isNotBlank()) {
                                            Text(
                                                text = print.setName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        if (print.cardNumber.isNotBlank()) {
                                            Text(
                                                text = print.cardNumber,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    FilledTonalButton(
                                        onClick = { onSelectPrint(print.imageUrl) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isCurrent) "Selecionado" else "Escolher Print",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Fechar")
                    }
                }
            }
        }
    }
}

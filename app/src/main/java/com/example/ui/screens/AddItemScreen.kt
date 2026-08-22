package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Item
import com.example.data.PriceHistoryPoint
import com.example.data.PriceOffer
import com.example.data.JsonParserHelper
import com.example.ui.CollectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: CollectorViewModel,
    onNavigateBack: () -> Unit
) {
    val editingItem by viewModel.isEditingItem.collectAsStateWithLifecycle()
    val isEditMode = editingItem != null

    var name by remember(editingItem) { mutableStateOf(editingItem?.name ?: "") }
    var category by remember(editingItem) { mutableStateOf(editingItem?.type ?: "Trading Cards") }
    var subCategory by remember(editingItem) { mutableStateOf(editingItem?.subCategory ?: "Pokémon TCG") }
    var collection by remember(editingItem) { mutableStateOf(editingItem?.collection ?: "") }
    var itemNumber by remember(editingItem) { mutableStateOf(editingItem?.itemNumber ?: "") }
    var rarity by remember(editingItem) { mutableStateOf(editingItem?.rarity ?: "Comum") }
    var variant by remember(editingItem) { mutableStateOf(editingItem?.variant ?: "Normal") }
    var condition by remember(editingItem) { mutableStateOf(editingItem?.condition ?: "Near Mint") }
    var language by remember(editingItem) { mutableStateOf(editingItem?.language ?: "PT-BR") }
    var quantityText by remember(editingItem) { mutableStateOf(editingItem?.quantity?.toString() ?: "1") }
    var purchasePriceText by remember(editingItem) { mutableStateOf(editingItem?.purchasePrice?.toString() ?: "0.00") }
    var estimatedValueText by remember(editingItem) { mutableStateOf(editingItem?.estimatedValue?.toString() ?: "0.00") }
    var storageLocation by remember(editingItem) { mutableStateOf(editingItem?.storageLocation ?: "") }
    var notes by remember(editingItem) { mutableStateOf(editingItem?.notes ?: "") }
    var tags by remember(editingItem) { mutableStateOf(editingItem?.tags ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Item" else "Cadastrar Novo Item", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Item / Carta / Carrinho *") },
                    placeholder = { Text("Ex: Charizard ex, '71 Datsun 510 Wagon") },
                    modifier = Modifier.fillMaxWidth().testTag("input_item_name")
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        modifier = Modifier.weight(1f).testTag("input_category")
                    )
                    OutlinedTextField(
                        value = subCategory,
                        onValueChange = { subCategory = it },
                        label = { Text("Jogo / Marca") },
                        modifier = Modifier.weight(1f).testTag("input_subcategory")
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = collection,
                        onValueChange = { collection = it },
                        label = { Text("Coleção / Série") },
                        modifier = Modifier.weight(1.2f).testTag("input_collection")
                    )
                    OutlinedTextField(
                        value = itemNumber,
                        onValueChange = { itemNumber = it },
                        label = { Text("Número (#151/165)") },
                        modifier = Modifier.weight(0.8f).testTag("input_number")
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = rarity,
                        onValueChange = { rarity = it },
                        label = { Text("Raridade") },
                        modifier = Modifier.weight(1f).testTag("input_rarity")
                    )
                    OutlinedTextField(
                        value = variant,
                        onValueChange = { variant = it },
                        label = { Text("Variante (Foil/STH)") },
                        modifier = Modifier.weight(1f).testTag("input_variant")
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it },
                        label = { Text("Condição") },
                        modifier = Modifier.weight(1f).testTag("input_condition")
                    )
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("Idioma / Escala") },
                        modifier = Modifier.weight(1f).testTag("input_language")
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantidade") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_quantity")
                    )
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Preço Pago (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_purchase_price")
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = estimatedValueText,
                    onValueChange = { estimatedValueText = it },
                    label = { Text("Valor Estimado de Mercado (R$) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_estimated_value")
                )
            }

            item {
                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Local Onde Está Guardado") },
                    placeholder = { Text("Ex: Pasta Charizard - Pág 3, Gaveta 2") },
                    modifier = Modifier.fillMaxWidth().testTag("input_storage_location")
                )
            }

            item {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (separadas por vírgula)") },
                    placeholder = { Text("Ex: pokémon, fogo, raro, troca") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("input_notes")
                )
            }

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
                                tags = tags
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
                        .height(52.dp)
                        .testTag("submit_item_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (isEditMode) "Salvar Alterações" else "Cadastrar Item na Coleção", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

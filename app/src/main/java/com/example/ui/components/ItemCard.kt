package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import com.example.data.AppCurrency
import com.example.data.Item
import com.example.util.OfficialCardImageHelper

@Composable
fun ItemCard(
    item: Item,
    currency: AppCurrency,
    onClick: () -> Unit
) {
    ItemCard(
        item = item,
        selectedCurrency = currency,
        onClick = onClick,
        onToggleFavorite = {}
    )
}

@Composable
fun ItemCard(
    item: Item,
    selectedCurrency: AppCurrency = AppCurrency.BRL,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit = {},
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectToggle: () -> Unit = {}
) {
    val isProfit = item.profitOrLoss >= 0
    val profitColor = if (isProfit) Color(0xFF16A34A) else Color(0xFFDC2626)
    val formattedPrice = selectedCurrency.format(item.estimatedValue)

    val (categoryColor, categoryIcon) = when {
        item.isCard -> Color(0xFF6366F1) to Icons.Default.Style
        item.isDiecast -> Color(0xFFEF4444) to Icons.Default.DirectionsCar
        item.type.contains("Figure", ignoreCase = true) -> Color(0xFF8B5CF6) to Icons.Default.SmartToy
        item.type.contains("Moeda", ignoreCase = true) -> Color(0xFFF59E0B) to Icons.Default.MonetizationOn
        else -> Color(0xFF10B981) to Icons.Default.Category
    }

    val cardBorder = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isSelectionMode) {
                    onSelectToggle()
                } else {
                    onClick()
                }
            }
            .testTag("item_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selection Checkbox in compare mode
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSelectToggle() },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                // Thumbnail / Photo visual
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(categoryColor.copy(alpha = 0.8f), categoryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val resolvedImageUrl = if (!item.imageUri.isNullOrBlank()) {
                        item.imageUri
                    } else {
                        OfficialCardImageHelper.getOfficialImageUrl(item.name, item.subCategory, item.collection, item.itemNumber)
                    }

                    if (!resolvedImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = resolvedImageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    if (item.quantity > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "x${item.quantity}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Details Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favoritar",
                                tint = if (item.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Subcategory & Collection
                    Text(
                        text = listOfNotNull(
                            item.subCategory.ifBlank { item.type },
                            item.collection.takeIf { it.isNotBlank() },
                            item.itemNumber.takeIf { it.isNotBlank() }
                        ).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Badges: Rarity, Variant, Condition
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rarity badge
                        if (item.rarity.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = categoryColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = item.rarity,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = categoryColor,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Special variant badge if applicable
                        if (item.variant.isNotBlank() && item.variant != "Normal") {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = item.variant,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Condition badge
                        if (item.condition.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = item.condition,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(6.dp))

            // Footer: Physical Storage Location & Price / Profit Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Storage location indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (item.storageLocation.isNotBlank()) Icons.Default.Inventory2 else Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = if (item.storageLocation.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (item.storageLocation.isNotBlank()) item.storageLocation else "Sem local definido",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = if (item.storageLocation.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Price and Profit/Loss
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (item.purchasePrice > 0) {
                        val sign = if (isProfit) "+" else ""
                        val pct = String.format(java.util.Locale.US, "%.0f", item.profitPercentage)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = profitColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "$sign$pct%",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = profitColor,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ItemGridCard(
    item: Item,
    currency: AppCurrency = AppCurrency.BRL,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isProfit = item.profitOrLoss >= 0
    val profitColor = if (isProfit) Color(0xFF16A34A) else Color(0xFFDC2626)
    val formattedPrice = currency.formatValue(item.estimatedValue)

    val (categoryColor, categoryIcon) = when {
        item.isCard -> Color(0xFF6366F1) to Icons.Default.Style
        item.isDiecast -> Color(0xFFEF4444) to Icons.Default.DirectionsCar
        item.type.contains("Figure", ignoreCase = true) -> Color(0xFF8B5CF6) to Icons.Default.SmartToy
        item.type.contains("Moeda", ignoreCase = true) -> Color(0xFFF59E0B) to Icons.Default.MonetizationOn
        else -> Color(0xFF10B981) to Icons.Default.Category
    }

    val resolvedImageUrl = remember(item.imageUri, item.name, item.subCategory, item.collection, item.itemNumber) {
        if (!item.imageUri.isNullOrBlank()) {
            item.imageUri
        } else {
            OfficialCardImageHelper.getOfficialImageUrl(item.name, item.subCategory, item.collection, item.itemNumber)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("grid_item_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Artwork / Image Container with fixed 2.5:3.5 aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.5f / 3.5f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFF181824)),
                contentAlignment = Alignment.Center
            ) {
                if (!resolvedImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = resolvedImageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = item.subCategory.ifBlank { item.type },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }

                // Top Right: Collector item number
                if (item.itemNumber.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = "#${item.itemNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Top Left: Quantity if > 1
                if (item.quantity > 1) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "x${item.quantity}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Body with uniform 2-line title to guarantee equal row heights
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
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = listOfNotNull(
                        item.subCategory.ifBlank { item.type },
                        item.collection.takeIf { it.isNotBlank() }
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.rarity.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = categoryColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = item.rarity,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (item.variant.isNotBlank() && item.variant != "Normal") {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = item.variant,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Price and Profit percentage row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )

                    if (item.purchasePrice > 0) {
                        val sign = if (isProfit) "+" else ""
                        val pct = String.format(java.util.Locale.US, "%.0f", item.profitPercentage)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = profitColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$sign$pct%",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = profitColor,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

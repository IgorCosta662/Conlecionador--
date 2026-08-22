package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Item

data class CategoryStat(
    val categoryName: String,
    val count: Int,
    val totalValue: Double,
    val color: Color
)

@Composable
fun CategoryDistributionChart(
    items: List<Item>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val totalItems = items.sumOf { it.quantity }
    if (totalItems == 0) return

    val cardsCount = items.filter { it.isCard }.sumOf { it.quantity }
    val diecastCount = items.filter { it.isDiecast }.sumOf { it.quantity }
    val figuresCount = items.filter { it.type.contains("Figure", ignoreCase = true) || it.type.contains("Action", ignoreCase = true) }.sumOf { it.quantity }
    val coinsCount = items.filter { it.type.contains("Moeda", ignoreCase = true) }.sumOf { it.quantity }
    val otherCount = totalItems - (cardsCount + diecastCount + figuresCount + coinsCount).coerceAtLeast(0)

    val stats = listOfNotNull(
        if (cardsCount > 0) CategoryStat("Trading Cards", cardsCount, items.filter { it.isCard }.sumOf { it.totalEstimatedValue }, Color(0xFF6750A4)) else null,
        if (diecastCount > 0) CategoryStat("Diecast / Carrinhos", diecastCount, items.filter { it.isDiecast }.sumOf { it.totalEstimatedValue }, Color(0xFFE53935)) else null,
        if (figuresCount > 0) CategoryStat("Action Figures", figuresCount, items.filter { it.type.contains("Figure", ignoreCase = true) }.sumOf { it.totalEstimatedValue }, Color(0xFF00897B)) else null,
        if (coinsCount > 0) CategoryStat("Moedas", coinsCount, items.filter { it.type.contains("Moeda", ignoreCase = true) }.sumOf { it.totalEstimatedValue }, Color(0xFFFFB300)) else null,
        if (otherCount > 0) CategoryStat("Outros", otherCount, items.filter { !it.isCard && !it.isDiecast && !it.type.contains("Figure", ignoreCase = true) && !it.type.contains("Moeda", ignoreCase = true) }.sumOf { it.totalEstimatedValue }, Color(0xFF5E35B1)) else null
    )

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "pie_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(16.dp)
    ) {
        Text(
            text = "Distribuição por Categoria",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Donut Chart
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    val strokeWidth = 20.dp.toPx()

                    stats.forEach { stat ->
                        val sweepAngle = (stat.count.toFloat() / totalItems) * 360f * animatedProgress
                        drawArc(
                            color = stat.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweepAngle
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalItems",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "itens",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Legend
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                stats.forEach { stat ->
                    val pct = ((stat.count.toDouble() / totalItems) * 100).toInt()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(stat.color)
                        )
                        Text(
                            text = "${stat.categoryName}:",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${stat.count} ($pct%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

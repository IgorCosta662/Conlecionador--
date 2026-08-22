package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppCurrency
import com.example.data.PriceHistoryPoint

@Composable
fun PriceEvolutionChart(
    history: List<PriceHistoryPoint>,
    selectedCurrency: AppCurrency = AppCurrency.BRL,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sem dados de histórico suficientes para traçar o gráfico.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    val pricesInSelectedCurrency = history.map { selectedCurrency.convertFromBRL(it.priceInBRL) }
    val minVal = (pricesInSelectedCurrency.minOrNull() ?: 0.0) * 0.9
    val maxVal = (pricesInSelectedCurrency.maxOrNull() ?: 100.0) * 1.1
    val range = if (maxVal - minVal > 0) maxVal - minVal else 1.0

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(history) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(750))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Evolução de Preço (Últimos Meses)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val latest = pricesInSelectedCurrency.lastOrNull() ?: 0.0
            val first = pricesInSelectedCurrency.firstOrNull() ?: 0.0
            val diff = latest - first
            val isPositive = diff >= 0
            Text(
                text = "${if (isPositive) "+" else ""}${selectedCurrency.formatExact(diff)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val paddingBottom = 20f
                val chartHeight = height - paddingBottom

                // Grid lines (3 horizontal lines)
                for (i in 0..2) {
                    val y = chartHeight * (i / 2f)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                val points = mutableListOf<Offset>()
                val stepX = if (history.size > 1) width / (history.size - 1) else width / 2f

                history.forEachIndexed { index, _ ->
                    val curVal = pricesInSelectedCurrency[index]
                    val normY = 1.0 - ((curVal - minVal) / range)
                    val x = if (history.size > 1) index * stepX else width / 2f
                    val y = (normY * chartHeight).toFloat()
                    val animatedY = chartHeight - (chartHeight - y) * animProgress.value
                    points.add(Offset(x, animatedY))
                }

                if (points.isNotEmpty()) {
                    // Draw filled gradient under line
                    val fillPath = Path().apply {
                        moveTo(points.first().x, chartHeight)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, chartHeight)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.35f * animProgress.value),
                                primaryColor.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = chartHeight
                        )
                    )

                    // Draw line
                    val strokePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val p0 = points[i - 1]
                            val p1 = points[i]
                            val controlX = (p0.x + p1.x) / 2f
                            cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }
                    }

                    drawPath(
                        path = strokePath,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw point dots
                    points.forEachIndexed { index, pt ->
                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }
        }

        // Labels row below chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            history.forEach { pt ->
                Text(
                    text = pt.period,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

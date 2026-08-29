package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.AppCurrency
import com.example.data.Item
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CollectionExporter {

    private const val PAGE_WIDTH = 595 // A4 standard width in points (72 dpi)
    private const val PAGE_HEIGHT = 842 // A4 standard height in points (72 dpi)
    private const val MARGIN_HORIZONTAL = 36f
    private const val MARGIN_TOP = 40f
    private const val MARGIN_BOTTOM = 50f

    /**
     * Generates a polished, multi-page PDF Portfolio Document.
     */
    fun exportToPdf(context: Context, items: List<Item>, currency: AppCurrency): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val pdfFile = File(exportDir, "Relatorio_Colecao_$dateStr.pdf")

        val document = PdfDocument()

        val totalItems = items.sumOf { it.quantity }
        val totalEstValue = items.sumOf { it.totalEstimatedValue }
        val totalInvested = items.sumOf { it.totalPurchasePrice }
        val profit = totalEstValue - totalInvested
        val profitPercentage = if (totalInvested > 0) (profit / totalInvested) * 100.0 else 0.0
        val currentDateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val tcgCount = items.filter { it.isCard }.sumOf { it.quantity }
        val diecastCount = items.filter { it.isDiecast }.sumOf { it.quantity }
        val figureCount = items.filter { it.type.contains("Figure", true) }.sumOf { it.quantity }
        val otherCount = totalItems - tcgCount - diecastCount - figureCount

        // Paints
        val titlePaint = Paint().apply {
            color = Color.rgb(24, 30, 75)
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(90, 95, 120)
            textSize = 9f
            isAntiAlias = true
        }

        val headerBoxPaint = Paint().apply {
            color = Color.rgb(238, 242, 255)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val headerBorderPaint = Paint().apply {
            color = Color.rgb(199, 210, 254)
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }

        val sectionTitlePaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val tableHeaderBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val tableHeaderPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val cellTextPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 8f
            isAntiAlias = true
        }

        val cellSubTextPaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 7f
            isAntiAlias = true
        }

        val profitPositivePaint = Paint().apply {
            color = Color.rgb(16, 185, 129)
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val profitNegativePaint = Paint().apply {
            color = Color.rgb(239, 68, 68)
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val dividerPaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 0.6f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = Color.rgb(148, 163, 184)
            textSize = 7.5f
            isAntiAlias = true
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var yPosition = MARGIN_TOP

        // Helper to draw standard Header & Title on first page
        fun drawFirstPageHeader() {
            // Main App & Document Title
            canvas.drawText("RELATÓRIO PATRIMONIAL DE COLECIONÁVEIS", MARGIN_HORIZONTAL, yPosition, titlePaint)
            yPosition += 13f
            canvas.drawText("Gerado automaticamente em $currentDateFormatted • Collector App", MARGIN_HORIZONTAL, yPosition, subTitlePaint)
            yPosition += 18f

            // Executive Summary Box
            val boxLeft = MARGIN_HORIZONTAL
            val boxTop = yPosition
            val boxRight = PAGE_WIDTH - MARGIN_HORIZONTAL
            val boxHeight = 72f

            canvas.drawRoundRect(boxLeft, boxTop, boxRight, boxTop + boxHeight, 8f, 8f, headerBoxPaint)
            canvas.drawRoundRect(boxLeft, boxTop, boxRight, boxTop + boxHeight, 8f, 8f, headerBorderPaint)

            val boxY = boxTop + 16f
            val colWidth = (boxRight - boxLeft) / 3f

            // Column 1: Total Value
            val kpiLabelPaint = Paint().apply { color = Color.rgb(71, 85, 105); textSize = 8f; isAntiAlias = true }
            val kpiValuePaint = Paint().apply { color = Color.rgb(30, 41, 59); textSize = 12f; isFakeBoldText = true; isAntiAlias = true }

            canvas.drawText("VALOR ESTIMADO TOTAL", boxLeft + 12f, boxY, kpiLabelPaint)
            canvas.drawText(currency.formatValue(totalEstValue), boxLeft + 12f, boxY + 16f, kpiValuePaint)
            canvas.drawText("Total de Itens: $totalItems un.", boxLeft + 12f, boxY + 30f, subTitlePaint)

            // Column 2: Total Invested
            canvas.drawText("TOTAL INVESTIDO", boxLeft + colWidth + 12f, boxY, kpiLabelPaint)
            canvas.drawText(currency.formatValue(totalInvested), boxLeft + colWidth + 12f, boxY + 16f, kpiValuePaint)
            canvas.drawText("Custo de aquisição", boxLeft + colWidth + 12f, boxY + 30f, subTitlePaint)

            // Column 3: Profit / ROI
            canvas.drawText("VALORIZAÇÃO ACUMULADA", boxLeft + colWidth * 2 + 12f, boxY, kpiLabelPaint)
            val roiText = "${if (profit >= 0) "+" else ""}${currency.formatValue(profit)} (${String.format("%.1f", profitPercentage)}%)"
            val roiPaint = if (profit >= 0) profitPositivePaint.apply { textSize = 11f } else profitNegativePaint.apply { textSize = 11f }
            canvas.drawText(roiText, boxLeft + colWidth * 2 + 12f, boxY + 16f, roiPaint)
            canvas.drawText("TCG: $tcgCount | Carros: $diecastCount | Fig: $figureCount", boxLeft + colWidth * 2 + 12f, boxY + 30f, subTitlePaint)

            yPosition = boxTop + boxHeight + 20f
        }

        // Helper to draw Table Header
        fun drawTableHeader() {
            val tableTop = yPosition
            val tableBottom = yPosition + 18f
            canvas.drawRect(MARGIN_HORIZONTAL, tableTop, PAGE_WIDTH - MARGIN_HORIZONTAL, tableBottom, tableHeaderBgPaint)

            val textY = tableTop + 12f
            canvas.drawText("Nº", MARGIN_HORIZONTAL + 4f, textY, tableHeaderPaint)
            canvas.drawText("ITEM / DESCRIÇÃO", MARGIN_HORIZONTAL + 26f, textY, tableHeaderPaint)
            canvas.drawText("CATEGORIA / COLEÇÃO", MARGIN_HORIZONTAL + 220f, textY, tableHeaderPaint)
            canvas.drawText("QTD", MARGIN_HORIZONTAL + 360f, textY, tableHeaderPaint)
            canvas.drawText("PAGO (UN)", MARGIN_HORIZONTAL + 395f, textY, tableHeaderPaint)
            canvas.drawText("COTAÇÃO", MARGIN_HORIZONTAL + 455f, textY, tableHeaderPaint)
            canvas.drawText("TOTAL", MARGIN_HORIZONTAL + 505f, textY, tableHeaderPaint)

            canvas.drawLine(MARGIN_HORIZONTAL, tableBottom, PAGE_WIDTH - MARGIN_HORIZONTAL, tableBottom, dividerPaint)
            yPosition = tableBottom + 4f
        }

        // Draw initial page
        drawFirstPageHeader()
        canvas.drawText("CATÁLOGO DETALHADO DA COLEÇÃO (${items.size} registros)", MARGIN_HORIZONTAL, yPosition, sectionTitlePaint)
        yPosition += 14f
        drawTableHeader()

        // Render Rows
        items.forEachIndexed { index, item ->
            val rowHeight = 24f
            if (yPosition + rowHeight > PAGE_HEIGHT - MARGIN_BOTTOM) {
                // Draw footer for current page
                canvas.drawText("Página $pageNumber • Collector Portfolio", MARGIN_HORIZONTAL, PAGE_HEIGHT - 20f, footerPaint)
                document.finishPage(page)

                // Start new page
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                yPosition = MARGIN_TOP

                // Header for subsequent pages
                canvas.drawText("RELATÓRIO PATRIMONIAL DA COLEÇÃO (Continuação)", MARGIN_HORIZONTAL, yPosition, titlePaint)
                yPosition += 16f
                drawTableHeader()
            }

            val rowY = yPosition + 11f
            // Index
            canvas.drawText("${index + 1}", MARGIN_HORIZONTAL + 4f, rowY, cellSubTextPaint)

            // Name (truncated if necessary)
            val nameText = if (item.name.length > 36) item.name.substring(0, 34) + "..." else item.name
            canvas.drawText(nameText, MARGIN_HORIZONTAL + 26f, rowY, cellTextPaint)

            // Sub info (Rarity, Condition, Language)
            val subDetails = buildString {
                if (item.rarity.isNotBlank() && item.rarity != "Comum") append(item.rarity).append(" • ")
                append(item.condition)
                if (item.language.isNotBlank() && item.language != "N/A") append(" • ").append(item.language)
            }
            val subDetailsShort = if (subDetails.length > 40) subDetails.substring(0, 38) + "..." else subDetails
            canvas.drawText(subDetailsShort, MARGIN_HORIZONTAL + 26f, rowY + 9f, cellSubTextPaint)

            // Category & Collection
            val catText = "${item.type} / ${item.collection.ifBlank { item.subCategory }}"
            val catTextShort = if (catText.length > 28) catText.substring(0, 26) + "..." else catText
            canvas.drawText(catTextShort, MARGIN_HORIZONTAL + 220f, rowY, cellSubTextPaint)

            // Quantity
            canvas.drawText("${item.quantity}", MARGIN_HORIZONTAL + 365f, rowY, cellTextPaint)

            // Purchase Price
            val paidText = if (item.purchasePrice > 0) currency.formatValue(item.purchasePrice) else "-"
            canvas.drawText(paidText, MARGIN_HORIZONTAL + 395f, rowY, cellSubTextPaint)

            // Current Quote
            canvas.drawText(currency.formatValue(item.estimatedValue), MARGIN_HORIZONTAL + 455f, rowY, cellTextPaint)

            // Total Value
            val totalPaint = Paint().apply {
                color = Color.rgb(16, 185, 129)
                textSize = 8f
                isFakeBoldText = true
                isAntiAlias = true
            }
            canvas.drawText(currency.formatValue(item.totalEstimatedValue), MARGIN_HORIZONTAL + 505f, rowY, totalPaint)

            yPosition += rowHeight
            canvas.drawLine(MARGIN_HORIZONTAL, yPosition - 2f, PAGE_WIDTH - MARGIN_HORIZONTAL, yPosition - 2f, dividerPaint)
        }

        // Draw last page footer
        canvas.drawText("Página $pageNumber • Documento gerado pelo Collector App para controle patrimonial", MARGIN_HORIZONTAL, PAGE_HEIGHT - 20f, footerPaint)
        document.finishPage(page)

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return pdfFile
    }

    /**
     * Generates a fully compatible Excel / CSV file with UTF-8 BOM.
     */
    fun exportToExcelCsv(context: Context, items: List<Item>, currency: AppCurrency): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val csvFile = File(exportDir, "Colecao_Completa_$dateStr.csv")

        fun escape(value: String): String {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }

        val csvContent = buildString {
            // UTF-8 BOM for Microsoft Excel compatibility
            append('\uFEFF')

            // Header line with semicolon for Excel pt-BR / international parsing
            append("ID;Nome;Categoria;SubCategoria;Coleção;Número;Raridade;Condição;Idioma;Quantidade;Preço Pago Unitário;Preço Pago Total;Cotação Atual Unitária;Valor Total Estimado;Lucro / Valorização;Local Físico;Favorito;Notas\n")

            items.forEach { item ->
                val paidUnit = item.purchasePrice
                val paidTotal = item.totalPurchasePrice
                val estUnit = item.estimatedValue
                val estTotal = item.totalEstimatedValue
                val profit = estTotal - paidTotal

                append("${item.id};")
                append("${escape(item.name)};")
                append("${escape(item.type)};")
                append("${escape(item.subCategory)};")
                append("${escape(item.collection)};")
                append("${escape(item.itemNumber)};")
                append("${escape(item.rarity)};")
                append("${escape(item.condition)};")
                append("${escape(item.language)};")
                append("${item.quantity};")
                append("${String.format(Locale.US, "%.2f", paidUnit)};")
                append("${String.format(Locale.US, "%.2f", paidTotal)};")
                append("${String.format(Locale.US, "%.2f", estUnit)};")
                append("${String.format(Locale.US, "%.2f", estTotal)};")
                append("${String.format(Locale.US, "%.2f", profit)};")
                append("${escape(item.storageLocation)};")
                append("${if (item.isFavorite) "Sim" else "Não"};")
                append("${escape(item.notes)}\n")
            }
        }

        csvFile.writeText(csvContent, Charsets.UTF_8)
        return csvFile
    }

    /**
     * Triggers the Android Share / Save Sheet for the exported file.
     */
    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Segue em anexo o arquivo de exportação da coleção (${file.name}).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao abrir compartilhador: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Copies CSV or text summary to Clipboard.
     */
    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
    }
}

package com.example.util

import com.example.data.*

object DeckCalculator {

    fun analyzeDeck(
        deck: Deck,
        cards: List<DeckCard>,
        userItems: List<Item>
    ): DeckInventoryAnalysis {
        val rule = FormatRegistry.getRule(deck.game, deck.format)
        val totalCount = cards.sumOf { it.quantity }

        val targetCountText = if (rule.minCards == rule.maxCards) {
            "${rule.minCards} cartas"
        } else {
            "${rule.minCards} a ${rule.maxCards} cartas"
        }

        val isCountValid = totalCount in rule.minCards..rule.maxCards
        val countWarning = when {
            totalCount < rule.minCards -> "Faltam ${rule.minCards - totalCount} cartas para o mínimo do formato (${rule.minCards})."
            totalCount > rule.maxCards -> "Excede em ${totalCount - rule.maxCards} cartas o limite do formato (${rule.maxCards})."
            else -> null
        }

        val legalityIssues = mutableListOf<String>()

        // Check copy limits
        for (card in cards) {
            if (!card.isBasicEnergyOrLand && card.quantity > rule.maxCopiesPerCard) {
                legalityIssues.add("${card.name}: possui ${card.quantity} cópias (limite do formato é ${rule.maxCopiesPerCard}).")
            }
        }

        // Check Pauper rule
        if (rule.onlyCommons) {
            val nonCommons = cards.filter {
                !it.isBasicEnergyOrLand && !it.rarity.contains("Comum", ignoreCase = true) && !it.rarity.contains("Common", ignoreCase = true)
            }
            if (nonCommons.isNotEmpty()) {
                legalityIssues.add("Formato Pauper aceita apenas cartas Comuns. Cartas inválidas: ${nonCommons.take(3).joinToString { it.name }}...")
            }
        }

        // Check Commander rule
        if (rule.hasCommanderOrLeader && deck.commanderOrLeader.isBlank()) {
            val hasFlaggedCommander = cards.any { it.isCommanderOrLeader }
            if (!hasFlaggedCommander) {
                legalityIssues.add("O formato ${rule.name} requer a definição de um Comandante / Líder.")
            }
        }

        // Cross check with user collection
        val tcgItems = userItems.filter { it.isCard }
        var ownedCardsCount = 0
        var missingCardsCount = 0
        val missingCardsList = mutableListOf<DeckCard>()
        var ownedValue = 0.0
        var missingCost = 0.0
        var totalDeckValue = 0.0

        for (card in cards) {
            val cardTotalValue = card.estimatedPriceBrl * card.quantity
            totalDeckValue += cardTotalValue

            // Basic energies and lands are usually considered readily available, but let's check properly
            val matchingOwned = tcgItems.filter {
                it.name.equals(card.name, ignoreCase = true) ||
                it.name.contains(card.name, ignoreCase = true) ||
                (card.itemNumber.isNotBlank() && it.itemNumber.equals(card.itemNumber, ignoreCase = true))
            }.sumOf { it.quantity }

            if (card.isBasicEnergyOrLand) {
                // Basic energy/land: count as owned if user has at least 1 or count full
                val ownedPart = if (matchingOwned > 0) card.quantity.coerceAtMost(matchingOwned) else card.quantity
                ownedCardsCount += ownedPart
                ownedValue += (card.estimatedPriceBrl * ownedPart)
                val missingPart = card.quantity - ownedPart
                if (missingPart > 0) {
                    missingCardsCount += missingPart
                    missingCost += (card.estimatedPriceBrl * missingPart)
                }
            } else {
                val ownedPart = card.quantity.coerceAtMost(matchingOwned)
                val missingPart = card.quantity - ownedPart

                ownedCardsCount += ownedPart
                ownedValue += (card.estimatedPriceBrl * ownedPart)

                if (missingPart > 0) {
                    missingCardsCount += missingPart
                    missingCost += (card.estimatedPriceBrl * missingPart)
                    missingCardsList.add(card.copy(quantity = missingPart))
                }
            }
        }

        val completionPercentage = if (totalCount > 0) {
            ((ownedCardsCount.toDouble() / totalCount.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        // Card type breakdown
        val cardTypeDistribution = cards.groupBy { it.cardType }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }

        return DeckInventoryAnalysis(
            totalCardsCount = totalCount,
            targetCountText = targetCountText,
            isCountValid = isCountValid,
            countWarning = countWarning,
            isLegalityValid = isCountValid && legalityIssues.isEmpty(),
            legalityIssues = legalityIssues,
            ownedCardsCount = ownedCardsCount,
            missingCardsCount = missingCardsCount,
            completionPercentage = completionPercentage,
            totalDeckMarketValue = totalDeckValue,
            ownedMarketValue = ownedValue,
            missingEstimatedCost = missingCost,
            cardTypeDistribution = cardTypeDistribution,
            missingCards = missingCardsList
        )
    }

    fun exportDecklistText(deck: Deck, cards: List<DeckCard>): String {
        val sb = StringBuilder()
        sb.appendLine("// Deck: ${deck.name}")
        sb.appendLine("// Jogo: ${deck.game} | Formato: ${deck.format}")
        if (deck.commanderOrLeader.isNotBlank()) {
            sb.appendLine("// Comandante/Líder: ${deck.commanderOrLeader}")
        }
        sb.appendLine("// Total de Cartas: ${cards.sumOf { it.quantity }}")
        sb.appendLine()

        val grouped = cards.groupBy { it.cardType }
        for ((type, list) in grouped) {
            sb.appendLine("// === $type (${list.sumOf { it.quantity }}) ===")
            for (card in list) {
                val setInfo = if (card.collection.isNotBlank()) " [${card.collection}]" else ""
                val numInfo = if (card.itemNumber.isNotBlank()) " #${card.itemNumber}" else ""
                sb.appendLine("${card.quantity} ${card.name}$setInfo$numInfo")
            }
            sb.appendLine()
        }

        return sb.toString().trim()
    }
}

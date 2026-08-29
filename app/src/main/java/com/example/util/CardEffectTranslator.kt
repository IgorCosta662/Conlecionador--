package com.example.util

import com.example.BuildConfig
import com.example.api.GeminiClient
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.Content
import com.example.api.TcgOnlineService
import com.example.api.scryfall.ScryfallDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CardEffectDetails(
    val originalText: String = "",
    val translatedEffect: String = "",
    val cardAttacks: String = "",
    val cardHp: String = "",
    val cardArtist: String = "",
    val manaCost: String = "",
    val typeLine: String = "",
    val flavorText: String = ""
)

object CardEffectTranslator {

    // Common MTG Keywords Mapping
    private val mtgKeywords = mapOf(
        "Flying" to "Voar",
        "Trample" to "Atropelar",
        "Lifelink" to "Vínculo com a vida",
        "First strike" to "Iniciativa",
        "Double strike" to "Golpe duplo",
        "Deathtouch" to "Toque mortífero",
        "Vigilance" to "Vigilância",
        "Flash" to "Lampejo",
        "Haste" to "Ímpeto",
        "Menace" to "Ameaçar",
        "Reach" to "Alcance",
        "Hexproof" to "Resistência a magia",
        "Indestructible" to "Indestrutível",
        "Defender" to "Defensor",
        "Ward" to "Salvaguarda",
        "Scry" to "Vidência",
        "Surveil" to "Vigiar",
        "Mill" to "Triturar",
        "Flashback" to "Recapitular",
        "Kicker" to "Reforçar",
        "Prowess" to "Destreza",
        "Convoke" to "Convocar",
        "Delve" to "Escavar",
        "Cascade" to "Cascata",
        "Storm" to "Rajada",
        "Evoke" to "Evocar",
        "Rebound" to "Rebote",
        "Equip" to "Equipar",
        "Crew" to "Tripular",
        "Proliferate" to "Proliferar",
        "Phasing" to "Desfasamento",
        "Cycling" to "Reciclar",
        "Morph" to "Metamorfose",
        "Mutate" to "Mutação",
        "Enchant" to "Encantar",
        "Fight" to "Lutar",
        "Blink" to "Piscar"
    )

    // Common phrase substitutions for Magic and general TCGs
    private val mtgPhrases = listOf(
        Regex("(?i)When(ever)? this creature enters the battlefield") to "Quando esta criatura entra no campo de batalha",
        Regex("(?i)When(ever)? this permanent enters the battlefield") to "Quando esta permanente entra no campo de batalha",
        Regex("(?i)When(ever)? this creature enters") to "Quando esta criatura entra no campo de batalha",
        Regex("(?i)When(ever)? this permanent enters") to "Quando esta permanente entra no campo de batalha",
        Regex("(?i)When(ever)? (.+) enters the battlefield") to "Quando $2 entra no campo de batalha",
        Regex("(?i)When(ever)? (.+) enters") to "Quando $2 entra no campo de batalha",
        Regex("(?i)Whenever this creature attacks") to "Toda vez que esta criatura atacar",
        Regex("(?i)Whenever this creature deals combat damage to a player") to "Toda vez que esta criatura causar dano de combate a um jogador",
        Regex("(?i)Whenever you cast a spell") to "Toda vez que você conjurar uma mágica",
        Regex("(?i)Whenever you cast an instant or sorcery spell") to "Toda vez que você conjurar uma mágica instantânea ou de feitiço",
        Regex("(?i)At the beginning of your upkeep") to "No início de sua manutenção",
        Regex("(?i)At the beginning of your end step") to "No início de sua etapa final",
        Regex("(?i)At the beginning of combat on your turn") to "No início do combate no seu turno",
        Regex("(?i)Draw a card") to "Compre um card",
        Regex("(?i)Draw two cards") to "Compre dois cards",
        Regex("(?i)Draw three cards") to "Compre três cards",
        Regex("(?i)Draws? (\\d+) cards?") to "Compre $1 cards",
        Regex("(?i)Discard a card") to "Descarte um card",
        Regex("(?i)Discard two cards") to "Descarte dois cards",
        Regex("(?i)Target player draws") to "O jogador alvo compra",
        Regex("(?i)Target player discards") to "O jogador alvo descarta",
        Regex("(?i)Target creature gets ([+-]\\d+/[+-]\\d+) until end of turn") to "A criatura alvo recebe $1 até o final do turno",
        Regex("(?i)Target opponent") to "O oponente alvo",
        Regex("(?i)Each opponent") to "Cada oponente",
        Regex("(?i)Each player") to "Cada jogador",
        Regex("(?i)Counter target spell") to "Anule a mágica alvo",
        Regex("(?i)Destroy target creature") to "Destrua a criatura alvo",
        Regex("(?i)Destroy target permanent") to "Destrua a permanente alvo",
        Regex("(?i)Destroy target artifact or enchantment") to "Destrua o artefato ou encantamento alvo",
        Regex("(?i)Destroy target") to "Destrua o alvo",
        Regex("(?i)Exile target creature") to "Exile a criatura alvo",
        Regex("(?i)Exile target permanent") to "Exile a permanente alvo",
        Regex("(?i)Exile target") to "Exile o alvo",
        Regex("(?i)Return target permanent to its owner's hand") to "Devolva a permanente alvo para a mão de seu dono",
        Regex("(?i)Return target creature to its owner's hand") to "Devolva a criatura alvo para a mão de seu dono",
        Regex("(?i)Search your library for a (.+?) card, reveal it, and put it into your hand. Then shuffle.") to "Procure em seu grimório por um card de $1, revele-o e coloque-o em sua mão. Depois embaralhe.",
        Regex("(?i)Search your library for a (.+?) card, then shuffle") to "Procure em seu grimório por um card de $1 e depois embaralhe",
        Regex("(?i)Search your library for") to "Procure em seu grimório por",
        Regex("(?i)put it onto the battlefield") to "coloque-o no campo de batalha",
        Regex("(?i)put it onto the battlefield tapped") to "coloque-o no campo de batalha virado",
        Regex("(?i)Then shuffle your library") to "Depois embaralhe seu grimório",
        Regex("(?i)Then shuffle") to "Depois embaralhe",
        Regex("(?i)You gain (\\d+) life") to "Você ganha $1 pontos de vida",
        Regex("(?i)deals (\\d+) damage to any target") to "causa $1 de dano a qualquer alvo",
        Regex("(?i)deals (\\d+) damage to target creature") to "causa $1 de dano à criatura alvo",
        Regex("(?i)deals (\\d+) damage to each opponent") to "causa $1 de dano a cada oponente",
        Regex("(?i)deals (\\d+) damage to each creature") to "causa $1 de dano a cada criatura",
        Regex("(?i)deals (\\d+) damage to") to "causa $1 de dano a",
        Regex("(?i)Until end of turn") to "Até o final do turno",
        Regex("(?i)Tap target") to "Vire o alvo",
        Regex("(?i)Untap target") to "Desvire o alvo",
        Regex("(?i)Create a (\\d+)/(\\d+) (.+?) creature token") to "Crie uma ficha de criatura $1/$2 $3",
        Regex("(?i)Create a Treasure token") to "Crie uma ficha de Tesouro",
        Regex("(?i)put a \\+1/\\+1 counter on") to "coloque um marcador +1/+1 em",
        Regex("(?i)put (\\d+) \\+1/\\+1 counters on") to "coloque $1 marcadores +1/+1 em",
        Regex("(?i)protection from all colors") to "proteção contra todas as cores",
        Regex("(?i)protection from") to "proteção contra",
        Regex("(?i)cannot be countered") to "não pode ser anulado(a)",
        Regex("(?i)spells you cast cost \\{(\\d+)\\} less to cast") to "mágicas que você conjura custam {$1} a menos para serem conjuradas",
        Regex("(?i)as long as it's your turn") to "enquanto for o seu turno",
        Regex("(?i)You may cast") to "Você pode conjurar",
        Regex("(?i)You may play") to "Você pode jogar"
    )

    // Pokémon TCG phrase patterns
    private val pokemonPhrases = listOf(
        Regex("(?i)Ability:") to "Habilidade:",
        Regex("(?i)Once during your turn") to "Uma vez durante o seu turno",
        Regex("(?i)If this Pokémon is in the Active Spot") to "Se este Pokémon estiver no Campo Ativo",
        Regex("(?i)If this Pokémon is on your Bench") to "Se este Pokémon estiver no seu Banco",
        Regex("(?i)You may search your deck for") to "Você pode procurar em seu baralho por",
        Regex("(?i)Search your deck for") to "Procure em seu baralho por",
        Regex("(?i)Shuffle your deck") to "Embaralhe o seu baralho",
        Regex("(?i)Discard an Energy from this Pokémon") to "Descarte uma Energia deste Pokémon",
        Regex("(?i)Discard (\\d+) Energy cards? from this Pokémon") to "Descarte $1 cards de Energia deste Pokémon",
        Regex("(?i)Discard your hand and draw (\\d+) cards") to "Descarte a sua mão e compre $1 cards",
        Regex("(?i)Attach an Energy card from your hand to") to "Ligue um card de Energia da sua mão a",
        Regex("(?i)Attach a Basic Energy card from your discard pile to") to "Ligue um card de Energia Básica da sua pilha de descarte a",
        Regex("(?i)Flip a coin\\. If heads,") to "Jogue uma moeda. Se sair cara,",
        Regex("(?i)Flip a coin\\. If tails,") to "Jogue uma moeda. Se sair coroa,",
        Regex("(?i)Flip (\\d+) coins") to "Jogue $1 moedas",
        Regex("(?i)During your next turn, this Pokémon can't attack") to "Durante o seu próximo turno, este Pokémon não pode atacar",
        Regex("(?i)During your opponent's next turn") to "Durante o próximo turno do seu oponente",
        Regex("(?i)This attack does (\\d+) damage to") to "Este ataque causa $1 de dano a",
        Regex("(?i)This attack does (\\d+) damage for each") to "Este ataque causa $1 de dano para cada",
        Regex("(?i)This attack does (\\d+) more damage") to "Este ataque causa $1 a mais de dano",
        Regex("(?i)This attack's damage isn't affected by Weakness or Resistance") to "O dano deste ataque não é afetado por Fraqueza ou Resistência",
        Regex("(?i)Your opponent's Active Pokémon is now Asleep") to "O Pokémon Ativo do oponente agora está Adormecido",
        Regex("(?i)Your opponent's Active Pokémon is now Confused") to "O Pokémon Ativo do oponente agora está Confuso",
        Regex("(?i)Your opponent's Active Pokémon is now Paralyzed") to "O Pokémon Ativo do oponente agora está Paralisado",
        Regex("(?i)Your opponent's Active Pokémon is now Poisoned") to "O Pokémon Ativo do oponente agora está Envenenado",
        Regex("(?i)Your opponent's Active Pokémon is now Burned") to "O Pokémon Ativo do oponente agora está Queimado",
        Regex("(?i)Heal (\\d+) damage from this Pokémon") to "Cure $1 de dano deste Pokémon",
        Regex("(?i)Look at the top (\\d+) cards of your deck") to "Olhe os $1 cards de cima do seu baralho",
        Regex("(?i)Put this card into your hand") to "Coloque este card na sua mão",
        Regex("(?i)Switch this Pokémon with 1 of your Benched Pokémon") to "Troque este Pokémon por 1 dos seus Pokémon no Banco",
        Regex("(?i)Switch out your opponent's Active Pokémon to the Bench") to "Mande o Pokémon Ativo do oponente para o Banco",
        Regex("(?i)You may play only 1 Supporter card during your turn") to "Você só pode jogar 1 card de Apoiador durante o seu turno",
        Regex("(?i)You can play this card only when") to "Você só pode jogar este card quando"
    )

    /**
     * Translates English card rules / oracle text / effects into Brazilian Portuguese
     */
    fun translateToPortuguese(englishText: String, franchise: String = ""): String {
        if (englishText.isBlank()) return ""

        var translated = englishText

        // Apply franchise-specific patterns
        if (franchise.contains("Pokémon", true) || franchise.contains("Pokemon", true)) {
            pokemonPhrases.forEach { (pattern, replacement) ->
                translated = pattern.replace(translated, replacement)
            }
        } else {
            mtgPhrases.forEach { (pattern, replacement) ->
                translated = pattern.replace(translated, replacement)
            }
        }

        // Apply general MTG / TCG keywords
        mtgKeywords.forEach { (en, pt) ->
            val regex = Regex("\\b$en\\b", RegexOption.IGNORE_CASE)
            translated = regex.replace(translated, pt)
        }

        return translated
    }

    /**
     * Translates card text using Gemini AI when available, with fallback to rule engine
     */
    suspend fun translateWithGeminiOrFallback(
        cardName: String,
        franchise: String,
        englishText: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "DEFAULT_API_KEY") {
            try {
                val systemPrompt = """
                    Você é um tradutor oficial especializado em TCGs (Magic: The Gathering, Pokémon TCG, Yu-Gi-Oh!, One Piece Card Game, Lorcana).
                    Traduza o texto e efeitos da carta informada em inglês para o Português do Brasil oficial e idiomático dos card games.
                    Mantenha a fidelidade total às regras do jogo, nomes oficiais de habilidades e clareza absoluta sobre o que a carta faz.
                    Retorne apenas o texto traduzido e explicado em português de forma clara, sem preâmbulos.
                """.trimIndent()

                val prompt = "Carta: $cardName ($franchise)\nTexto Original:\n$englishText\nTraduza para o Português do Brasil oficial do TCG:"

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                    generationConfig = GenerationConfig(temperature = 0.2f)
                )

                val response = GeminiClient.api.generateContent(apiKey, request)
                val geminiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!geminiText.isNullOrBlank()) {
                    return@withContext geminiText.trim()
                }
            } catch (_: Exception) {
                // Fallback below
            }
        }

        // Fallback to pattern-based translation
        return@withContext translateToPortuguese(englishText, franchise)
    }

    /**
     * Searches card rules & effects online (Scryfall or TCGDex) and formats Portuguese effects
     */
    suspend fun searchCardEffectsOnline(
        name: String,
        subCategory: String,
        collection: String = "",
        itemNumber: String = ""
    ): CardEffectDetails = withContext(Dispatchers.IO) {
        if (name.isBlank()) return@withContext CardEffectDetails()

        val isMagic = subCategory.contains("Magic", true) || name.contains("Lotus", true) || name.contains("Ring", true)
        val isPokemon = subCategory.contains("Pokémon", true) || subCategory.contains("Pokemon", true)

        if (isMagic) {
            try {
                val scryCard = ScryfallDataService.findCardByFuzzyName(name)
                if (scryCard != null) {
                    val original = scryCard.oracleText ?: scryCard.cardFaces?.joinToString("\n---\n") { "${it.name}: ${it.oracleText ?: ""}" } ?: ""
                    val translated = translateWithGeminiOrFallback(scryCard.name, "Magic: The Gathering", original)
                    return@withContext CardEffectDetails(
                        originalText = original,
                        translatedEffect = translated,
                        cardAttacks = if (scryCard.power != null && scryCard.toughness != null) "Poder/Resistência: ${scryCard.power}/${scryCard.toughness}" else "",
                        cardHp = if (scryCard.loyalty != null) "Lealdade: ${scryCard.loyalty}" else "",
                        cardArtist = scryCard.artist ?: "",
                        manaCost = scryCard.manaCost ?: "",
                        typeLine = scryCard.typeLine ?: "",
                        flavorText = scryCard.flavorText ?: ""
                    )
                }
            } catch (_: Exception) {}
        }

        if (isPokemon) {
            try {
                // 1. Try Portuguese TCGDex first
                val briefsPt = TcgOnlineService.searchPokemonCards(name)
                val targetBrief = briefsPt.firstOrNull { 
                    if (itemNumber.isNotBlank()) it.localId.contains(itemNumber.filter { c -> c.isDigit() }) else true 
                } ?: briefsPt.firstOrNull()

                if (targetBrief != null) {
                    val detail = TcgOnlineService.getPokemonCardDetail(targetBrief.id)
                    if (detail != null) {
                        val attacksFormatted = detail.attacks?.joinToString("\n") { att ->
                            val cost = att.cost?.joinToString(", ") ?: ""
                            val dmg = att.getDamageString().ifBlank { "0" }
                            val effect = if (!att.effect.isNullOrBlank()) " - ${att.effect}" else ""
                            "⚔️ ${att.name} ($dmg dmg) [${cost}]$effect"
                        } ?: ""

                        val abilitiesFormatted = detail.abilities?.joinToString("\n") { ab ->
                            "[${ab.type ?: "Habilidade"}] ${ab.name}: ${ab.effect ?: ""}"
                        } ?: ""

                        val fullEffect = listOfNotNull(
                            if (abilitiesFormatted.isNotBlank()) abilitiesFormatted else null,
                            if (attacksFormatted.isNotBlank()) attacksFormatted else null,
                            if (!detail.effect.isNullOrBlank()) "Efeito: ${detail.effect}" else null,
                            if (!detail.description.isNullOrBlank()) "Descrição: ${detail.description}" else null
                        ).joinToString("\n\n")

                        val translated = translateWithGeminiOrFallback(detail.name, "Pokémon TCG", fullEffect)

                        return@withContext CardEffectDetails(
                            originalText = fullEffect,
                            translatedEffect = translated.ifBlank { fullEffect },
                            cardAttacks = detail.attacks?.joinToString(", ") { "${it.name} (${it.getDamageString()})" } ?: "",
                            cardHp = detail.hp?.let { "HP $it" } ?: "",
                            cardArtist = detail.illustrator ?: "",
                            typeLine = detail.types?.joinToString(", ") ?: (detail.category ?: "Pokémon")
                        )
                    }
                }
            } catch (_: Exception) {}
        }

        // Generic fallback for any card
        val translated = translateWithGeminiOrFallback(name, subCategory, "")
        return@withContext CardEffectDetails(
            originalText = "",
            translatedEffect = translated
        )
    }

    /**
     * Formats a complete summary of what the card does in Portuguese
     */
    fun formatCompleteCardEffectSummary(
        name: String,
        typeLine: String = "",
        manaCost: String = "",
        hp: String = "",
        attacks: String = "",
        translatedEffect: String = "",
        originalText: String = ""
    ): String {
        val sb = StringBuilder()
        if (name.isNotBlank()) sb.append("$name\n")
        if (typeLine.isNotBlank()) sb.append("Tipo: $typeLine\n")
        if (manaCost.isNotBlank()) sb.append("Custo de Mana: $manaCost\n")
        if (hp.isNotBlank()) sb.append("Vida: $hp\n")
        if (attacks.isNotBlank()) sb.append("Ataques: $attacks\n")
        if (translatedEffect.isNotBlank()) {
            sb.append("\nO que a carta faz (Tradução PT-BR):\n$translatedEffect")
        } else if (originalText.isNotBlank()) {
            sb.append("\nRegras / Efeitos:\n$originalText")
        }
        return sb.toString().trim()
    }
}

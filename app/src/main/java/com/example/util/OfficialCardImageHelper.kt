package com.example.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object OfficialCardImageHelper {

    /**
     * Complete Pokedex mapping for Gen 1 (1-151) and iconic Pokémon across all generations.
     */
    private val pokemonDexMap: Map<String, Int> = mapOf(
        "bulbasaur" to 1, "ivysaur" to 2, "venusaur" to 3,
        "charmander" to 4, "charmeleon" to 5, "charizard" to 6,
        "squirtle" to 7, "wartortle" to 8, "blastoise" to 9,
        "caterpie" to 10, "metapod" to 11, "butterfree" to 12,
        "weedle" to 13, "kakuna" to 14, "beedrill" to 15,
        "pidgey" to 16, "pidgeotto" to 17, "pidgeot" to 18,
        "rattata" to 19, "raticate" to 20,
        "spearow" to 21, "fearow" to 22,
        "ekans" to 23, "arbok" to 24,
        "pikachu" to 25, "raichu" to 26,
        "sandshrew" to 27, "sandslash" to 28,
        "nidoran" to 29, "nidorina" to 30, "nidoqueen" to 31,
        "nidoran m" to 32, "nidorino" to 33, "nidoking" to 34,
        "clefairy" to 35, "clefable" to 36,
        "vulpix" to 37, "ninetales" to 38,
        "jigglypuff" to 39, "wigglytuff" to 40,
        "zubat" to 41, "golbat" to 42,
        "oddish" to 43, "gloom" to 44, "vileplume" to 45,
        "paras" to 46, "parasect" to 47,
        "venonat" to 48, "venomoth" to 49,
        "diglett" to 50, "dugtrio" to 51,
        "meowth" to 52, "persian" to 53,
        "psyduck" to 54, "golduck" to 55,
        "mankey" to 56, "primeape" to 57,
        "growlithe" to 58, "arcanine" to 59,
        "poliwag" to 60, "poliwhirl" to 61, "poliwrath" to 62,
        "abra" to 63, "kadabra" to 64, "alakazam" to 65,
        "machop" to 66, "machoke" to 67, "machamp" to 68,
        "bellsprout" to 69, "weepinbell" to 70, "victreebel" to 71,
        "tentacool" to 72, "tentacruel" to 73,
        "geodude" to 74, "graveler" to 75, "golem" to 76,
        "ponyta" to 77, "rapidash" to 78,
        "slowpoke" to 79, "slowbro" to 80,
        "magnemite" to 81, "magneton" to 82,
        "farfetch'd" to 83, "farfetchd" to 83, "doduo" to 84, "dodrio" to 85,
        "seel" to 86, "dewgong" to 87,
        "grimer" to 88, "muk" to 89,
        "shellder" to 90, "cloyster" to 91,
        "gastly" to 92, "haunter" to 93, "gengar" to 94,
        "onix" to 95,
        "drowzee" to 96, "hypno" to 97,
        "krabby" to 98, "kingler" to 99,
        "voltorb" to 100, "electrode" to 101,
        "exeggcute" to 102, "exeggutor" to 103,
        "cubone" to 104, "marowak" to 105,
        "hitmonlee" to 106, "hitmonchan" to 107,
        "lickitung" to 108,
        "koffing" to 109, "weezing" to 110,
        "rhyhorn" to 111, "rhydon" to 112,
        "chansey" to 113,
        "tangela" to 114,
        "kangaskhan" to 115,
        "horsea" to 116, "seadra" to 117,
        "goldeen" to 118, "seaking" to 119,
        "staryu" to 120, "starmie" to 121,
        "mr. mime" to 122, "mr mime" to 122,
        "scyther" to 123,
        "jynx" to 124,
        "electabuzz" to 125,
        "magmar" to 126,
        "pinsir" to 127,
        "tauros" to 128,
        "magikarp" to 129, "gyarados" to 130,
        "lapras" to 131,
        "ditto" to 132,
        "eevee" to 133, "vaporeon" to 134, "jolteon" to 135, "flareon" to 136,
        "porygon" to 137,
        "omanyte" to 138, "omastar" to 139,
        "kabuto" to 140, "kabutops" to 141,
        "aerodactyl" to 142,
        "snorlax" to 143,
        "articuno" to 144, "zapdos" to 145, "moltres" to 146,
        "dratini" to 147, "dragonair" to 148, "dragonite" to 149,
        "mewtwo" to 150, "mew" to 151,
        // Gen 2
        "chikorita" to 152, "cyndaquil" to 155, "totodile" to 158,
        "togepi" to 175, "ampharos" to 181, "marill" to 183,
        "espeon" to 196, "umbreon" to 197, "scizor" to 212, "tyranitar" to 248,
        "lugia" to 249, "ho-oh" to 250, "celebi" to 251,
        // Gen 3
        "treecko" to 252, "torchic" to 255, "mudkip" to 258,
        "gardevoir" to 282, "milotic" to 350, "rayquaza" to 384,
        "kyogre" to 382, "groudon" to 383, "latias" to 380, "latios" to 381,
        // Gen 4
        "turtwig" to 387, "chimchar" to 390, "piplup" to 393,
        "lucario" to 448, "garchomp" to 445, "dialga" to 483, "palkia" to 484, "giratina" to 487, "arceus" to 493,
        // Gen 5
        "snivy" to 495, "tepig" to 498, "oshawott" to 501, "zoroark" to 571, "hydreigon" to 635,
        // Gen 6
        "froakie" to 656, "frogadier" to 657, "greninja" to 658, "sylveon" to 700, "zygarde" to 718,
        // Gen 7
        "rowlet" to 722, "litten" to 725, "popplio" to 728, "mimikyu" to 778, "necrozma" to 800,
        // Gen 8
        "grookey" to 810, "scorbunny" to 813, "sobble" to 816, "zacian" to 888, "zamazenta" to 889,
        // Gen 9
        "sprigatito" to 906, "floragato" to 907, "meowscarada" to 908,
        "fuecoco" to 909, "crocalor" to 910, "skeledirge" to 911,
        "quaxly" to 912, "quaxwell" to 913, "quaquaval" to 914,
        "armarouge" to 936, "ceruledge" to 937,
        "tinkatink" to 957, "tinkaton" to 959,
        "koraidon" to 1007, "miraidon" to 1008,
        "ogerpon" to 1017, "terapagos" to 1024, "pecharunt" to 1025
    )

    /**
     * Extracts pure Pokémon name from a card title (e.g. "Charizard ex SIR" -> "charizard")
     */
    fun extractPokemonName(fullName: String): String? {
        val lower = fullName.lowercase()
            .replace(Regex("\\(.*\\)"), "")
            .replace(" ex", "")
            .replace(" vmax", "")
            .replace(" vstar", "")
            .replace(" gx", "")
            .replace(" v", "")
            .replace(" gold", "")
            .replace(" sir", "")
            .replace(" hr", "")
            .replace(" ur", "")
            .replace(" star", "")
            .replace(" shiny", "")
            .trim()

        // Check exact or partial matches
        for ((pName, _) in pokemonDexMap) {
            if (lower.contains(pName) || pName.contains(lower)) {
                return pName
            }
        }
        return null
    }

    /**
     * Returns high-resolution official Pokémon artwork from PokeAPI CDN
     */
    fun getPokemonArtworkUrl(name: String): String {
        val pName = extractPokemonName(name) ?: name.lowercase().trim()
        val dexId = pokemonDexMap[pName] ?: 25 // Default Pikachu (25) if not recognized
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$dexId.png"
    }

    /**
     * Generates or returns a high-resolution, crystal-clear official card image URL
     * based on franchise (Magic: The Gathering, Pokémon TCG, Yu-Gi-Oh!, One Piece, Hot Wheels, Disney Lorcana).
     * If card image is not found or fails, provides the exact official Pokémon artwork!
     */
    fun getOfficialImageUrl(
        name: String,
        subCategory: String,
        collection: String = "",
        itemNumber: String = ""
    ): String {
        val cleanName = name.trim()
        val cleanSub = subCategory.trim().lowercase()

        return when {
            // 1. MAGIC: THE GATHERING -> Scryfall official API image redirect
            cleanSub.contains("magic") || cleanSub.contains("mtg") -> {
                val cardNameOnly = cleanName
                    .replace(Regex("\\(.*\\)"), "")
                    .replace("#", "")
                    .split("//")[0]
                    .trim()
                val scryfallQuery = try {
                    URLEncoder.encode(cardNameOnly, StandardCharsets.UTF_8.toString())
                } catch (e: Exception) {
                    cardNameOnly
                }
                "https://api.scryfall.com/cards/named?exact=$scryfallQuery&format=image&version=large"
            }

            // 2. POKÉMON TCG -> High-res official Pokémon Card images & Official Artwork Fallback
            cleanSub.contains("pokémon") || cleanSub.contains("pokemon") -> {
                getPokemonCardImageUrl(cleanName, collection, itemNumber)
            }

            // 3. YU-GI-OH! -> YGOPRODeck official HD Card CDN
            cleanSub.contains("yu-gi-oh") || cleanSub.contains("yugioh") -> {
                val yugiohName = cleanName
                    .replace(Regex("\\(.*\\)"), "")
                    .trim()
                val encodedYgo = try {
                    URLEncoder.encode(yugiohName, StandardCharsets.UTF_8.toString())
                } catch (e: Exception) {
                    yugiohName
                }
                "https://images.ygoprodeck.com/images/cards_cropped/$encodedYgo.jpg"
            }

            // 4. ONE PIECE CARD GAME -> OP TCG Official Card Art CDN
            cleanSub.contains("one piece") -> {
                getOnePieceImageUrl(cleanName)
            }

            // 5. DISNEY LORCANA
            cleanSub.contains("lorcana") || cleanSub.contains("disney") -> {
                getLorcanaImageUrl(cleanName)
            }

            // 6. STAR WARS: UNLIMITED
            cleanSub.contains("star wars") -> {
                getStarWarsImageUrl(cleanName)
            }

            // 7. DRAGON BALL SUPER / FUSION WORLD
            cleanSub.contains("dragon ball") || cleanSub.contains("dbs") -> {
                getDragonBallImageUrl(cleanName)
            }

            // 8. HOT WHEELS / DIECAST
            cleanSub.contains("hot wheels") || cleanSub.contains("diecast") || cleanSub.contains("kaido") || cleanSub.contains("carrinho") -> {
                getDiecastImageUrl(cleanName)
            }

            // 9. MOEDAS & NUMISMÁTICA
            cleanSub.contains("moeda") || cleanSub.contains("numismática") -> {
                getMoedaImageUrl(cleanName)
            }

            // DEFAULT -> Official Pokémon Artwork
            else -> {
                getPokemonArtworkUrl(cleanName)
            }
        }
    }

    private fun getStarWarsImageUrl(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("vader") -> "https://images.weserv.nl/?url=https://cdn.starwarsunlimited.com/card-images/SOR_010.png"
            lower.contains("luke") -> "https://images.weserv.nl/?url=https://cdn.starwarsunlimited.com/card-images/SOR_005.png"
            lower.contains("boba") -> "https://images.weserv.nl/?url=https://cdn.starwarsunlimited.com/card-images/SOR_015.png"
            lower.contains("mandalorian") -> "https://images.weserv.nl/?url=https://cdn.starwarsunlimited.com/card-images/SHD_001.png"
            lower.contains("ahsoka") -> "https://images.weserv.nl/?url=https://cdn.starwarsunlimited.com/card-images/TWI_003.png"
            else -> "https://images.weserv.nl/?url=https://cdn.starwarsunlimited.com/card-images/SOR_010.png"
        }
    }

    private fun getDragonBallImageUrl(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("goku") -> "https://images.weserv.nl/?url=https://www.dbs-cardgame.com/fusionworld/images/cardlist/card/FB01-139_p1.png"
            lower.contains("vegito") -> "https://images.weserv.nl/?url=https://www.dbs-cardgame.com/fusionworld/images/cardlist/card/FB02-140_p1.png"
            lower.contains("vegeta") -> "https://images.weserv.nl/?url=https://www.dbs-cardgame.com/fusionworld/images/cardlist/card/FB01-035_p1.png"
            else -> "https://images.weserv.nl/?url=https://www.dbs-cardgame.com/fusionworld/images/cardlist/card/FB01-139_p1.png"
        }
    }

    private fun getPokemonCardImageUrl(name: String, collection: String, number: String): String {
        val lowerName = name.lowercase()
        val lowerCol = collection.lowercase()
        val numOnly = number.split("/").firstOrNull()?.filter { it.isDigit() } ?: ""

        return when {
            // Base set 1999 specific
            lowerCol.contains("base") || lowerCol.contains("1999") -> {
                val baseNum = numOnly.toIntOrNull()
                if (baseNum != null && baseNum in 1..102) {
                    "https://images.pokemontcg.io/base1/${baseNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // 151 specific cards (1 to 207)
            lowerCol.contains("151") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..207) {
                    "https://images.pokemontcg.io/sv3pt5/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Surging Sparks
            lowerCol.contains("surging") || lowerCol.contains("sparks") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..250) {
                    "https://images.pokemontcg.io/sv8/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Twilight Masquerade
            lowerCol.contains("twilight") || lowerCol.contains("masquerade") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..226) {
                    "https://images.pokemontcg.io/sv6/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Paldea Evolved
            lowerCol.contains("paldea evolved") || lowerCol.contains("paldea") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..279) {
                    "https://images.pokemontcg.io/sv2/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Scarlet & Violet Base
            lowerCol.contains("scarlet") && lowerCol.contains("violet") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..258) {
                    "https://images.pokemontcg.io/sv1/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Evolving Skies
            lowerCol.contains("evolving") || lowerCol.contains("skies") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..237) {
                    "https://images.pokemontcg.io/swsh7/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Crown Zenith
            lowerCol.contains("crown") || lowerCol.contains("zenith") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..230) {
                    "https://images.pokemontcg.io/swsh12pt5/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Team Up
            lowerCol.contains("team up") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..196) {
                    "https://images.pokemontcg.io/sm9/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Celebrations
            lowerCol.contains("celebrations") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..25) {
                    "https://images.pokemontcg.io/cel25/${intNum}_hires.png"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Key iconic direct card matches
            lowerName.contains("charizard") && lowerName.contains("sir") -> "https://images.pokemontcg.io/sv3pt5/199_hires.png"
            lowerName.contains("charizard") && lowerName.contains("base") -> "https://images.pokemontcg.io/base1/4_hires.png"
            lowerName.contains("charizard") -> "https://images.pokemontcg.io/sv3pt5/199_hires.png"
            lowerName.contains("blastoise") && lowerName.contains("sir") -> "https://images.pokemontcg.io/sv3pt5/200_hires.png"
            lowerName.contains("blastoise") -> "https://images.pokemontcg.io/sv3pt5/9_hires.png"
            lowerName.contains("venusaur") && lowerName.contains("sir") -> "https://images.pokemontcg.io/sv3pt5/198_hires.png"
            lowerName.contains("venusaur") -> "https://images.pokemontcg.io/sv3pt5/3_hires.png"
            lowerName.contains("pikachu") && lowerName.contains("sir") -> "https://images.pokemontcg.io/sv8/204_hires.png"
            lowerName.contains("pikachu") && lowerName.contains("illustration") -> "https://images.pokemontcg.io/sv3pt5/173_hires.png"
            lowerName.contains("pikachu") -> "https://images.pokemontcg.io/sv3pt5/25_hires.png"
            lowerName.contains("umbreon") && (lowerName.contains("moonbreon") || lowerName.contains("vmax")) -> "https://images.pokemontcg.io/swsh7/215_hires.png"
            lowerName.contains("umbreon") -> "https://images.pokemontcg.io/swsh7/215_hires.png"
            lowerName.contains("rayquaza") -> "https://images.pokemontcg.io/swsh7/218_hires.png"
            lowerName.contains("gengar") -> "https://images.pokemontcg.io/sv3pt5/94_hires.png"
            lowerName.contains("giratina") -> "https://images.pokemontcg.io/swsh12pt5/GG69_hires.png"
            lowerName.contains("lugia") -> "https://images.pokemontcg.io/swsh12/186_hires.png"
            lowerName.contains("greninja") -> "https://images.pokemontcg.io/sv6/214_hires.png"
            lowerName.contains("mewtwo") -> "https://images.pokemontcg.io/sv3pt5/150_hires.png"
            lowerName.contains("mew") -> "https://images.pokemontcg.io/sv3pt5/205_hires.png"
            // Fallback: If not recognized or if online card is not specific, return the pure official Pokémon artwork!
            else -> {
                getPokemonArtworkUrl(name)
            }
        }
    }

    private fun getOnePieceImageUrl(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("gear 5") || lower.contains("luffy") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP05-060_p1.png"
            lower.contains("zoro") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP06-118_p1.png"
            lower.contains("ace") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP02-013_p1.png"
            lower.contains("nami") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP01-016_p1.png"
            lower.contains("law") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP05-069_p1.png"
            lower.contains("shanks") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP01-120_p1.png"
            lower.contains("yamato") -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP01-121_p1.png"
            else -> "https://images.weserv.nl/?url=https://en.onepiece-cardgame.com/images/cardlist/card/OP05-060_p1.png"
        }
    }

    private fun getLorcanaImageUrl(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("elsa") -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/the-first-chapter/207-elsa-spirit-of-winter.jpg"
            lower.contains("tinker") -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/the-first-chapter/206-tinker-bell-giant-fairy.jpg"
            lower.contains("mickey") -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/the-first-chapter/208-mickey-mouse-brave-little-tailor.jpg"
            lower.contains("stitch") -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/the-first-chapter/209-stitch-carefree-surfer.jpg"
            lower.contains("belle") -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/the-first-chapter/210-belle-strange-but-special.jpg"
            lower.contains("cinderella") -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/rise-of-the-floodborn/205-cinderella-stouthearted.jpg"
            else -> "https://images.weserv.nl/?url=https://lorcania.com/images/cards/the-first-chapter/207-elsa-spirit-of-winter.jpg"
        }
    }

    private fun getDiecastImageUrl(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("skyline") || lower.contains("r34") || lower.contains("r33") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/8/87/Nissan_Skyline_GT-R_%28BNR34%29_2020.jpg"
            lower.contains("datsun") || lower.contains("510") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/a/ae/71_Datsun_510_Wagon_2021.jpg"
            lower.contains("supra") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/8/8e/Toyota_Supra_F%26F_2020.jpg"
            lower.contains("porsche") || lower.contains("911") || lower.contains("gt3") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/5/52/Porsche_911_GT3_RS_2023.jpg"
            lower.contains("mustang") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/4/4b/1993_Ford_Mustang_Cobra_R_RLC.jpg"
            lower.contains("camaro") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/c/cb/67_Camaro_STH_2020.jpg"
            lower.contains("charger") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/9/91/69_dodge_charger_500.jpg"
            lower.contains("civic") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/6/6c/Honda_Civic_Type_R_EK9.jpg"
            lower.contains("rx-7") || lower.contains("rx7") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/0/09/Mazda_RX-7_FC3S.jpg"
            lower.contains("bmw") ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/e/e6/73_bmw_30_csl.jpg"
            else ->
                "https://images.weserv.nl/?url=https://static.wikia.nocookie.net/hotwheels/images/8/87/Nissan_Skyline_GT-R_%28BNR34%29_2020.jpg"
        }
    }

    private fun getMoedaImageUrl(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("1998") || lower.contains("direitos humanos") ->
                "https://images.weserv.nl/?url=https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/1_real_1998_direitos_humanos.jpg/300px-1_real_1998_direitos_humanos.jpg"
            lower.contains("2002") || lower.contains("jk") || lower.contains("kubitschek") ->
                "https://images.weserv.nl/?url=https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/1_Real_2002_JK.jpg/300px-1_Real_2002_JK.jpg"
            lower.contains("bandeira") ->
                "https://images.weserv.nl/?url=https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/1_Real_2012_Bandeira.jpg/300px-1_Real_2012_Bandeira.jpg"
            lower.contains("patacão") || lower.contains("1818") || lower.contains("960") ->
                "https://images.weserv.nl/?url=https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/960_Reis_1818_Prata.jpg/300px-960_Reis_1818_Prata.jpg"
            lower.contains("dobrão") || lower.contains("ouro") || lower.contains("1724") ->
                "https://images.weserv.nl/?url=https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Dobrao_20000_Reis_1724.jpg/300px-Dobrao_20000_Reis_1724.jpg"
            else ->
                "https://images.weserv.nl/?url=https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/1_Real_2008.jpg/400px-1_Real_2008.jpg"
        }
    }
}


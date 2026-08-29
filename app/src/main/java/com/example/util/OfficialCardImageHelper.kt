package com.example.util

import com.example.api.TcgOnlineService
import com.example.api.scryfall.ScryfallDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class OfficialCardPrintOption(
    val id: String = "",
    val title: String = "",
    val setName: String = "",
    val cardNumber: String = "",
    val imageUrl: String = "",
    val rarity: String = ""
)

object OfficialCardImageHelper {

    // Common Portuguese to English Magic: The Gathering card name dictionary
    private val mtgPtToEnMap = mapOf(
        "lótus negra" to "Black Lotus",
        "lotus negra" to "Black Lotus",
        "o um anel" to "The One Ring",
        "anel solar" to "Sol Ring",
        "mox de diamante" to "Mox Diamond",
        "mox diamante" to "Mox Diamond",
        "mox de safira" to "Mox Sapphire",
        "mox de rubi" to "Mox Ruby",
        "mox de azeviche" to "Mox Jet",
        "mox de esmeralda" to "Mox Emerald",
        "mox de pérola" to "Mox Pearl",
        "cópia furtiva" to "Phantasmal Image",
        "força da vontade" to "Force of Will",
        "força de vontade" to "Force of Will",
        "força da negação" to "Force of Negation",
        "raio" to "Lightning Bolt",
        "contramágica" to "Counterspell",
        "passagem lendária" to "Fabled Passage",
        "tumba dos ancestrais" to "Ancient Tomb",
        "cidade dos traidores" to "City of Traitors",
        "berço de gaea" to "Gaea's Cradle",
        "savana" to "Savannah",
        "taiga" to "Taiga",
        "tundra" to "Tundra",
        "mar subterrâneo" to "Underground Sea",
        "mar vulcânico" to "Volcanic Island",
        "pantanal verdejante" to "Verdant Catacombs",
        "delta poluído" to "Polluted Delta",
        "praia inundada" to "Flooded Strand",
        "sopé da montanha arborizado" to "Wooded Foothills",
        "planalto árido" to "Arid Mesa",
        "charneca escaldante" to "Scalding Tarn",
        "floresta tropical enevoada" to "Misty Rainforest",
        "pântano ensanguentado" to "Bloodstained Mire",
        "estepe árida" to "Windswept Heath",
        "várzea pantanosa" to "Marsh Flats",
        "terreno pisoteável" to "Stomping Ground",
        "fonte de água benta" to "Hallowed Fountain",
        "túmulo alagado" to "Watery Grave",
        "cripta de sangue" to "Blood Crypt",
        "chaminé de vapor" to "Steam Vents",
        "pântano" to "Swamp",
        "floresta" to "Forest",
        "ilha" to "Island",
        "montanha" to "Mountain",
        "planície" to "Plains",
        "jace, o escultor de mentes" to "Jace, the Mind Sculptor",
        "liliana do véu" to "Liliana of the Veil",
        "carniceiro de karn" to "Karn Liberated",
        "urgoroth" to "Ulamog, the Infinite Gyre",
        "emrakul" to "Emrakul, the Aeons Torn",
        "ragavan" to "Ragavan, Nimble Pilferer",
        "sheoldred" to "Sheoldred, the Apocalypse",
        "atraxa" to "Atraxa, Grand Unifier"
    )

    /**
     * Comprehensive Pokédex mapping covering Gen 1 to Gen 9 + iconic forms
     */
    private val pokemonDexMap: Map<String, Int> = mapOf(
        // Gen 1 (1 - 151)
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

        // Gen 2 (152 - 251)
        "chikorita" to 152, "bayleef" to 153, "meganium" to 154,
        "cyndaquil" to 155, "quilava" to 156, "typhlosion" to 157,
        "totodile" to 158, "croconaw" to 159, "feraligatr" to 160,
        "sentret" to 161, "furret" to 162, "hoothoot" to 163, "noctowl" to 164,
        "crobat" to 169, "chinchou" to 170, "lanturn" to 171,
        "pichu" to 172, "cleffa" to 173, "igglybuff" to 174,
        "togepi" to 175, "togetic" to 176,
        "natu" to 177, "xatu" to 178, "mareep" to 179, "flaaffy" to 180, "ampharos" to 181,
        "marill" to 183, "azumarill" to 184, "sudowoodo" to 185,
        "espeon" to 196, "umbreon" to 197, "murkrow" to 198, "slowking" to 199,
        "misdreavus" to 200, "unown" to 201, "wobbuffet" to 202,
        "steelix" to 208, "scizor" to 212, "shuckle" to 213, "heracross" to 214,
        "sneasel" to 215, "teddiursa" to 216, "ursaring" to 217,
        "swinub" to 220, "piloswine" to 221, "corsola" to 222,
        "skarmory" to 227, "houndour" to 228, "houndoom" to 229,
        "kingdra" to 230, "donphan" to 232, "porygon2" to 233,
        "smeargle" to 235, "tyrogue" to 236, "hitmontop" to 237,
        "elekid" to 239, "magby" to 240, "blissey" to 242,
        "raikou" to 243, "entei" to 244, "suicune" to 245,
        "larvitar" to 246, "pupitar" to 247, "tyranitar" to 248,
        "lugia" to 249, "ho-oh" to 250, "ho oh" to 250, "celebi" to 251,

        // Gen 3 (252 - 386)
        "treecko" to 252, "grovyle" to 253, "sceptile" to 254,
        "torchic" to 255, "combusken" to 256, "blaziken" to 257,
        "mudkip" to 258, "marshtomp" to 259, "swampert" to 260,
        "poochyena" to 261, "mightyena" to 262, "zigzagoon" to 263, "linoone" to 264,
        "ralts" to 280, "kirlia" to 281, "gardevoir" to 282, "gallade" to 475,
        "slakoth" to 287, "vigoroth" to 288, "slaking" to 289,
        "shedinja" to 292, "exploud" to 295, "sableye" to 302, "mawile" to 303,
        "aggron" to 306, "medicham" to 308, "manectric" to 310,
        "wailmer" to 320, "wailord" to 321, "torkoal" to 324, "flygon" to 330,
        "altaria" to 334, "zangoose" to 335, "seviper" to 336,
        "milotic" to 350, "castform" to 351, "banette" to 354, "duskull" to 355, "dusclops" to 356,
        "absol" to 359, "snorunt" to 361, "glalie" to 362, "walrein" to 365,
        "bagon" to 371, "shelgon" to 372, "salamence" to 373,
        "beldum" to 374, "metang" to 375, "metagross" to 376,
        "regirock" to 377, "regice" to 378, "registeel" to 379,
        "latias" to 380, "latios" to 381,
        "kyogre" to 382, "groudon" to 383, "rayquaza" to 384,
        "jirachi" to 385, "deoxys" to 386,

        // Gen 4 (387 - 493)
        "turtwig" to 387, "grotle" to 388, "torterra" to 389,
        "chimchar" to 390, "monferno" to 391, "infernape" to 392,
        "piplup" to 393, "prinplup" to 394, "empoleon" to 395,
        "staraptor" to 398, "luxray" to 405, "roserade" to 407, "rampardos" to 409,
        "garchomp" to 445, "lucario" to 448, "riolu" to 447, "hippowdon" to 450, "drapion" to 452, "toxicroak" to 454,
        "abomasnow" to 460, "weavile" to 461, "magnezone" to 462, "rhyperior" to 464,
        "electivire" to 466, "magmortar" to 467, "togekiss" to 468, "yanmega" to 469,
        "leafeon" to 470, "glaceon" to 471, "gliscor" to 472, "mamoswine" to 473, "porygon-z" to 474,
        "dusknoir" to 477, "froslass" to 478, "rotom" to 479,
        "uxie" to 480, "mesprit" to 481, "azelf" to 482,
        "dialga" to 483, "palkia" to 484, "heatran" to 485, "regigigas" to 486,
        "giratina" to 487, "cresselia" to 488, "phione" to 489, "manaphy" to 490,
        "darkrai" to 491, "shaymin" to 492, "arceus" to 493,

        // Gen 5 (494 - 649)
        "victini" to 494, "snivy" to 495, "servine" to 496, "serperior" to 497,
        "tepig" to 498, "pignite" to 499, "emboar" to 500,
        "oshawott" to 501, "dewott" to 502, "samurott" to 503,
        "excadrill" to 530, "conkeldurr" to 534, "krookodile" to 553,
        "zoroark" to 571, "zorua" to 570, "cinccino" to 573, "reuniclus" to 579,
        "chandelure" to 609, "haxorus" to 612, "hydreigon" to 635, "volcarona" to 637,
        "cobalion" to 638, "terrakion" to 639, "virizion" to 640,
        "tornadus" to 641, "thundurus" to 642, "reshiram" to 643, "zekrom" to 644,
        "landorus" to 645, "kyurem" to 646, "keldeo" to 647, "meloetta" to 648, "genesect" to 649,

        // Gen 6 (650 - 721)
        "chespin" to 650, "quilladin" to 651, "chesnaught" to 652,
        "fennekin" to 653, "braixen" to 654, "delphox" to 655,
        "froakie" to 656, "frogadier" to 657, "greninja" to 658,
        "talonflame" to 663, "aegislash" to 681, "sylveon" to 700, "hawlucha" to 701, "goodra" to 706,
        "noivern" to 715, "xerneas" to 716, "yveltal" to 717, "zygarde" to 718, "diancie" to 719, "hoopa" to 720, "volcanion" to 721,

        // Gen 7 (722 - 809)
        "rowlet" to 722, "dartrix" to 723, "decidueye" to 724,
        "litten" to 725, "torracat" to 726, "incineroar" to 727,
        "popplio" to 728, "brionne" to 729, "primarina" to 730,
        "vikavolt" to 738, "lycanroc" to 745, "toxapex" to 748, "salazzle" to 758,
        "mimikyu" to 778, "dhelmise" to 781, "kommo-o" to 784,
        "tapu koko" to 785, "tapu lele" to 786, "tapu bulu" to 787, "tapu fini" to 788,
        "solgaleo" to 791, "lunala" to 792, "nihilego" to 793, "buzzwole" to 794,
        "kartana" to 798, "necrozma" to 800, "magearna" to 801, "marshadow" to 802, "zeraora" to 807,

        // Gen 8 (810 - 905)
        "grookey" to 810, "thwackey" to 811, "rillaboom" to 812,
        "scorbunny" to 813, "raboot" to 814, "cinderace" to 815,
        "sobble" to 816, "drizzile" to 817, "inteleon" to 818,
        "corviknight" to 823, "orbeetle" to 826, "toxtricity" to 849, "centiskorch" to 851,
        "grimmsnarl" to 861, "obstagoon" to 862, "sirfetch'd" to 865, "frosmoth" to 873, "dragapult" to 887,
        "zacian" to 888, "zamazenta" to 889, "eternatus" to 890, "kubfu" to 891, "urshifu" to 892,
        "zarude" to 893, "regieleki" to 894, "regidrago" to 895, "glastrier" to 896, "spectrier" to 897, "calyrex" to 898,

        // Gen 9 (906 - 1025)
        "sprigatito" to 906, "floragato" to 907, "meowscarada" to 908,
        "fuecoco" to 909, "crocalor" to 910, "skeledirge" to 911,
        "quaxly" to 912, "quaxwell" to 913, "quaquaval" to 914,
        "pawmi" to 921, "pawmo" to 922, "pawmot" to 923,
        "garganacl" to 934, "armarouge" to 936, "ceruledge" to 937, "bellibolt" to 939,
        "tinkatink" to 957, "tinkatuff" to 958, "tinkaton" to 959,
        "palafin" to 964, "kingambit" to 983,
        "great tusk" to 984, "scream tail" to 985, "brute bonnet" to 986, "flutter mane" to 987,
        "slither wing" to 988, "sandy shocks" to 989, "iron treads" to 990, "iron bundle" to 991,
        "iron hands" to 992, "iron jugulis" to 993, "iron moth" to 994, "iron thorns" to 995,
        "baxcalibur" to 998, "gholdengo" to 1000,
        "roaring moon" to 1005, "iron valiant" to 1006,
        "koraidon" to 1007, "miraidon" to 1008,
        "walking wake" to 1009, "iron leaves" to 1010,
        "chien-pao" to 1002, "ting-lu" to 1003, "wo-chien" to 1001, "chi-yu" to 1004,
        "ogerpon" to 1017, "gouging fire" to 1020, "raging bolt" to 1021, "iron boulder" to 1022, "iron crown" to 1023,
        "terapagos" to 1024, "pecharunt" to 1025
    )

    /**
     * Determines franchise from all metadata fields with high accuracy
     */
    fun detectFranchise(name: String, subCategory: String, collection: String): String {
        val s = "${subCategory.lowercase()} ${collection.lowercase()} ${name.lowercase()}"
        return when {
            s.contains("magic") || s.contains("mtg") || s.contains("scryfall") || s.contains("gathering") -> "Magic: The Gathering"
            s.contains("pokemon") || s.contains("pokémon") || s.contains("tcgdex") || s.contains("pokedex") || s.contains("pikachu") || s.contains("charizard") -> "Pokémon TCG"
            s.contains("yu-gi-oh") || s.contains("yugioh") || s.contains("ygoprodeck") || s.contains("konami") -> "Yu-Gi-Oh!"
            s.contains("one piece") || s.contains("optcg") || s.contains("luffy") -> "One Piece Card Game"
            s.contains("lorcana") || s.contains("disney") -> "Disney Lorcana"
            s.contains("star wars") -> "Star Wars: Unlimited"
            s.contains("dragon ball") || s.contains("dbs") || s.contains("fusion world") -> "Dragon Ball Super"
            s.contains("hot wheels") || s.contains("diecast") || s.contains("kaido") || s.contains("matchbox") || s.contains("mini gt") || s.contains("carrinho") -> "Hot Wheels"
            s.contains("moeda") || s.contains("numismat") || s.contains("cédula") || s.contains("real") || s.contains("cruzeiro") -> "Moedas"
            s.contains("figure") || s.contains("funko") || s.contains("nendoroid") || s.contains("boneco") -> "Action Figures"
            else -> subCategory.ifBlank { "Trading Cards" }
        }
    }

    /**
     * Extracts pure Pokémon name from a card title (e.g. "Charizard ex SIR #199/165" -> "charizard")
     */
    fun extractPokemonName(fullName: String): String? {
        val lower = fullName.lowercase()
            .replace(Regex("\\(.*\\)"), "")
            .replace(Regex("\\[.*\\]"), "")
            .replace(Regex("#[0-9/]+"), "")
            .replace(" ex", "")
            .replace(" vmax", "")
            .replace(" vstar", "")
            .replace(" gx", "")
            .replace(" v", "")
            .replace(" gold", "")
            .replace(" sir", "")
            .replace(" hr", "")
            .replace(" ur", "")
            .replace(" sar", "")
            .replace(" ar", "")
            .replace(" ir", "")
            .replace(" star", "")
            .replace(" shiny", "")
            .replace(" secret", "")
            .replace(" holo", "")
            .replace(" foil", "")
            .replace(" illustration", "")
            .replace(" rare", "")
            .trim()

        // 1. Direct match in dictionary
        if (pokemonDexMap.containsKey(lower)) {
            return lower
        }

        // 2. Tokenized word match
        val words = lower.split(" ", "-", "_", "/")
        for (word in words) {
            val cleanWord = word.trim()
            if (cleanWord.length >= 3 && pokemonDexMap.containsKey(cleanWord)) {
                return cleanWord
            }
        }

        // 3. Substring match
        for ((pName, _) in pokemonDexMap) {
            if (lower.contains(pName) || (pName.length > 4 && pName.contains(lower))) {
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
     * Cleans Magic: The Gathering card name and translates PT -> EN if needed for Scryfall
     */
    fun getCleanMtgCardName(name: String): String {
        val lowerRaw = name.lowercase().trim()
        val withoutParens = lowerRaw
            .replace(Regex("\\(.*\\)"), "")
            .replace(Regex("\\[.*\\]"), "")
            .replace(Regex("#[0-9/]+"), "")
            .split("//")[0]
            .trim()

        // Translate Portuguese name if known
        mtgPtToEnMap[withoutParens]?.let { return it }
        for ((ptKey, enVal) in mtgPtToEnMap) {
            if (withoutParens.contains(ptKey) || ptKey.contains(withoutParens)) {
                return enVal
            }
        }

        return name
            .replace(Regex("\\(.*\\)"), "")
            .replace(Regex("\\[.*\\]"), "")
            .replace(Regex("#[0-9/]+"), "")
            .split("//")[0]
            .trim()
    }

    /**
     * Synchronously returns a high-resolution, valid direct image URL for the item.
     */
    fun getOfficialImageUrl(
        name: String,
        subCategory: String,
        collection: String = "",
        itemNumber: String = ""
    ): String {
        val cleanName = name.trim()
        val franchise = detectFranchise(cleanName, subCategory, collection)

        return when (franchise) {
            "Magic: The Gathering" -> {
                val cleanMtgName = getCleanMtgCardName(cleanName)
                val encodedQuery = try {
                    URLEncoder.encode(cleanMtgName, StandardCharsets.UTF_8.toString())
                } catch (_: Exception) {
                    cleanMtgName
                }
                // Scryfall fuzzy endpoint with 302 redirect directly to large image
                "https://api.scryfall.com/cards/named?fuzzy=$encodedQuery&format=image&version=large"
            }

            "Pokémon TCG" -> {
                getPokemonCardImageUrl(cleanName, collection, itemNumber)
            }

            "Yu-Gi-Oh!" -> {
                val yugiohName = cleanName
                    .replace(Regex("\\(.*\\)"), "")
                    .replace(Regex("\\[.*\\]"), "")
                    .replace(Regex("#[0-9/]+"), "")
                    .trim()
                val encodedYgo = try {
                    URLEncoder.encode(yugiohName, StandardCharsets.UTF_8.toString())
                } catch (_: Exception) {
                    yugiohName
                }
                "https://images.ygoprodeck.com/images/cards_cropped/$encodedYgo.jpg"
            }

            "One Piece Card Game" -> getOnePieceImageUrl(cleanName)
            "Disney Lorcana" -> getLorcanaImageUrl(cleanName)
            "Star Wars: Unlimited" -> getStarWarsImageUrl(cleanName)
            "Dragon Ball Super" -> getDragonBallImageUrl(cleanName)
            "Hot Wheels" -> getDiecastImageUrl(cleanName)
            "Moedas" -> getMoedaImageUrl(cleanName)
            else -> getPokemonArtworkUrl(cleanName)
        }
    }

    /**
     * Asynchronously queries live web APIs (Scryfall API for Magic, TCGDex API for Pokémon)
     * to fetch the exact official card image URL.
     */
    suspend fun searchOfficialImageOnline(
        name: String,
        subCategory: String,
        collection: String = "",
        itemNumber: String = ""
    ): String? = withContext(Dispatchers.IO) {
        val cleanName = name.trim()
        val franchise = detectFranchise(cleanName, subCategory, collection)

        try {
            when (franchise) {
                "Magic: The Gathering" -> {
                    val cleanMtgName = getCleanMtgCardName(cleanName)
                    // 1. Try Scryfall fuzzy search with translated/cleaned name
                    val fuzzyCard = ScryfallDataService.findCardByFuzzyName(cleanMtgName)
                    val fuzzyImage = fuzzyCard?.getHighResImage()
                    if (!fuzzyImage.isNullOrBlank()) {
                        return@withContext fuzzyImage
                    }

                    // 2. Try Scryfall fuzzy search with raw name
                    if (cleanMtgName != cleanName) {
                        val rawFuzzyCard = ScryfallDataService.findCardByFuzzyName(cleanName)
                        val rawFuzzyImage = rawFuzzyCard?.getHighResImage()
                        if (!rawFuzzyImage.isNullOrBlank()) {
                            return@withContext rawFuzzyImage
                        }
                    }

                    // 3. Try general search across sets
                    val searchResults = ScryfallDataService.searchCardsByName(cleanMtgName, maxResults = 5)
                    val match = searchResults.firstOrNull { it.getHighResImage()?.isNotBlank() == true }
                    if (match != null) {
                        return@withContext match.getHighResImage()
                    }

                    // 4. Fallback direct Scryfall redirect URL
                    val encodedQuery = try {
                        URLEncoder.encode(cleanMtgName, StandardCharsets.UTF_8.toString())
                    } catch (_: Exception) {
                        cleanMtgName
                    }
                    return@withContext "https://api.scryfall.com/cards/named?fuzzy=$encodedQuery&format=image&version=large"
                }

                "Pokémon TCG" -> {
                    val pName = extractPokemonName(cleanName) ?: cleanName
                    // 1. Query TCGDex API with full name
                    var searchResults = TcgOnlineService.searchPokemonCards(cleanName)
                    if (searchResults.isEmpty() && pName != cleanName) {
                        searchResults = TcgOnlineService.searchPokemonCards(pName)
                    }

                    // If collection or item number is given, try matching best specific card
                    val numOnly = itemNumber.split("/").firstOrNull()?.filter { it.isDigit() } ?: ""
                    val bestMatch = if (numOnly.isNotBlank()) {
                        searchResults.firstOrNull { it.localId == numOnly || it.id.endsWith("-$numOnly") || it.id.endsWith("/$numOnly") }
                            ?: searchResults.firstOrNull { it.image != null && it.image.isNotBlank() }
                    } else {
                        searchResults.firstOrNull { it.image != null && it.image.isNotBlank() }
                    }

                    if (bestMatch != null) {
                        val cardDetail = TcgOnlineService.getPokemonCardDetail(bestMatch.id)
                        val highImg = cardDetail?.getHighResImage() ?: bestMatch.getHighResImage()
                        if (!highImg.isNullOrBlank()) {
                            return@withContext highImg
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // Fallback to static URL resolver
        return@withContext getOfficialImageUrl(name, subCategory, collection, itemNumber)
    }

    /**
     * Search all available official card printings/editions online (TCGDex for Pokémon, Scryfall for Magic)
     */
    suspend fun searchOfficialCardPrintOptions(
        name: String,
        subCategory: String,
        collection: String = "",
        itemNumber: String = ""
    ): List<OfficialCardPrintOption> = withContext(Dispatchers.IO) {
        val cleanName = name.trim()
        val franchise = detectFranchise(cleanName, subCategory, collection)
        val list = mutableListOf<OfficialCardPrintOption>()

        try {
            when (franchise) {
                "Pokémon TCG" -> {
                    val pName = extractPokemonName(cleanName) ?: cleanName
                    var briefs = TcgOnlineService.searchPokemonCards(cleanName)
                    if (briefs.isEmpty() && pName != cleanName) {
                        briefs = TcgOnlineService.searchPokemonCards(pName)
                    }

                    for (brief in briefs) {
                        val highImg = brief.getHighResImage()
                        if (!highImg.isNullOrBlank()) {
                            val cardSet = brief.id.split("-").firstOrNull()?.uppercase() ?: "TCG"
                            val num = if (brief.localId.isNotBlank()) "#${brief.localId}" else ""
                            list.add(
                                OfficialCardPrintOption(
                                    id = brief.id,
                                    title = brief.name,
                                    setName = cardSet,
                                    cardNumber = num,
                                    imageUrl = highImg
                                )
                            )
                        }
                    }
                }

                "Magic: The Gathering" -> {
                    val cleanMtgName = getCleanMtgCardName(cleanName)
                    val scryfallCards = ScryfallDataService.searchCardsByName(cleanMtgName, maxResults = 25)
                    for (c in scryfallCards) {
                        val img = c.getHighResImage()
                        if (!img.isNullOrBlank()) {
                            list.add(
                                OfficialCardPrintOption(
                                    id = c.id,
                                    title = c.name,
                                    setName = c.setName.ifBlank { c.set.uppercase() },
                                    cardNumber = "#${c.collectorNumber}",
                                    imageUrl = img,
                                    rarity = c.rarity.replaceFirstChar { it.uppercase() }
                                )
                            )
                        }
                    }
                }

                else -> {
                    val defaultUrl = getOfficialImageUrl(cleanName, subCategory, collection, itemNumber)
                    if (defaultUrl.isNotBlank()) {
                        list.add(
                            OfficialCardPrintOption(
                                id = "default_1",
                                title = cleanName,
                                setName = collection.ifBlank { subCategory },
                                cardNumber = itemNumber,
                                imageUrl = defaultUrl
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {}

        list
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
                    "https://assets.tcgdex.net/en/base/base1/$baseNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // 151 (Scarlet & Violet: 151)
            lowerCol.contains("151") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..207) {
                    "https://assets.tcgdex.net/en/sv/sv03.5/$intNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Surging Sparks (sv08)
            lowerCol.contains("surging") || lowerCol.contains("sparks") || lowerCol.contains("faíscas") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..252) {
                    "https://assets.tcgdex.net/en/sv/sv08/$intNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Twilight Masquerade (sv06)
            lowerCol.contains("twilight") || lowerCol.contains("masquerade") || lowerCol.contains("máscaras") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..226) {
                    "https://assets.tcgdex.net/en/sv/sv06/$intNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Paldea Evolved (sv02)
            lowerCol.contains("paldea evolved") || lowerCol.contains("evoluções em paldea") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..279) {
                    "https://assets.tcgdex.net/en/sv/sv02/$intNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Evolving Skies (swsh07)
            lowerCol.contains("evolving") || lowerCol.contains("skies") || lowerCol.contains("céus em evolução") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..237) {
                    "https://assets.tcgdex.net/en/swsh/swsh07/$intNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }
            // Crown Zenith (swsh12.5)
            lowerCol.contains("crown") || lowerCol.contains("zenith") || lowerCol.contains("zênite régio") -> {
                val intNum = numOnly.toIntOrNull()
                if (intNum != null && intNum in 1..230) {
                    "https://assets.tcgdex.net/en/swsh/swsh12.5/$intNum/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
            }

            // Key iconic direct card matches (Real TCG Card Prints)
            lowerName.contains("charizard") && (lowerName.contains("sir") || lowerName.contains("199")) -> "https://assets.tcgdex.net/en/sv/sv03.5/199/high.webp"
            lowerName.contains("charizard") && lowerName.contains("base") -> "https://assets.tcgdex.net/en/base/base1/4/high.webp"
            lowerName.contains("charizard") -> "https://assets.tcgdex.net/en/sv/sv03.5/199/high.webp"
            lowerName.contains("blastoise") && (lowerName.contains("sir") || lowerName.contains("200")) -> "https://assets.tcgdex.net/en/sv/sv03.5/200/high.webp"
            lowerName.contains("blastoise") -> "https://assets.tcgdex.net/en/sv/sv03.5/009/high.webp"
            lowerName.contains("venusaur") && (lowerName.contains("sir") || lowerName.contains("198")) -> "https://assets.tcgdex.net/en/sv/sv03.5/198/high.webp"
            lowerName.contains("venusaur") -> "https://assets.tcgdex.net/en/sv/sv03.5/003/high.webp"
            lowerName.contains("pikachu") && (lowerName.contains("sir") || lowerName.contains("204")) -> "https://assets.tcgdex.net/en/sv/sv08/204/high.webp"
            lowerName.contains("pikachu") && lowerName.contains("173") -> "https://assets.tcgdex.net/en/sv/sv03.5/173/high.webp"
            lowerName.contains("pikachu") -> "https://assets.tcgdex.net/en/sv/sv03.5/025/high.webp"
            lowerName.contains("umbreon") && (lowerName.contains("moonbreon") || lowerName.contains("vmax") || lowerName.contains("215")) -> "https://assets.tcgdex.net/en/swsh/swsh07/215/high.webp"
            lowerName.contains("umbreon") -> "https://assets.tcgdex.net/en/swsh/swsh07/215/high.webp"
            lowerName.contains("rayquaza") -> "https://assets.tcgdex.net/en/swsh/swsh07/218/high.webp"
            lowerName.contains("gengar") -> "https://assets.tcgdex.net/en/sv/sv03.5/094/high.webp"
            lowerName.contains("lugia") -> "https://assets.tcgdex.net/en/swsh/swsh12/186/high.webp"
            lowerName.contains("greninja") -> "https://assets.tcgdex.net/en/sv/sv06/214/high.webp"
            lowerName.contains("mewtwo") -> "https://assets.tcgdex.net/en/sv/sv03.5/150/high.webp"
            lowerName.contains("mew") -> "https://assets.tcgdex.net/en/sv/sv03.5/205/high.webp"
            lowerName.contains("eevee") -> "https://assets.tcgdex.net/en/sv/sv06/188/high.webp"
            lowerName.contains("snorlax") -> "https://assets.tcgdex.net/en/sv/sv03.5/143/high.webp"
            lowerName.contains("dragonite") -> "https://assets.tcgdex.net/en/sv/sv03.5/149/high.webp"
            lowerName.contains("lucario") -> "https://assets.tcgdex.net/en/sv/sv01/114/high.webp"
            lowerName.contains("gardevoir") -> "https://assets.tcgdex.net/en/sv/sv01/245/high.webp"
            lowerName.contains("mimikyu") -> "https://assets.tcgdex.net/en/sv/sv02/097/high.webp"
            lowerName.contains("squirtle") -> "https://assets.tcgdex.net/en/sv/sv03.5/170/high.webp"
            lowerName.contains("charmander") -> "https://assets.tcgdex.net/en/sv/sv03.5/168/high.webp"
            lowerName.contains("bulbasaur") -> "https://assets.tcgdex.net/en/sv/sv03.5/166/high.webp"
            lowerName.contains("gyarados") -> "https://assets.tcgdex.net/en/sv/sv01/225/high.webp"
            lowerName.contains("alakazam") -> "https://assets.tcgdex.net/en/sv/sv03.5/201/high.webp"
            lowerName.contains("zapdos") -> "https://assets.tcgdex.net/en/sv/sv03.5/202/high.webp"
            lowerName.contains("moltres") -> "https://assets.tcgdex.net/en/sv/sv03.5/146/high.webp"
            lowerName.contains("articuno") -> "https://assets.tcgdex.net/en/sv/sv03.5/144/high.webp"
            lowerName.contains("raichu") -> "https://assets.tcgdex.net/en/sv/sv02/211/high.webp"
            lowerName.contains("machamp") -> "https://assets.tcgdex.net/en/base/base1/8/high.webp"
            lowerName.contains("arceus") -> "https://assets.tcgdex.net/en/swsh/swsh09/166/high.webp"
            lowerName.contains("giratina") -> "https://assets.tcgdex.net/en/swsh/swsh11/186/high.webp"
            lowerName.contains("dialga") -> "https://assets.tcgdex.net/en/swsh/swsh10/177/high.webp"
            lowerName.contains("palkia") -> "https://assets.tcgdex.net/en/swsh/swsh10/167/high.webp"

            // Fallback: TCGDex standard card print scan
            else -> {
                val pName = extractPokemonName(name) ?: name.lowercase().trim()
                val dexId = pokemonDexMap[pName]
                if (dexId != null && dexId in 1..151) {
                    val formatted = String.format(java.util.Locale.US, "%03d", dexId)
                    "https://assets.tcgdex.net/en/sv/sv03.5/$formatted/high.webp"
                } else {
                    getPokemonArtworkUrl(name)
                }
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

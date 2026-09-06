package com.example.data

data class RealCatalogEntry(
    val name: String,
    val category: String,
    val subCategory: String,
    val collection: String,
    val itemNumber: String,
    val rarity: String,
    val variant: String,
    val defaultCondition: String,
    val languageOrScale: String,
    val realMarketPriceBrl: Double,
    val suggestedPurchasePriceBrl: Double,
    val defaultStorage: String,
    val notes: String,
    val tags: String,
    val highPriceBrl: Double = realMarketPriceBrl * 1.25,
    val lowPriceBrl: Double = realMarketPriceBrl * 0.85,
    val releaseYear: String = "",
    val setCode: String = "",
    val era: String = "",
    val patternVariant: String = "Standard"
) {
    val effectiveYear: String
        get() {
            if (releaseYear.isNotBlank()) return releaseYear
            // Extract 4-digit year from collection or notes or name
            val yearRegex = Regex("\\b(19[7-9]\\d|20[0-2]\\d)\\b")
            val fromCollection = yearRegex.find(collection)?.value
            if (fromCollection != null) return fromCollection
            val fromName = yearRegex.find(name)?.value
            if (fromName != null) return fromName
            val fromNotes = yearRegex.find(notes)?.value
            if (fromNotes != null) return fromNotes

            // Known set year fallbacks
            return when {
                collection.contains("151", true) -> "2023"
                collection.contains("Paldea", true) -> "2023"
                collection.contains("Surging", true) -> "2024"
                collection.contains("Twilight", true) -> "2024"
                collection.contains("Crown Zenith", true) -> "2023"
                collection.contains("Evolving Skies", true) -> "2021"
                collection.contains("Fusion Strike", true) -> "2021"
                collection.contains("Lost Origin", true) -> "2022"
                collection.contains("Silver Tempest", true) -> "2022"
                collection.contains("Base Set", true) -> "1999"
                collection.contains("Modern Horizons 3", true) -> "2024"
                collection.contains("Modern Horizons 2", true) -> "2021"
                collection.contains("Dominaria", true) -> "2022"
                collection.contains("Lord of the Rings", true) -> "2023"
                collection.contains("Ixalan", true) -> "2023"
                collection.contains("Alpha", true) -> "1993"
                collection.contains("Rarity Collection", true) -> "2023"
                collection.contains("Age of Overlord", true) -> "2023"
                collection.contains("Legend of Blue Eyes", true) -> "2002"
                collection.contains("OP-05", true) || collection.contains("OP05", true) -> "2023"
                collection.contains("OP-06", true) || collection.contains("OP06", true) -> "2024"
                collection.contains("OP-01", true) || collection.contains("OP01", true) -> "2022"
                collection.contains("OP-02", true) || collection.contains("OP02", true) -> "2023"
                collection.contains("First Chapter", true) -> "2023"
                collection.contains("Floodborn", true) -> "2023"
                collection.contains("Mainline 2025", true) -> "2025"
                collection.contains("Mainline 2024", true) -> "2024"
                else -> "2024"
            }
        }
}

object RealMarketCatalog {

    private val rawEntries: List<RealCatalogEntry> = listOf(
        // ==================== POKÉMON TCG ====================
        RealCatalogEntry(
            name = "Charizard ex (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "199/165",
            rarity = "Special Illustration Rare",
            variant = "Foil Holográfico Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 850.00,
            suggestedPurchasePriceBrl = 550.00,
            defaultStorage = "Pasta 151 - Pág 1 (TopLoader)",
            notes = "A carta mais cobiçada da coleção 151. Arte de mestre com Charizard voando sobre um cânion de lava.",
            tags = "pokemon, charizard, sir, 151, fogo, kanto, ultra rara"
        ),
        RealCatalogEntry(
            name = "Pikachu (Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "173/165",
            rarity = "Illustration Rare",
            variant = "Foil / Holográfico",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 190.00,
            suggestedPurchasePriceBrl = 110.00,
            defaultStorage = "Pasta 151 - Pág 1",
            notes = "Ilustração secreta de Pikachu caminhando entre as casas de Kanto.",
            tags = "pokemon, pikachu, eletrico, 151, secret, ir"
        ),
        RealCatalogEntry(
            name = "Mew ex (Hyper Rare Gold)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "205/165",
            rarity = "Hyper Rare (Gold)",
            variant = "Gold Foil Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 340.00,
            suggestedPurchasePriceBrl = 220.00,
            defaultStorage = "Pasta 151 - Pág 2 (Slab)",
            notes = "Versão Dourada Hyper Rare exclusiva da coleção 151.",
            tags = "pokemon, mew, gold, 151, hyper rare, psiquico"
        ),
        RealCatalogEntry(
            name = "Blastoise ex (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "200/165",
            rarity = "Special Illustration Rare",
            variant = "Foil Holográfico Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 320.00,
            suggestedPurchasePriceBrl = 200.00,
            defaultStorage = "Pasta 151 - Pág 1",
            notes = "Arte espetacular de Blastoise no fundo do oceano.",
            tags = "pokemon, blastoise, sir, 151, agua, kanto"
        ),
        RealCatalogEntry(
            name = "Venusaur ex (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "198/165",
            rarity = "Special Illustration Rare",
            variant = "Foil Holográfico Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 280.00,
            suggestedPurchasePriceBrl = 180.00,
            defaultStorage = "Pasta 151 - Pág 1",
            notes = "Venusaur em uma floresta tropical exuberante. Efeito de relevo brilhante.",
            tags = "pokemon, venusaur, sir, 151, grama, kanto"
        ),
        RealCatalogEntry(
            name = "Alakazam ex (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "201/165",
            rarity = "Special Illustration Rare",
            variant = "Foil Holográfico Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 220.00,
            suggestedPurchasePriceBrl = 140.00,
            defaultStorage = "Pasta 151 - Pág 2",
            notes = "Alakazam meditando em uma sala clássica com suas colheres flutuantes.",
            tags = "pokemon, alakazam, sir, 151, psiquico"
        ),
        RealCatalogEntry(
            name = "Zapdos ex (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "202/165",
            rarity = "Special Illustration Rare",
            variant = "Foil Holográfico Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 250.00,
            suggestedPurchasePriceBrl = 160.00,
            defaultStorage = "Pasta 151 - Pág 2",
            notes = "Zapdos cortando os céus em tempestade elétrica junto com Articuno e Moltres ao fundo.",
            tags = "pokemon, zapdos, sir, 151, lendario, eletrico"
        ),
        RealCatalogEntry(
            name = "Erika's Invitation (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Scarlet & Violet 151",
            itemNumber = "203/165",
            rarity = "Special Illustration Rare",
            variant = "Full Art Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 160.00,
            suggestedPurchasePriceBrl = 95.00,
            defaultStorage = "Pasta 151 - Pág 2",
            notes = "Líder de ginásio Erika de Celadon City em arte estendida.",
            tags = "pokemon, erika, waifu, treinador, 151, sir"
        ),
        RealCatalogEntry(
            name = "Umbreon VMAX (Moonbreon Alternate Art)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Evolving Skies",
            itemNumber = "215/203",
            rarity = "Secret Rare Alternate Art",
            variant = "Foil Texturizado Especial",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 4800.00,
            suggestedPurchasePriceBrl = 3200.00,
            defaultStorage = "Cofre Graduado / Slab PSA 10",
            notes = "Conhecida mundialmente como 'Moonbreon'. Uma das cartas modernas mais valiosas da história do Pokémon TCG.",
            tags = "pokemon, umbreon, moonbreon, evolving skies, eeveelution, grail"
        ),
        RealCatalogEntry(
            name = "Rayquaza VMAX (Alternate Art Secret)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Evolving Skies",
            itemNumber = "218/203",
            rarity = "Secret Rare Alternate Art",
            variant = "Foil Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 1950.00,
            suggestedPurchasePriceBrl = 1300.00,
            defaultStorage = "Slab Magnético Case",
            notes = "Rayquaza gigante voando sobre a floresta com Zinnia.",
            tags = "pokemon, rayquaza, evolving skies, dragao, secret rare"
        ),
        RealCatalogEntry(
            name = "Gengar VMAX (Alternate Art Secret)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Fusion Strike",
            itemNumber = "271/264",
            rarity = "Secret Rare Alternate Art",
            variant = "Foil Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 1450.00,
            suggestedPurchasePriceBrl = 950.00,
            defaultStorage = "TopLoader Case Protegido",
            notes = "Gengar Gigantamax engolindo árvores e objetos. Grande valorização no mercado.",
            tags = "pokemon, gengar, fusion strike, fantasma, alt art"
        ),
        RealCatalogEntry(
            name = "Giratina V (Alternate Art)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Lost Origin",
            itemNumber = "186/196",
            rarity = "Ultra Rare Alternate Art",
            variant = "Foil Texturizado Psicodélico",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 1600.00,
            suggestedPurchasePriceBrl = 1100.00,
            defaultStorage = "Slab Acrílico",
            notes = "Arte dimensional do Mundo Distorcido criada pelo ilustrador Shinji Kanda.",
            tags = "pokemon, giratina, lost origin, shinji kanda, alt art"
        ),
        RealCatalogEntry(
            name = "Lugia V (Alternate Art)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Silver Tempest",
            itemNumber = "186/195",
            rarity = "Ultra Rare Alternate Art",
            variant = "Foil Texturizado",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 980.00,
            suggestedPurchasePriceBrl = 650.00,
            defaultStorage = "Pasta Tempestade Prateada",
            notes = "Lugia sobrevoando uma tempestade no oceano com um pescador no barco.",
            tags = "pokemon, lugia, silver tempest, johto, alt art"
        ),
        RealCatalogEntry(
            name = "Iono (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Paldea Evolved",
            itemNumber = "269/193",
            rarity = "Special Illustration Rare",
            variant = "Full Art Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 480.00,
            suggestedPurchasePriceBrl = 300.00,
            defaultStorage = "Pasta Paldea - Pág 1",
            notes = "Iono transmitindo live de Levincia. Carta meta e de alto valor de colecionismo.",
            tags = "pokemon, iono, paldea evolved, waifu, treinador, sir"
        ),
        RealCatalogEntry(
            name = "Greninja ex (Special Illustration Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Twilight Masquerade",
            itemNumber = "214/167",
            rarity = "Special Illustration Rare",
            variant = "Foil Texturizado Estilo Ukiyo-e",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 1350.00,
            suggestedPurchasePriceBrl = 900.00,
            defaultStorage = "Slab Acrílico Protetor",
            notes = "Arte inspirada na pintura tradicional japonesa com respingos de tinta e shurikens de água.",
            tags = "pokemon, greninja, twilight masquerade, sir, kalos"
        ),
        RealCatalogEntry(
            name = "Pikachu ex (Special Art Rare)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Surging Sparks",
            itemNumber = "238/191",
            rarity = "Special Art Rare",
            variant = "Stellar Terastal Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 1850.00,
            suggestedPurchasePriceBrl = 1200.00,
            defaultStorage = "TopLoader Case",
            notes = "Pikachu Tera Estelar com a coroa brilhante de gemas. Chase card de Surging Sparks.",
            tags = "pokemon, pikachu, surging sparks, stellar, tera"
        ),
        RealCatalogEntry(
            name = "Charizard (Base Set 1999 Holo #4/102)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Base Set 1999",
            itemNumber = "4/102",
            rarity = "Holo Rara Vintage",
            variant = "Cosmo Holofoil Clássico",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "PT-BR / EN",
            realMarketPriceBrl = 3500.00,
            suggestedPurchasePriceBrl = 2400.00,
            defaultStorage = "Cofre Graduado / Slab Magnético",
            notes = "O Santo Graal dos colecionadores vintage de Pokémon. Impressão clássica original de 1999.",
            tags = "pokemon, charizard, base set, 1999, vintage, wizards of the coast, grail"
        ),
        RealCatalogEntry(
            name = "Blastoise (Base Set 1999 Holo #2/102)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Base Set 1999",
            itemNumber = "2/102",
            rarity = "Holo Rara Vintage",
            variant = "Cosmo Holofoil Clássico",
            defaultCondition = "Lightly Played (LP)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 650.00,
            suggestedPurchasePriceBrl = 400.00,
            defaultStorage = "Pasta Vintage WOTC",
            notes = "Carta holográfica original de 1999. Rain Dance ability icônica.",
            tags = "pokemon, blastoise, base set, 1999, wotc, vintage"
        ),
        RealCatalogEntry(
            name = "Venusaur (Base Set 1999 Holo #15/102)",
            category = "Trading Cards",
            subCategory = "Pokémon TCG",
            collection = "Base Set 1999",
            itemNumber = "15/102",
            rarity = "Holo Rara Vintage",
            variant = "Cosmo Holofoil Clássico",
            defaultCondition = "Lightly Played (LP)",
            languageOrScale = "PT-BR",
            realMarketPriceBrl = 550.00,
            suggestedPurchasePriceBrl = 350.00,
            defaultStorage = "Pasta Vintage WOTC",
            notes = "Trio inicial clássico do Base Set de 1999.",
            tags = "pokemon, venusaur, base set, 1999, wotc"
        ),

        // ==================== MAGIC: THE GATHERING ====================
        RealCatalogEntry(
            name = "Black Lotus (Beta / Unlimited)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Unlimited Edition / Beta",
            itemNumber = "#232",
            rarity = "Rara Power Nine",
            variant = "Vintage Original",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 45000.00,
            suggestedPurchasePriceBrl = 32000.00,
            defaultStorage = "Cofre / Slab Graduado BGS",
            notes = "O artefato mais famoso e valioso da história do Magic: The Gathering. Membro do Power Nine.",
            tags = "mtg, black lotus, power nine, vintage, unlimited, beta, artefato"
        ),
        RealCatalogEntry(
            name = "Sol Ring (Masterpiece / Retro)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Commander Masters / Kaladesh",
            itemNumber = "#024",
            rarity = "Mítica Especial",
            variant = "Foil Borderless",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 280.00,
            suggestedPurchasePriceBrl = 180.00,
            defaultStorage = "Pasta Commander Pág 1",
            notes = "A staple número 1 absoluta do formato Commander mundial.",
            tags = "mtg, sol ring, commander, artefato, mana, staple"
        ),
        RealCatalogEntry(
            name = "Force of Will (Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Double Masters",
            itemNumber = "#340",
            rarity = "Mítica Rara",
            variant = "Borderless Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 480.00,
            suggestedPurchasePriceBrl = 320.00,
            defaultStorage = "Pasta Legacy / Commander",
            notes = "A anulação de mágica gratuita mais icônica de formatos eternos.",
            tags = "mtg, force of will, azul, counter, legacy, vintage, commander"
        ),
        RealCatalogEntry(
            name = "Mox Diamond (Stronghold)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Stronghold (Reserved List)",
            itemNumber = "#138",
            rarity = "Rara Reservada",
            variant = "Original Vintage",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 3400.00,
            suggestedPurchasePriceBrl = 2400.00,
            defaultStorage = "Slab Magnético / TopLoader",
            notes = "Carta da Lista Reservada (Reserved List). Aceleração essencial para cEDH e Legacy.",
            tags = "mtg, mox diamond, reserved list, stronghold, cedh, artefato"
        ),
        RealCatalogEntry(
            name = "Underground Sea (Revised Edition)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Revised Edition 1994",
            itemNumber = "#285",
            rarity = "Rara Dual Land",
            variant = "Original Dual Land",
            defaultCondition = "Lightly Played (LP)",
            languageOrScale = "EN",
            realMarketPriceBrl = 4200.00,
            suggestedPurchasePriceBrl = 3100.00,
            defaultStorage = "Cofre / Pasta Dual Lands",
            notes = "Dual Land original (Ilha/Pântano) sem restrição de virar. Lista Reservada.",
            tags = "mtg, underground sea, dual land, revised, reserved list, terreno"
        ),
        RealCatalogEntry(
            name = "Volcanic Island (Revised Edition)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Revised Edition 1994",
            itemNumber = "#286",
            rarity = "Rara Dual Land",
            variant = "Original Dual Land",
            defaultCondition = "Lightly Played (LP)",
            languageOrScale = "EN",
            realMarketPriceBrl = 3900.00,
            suggestedPurchasePriceBrl = 2800.00,
            defaultStorage = "Cofre / Pasta Dual Lands",
            notes = "Dual Land original (Ilha/Montanha). Pilar do formato Legacy e Commander.",
            tags = "mtg, volcanic island, dual land, revised, reserved list, terreno"
        ),
        RealCatalogEntry(
            name = "Gaea's Cradle (Urza's Saga)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Urza's Saga 1998",
            itemNumber = "#321",
            rarity = "Rara Reservada",
            variant = "Original Vintage",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 5800.00,
            suggestedPurchasePriceBrl = 4200.00,
            defaultStorage = "Cofre / Slab Acrílico",
            notes = "Terreno lendário que gera mana verde para cada criatura que você controla.",
            tags = "mtg, gaea's cradle, urza, verde, reserved list, cEDH, elfo"
        ),
        RealCatalogEntry(
            name = "Rhystic Study (Anime Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Wilds of Eldraine: Enchanting Tales",
            itemNumber = "#0015",
            rarity = "Mítica Especial",
            variant = "Anime Art Confetti Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 550.00,
            suggestedPurchasePriceBrl = 380.00,
            defaultStorage = "Pasta Commander Luxo",
            notes = "Você vai pagar 1? O encantamento mais conhecido e temido das mesas de Commander.",
            tags = "mtg, rhystic study, anime, eldraine, commander, azul"
        ),
        RealCatalogEntry(
            name = "Cyclonic Rift (Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Commander Masters",
            itemNumber = "#654",
            rarity = "Rara",
            variant = "Borderless Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 190.00,
            suggestedPurchasePriceBrl = 130.00,
            defaultStorage = "Pasta Commander",
            notes = "Sobrecarga (Overload) devolve todas as permanências dos oponentes para a mão.",
            tags = "mtg, cyclonic rift, rvnica, commander, azul, feitiço"
        ),
        RealCatalogEntry(
            name = "The Thing, Ben Grimm",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Marvel Super Heroes Scene",
            itemNumber = "004/006",
            rarity = "Incomum",
            variant = "Normal",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 2.68,
            suggestedPurchasePriceBrl = 1.50,
            defaultStorage = "Pasta Marvel / MTG Promos",
            notes = "Cotação oficial de marketplace da LigaMagic: Menor R$ 0,90 | Médio R$ 2,68 | Maior R$ 5,00. Card de cena promocional Marvel.",
            tags = "mtg, the thing, ben grimm, marvel, universos além, incomum, vermelho, verde",
            highPriceBrl = 5.00,
            lowPriceBrl = 0.90,
            releaseYear = "2024",
            setCode = "SCMSH"
        ),
        RealCatalogEntry(
            name = "The Thing, Ben Grimm (Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Marvel Super Heroes Scene",
            itemNumber = "004/006",
            rarity = "Incomum",
            variant = "Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 4.19,
            suggestedPurchasePriceBrl = 2.50,
            defaultStorage = "Pasta Marvel / MTG Promos",
            notes = "Cotação oficial de marketplace da LigaMagic (Foil): Menor R$ 1,40 | Médio R$ 4,19 | Maior R$ 9,00.",
            tags = "mtg, the thing, ben grimm, foil, marvel, universos além, incomum",
            highPriceBrl = 9.00,
            lowPriceBrl = 1.40,
            releaseYear = "2024",
            setCode = "SCMSH"
        ),
        RealCatalogEntry(
            name = "Smothering Tithe (Anime Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Wilds of Eldraine: Enchanting Tales",
            itemNumber = "#0003",
            rarity = "Mítica Especial",
            variant = "Anime Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 240.00,
            suggestedPurchasePriceBrl = 160.00,
            defaultStorage = "Pasta Commander",
            notes = "Você vai pagar 2? Cria fichas de Tesouro continuamente.",
            tags = "mtg, smothering tithe, dízimo, branco, anime, tesouro"
        ),
        RealCatalogEntry(
            name = "Demonic Tutor (Strixhaven Mystical Archive Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Strixhaven Mystical Archive",
            itemNumber = "#090",
            rarity = "Mítica Especial",
            variant = "Japanese Alt Art Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "JP / EN",
            realMarketPriceBrl = 420.00,
            suggestedPurchasePriceBrl = 280.00,
            defaultStorage = "Pasta cEDH",
            notes = "Busque qualquer carta em seu grimório e coloque-a em sua mão.",
            tags = "mtg, demonic tutor, tutor, strixhaven, preto, cEDH"
        ),
        RealCatalogEntry(
            name = "Lightning Bolt (Beta / Secret Lair Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Secret Lair Drop / Classic",
            itemNumber = "#011",
            rarity = "Especial Foil",
            variant = "Foil Full Art",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 85.00,
            suggestedPurchasePriceBrl = 50.00,
            defaultStorage = "Deck Modern Burn",
            notes = "3 de dano por 1 mana vermelha. A mágica instantânea de dano mais clássica do MTG.",
            tags = "mtg, lightning bolt, raio, vermelho, burn, modern"
        ),
        RealCatalogEntry(
            name = "Counterspell (Borderless Retro Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Modern Horizons 2",
            itemNumber = "#412",
            rarity = "Incomum Especial",
            variant = "Retro Frame Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 45.00,
            suggestedPurchasePriceBrl = 25.00,
            defaultStorage = "Pasta Modern",
            notes = "Anule a mágica alvo. O clássico contrafeitiço de 2 manas azuis.",
            tags = "mtg, counterspell, contrafeitiço, azul, retro, modern"
        ),
        RealCatalogEntry(
            name = "The One Ring (Extended Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "The Lord of the Rings: Tales of Middle-earth",
            itemNumber = "#0246",
            rarity = "Mítica Rara",
            variant = "Foil Extended Art",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 620.00,
            suggestedPurchasePriceBrl = 400.00,
            defaultStorage = "Pasta Luxo MTG - Pág 1",
            notes = "Indestrutível. O artefato mais jogado e influente dos formatos Modern e Commander.",
            tags = "mtg, lord of the rings, the one ring, commander, modern, lotr"
        ),
        RealCatalogEntry(
            name = "The One Ring (Borderless Poster Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "The Lord of the Rings: Special Edition",
            itemNumber = "#0745",
            rarity = "Mítica Rara",
            variant = "Poster Art Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 890.00,
            suggestedPurchasePriceBrl = 580.00,
            defaultStorage = "Slab Acrílico MTG",
            notes = "Arte estilizada psicodélica estilo pôster de show anos 70.",
            tags = "mtg, the one ring, poster, lotr, foil"
        ),
        RealCatalogEntry(
            name = "Sheoldred, the Apocalypse (Phyrexian Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Dominaria United",
            itemNumber = "#097",
            rarity = "Mítica Rara",
            variant = "Phyrexian Text Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "Phyrexian / EN",
            realMarketPriceBrl = 460.00,
            suggestedPurchasePriceBrl = 300.00,
            defaultStorage = "Pasta Dominaria - Pág 1",
            notes = "Texto impresso no alfabeto ficcional Phyrexiano. Dominante em Standard, Pioneer e Modern.",
            tags = "mtg, sheoldred, phyrexian, dominaria united, preto"
        ),
        RealCatalogEntry(
            name = "Ragavan, Nimble Pilferer (Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Modern Horizons 2",
            itemNumber = "#138",
            rarity = "Mítica Rara",
            variant = "Borderless Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 320.00,
            suggestedPurchasePriceBrl = 210.00,
            defaultStorage = "Deckbox Modern Competitivo",
            notes = "Criatura lendária de custo 1 mana mais forte da história do formato Modern.",
            tags = "mtg, ragavan, modern horizons 2, macaco, modern"
        ),
        RealCatalogEntry(
            name = "Orcish Bowmasters (Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "The Lord of the Rings: Tales of Middle-earth",
            itemNumber = "#0433",
            rarity = "Rara",
            variant = "Borderless Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 290.00,
            suggestedPurchasePriceBrl = 190.00,
            defaultStorage = "Pasta Commander",
            notes = "Puna qualquer oponente que compre cartas extras. Staple em Legacy, Modern e Commander.",
            tags = "mtg, orcish bowmasters, lotr, commander, modern"
        ),
        RealCatalogEntry(
            name = "Mana Crypt (Cosmium Neon Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Special Guests: Lost Caverns of Ixalan",
            itemNumber = "#0017",
            rarity = "Mítica Especial",
            variant = "Neon Ink Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 1250.00,
            suggestedPurchasePriceBrl = 800.00,
            defaultStorage = "Slab Magnético Luxo",
            notes = "Artefato de aceleração com tinta neon especial brilhante.",
            tags = "mtg, mana crypt, ixalan, neon ink, artefato"
        ),
        RealCatalogEntry(
            name = "Tamiyo, Inquisitive Student (Textured Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Modern Horizons 3",
            itemNumber = "#0242",
            rarity = "Mítica Rara (Flip)",
            variant = "Textured Foil Borderless",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 390.00,
            suggestedPurchasePriceBrl = 240.00,
            defaultStorage = "Pasta MH3",
            notes = "Carta dupla face que se transforma no Planeswalker Tamiyo, Seasoned Scholar.",
            tags = "mtg, tamiyo, modern horizons 3, planeswalker, mh3"
        ),
        RealCatalogEntry(
            name = "Ulamog, the Defiler (Borderless Foil)",
            category = "Trading Cards",
            subCategory = "Magic: The Gathering",
            collection = "Modern Horizons 3",
            itemNumber = "#0245",
            rarity = "Mítica Rara",
            variant = "Borderless Eldrazi Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 380.00,
            suggestedPurchasePriceBrl = 250.00,
            defaultStorage = "Pasta MH3 Eldrazi",
            notes = "Eldrazi colossal que aniquila permanentes e rouba contadores.",
            tags = "mtg, ulamog, eldrazi, modern horizons 3, mh3"
        ),

        // ==================== YU-GI-OH! ====================
        RealCatalogEntry(
            name = "Blue-Eyes White Dragon (Quarter Century Secret Rare)",
            category = "Trading Cards",
            subCategory = "Yu-Gi-Oh!",
            collection = "25th Anniversary Rarity Collection",
            itemNumber = "RA01-EN001",
            rarity = "Quarter Century Secret Rare",
            variant = "25th Logo Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 380.00,
            suggestedPurchasePriceBrl = 240.00,
            defaultStorage = "TopLoader YuGiOh Case",
            notes = "Logo comemorativo dourado do 25º aniversário da franquia Yu-Gi-Oh! gravado no centro.",
            tags = "yugioh, blue eyes, 25th, kaiba, dragao, qcr"
        ),
        RealCatalogEntry(
            name = "Dark Magician Girl (Quarter Century Secret Rare)",
            category = "Trading Cards",
            subCategory = "Yu-Gi-Oh!",
            collection = "25th Anniversary Rarity Collection",
            itemNumber = "RA01-EN003",
            rarity = "Quarter Century Secret Rare",
            variant = "25th Logo Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 680.00,
            suggestedPurchasePriceBrl = 450.00,
            defaultStorage = "Slab Acrílico YGO",
            notes = "A Maga Negra em raridade máxima de 25 anos. Altíssima demanda global.",
            tags = "yugioh, dark magician girl, maga negra, 25th, qcr, yugi"
        ),
        RealCatalogEntry(
            name = "Ash Blossom & Joyous Spring (Quarter Century Secret Rare)",
            category = "Trading Cards",
            subCategory = "Yu-Gi-Oh!",
            collection = "25th Anniversary Rarity Collection",
            itemNumber = "RA01-EN005",
            rarity = "Quarter Century Secret Rare",
            variant = "25th Logo Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 290.00,
            suggestedPurchasePriceBrl = 190.00,
            defaultStorage = "Deckbox Competitiva",
            notes = "A handtrap mais famosa e jogada do Yu-Gi-Oh competitivo.",
            tags = "yugioh, ash blossom, handtrap, 25th, qcr"
        ),
        RealCatalogEntry(
            name = "S:P Little Knight (Secret Rare)",
            category = "Trading Cards",
            subCategory = "Yu-Gi-Oh!",
            collection = "Age of Overlord",
            itemNumber = "AGOV-EN046",
            rarity = "Secret Rare",
            variant = "Foil Holográfico",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 490.00,
            suggestedPurchasePriceBrl = 320.00,
            defaultStorage = "Extra Deck Case",
            notes = "Monstro Link-2 indispensável em quase 100% dos decks competitivos do meta atual.",
            tags = "yugioh, sp little knight, link, meta, age of overlord"
        ),

        // ==================== ONE PIECE CARD GAME ====================
        RealCatalogEntry(
            name = "Monkey.D.Luffy (Manga Rare Alternate Art Gear 5)",
            category = "Trading Cards",
            subCategory = "One Piece Card Game",
            collection = "Awakening of the New Era [OP-05]",
            itemNumber = "OP05-060",
            rarity = "Secret Rare Manga",
            variant = "Manga Alternate Art Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "JP / EN",
            realMarketPriceBrl = 7800.00,
            suggestedPurchasePriceBrl = 5200.00,
            defaultStorage = "Cofre Graduado / Slab Magnético",
            notes = "O lendário Gear 5 com os painéis desenhados à mão pelo mestre Eiichiro Oda no fundo. O Holy Grail de One Piece.",
            tags = "one piece, luffy, manga rare, gear 5, op05, grail"
        ),
        RealCatalogEntry(
            name = "Roronoa Zoro (Manga Rare Alternate Art)",
            category = "Trading Cards",
            subCategory = "One Piece Card Game",
            collection = "Wings of the Captain [OP-06]",
            itemNumber = "OP06-118",
            rarity = "Secret Rare Manga",
            variant = "Manga Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 5400.00,
            suggestedPurchasePriceBrl = 3600.00,
            defaultStorage = "Slab Magnético OP",
            notes = "Manga Rare de Zoro com seus ataques clássicos e fundo de mangá.",
            tags = "one piece, zoro, manga rare, op06, espadachim"
        ),
        RealCatalogEntry(
            name = "Portgas.D.Ace (Manga Rare Alternate Art)",
            category = "Trading Cards",
            subCategory = "One Piece Card Game",
            collection = "Paramount War [OP-02]",
            itemNumber = "OP02-013",
            rarity = "Secret Rare Manga",
            variant = "Manga Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 6200.00,
            suggestedPurchasePriceBrl = 4100.00,
            defaultStorage = "Slab Acrílico Graduado",
            notes = "Ace punhos de fogo no arco de Marineford. Uma das primeiras e mais raras Manga Rares.",
            tags = "one piece, ace, manga rare, op02, fogo"
        ),
        RealCatalogEntry(
            name = "Nami (Parallel Alternate Art Leader/Card)",
            category = "Trading Cards",
            subCategory = "One Piece Card Game",
            collection = "Romance Dawn [OP-01]",
            itemNumber = "OP01-016",
            rarity = "Super Rare Alternate Art",
            variant = "Parallel Foil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 1450.00,
            suggestedPurchasePriceBrl = 950.00,
            defaultStorage = "Pasta One Piece Luxo",
            notes = "Arte alternativa clássica de Nami do primeiro set Romance Dawn.",
            tags = "one piece, nami, romance dawn, op01, alt art"
        ),

        // ==================== DISNEY LORCANA ====================
        RealCatalogEntry(
            name = "Elsa - Spirit of Winter (Enchanted Foil)",
            category = "Trading Cards",
            subCategory = "Disney Lorcana",
            collection = "The First Chapter",
            itemNumber = "#207/204",
            rarity = "Enchanted Rare",
            variant = "Enchanted Holofoil Sem Bordas",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 5900.00,
            suggestedPurchasePriceBrl = 3900.00,
            defaultStorage = "Cofre / Slab Magnético UV",
            notes = "A carta mais valiosa e emblemática do primeiro capítulo de Disney Lorcana.",
            tags = "lorcana, disney, elsa, frozen, enchanted, first chapter, grail"
        ),
        RealCatalogEntry(
            name = "Mickey Mouse - Brave Little Tailor (Enchanted)",
            category = "Trading Cards",
            subCategory = "Disney Lorcana",
            collection = "The First Chapter",
            itemNumber = "#206/204",
            rarity = "Enchanted Rare",
            variant = "Enchanted Holofoil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 1800.00,
            suggestedPurchasePriceBrl = 1200.00,
            defaultStorage = "Pasta Lorcana First Chapter",
            notes = "Mickey alfaiate em arte completa com acabamento brilhante encantado.",
            tags = "lorcana, disney, mickey, enchanted, first chapter"
        ),
        RealCatalogEntry(
            name = "Cinderella - Stouthearted (Enchanted)",
            category = "Trading Cards",
            subCategory = "Disney Lorcana",
            collection = "Rise of the Floodborn",
            itemNumber = "#205/204",
            rarity = "Enchanted Rare",
            variant = "Enchanted Holofoil",
            defaultCondition = "Near Mint (NM)",
            languageOrScale = "EN",
            realMarketPriceBrl = 1400.00,
            suggestedPurchasePriceBrl = 950.00,
            defaultStorage = "TopLoader Case Lorcana",
            notes = "Cinderela cavaleira em armadura de aço com espada mágica.",
            tags = "lorcana, cinderella, floodborn, enchanted, princesa"
        ),

        // ==================== CARRINHOS / DIECAST ====================
        RealCatalogEntry(
            name = "Nissan Skyline GT-R (BNR34) Super Treasure Hunt",
            category = "Carrinhos / Diecast",
            subCategory = "Hot Wheels",
            collection = "Mainline Factory Sealed 2025",
            itemNumber = "#142/250",
            rarity = "Super Treasure Hunt (STH)",
            variant = "Spectraflame Bayside Blue + Real Riders",
            defaultCondition = "Lacrado no Blister (Mint)",
            languageOrScale = "1:64",
            realMarketPriceBrl = 380.00,
            suggestedPurchasePriceBrl = 240.00,
            defaultStorage = "Protetor Acrílico Blister #1",
            notes = "Pneus de borracha Real Riders com rodas raiadas cromadas, pintura Spectraflame e logotipo TH discreto.",
            tags = "hotwheels, sth, super treasure hunt, skyline, nissan, jdm, 1:64"
        ),
        RealCatalogEntry(
            name = "'71 Datsun 510 Wagon Super Treasure Hunt",
            category = "Carrinhos / Diecast",
            subCategory = "Hot Wheels",
            collection = "Mainline 2024",
            itemNumber = "#045/250",
            rarity = "Super Treasure Hunt (STH)",
            variant = "Spectraflame Verde Oliva + Real Riders",
            defaultCondition = "Lacrado no Blister (Mint)",
            languageOrScale = "1:64",
            realMarketPriceBrl = 340.00,
            suggestedPurchasePriceBrl = 210.00,
            defaultStorage = "Protetor Acrílico Blister #2",
            notes = "Clássica perua Datsun 510 em versão Super Treasure Hunt altamente valorizada.",
            tags = "hotwheels, datsun, wagon, sth, super treasure hunt"
        ),
        RealCatalogEntry(
            name = "Porsche 911 GT3 RS (Car Culture Boulevard)",
            category = "Carrinhos / Diecast",
            subCategory = "Hot Wheels",
            collection = "Car Culture: Boulevard",
            itemNumber = "#78",
            rarity = "Premium Metal/Metal",
            variant = "Real Riders + Chassis 100% Metal",
            defaultCondition = "Lacrado no Blister (Mint)",
            languageOrScale = "1:64",
            realMarketPriceBrl = 95.00,
            suggestedPurchasePriceBrl = 55.00,
            defaultStorage = "Gaveta Colecionáveis #2",
            notes = "Linha Boulevard com acabamento de colecionador, faróis e lanternas detalhados.",
            tags = "hotwheels, boulevard, premium, porsche, 911 gt3"
        ),
        RealCatalogEntry(
            name = "Toyota Supra MK4 Fast & Furious (Premium)",
            category = "Carrinhos / Diecast",
            subCategory = "Hot Wheels",
            collection = "Fast & Furious Premium Series",
            itemNumber = "#01",
            rarity = "Premium Metal/Metal",
            variant = "Laranja Brian O'Conner + Real Riders",
            defaultCondition = "Lacrado no Blister (Mint)",
            languageOrScale = "1:64",
            realMarketPriceBrl = 140.00,
            suggestedPurchasePriceBrl = 75.00,
            defaultStorage = "Vitrine Miniaturas #1",
            notes = "O icônico Toyota Supra laranja de Brian O'Conner do primeiro Velozes e Furiosos.",
            tags = "hotwheels, supra, toyota, fast and furious, paul walker"
        ),
        RealCatalogEntry(
            name = "Kaido House Datsun 510 Pro Street (Raw Metal Chase)",
            category = "Carrinhos / Diecast",
            subCategory = "Kaido House",
            collection = "Kaido House x Mini GT",
            itemNumber = "KHMG042-CHASE",
            rarity = "Chase 1:24",
            variant = "Raw Metal Cru Polido",
            defaultCondition = "Novo na Caixa Lacrada (MIB)",
            languageOrScale = "1:64",
            realMarketPriceBrl = 380.00,
            suggestedPurchasePriceBrl = 220.00,
            defaultStorage = "Vitrine Principal",
            notes = "Design assinado pelo designer Jun Imai. Versão secreta Raw Metal Chase (1 a cada 24 caixas).",
            tags = "kaido house, minigt, chase, datsun, 510, jun imai, raw"
        ),
        RealCatalogEntry(
            name = "Red Line Club (RLC) 1993 Ford Mustang Cobra R",
            category = "Carrinhos / Diecast",
            subCategory = "Hot Wheels",
            collection = "Red Line Club Exclusive (RLC)",
            itemNumber = "RLC-2024-03",
            rarity = "Red Line Club (RLC)",
            variant = "Spectraflame Vermelho + Capô que abre",
            defaultCondition = "Novo no Blister com Holograma",
            languageOrScale = "1:64",
            realMarketPriceBrl = 420.00,
            suggestedPurchasePriceBrl = 280.00,
            defaultStorage = "Protetor Acrílico RLC",
            notes = "Exclusivo para membros do Red Line Club americano com numeração de série na cartela.",
            tags = "hotwheels, rlc, mustang, cobra, red line club"
        ),
        RealCatalogEntry(
            name = "Matchbox 1968 Dodge D-200 4x4 (Super Chase)",
            category = "Carrinhos / Diecast",
            subCategory = "Matchbox",
            collection = "Matchbox Super Chase 2024",
            itemNumber = "#04/100",
            rarity = "Super Chase",
            variant = "Pneus de Borracha + Pintura Especial",
            defaultCondition = "Lacrado no Blister (Mint)",
            languageOrScale = "1:64",
            realMarketPriceBrl = 180.00,
            suggestedPurchasePriceBrl = 90.00,
            defaultStorage = "Protetor Acrílico #3",
            notes = "Rara versão Super Chase da Matchbox, equivalente aos STH da Hot Wheels.",
            tags = "matchbox, super chase, dodge, 4x4, pickup"
        ),

        // ==================== ACTION FIGURES & FUNKO ====================
        RealCatalogEntry(
            name = "Homem-Aranha (Marvel Legends Retro Card Toy Biz)",
            category = "Action Figures",
            subCategory = "Marvel Legends",
            collection = "Marvel Legends Retro Card",
            itemNumber = "F0228",
            rarity = "Edição Especial Retro",
            variant = "Cartela Vintage Toy Biz",
            defaultCondition = "Lacrado na Cartela (Mint)",
            languageOrScale = "1:12 (6 polegadas)",
            realMarketPriceBrl = 260.00,
            suggestedPurchasePriceBrl = 160.00,
            defaultStorage = "Nicho Parede Quarto",
            notes = "Mais de 30 pontos de articulação no clássico uniforme vermelho e azul com olhos grandes dos anos 90.",
            tags = "marvel, spiderman, hasbro, legends, retro, toy biz"
        ),
        RealCatalogEntry(
            name = "Wolverine '97 (Marvel Legends X-Men Animated)",
            category = "Action Figures",
            subCategory = "Marvel Legends",
            collection = "X-Men '97 Series",
            itemNumber = "F8402",
            rarity = "Edição Colecionador",
            variant = "Uniforme Amarelo e Azul com Garras",
            defaultCondition = "Lacrado na Cartela (Mint)",
            languageOrScale = "1:12 (6 polegadas)",
            realMarketPriceBrl = 280.00,
            suggestedPurchasePriceBrl = 180.00,
            defaultStorage = "Estante X-Men",
            notes = "Baseado no design da aclamada animação X-Men '97 do Disney+.",
            tags = "marvel, wolverine, x-men, xmen 97, hasbro"
        ),
        RealCatalogEntry(
            name = "Funko Pop! Luffy Gear 5 #1607 (Glow in the Dark Chase)",
            category = "Action Figures",
            subCategory = "Funko Pop!",
            collection = "One Piece Animation",
            itemNumber = "#1607 Chase",
            rarity = "Chase 1:6",
            variant = "Brilha no Escuro (GITD)",
            defaultCondition = "Novo com Protetor Rígido UV",
            languageOrScale = "4 polegadas",
            realMarketPriceBrl = 340.00,
            suggestedPurchasePriceBrl = 190.00,
            defaultStorage = "Estante Funko Sala",
            notes = "Selo Chase oficial da Funko com protetor Pop Shield UV.",
            tags = "funko, pop, one piece, luffy, gear 5, gitd, chase"
        ),

        // ==================== MOEDAS & NUMISMÁTICA ====================
        RealCatalogEntry(
            name = "Moeda 1 Real 50 Anos Banco Central (BCB)",
            category = "Moedas",
            subCategory = "Moedas do Brasil (Real)",
            collection = "Moedas Comemorativas do Real",
            itemNumber = "BCB-50-2015",
            rarity = "Comemorativa Escassa",
            variant = "Bimetálica Flor de Cunho",
            defaultCondition = "Flor de Cunho (FC)",
            languageOrScale = "Brasil - 2015",
            realMarketPriceBrl = 65.00,
            suggestedPurchasePriceBrl = 25.00,
            defaultStorage = "Cápsula Acrílica / Álbum Numismático",
            notes = "Cunhada em comemoração aos 50 anos do Banco Central do Brasil em estado impecável Flor de Cunho.",
            tags = "moeda, real, bcb, 50 anos, flor de cunho, numismatica"
        ),
        RealCatalogEntry(
            name = "Moeda 1 Real Declaração Universal dos Direitos Humanos 1998",
            category = "Moedas",
            subCategory = "Moedas do Brasil (Real)",
            collection = "Moedas Comemorativas do Real",
            itemNumber = "DH-1998",
            rarity = "Raríssima (Tiragem 600 mil)",
            variant = "Cuproníquel / Alpaca",
            defaultCondition = "Flor de Cunho (FC)",
            languageOrScale = "Brasil - 1998",
            realMarketPriceBrl = 450.00,
            suggestedPurchasePriceBrl = 280.00,
            defaultStorage = "Cápsula Acrílica Selada",
            notes = "A moeda mais rara e valiosa de toda a família do Real (tiragem de apenas 600.000 unidades).",
            tags = "moeda, real, direitos humanos, dh, 1998, rara, flor de cunho"
        ),
        RealCatalogEntry(
            name = "Moeda 1 Real Entrega da Bandeira Olímpica Londres 2012",
            category = "Moedas",
            subCategory = "Moedas do Brasil (Real)",
            collection = "Moedas Comemorativas do Real",
            itemNumber = "Bandeira-2012",
            rarity = "Comemorativa Valiosa",
            variant = "Bimetálica Flor de Cunho",
            defaultCondition = "Flor de Cunho (FC)",
            languageOrScale = "Brasil - 2012",
            realMarketPriceBrl = 190.00,
            suggestedPurchasePriceBrl = 110.00,
            defaultStorage = "Cápsula Numismática Protetora",
            notes = "Moeda comemorativa da passagem da bandeira olímpica de Londres 2012 para o Rio 2016.",
            tags = "moeda, real, bandeira, olimpiadas, londres 2012, rio 2016"
        ),
        RealCatalogEntry(
            name = "Moeda 2000 Réis 1900 (IV Centenário do Descobrimento)",
            category = "Moedas",
            subCategory = "Moedas do Brasil (Réis / Cruzeiro)",
            collection = "Réis da República / Comemorativas",
            itemNumber = "4Cent-1900",
            rarity = "Prata Histórica Rara",
            variant = "Prata Lei 0.916",
            defaultCondition = "Soberba (SOB)",
            languageOrScale = "Brasil - 1900",
            realMarketPriceBrl = 850.00,
            suggestedPurchasePriceBrl = 550.00,
            defaultStorage = "Cofre Numismático",
            notes = "Cunhada na Casa da Moeda do Rio de Janeiro para celebrar 400 anos do descobrimento do Brasil.",
            tags = "moeda, reis, prata, 1900, descobrimento, cabral, numismatica"
        )
    )

    val allEntries: List<RealCatalogEntry> by lazy {
        val fromSets = SetRegistry.popularSets.flatMap { set ->
            set.items.map { item ->
                RealCatalogEntry(
                    name = item.name,
                    category = when (set.franchise) {
                        "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game", "Disney Lorcana", "Star Wars: Unlimited", "Dragon Ball Super" -> "Trading Cards"
                        "Hot Wheels" -> "Carrinhos / Diecast"
                        "Moedas" -> "Moedas"
                        else -> "Outros Colecionáveis"
                    },
                    subCategory = set.franchise,
                    collection = set.name,
                    itemNumber = item.number,
                    rarity = item.rarity,
                    variant = item.variant,
                    defaultCondition = "Near Mint (NM)",
                    languageOrScale = if (set.franchise == "Hot Wheels") "1:64" else item.language,
                    realMarketPriceBrl = item.estimatedPriceBrl,
                    suggestedPurchasePriceBrl = item.estimatedPriceBrl * 0.65,
                    defaultStorage = "Pasta ${set.name}",
                    notes = "Item oficial catalogado na coleção ${set.name} (${set.year}) [${set.code}].",
                    tags = "${set.franchise}, ${set.name}, ${set.code}, ${item.name}, ${item.rarity}, ${item.patternVariant}".lowercase(),
                    releaseYear = set.year,
                    setCode = if (item.setCode.isNotBlank()) item.setCode else set.code,
                    era = set.era,
                    patternVariant = item.patternVariant
                )
            }
        }

        val seen = mutableSetOf<String>()
        val combined = mutableListOf<RealCatalogEntry>()

        for (entry in rawEntries) {
            val key = "${entry.collection.lowercase().trim()}_${entry.itemNumber.lowercase().trim()}_${entry.name.lowercase().trim()}"
            if (seen.add(key)) {
                combined.add(entry)
            }
        }

        for (entry in fromSets) {
            val key = "${entry.collection.lowercase().trim()}_${entry.itemNumber.lowercase().trim()}_${entry.name.lowercase().trim()}"
            if (seen.add(key)) {
                combined.add(entry)
            }
        }

        combined
    }

    /**
     * Live search matching card name, franchise, set, setCode, number, or tags.
     */
    fun search(query: String, maxResults: Int = 10): List<RealCatalogEntry> {
        val trimmed = query.trim().lowercase()
        if (trimmed.length < 2) return emptyList()

        return allEntries
            .filter { entry ->
                entry.name.lowercase().contains(trimmed) ||
                entry.collection.lowercase().contains(trimmed) ||
                entry.setCode.lowercase().contains(trimmed) ||
                entry.subCategory.lowercase().contains(trimmed) ||
                entry.itemNumber.lowercase().contains(trimmed) ||
                entry.tags.lowercase().contains(trimmed) ||
                entry.rarity.lowercase().contains(trimmed)
            }
            .sortedByDescending { entry ->
                when {
                    entry.setCode.lowercase() == trimmed -> 120
                    entry.name.lowercase().startsWith(trimmed) -> 100
                    entry.name.lowercase().contains(trimmed) -> 50
                    entry.subCategory.lowercase().contains(trimmed) -> 30
                    entry.collection.lowercase().contains(trimmed) -> 20
                    else -> 10
                }
            }
            .take(maxResults)
    }

    /**
     * Get real market price lookup for any given item by matching name/number.
     */
    fun findBestMatch(name: String, number: String = "", set: String = ""): RealCatalogEntry? {
        val cleanName = name.trim().lowercase()
        if (cleanName.isBlank()) return null

        // 1. Direct or reciprocal match
        val directMatch = allEntries.firstOrNull { entry ->
            val entryLower = entry.name.lowercase()
            entryLower == cleanName ||
            (cleanName.length > 3 && entryLower.contains(cleanName)) ||
            (entryLower.length > 3 && cleanName.contains(entryLower))
        }
        if (directMatch != null) return directMatch

        // 2. Number + Set match
        if (number.isNotBlank()) {
            val numMatch = allEntries.firstOrNull { entry ->
                entry.itemNumber.contains(number, ignoreCase = true) &&
                (set.isBlank() || entry.collection.contains(set, ignoreCase = true) || entry.setCode.equals(set, ignoreCase = true))
            }
            if (numMatch != null) return numMatch
        }

        // 3. Extract individual words / tokens (e.g. "pikachu", "bulbasaur", "squirtle", "gengar", "mewtwo", "173/165")
        val tokens = cleanName.split(Regex("[\\s,/#\\-_]+")).filter { it.length >= 3 }
        for (token in tokens) {
            val tokenMatch = allEntries.firstOrNull { entry ->
                entry.name.lowercase().contains(token) ||
                entry.itemNumber.lowercase().contains(token) ||
                entry.tags.lowercase().contains(token)
            }
            if (tokenMatch != null) return tokenMatch
        }

        return null
    }
}

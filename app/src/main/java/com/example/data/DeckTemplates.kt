package com.example.data

object DeckTemplates {

    fun getStarterDecks(): List<Pair<Deck, List<DeckCard>>> {
        return listOf(
            createCharizardStandardDeck(),
            createPikachuStandardDeck(),
            createTheThingCommanderDeck(),
            createMonoRedPauperDeck(),
            createBlueEyesYugiohDeck(),
            createLuffyOnePieceDeck()
        )
    }

    private fun createCharizardStandardDeck(): Pair<Deck, List<DeckCard>> {
        val deck = Deck(
            id = 0,
            name = "Charizard ex & Pidgeot ex",
            game = "Pokémon TCG",
            format = "Padrão (Standard)",
            commanderOrLeader = "",
            description = "Deck Tier 1 competitivo de Pokémon TCG Standard. Busca peças com Busca Rápida de Pidgeot ex e finaliza com o dano colossal de Charizard ex Tera Escuridão.",
            coverImageUrl = "https://assets.tcgdex.net/en/sv/sv3pt5/199/high.webp",
            themeColorHex = "#DC2626"
        )
        val cards = listOf(
            DeckCard(0, 0, "Charizard ex", "Pokémon", "Estágio 2 (Tera)", "Scarlet & Violet 151", "151/165", "https://assets.tcgdex.net/en/sv/sv3pt5/199/high.webp", 3, 680.0, "HP 330", "Special Illustration Rare"),
            DeckCard(0, 0, "Charmeleon", "Pokémon", "Estágio 1", "Scarlet & Violet 151", "005/165", "https://assets.tcgdex.net/en/sv/sv3pt5/005/high.webp", 1, 12.0, "HP 90", "Incomum"),
            DeckCard(0, 0, "Charmander", "Pokémon", "Básico", "Scarlet & Violet 151", "004/165", "https://assets.tcgdex.net/en/sv/sv3pt5/004/high.webp", 4, 18.0, "HP 70", "Comum"),
            DeckCard(0, 0, "Pidgeot ex", "Pokémon", "Estágio 2", "Obsidian Flames", "164/197", "https://assets.tcgdex.net/en/sv/sv03/164/high.webp", 2, 85.0, "HP 280", "Ultra Raro"),
            DeckCard(0, 0, "Pidgey", "Pokémon", "Básico", "Obsidian Flames", "162/197", "https://assets.tcgdex.net/en/sv/sv03/162/high.webp", 2, 5.0, "HP 60", "Comum"),
            DeckCard(0, 0, "Luminion V", "Pokémon", "Básico (Rule Box)", "Crown Zenith", "040/159", "https://assets.tcgdex.net/en/swsh/swsh12pt5/040/high.webp", 1, 35.0, "HP 170", "Ultra Raro"),
            DeckCard(0, 0, "Rotom V", "Pokémon", "Básico (Rule Box)", "Lost Origin", "058/196", "https://assets.tcgdex.net/en/swsh/swsh11/058/high.webp", 1, 28.0, "HP 190", "Ultra Raro"),
            DeckCard(0, 0, "Manaphy", "Pokémon", "Básico", "Brilliant Stars", "041/172", "https://assets.tcgdex.net/en/swsh/swsh09/041/high.webp", 1, 8.0, "HP 70", "Raro"),
            DeckCard(0, 0, "Radian Charizard", "Pokémon", "Básico Radiante", "Pokémon GO", "011/078", "https://assets.tcgdex.net/en/swsh/swshp/011/high.webp", 1, 45.0, "HP 160", "Radiante"),
            DeckCard(0, 0, "Doce Raro (Rare Candy)", "Treinador", "Item", "Scarlet & Violet", "191/198", "", 4, 6.0, "", "Incomum"),
            DeckCard(0, 0, "Ultra Bola (Ultra Ball)", "Treinador", "Item", "Scarlet & Violet", "196/198", "", 4, 4.0, "", "Incomum"),
            DeckCard(0, 0, "Bola de Ninho (Nest Ball)", "Treinador", "Item", "Scarlet & Violet", "181/198", "", 4, 5.0, "", "Incomum"),
            DeckCard(0, 0, "Maca de Resgate (Super Rod)", "Treinador", "Item", "Paldea Evolved", "188/193", "", 2, 7.0, "", "Incomum"),
            DeckCard(0, 0, "Poffin de Companheiro (Buddy-Buddy Poffin)", "Treinador", "Item", "Temporal Forces", "144/162", "", 4, 15.0, "", "Incomum"),
            DeckCard(0, 0, "Ordem da Chefia (Boss's Orders)", "Treinador", "Apoiador", "Paldea Evolved", "172/193", "", 2, 10.0, "", "Raro"),
            DeckCard(0, 0, "Pesquisa de Professores", "Treinador", "Apoiador", "Scarlet & Violet", "189/198", "", 3, 3.0, "", "Comum"),
            DeckCard(0, 0, "Iono", "Treinador", "Apoiador", "Paldea Evolved", "185/193", "", 3, 16.0, "", "Incomum"),
            DeckCard(0, 0, "Arven", "Treinador", "Apoiador", "Scarlet & Violet", "166/198", "", 2, 12.0, "", "Incomum"),
            DeckCard(0, 0, "Estádio Desmoronado (Collapsed Stadium)", "Treinador", "Estádio", "Brilliant Stars", "137/172", "", 1, 4.0, "", "Incomum"),
            DeckCard(0, 0, "Cinto da Bravura (Bravery Charm)", "Treinador", "Ferramenta", "Paldea Evolved", "173/193", "", 1, 8.0, "", "Incomum"),
            DeckCard(0, 0, "Energia Básica de Fogo", "Energia", "Básica", "Scarlet & Violet", "Basic Energy", "", 6, 1.0, "Fogo", "Comum", isBasicEnergyOrLand = true),
            DeckCard(0, 0, "Energia Básica de Escuridão", "Energia", "Básica", "Scarlet & Violet", "Basic Energy", "", 1, 1.0, "Escuridão", "Comum", isBasicEnergyOrLand = true)
        )
        return Pair(deck, cards)
    }

    private fun createPikachuStandardDeck(): Pair<Deck, List<DeckCard>> {
        val deck = Deck(
            id = 0,
            name = "Pikachu & Miraidon Turbo",
            game = "Pokémon TCG",
            format = "Padrão (Standard)",
            commanderOrLeader = "",
            description = "Aceleração rápida de energia elétrica com tandem unit e ataque veloz do Pikachu Illustration Rare e Raichu.",
            coverImageUrl = "https://assets.tcgdex.net/en/sv/sv3pt5/173/high.webp",
            themeColorHex = "#EAB308"
        )
        val cards = listOf(
            DeckCard(0, 0, "Pikachu (Illustration Rare)", "Pokémon", "Básico", "Scarlet & Violet 151", "173/165", "https://assets.tcgdex.net/en/sv/sv3pt5/173/high.webp", 4, 115.0, "HP 60", "Illustration Rare"),
            DeckCard(0, 0, "Raichu", "Pokémon", "Estágio 1", "Scarlet & Violet 151", "026/165", "https://assets.tcgdex.net/en/sv/sv3pt5/026/high.webp", 3, 8.0, "HP 120", "Raro"),
            DeckCard(0, 0, "Miraidon ex", "Pokémon", "Básico (Rule Box)", "Scarlet & Violet", "081/198", "https://assets.tcgdex.net/en/sv/sv01/081/high.webp", 2, 45.0, "HP 220", "Ultra Raro"),
            DeckCard(0, 0, "Iron Hands ex", "Pokémon", "Básico (Futuro)", "Paradox Rift", "070/182", "", 2, 70.0, "HP 230", "Ultra Raro"),
            DeckCard(0, 0, "Zapdos ex", "Pokémon", "Básico (Rule Box)", "Scarlet & Violet 151", "145/165", "https://assets.tcgdex.net/en/sv/sv3pt5/145/high.webp", 1, 65.0, "HP 200", "Ultra Raro"),
            DeckCard(0, 0, "Gerador Elétrico (Electric Generator)", "Treinador", "Item", "Scarlet & Violet", "170/198", "", 4, 12.0, "", "Incomum"),
            DeckCard(0, 0, "Ultra Bola (Ultra Ball)", "Treinador", "Item", "Scarlet & Violet", "196/198", "", 4, 4.0, "", "Incomum"),
            DeckCard(0, 0, "Bola de Ninho (Nest Ball)", "Treinador", "Item", "Scarlet & Violet", "181/198", "", 4, 5.0, "", "Incomum"),
            DeckCard(0, 0, "Interruptor de Energia (Energy Switch)", "Treinador", "Item", "Scarlet & Violet", "173/198", "", 3, 3.0, "", "Comum"),
            DeckCard(0, 0, "Ordem da Chefia (Boss's Orders)", "Treinador", "Apoiador", "Paldea Evolved", "172/193", "", 2, 10.0, "", "Raro"),
            DeckCard(0, 0, "Pesquisa de Professores", "Treinador", "Apoiador", "Scarlet & Violet", "189/198", "", 4, 3.0, "", "Comum"),
            DeckCard(0, 0, "Iono", "Treinador", "Apoiador", "Paldea Evolved", "185/193", "", 4, 16.0, "", "Incomum"),
            DeckCard(0, 0, "Praia da Cidade Costeira (Beach Court)", "Treinador", "Estádio", "Scarlet & Violet", "167/198", "", 2, 5.0, "", "Incomum"),
            DeckCard(0, 0, "Cápsula de Energia do Futuro", "Treinador", "Ferramenta", "Paradox Rift", "164/182", "", 2, 6.0, "", "Incomum"),
            DeckCard(0, 0, "Energia Básica de Raios", "Energia", "Básica", "Scarlet & Violet", "Lightning Energy", "", 19, 1.0, "Raios", "Comum", isBasicEnergyOrLand = true)
        )
        return Pair(deck, cards)
    }

    private fun createTheThingCommanderDeck(): Pair<Deck, List<DeckCard>> {
        val deck = Deck(
            id = 0,
            name = "The Thing, Ben Grimm & Marvel Heroes",
            game = "Magic: The Gathering",
            format = "Commander (EDH)",
            commanderOrLeader = "The Thing, Ben Grimm",
            description = "Deck de Commander Temático Marvel & Artefatos de 100 cartas únicas (Singleton). The Thing como Comandante de combate indestrutível.",
            coverImageUrl = "https://cards.scryfall.io/large/front/d/5/d5806e68-1054-458e-866d-5f4707f68295.jpg",
            themeColorHex = "#EA580C"
        )
        val cards = listOf(
            DeckCard(0, 0, "The Thing, Ben Grimm", "Comandante", "Lendária", "Secret Lair x Marvel", "003", "", 1, 150.0, "{2}{R}{W}", "Mítica", isCommanderOrLeader = true),
            DeckCard(0, 0, "O Um Anel (The One Ring)", "Artefato", "Lendário", "Lord of the Rings", "0269", "https://cards.scryfall.io/large/front/d/5/d5806e68-1054-458e-866d-5f4707f68295.jpg", 1, 460.0, "{4}", "Mítica"),
            DeckCard(0, 0, "Sol Ring", "Artefato", "Mana Rock", "Commander Masters", "047", "", 1, 12.0, "{1}", "Incomum"),
            DeckCard(0, 0, "Arcane Signet", "Artefato", "Mana Rock", "Commander Masters", "048", "", 1, 8.0, "{2}", "Comum"),
            DeckCard(0, 0, "Boros Signet", "Artefato", "Mana Rock", "Ravnica", "023", "", 1, 5.0, "{2}", "Comum"),
            DeckCard(0, 0, "Talisman of Conviction", "Artefato", "Mana Rock", "Modern Horizons", "230", "", 1, 6.0, "{2}", "Incomum"),
            DeckCard(0, 0, "Esper Sentinel", "Criatura", "Soldado", "Modern Horizons 2", "012", "", 1, 190.0, "{W}", "Mítica"),
            DeckCard(0, 0, "Ragavan, Nimble Pilferer", "Criatura", "Pirata Macaco", "Modern Horizons 2", "138", "", 1, 280.0, "{R}", "Mítica"),
            DeckCard(0, 0, "Stoneforge Mystic", "Criatura", "Artífice", "Double Masters", "031", "", 1, 95.0, "{1}{W}", "Rara"),
            DeckCard(0, 0, "Puresteel Paladin", "Criatura", "Cavaleiro", "New Phyrexia", "020", "", 1, 40.0, "{W}{W}", "Rara"),
            DeckCard(0, 0, "Sram, Senior Edificer", "Criatura", "Lendária", "Aether Revolt", "023", "", 1, 15.0, "{1}{W}", "Rara"),
            DeckCard(0, 0, "Aurelia, the Warleader", "Criatura", "Anjo Lendário", "Gatecrash", "143", "", 1, 55.0, "{2}{R}{R}{W}{W}", "Mítica"),
            DeckCard(0, 0, "Gisela, Blade of Goldnight", "Criatura", "Anjo Lendário", "Avacyn Restored", "209", "", 1, 45.0, "{4}{R}{W}{W}", "Mítica"),
            DeckCard(0, 0, "Sun Titan", "Criatura", "Gigante", "Magic 2011", "035", "", 1, 18.0, "{4}{W}{W}", "Mítica"),
            DeckCard(0, 0, "Lightning Bolt", "Mágica Instantânea", "Remoção", "Magic 2010", "149", "", 1, 14.0, "{R}", "Comum"),
            DeckCard(0, 0, "Swords to Plowshares", "Mágica Instantânea", "Remoção", "Commander", "032", "", 1, 12.0, "{W}", "Incomum"),
            DeckCard(0, 0, "Path to Exile", "Mágica Instantânea", "Remoção", "Conflux", "015", "", 1, 15.0, "{W}", "Incomum"),
            DeckCard(0, 0, "Teferi's Protection", "Mágica Instantânea", "Proteção", "Commander 2017", "008", "", 1, 180.0, "{2}{W}", "Rara"),
            DeckCard(0, 0, "Chaos Warp", "Mágica Instantânea", "Remoção", "Commander", "139", "", 1, 8.0, "{2}{R}", "Rara"),
            DeckCard(0, 0, "Boros Charm", "Mágica Instantânea", "Flexível", "Gatecrash", "148", "", 1, 15.0, "{R}{W}", "Incomum"),
            DeckCard(0, 0, "Blasphemous Act", "Feitiço", "Board Wipe", "Innistrad", "130", "", 1, 20.0, "{8}{R}", "Rara"),
            DeckCard(0, 0, "Wrath of God", "Feitiço", "Board Wipe", "Double Masters", "036", "", 1, 25.0, "{2}{W}{W}", "Rara"),
            DeckCard(0, 0, "Farewell", "Feitiço", "Exílio Global", "Kamigawa: Neon Dynasty", "013", "", 1, 55.0, "{4}{W}{W}", "Rara"),
            DeckCard(0, 0, "Smothering Tithe", "Encantamento", "Aceleração", "Ravnica Allegiance", "022", "", 1, 110.0, "{3}{W}", "Mítica"),
            DeckCard(0, 0, "Land Tax", "Encantamento", "Terrenos", "Legends", "026", "", 1, 160.0, "{W}", "Mítica"),
            DeckCard(0, 0, "Colossus Hammer", "Artefato", "Equipamento", "Core Set 2020", "223", "", 1, 15.0, "{1}", "Incomum"),
            DeckCard(0, 0, "Shadowspear", "Artefato", "Equipamento Lendário", "Theros Beyond Death", "236", "", 1, 95.0, "{1}", "Rara"),
            DeckCard(0, 0, "Lightning Greaves", "Artefato", "Equipamento", "Mirrodin", "199", "", 1, 35.0, "{2}", "Incomum"),
            DeckCard(0, 0, "Swiftfoot Boots", "Artefato", "Equipamento", "Magic 2012", "219", "", 1, 10.0, "{2}", "Incomum"),
            DeckCard(0, 0, "Command Tower", "Terreno", "Multicolor", "Commander", "228", "", 1, 6.0, "", "Comum"),
            DeckCard(0, 0, "Sacred Foundry", "Terreno", "Shock Land", "Guildpact", "157", "", 1, 85.0, "", "Rara"),
            DeckCard(0, 0, "Plateau", "Terreno", "Dual Land Reservada", "Revised", "284", "", 1, 1800.0, "", "Rara"),
            DeckCard(0, 0, "Arid Mesa", "Terreno", "Fetch Land", "Zendikar", "211", "", 1, 95.0, "", "Rara"),
            DeckCard(0, 0, "Planalto Básico (Plains)", "Terreno", "Básico", "Magic Standard", "Plains", "", 18, 1.0, "", "Comum", isBasicEnergyOrLand = true),
            DeckCard(0, 0, "Montanha Básica (Mountain)", "Terreno", "Básico", "Magic Standard", "Mountain", "", 18, 1.0, "", "Comum", isBasicEnergyOrLand = true)
        )
        return Pair(deck, cards)
    }

    private fun createMonoRedPauperDeck(): Pair<Deck, List<DeckCard>> {
        val deck = Deck(
            id = 0,
            name = "Mono Red Kuldotha Burn",
            game = "Magic: The Gathering",
            format = "Pauper",
            commanderOrLeader = "",
            description = "Deck rápido e agressivo do formato Pauper (100% de cartas Comuns). Custo acessível e alta eficiência de dano direto.",
            coverImageUrl = "https://cards.scryfall.io/large/front/2/3/23010b90-99c5-4122-835a-43d94183d256.jpg",
            themeColorHex = "#B91C1C"
        )
        val cards = listOf(
            DeckCard(0, 0, "Lightning Bolt", "Mágica Instantânea", "Dano Direto", "Magic 2010", "149", "", 4, 14.0, "{R}", "Comum"),
            DeckCard(0, 0, "Chain Lightning", "Feitiço", "Dano Direto", "Legends", "137", "", 4, 16.0, "{R}", "Comum"),
            DeckCard(0, 0, "Kuldotha Rebirth", "Feitiço", "Tokens", "Scars of Mirrodin", "096", "", 4, 12.0, "{R}", "Comum"),
            DeckCard(0, 0, "Monastery Swiftspear", "Criatura", "Monge Humano", "Khans of Tarkir", "118", "", 4, 18.0, "{R}", "Comum"),
            DeckCard(0, 0, "Goblin Blast-Runner", "Criatura", "Goblin", "The Brothers' War", "137", "", 4, 2.0, "{R}", "Comum"),
            DeckCard(0, 0, "Voldaren Epicure", "Criatura", "Vampiro Nobre", "Crimson Vow", "182", "", 4, 5.0, "{R}", "Comum"),
            DeckCard(0, 0, "Galvanic Blast", "Mágica Instantânea", "Metalcraft", "Scars of Mirrodin", "091", "", 4, 10.0, "{R}", "Comum"),
            DeckCard(0, 0, "Experimental Synthesizer", "Artefato", "Vantagem de Cartas", "Neon Dynasty", "138", "", 4, 8.0, "{R}", "Comum"),
            DeckCard(0, 0, "Improvised Club", "Mágica Instantânea", "Sacrifício", "Wilds of Eldraine", "137", "", 4, 3.0, "{1}{R}", "Comum"),
            DeckCard(0, 0, "Great Furnace", "Terreno", "Artefato", "Mirrodin", "282", "", 4, 15.0, "", "Comum"),
            DeckCard(0, 0, "Montanha Básica (Mountain)", "Terreno", "Básico", "Magic Standard", "Mountain", "", 16, 1.0, "", "Comum", isBasicEnergyOrLand = true)
        )
        return Pair(deck, cards)
    }

    private fun createBlueEyesYugiohDeck(): Pair<Deck, List<DeckCard>> {
        val deck = Deck(
            id = 0,
            name = "Blue-Eyes White Dragon Beatdown",
            game = "Yu-Gi-Oh!",
            format = "Avançado (Advanced)",
            commanderOrLeader = "",
            description = "Deck clássico e potente de Dragão Branco de Olhos Azuis com invocações especiais e sincronia do Dragão Espiritual.",
            coverImageUrl = "https://images.ygoprodeck.com/images/cards/89631139.jpg",
            themeColorHex = "#2563EB"
        )
        val cards = listOf(
            DeckCard(0, 0, "Blue-Eyes White Dragon", "Monstro", "Normal / Luz", "Legend of Blue Eyes", "LOB-001", "https://images.ygoprodeck.com/images/cards/89631139.jpg", 3, 45.0, "Nível 8 / ATK 3000", "Ultra Raro"),
            DeckCard(0, 0, "Blue-Eyes Alternative White Dragon", "Monstro", "Efeito", "Movie Pack", "MVP1-EN046", "", 3, 35.0, "Nível 8 / ATK 3000", "Secret Rare"),
            DeckCard(0, 0, "The White Stone of Ancients", "Monstro", "Tuner / Efeito", "Shining Victories", "SHVI-EN022", "", 3, 12.0, "Nível 1", "Ultra Raro"),
            DeckCard(0, 0, "The White Stone of Legend", "Monstro", "Tuner", "Crossroads of Chaos", "CSOC-EN079", "", 2, 8.0, "Nível 1", "Raro"),
            DeckCard(0, 0, "Sage with Eyes of Blue", "Monstro", "Tuner / Efeito", "Shining Victories", "SHVI-EN020", "", 3, 18.0, "Nível 1", "Ultra Raro"),
            DeckCard(0, 0, "Dragon Shrine", "Magia", "Normal", "Structure Deck", "SDBE-EN023", "", 3, 10.0, "", "Super Raro"),
            DeckCard(0, 0, "Return of the Dragon Lords", "Magia", "Normal", "Structure Deck", "SR02-EN025", "", 3, 22.0, "", "Ultra Raro"),
            DeckCard(0, 0, "Trade-In", "Magia", "Normal", "Ancient Prophecy", "ANPR-EN056", "", 3, 15.0, "", "Super Raro"),
            DeckCard(0, 0, "The Melody of Awakening Dragon", "Magia", "Normal", "Crossed Souls", "CROS-EN065", "", 3, 16.0, "", "Super Raro"),
            DeckCard(0, 0, "Silver's Cry", "Magia", "Quick-Play", "Structure Deck", "SDBE-EN021", "", 2, 7.0, "", "Super Raro"),
            DeckCard(0, 0, "Monster Reborn", "Magia", "Normal", "Legend of Blue Eyes", "LOB-118", "", 1, 20.0, "", "Ultra Raro"),
            DeckCard(0, 0, "Harpie's Feather Duster", "Magia", "Normal", "Tournament Pack", "TP8-EN002", "", 1, 25.0, "", "Super Raro"),
            DeckCard(0, 0, "Ash Blossom & Joyous Spring", "Monstro", "Handtrap", "Maximum Crisis", "MACR-EN036", "", 3, 35.0, "Nível 3", "Secret Rare"),
            DeckCard(0, 0, "Infinite Impermanence", "Armadilha", "Normal", "Flames of Destruction", "FLOD-EN077", "", 3, 40.0, "", "Ultra Raro"),
            DeckCard(0, 0, "Solemn Strike", "Armadilha", "Counter", "Breakers of Shadow", "BOSH-EN078", "", 3, 15.0, "", "Secret Rare")
        )
        return Pair(deck, cards)
    }

    private fun createLuffyOnePieceDeck(): Pair<Deck, List<DeckCard>> {
        val deck = Deck(
            id = 0,
            name = "Monkey D. Luffy Straw Hat Aggro",
            game = "One Piece Card Game",
            format = "Padrão",
            commanderOrLeader = "Monkey D. Luffy (ST01-001)",
            description = "Deck agressivo do bando do Chapéu de Palha com cartas de Rush e aumento de poder com DON!!",
            coverImageUrl = "",
            themeColorHex = "#DC2626"
        )
        val cards = listOf(
            DeckCard(0, 0, "Monkey D. Luffy (Leader)", "Líder", "Líder Vermelho", "Starter Deck 01", "ST01-001", "", 1, 40.0, "Poder 5000 / Vida 5", "Líder", isCommanderOrLeader = true),
            DeckCard(0, 0, "Roronoa Zoro (Rush)", "Personagem", "Supernova", "Romance Dawn", "OP01-025", "", 4, 90.0, "Custo 3 / Poder 5000", "Super Raro"),
            DeckCard(0, 0, "Monkey D. Luffy (Rush)", "Personagem", "Chapéu de Palha", "Starter Deck 01", "ST01-012", "", 4, 30.0, "Custo 5 / Poder 6000", "Super Raro"),
            DeckCard(0, 0, "Nami (Searcher)", "Personagem", "Chapéu de Palha", "Romance Dawn", "OP01-016", "", 4, 45.0, "Custo 1 / Poder 1000", "Raro"),
            DeckCard(0, 0, "Brook", "Personagem", "Chapéu de Palha", "Starter Deck 01", "ST01-011", "", 4, 10.0, "Custo 2 / Poder 3000", "Comum"),
            DeckCard(0, 0, "Tony Tony Chopper (Blocker)", "Personagem", "Chapéu de Palha", "Starter Deck 01", "ST01-006", "", 4, 15.0, "Custo 1 / Poder 1000", "Comum"),
            DeckCard(0, 0, "Sanji (Blocker)", "Personagem", "Chapéu de Palha", "Starter Deck 01", "ST01-004", "", 4, 12.0, "Custo 2 / Poder 3000", "Comum"),
            DeckCard(0, 0, "Nico Robin", "Personagem", "Chapéu de Palha", "Romance Dawn", "OP01-017", "", 4, 20.0, "Custo 3 / Poder 4000", "Raro"),
            DeckCard(0, 0, "Otama (Counter)", "Personagem", "País de Wano", "Romance Dawn", "OP01-006", "", 4, 25.0, "Custo 1 / Poder 0", "Incomum"),
            DeckCard(0, 0, "Jet Pistol", "Evento", "Remoção", "Starter Deck 01", "ST01-015", "", 4, 18.0, "Custo 4", "Comum"),
            DeckCard(0, 0, "Radical Beam", "Evento", "Contra-Ataque", "Romance Dawn", "OP01-029", "", 4, 22.0, "Custo 1", "Incomum"),
            DeckCard(0, 0, "Guard Point", "Evento", "Contra-Ataque", "Starter Deck 01", "ST01-014", "", 4, 8.0, "Custo 1", "Comum"),
            DeckCard(0, 0, "Thousand Sunny", "Palco", "Navio", "Starter Deck 01", "ST01-017", "", 2, 6.0, "Custo 2", "Comum"),
            DeckCard(0, 0, "Cartas DON!!", "DON!!", "Recurso", "One Piece Core", "DON!!", "", 10, 1.0, "DON!!", "Comum", isBasicEnergyOrLand = true)
        )
        return Pair(deck, cards)
    }
}

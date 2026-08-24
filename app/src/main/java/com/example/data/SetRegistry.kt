package com.example.data

object SetRegistry {

    // =========================================================================
    // 2026 MEGA ERA & 30TH CELEBRATION (Sets & Variants requested by user)
    // =========================================================================

    // 1. 30th Celebration (30C)
    private val set30thCelebrationItems: List<SetCardItem> = listOf(
        SetCardItem("001/030", "Pikachu (30th Anniversary Stamp)", "Promo Holo", "Foil Stamped", 180.00, setCode = "30C"),
        SetCardItem("002/030", "Charizard ex (Mega 30th Art)", "Double Rare", "Foil Textured", 450.00, setCode = "30C"),
        SetCardItem("003/030", "Blastoise ex (Mega 30th Art)", "Double Rare", "Foil Textured", 220.00, setCode = "30C"),
        SetCardItem("004/030", "Venusaur ex (Mega 30th Art)", "Double Rare", "Foil Textured", 190.00, setCode = "30C"),
        SetCardItem("005/030", "Mew ex (30th Cosmic Holo)", "Double Rare", "Cosmic Foil", 260.00, setCode = "30C"),
        SetCardItem("006/030", "Mewtwo ex (30th Gold Border)", "Ultra Rare", "Gold Foil", 380.00, setCode = "30C"),
        SetCardItem("007/030", "Lugia ex (30th Silver Relic)", "Ultra Rare", "Silver Foil", 340.00, setCode = "30C"),
        SetCardItem("008/030", "Ho-Oh ex (30th Rainbow Shine)", "Ultra Rare", "Foil", 290.00, setCode = "30C"),
        SetCardItem("009/030", "Rayquaza ex (Mega Emerald Spark)", "Ultra Rare", "Foil", 520.00, setCode = "30C"),
        SetCardItem("010/030", "Dialga Origin ex", "Ultra Rare", "Foil", 210.00, setCode = "30C"),
        SetCardItem("011/030", "Palkia Origin ex", "Ultra Rare", "Foil", 210.00, setCode = "30C"),
        SetCardItem("012/030", "Giratina Origin ex", "Ultra Rare", "Foil", 350.00, setCode = "30C"),
        SetCardItem("013/030", "Reshiram & Zekrom ex", "Ultra Rare", "Tag Duo Foil", 320.00, setCode = "30C"),
        SetCardItem("014/030", "Xerneas & Yveltal ex", "Ultra Rare", "Foil", 280.00, setCode = "30C"),
        SetCardItem("015/030", "Solgaleo & Lunala ex", "Ultra Rare", "Foil", 310.00, setCode = "30C"),
        SetCardItem("016/030", "Zacian & Zamazenta ex", "Ultra Rare", "Foil", 240.00, setCode = "30C"),
        SetCardItem("017/030", "Koraidon & Miraidon ex", "Ultra Rare", "Foil", 320.00, setCode = "30C"),
        SetCardItem("025/030", "Pikachu & Ash (30th Grand Journey)", "Special Illustration Rare", "Holo Textured", 1650.00, setCode = "30C"),
        SetCardItem("026/030", "Professor Oak 30th Tribute", "Special Illustration Rare", "Full Art Foil", 680.00, setCode = "30C"),
        SetCardItem("027/030", "Red & Blue Rivalry Legend", "Special Illustration Rare", "Full Art Foil", 950.00, setCode = "30C"),
        SetCardItem("030/030", "Arceus 30th Anniversary Gold HR", "Hyper Rare", "Pure Gold Foil", 2200.00, setCode = "30C")
    )

    // 2. 30th Celebration Classic Cards (30C-C)
    private val set30thClassicItems: List<SetCardItem> = listOf(
        SetCardItem("004/025", "Charizard (Base Set 1996 Reprint / 30th Stamp)", "Classic Holo", "Original Holofoil Pattern", 2400.00, setCode = "30C-C"),
        SetCardItem("002/025", "Blastoise (Base Set Reprint / 30th Stamp)", "Classic Holo", "Original Holofoil Pattern", 750.00, setCode = "30C-C"),
        SetCardItem("015/025", "Venusaur (Base Set Reprint / 30th Stamp)", "Classic Holo", "Original Holofoil Pattern", 600.00, setCode = "30C-C"),
        SetCardItem("024/025", "Birthday Pikachu (WotC Promo 30th)", "Classic Holo", "Confetti Foil", 480.00, setCode = "30C-C"),
        SetCardItem("010/025", "Shining Magikarp (Neo Revelation Reprint)", "Classic Secret", "Reflective Scales", 950.00, setCode = "30C-C"),
        SetCardItem("011/025", "Shining Gyarados (Neo Revelation Reprint)", "Classic Secret", "Reflective Scales", 1400.00, setCode = "30C-C"),
        SetCardItem("017/025", "Umbreon Star (POP Series 5 Reprint)", "Classic Gold Star", "Textured Gold Star", 3800.00, setCode = "30C-C"),
        SetCardItem("018/025", "Espeon Star (POP Series 5 Reprint)", "Classic Gold Star", "Textured Gold Star", 2900.00, setCode = "30C-C"),
        SetCardItem("020/025", "Rayquaza Star (EX Deoxys Reprint)", "Classic Gold Star", "Textured Gold Star", 4200.00, setCode = "30C-C"),
        SetCardItem("009/025", "Dark Blastoise (Team Rocket Reprint)", "Classic Holo", "Rocket Foil", 520.00, setCode = "30C-C"),
        SetCardItem("013/025", "Rocket's Mewtwo (Gym Challenge Reprint)", "Classic Holo", "Gym Foil", 680.00, setCode = "30C-C"),
        SetCardItem("022/025", "Mew ex (EX Legend Maker Reprint)", "Classic Holo", "EX Holo Border", 850.00, setCode = "30C-C")
    )

    // 3. ex Starter Sets: Sprigatito (MEM), Zorua (MEZ), Eevee (MEE)
    private val starterMemItems: List<SetCardItem> = listOf(
        SetCardItem("001/020", "Sprigatito", "Comum", "Normal", 3.00, setCode = "MEM"),
        SetCardItem("002/020", "Floragato", "Incomum", "Normal", 5.00, setCode = "MEM"),
        SetCardItem("003/020", "Meowscarada ex (Mega Starter)", "Double Rare", "Starter Foil", 35.00, setCode = "MEM"),
        SetCardItem("010/020", "Mega Energy Bloom", "Incomum", "Treinador", 4.00, setCode = "MEM")
    )
    private val starterMezItems: List<SetCardItem> = listOf(
        SetCardItem("001/020", "Hisuian Zorua", "Comum", "Normal", 3.50, setCode = "MEZ"),
        SetCardItem("002/020", "Hisuian Zoroark ex (Mega Starter)", "Double Rare", "Starter Foil", 40.00, setCode = "MEZ"),
        SetCardItem("008/020", "Darkness Claw", "Incomum", "Treinador", 4.00, setCode = "MEZ")
    )
    private val starterMeeItems: List<SetCardItem> = listOf(
        SetCardItem("001/020", "Eevee (Starter Art)", "Comum", "Normal", 6.00, setCode = "MEE"),
        SetCardItem("002/020", "Eevee ex (Prismatic Starter)", "Double Rare", "Starter Rainbow Foil", 55.00, setCode = "MEE"),
        SetCardItem("005/020", "Evolutionary Stone Trio", "Incomum", "Treinador", 5.00, setCode = "MEE")
    )

    // 4. Storm Emeralda (M6) & Pitch Black (PBL)
    private val stormEmeraldaItems: List<SetCardItem> = listOf(
        SetCardItem("015/090", "Rayquaza ex (Emeralda Storm)", "Double Rare", "Foil", 95.00, setCode = "M6"),
        SetCardItem("032/090", "Mega Sceptile ex", "Double Rare", "Mega Foil", 75.00, setCode = "M6"),
        SetCardItem("088/090", "Rayquaza ex SIR (Emerald Zenith)", "Special Art Rare", "Foil Textured", 1850.00, setCode = "M6"),
        SetCardItem("090/090", "Steven's Determination SIR", "Special Art Rare", "Full Art Foil", 620.00, setCode = "M6")
    )
    private val pitchBlackItems: List<SetCardItem> = listOf(
        SetCardItem("020/090", "Darkrai ex (Nightmare Realm)", "Double Rare", "Foil", 85.00, setCode = "PBL"),
        SetCardItem("045/090", "Mega Gengar ex (Pitch Abyss)", "Double Rare", "Mega Foil", 110.00, setCode = "PBL"),
        SetCardItem("088/090", "Darkrai ex SIR", "Special Art Rare", "Foil Textured", 1450.00, setCode = "PBL"),
        SetCardItem("089/090", "Mega Gengar ex SIR", "Special Art Rare", "Foil Textured", 1950.00, setCode = "PBL")
    )

    // 5. Play! Pokémon Prize Packs (PPPS9 & PPPS8 down to PPPS1)
    private val prizePack9Items: List<SetCardItem> = listOf(
        SetCardItem("001/080", "Pikachu ex (Prize Pack Stamp)", "Double Rare", "Prize Stamp Foil", 95.00, setCode = "PPPS9"),
        SetCardItem("015/080", "Charizard ex (Prize Pack Stamp)", "Double Rare", "Prize Stamp Foil", 280.00, setCode = "PPPS9"),
        SetCardItem("045/080", "Iono (Prize Pack Stamp)", "Ultra Rare", "Prize Stamp Foil", 160.00, setCode = "PPPS9"),
        SetCardItem("078/080", "Buddy-Buddy Poffin (Play Stamp Holo)", "Incomum", "Prize Stamp Foil", 45.00, setCode = "PPPS9")
    )
    private val prizePack8Items: List<SetCardItem> = listOf(
        SetCardItem("005/080", "Greninja ex (Prize Pack Stamp)", "Double Rare", "Prize Stamp Foil", 110.00, setCode = "PPPS8"),
        SetCardItem("022/080", "Ogerpon Teal Mask ex (Prize Stamp)", "Double Rare", "Prize Stamp Foil", 65.00, setCode = "PPPS8"),
        SetCardItem("060/080", "Carmine (Prize Pack Holo)", "Incomum", "Prize Stamp Foil", 50.00, setCode = "PPPS8")
    )

    // 6. Terastal Gathering Trio (C9.5C, Poké Ball C9.5P, Master Ball C9.5M)
    private val terastalGatheringItems: List<SetCardItem> = listOf(
        SetCardItem("001/187", "Bulbasaur (Terastal)", "Comum", "Normal", 5.00, setCode = "C9.5C"),
        SetCardItem("025/187", "Pikachu ex (Terastal Gathering)", "Double Rare", "Stellar Foil", 120.00, setCode = "C9.5C"),
        SetCardItem("050/187", "Terapagos ex (Stellar Crown)", "Double Rare", "Rainbow Foil", 85.00, setCode = "C9.5C"),
        SetCardItem("180/187", "Eevee (Terastal Gathering SIR)", "Special Art Rare", "Foil", 750.00, setCode = "C9.5C"),
        SetCardItem("187/187", "Terapagos ex Gold HR", "Hyper Rare", "Gold Foil", 680.00, setCode = "C9.5C")
    )
    private val terastalPokeBallItems: List<SetCardItem> = listOf(
        SetCardItem("001/187", "Bulbasaur (Poké Ball Reverse)", "Comum", "Poké Ball Reverse Holo", 22.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P"),
        SetCardItem("004/187", "Charmander (Poké Ball Reverse)", "Comum", "Poké Ball Reverse Holo", 35.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P"),
        SetCardItem("007/187", "Squirtle (Poké Ball Reverse)", "Comum", "Poké Ball Reverse Holo", 28.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P"),
        SetCardItem("025/187", "Pikachu (Poké Ball Reverse)", "Comum", "Poké Ball Reverse Holo", 65.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P"),
        SetCardItem("133/187", "Eevee (Poké Ball Reverse)", "Comum", "Poké Ball Reverse Holo", 45.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P"),
        SetCardItem("143/187", "Snorlax (Poké Ball Reverse)", "Rara", "Poké Ball Reverse Holo", 38.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P"),
        SetCardItem("150/187", "Mewtwo (Poké Ball Reverse)", "Rara", "Poké Ball Reverse Holo", 75.00, patternVariant = "Poké Ball Pattern", setCode = "C9.5P")
    )
    private val terastalMasterBallItems: List<SetCardItem> = listOf(
        SetCardItem("001/187", "Bulbasaur (Master Ball Mirror)", "Comum", "Master Ball Reverse Holo", 140.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M"),
        SetCardItem("004/187", "Charmander (Master Ball Mirror)", "Comum", "Master Ball Reverse Holo", 220.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M"),
        SetCardItem("007/187", "Squirtle (Master Ball Mirror)", "Comum", "Master Ball Reverse Holo", 180.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M"),
        SetCardItem("025/187", "Pikachu (Master Ball Mirror)", "Comum", "Master Ball Reverse Holo", 850.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M"),
        SetCardItem("130/187", "Gyarados (Master Ball Mirror)", "Rara", "Master Ball Reverse Holo", 260.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M"),
        SetCardItem("133/187", "Eevee (Master Ball Mirror)", "Comum", "Master Ball Reverse Holo", 380.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M"),
        SetCardItem("150/187", "Mewtwo (Master Ball Mirror)", "Rara", "Master Ball Reverse Holo", 490.00, patternVariant = "Master Ball Pattern", setCode = "C9.5M")
    )

    // 7. Abyss Eye (M5) & Chaos Rising (CRI)
    private val abyssEyeItems: List<SetCardItem> = listOf(
        SetCardItem("018/080", "Kyogre ex (Abyss Surge)", "Double Rare", "Foil", 85.00, setCode = "M5"),
        SetCardItem("075/080", "Kyogre ex SIR", "Special Art Rare", "Foil Textured", 1400.00, setCode = "M5")
    )
    private val chaosRisingItems: List<SetCardItem> = listOf(
        SetCardItem("020/095", "Groudon ex (Magma Core)", "Double Rare", "Foil", 90.00, setCode = "CRI"),
        SetCardItem("092/095", "Groudon ex SIR", "Special Art Rare", "Foil Textured", 1550.00, setCode = "CRI")
    )

    // 8. Stellar Crystal Trio (CSV9C, Poké Ball CSV9P, Master Ball CSV9M) & Gem Pack 5 (CBB5C)
    private val stellarCrystalItems: List<SetCardItem> = listOf(
        SetCardItem("001/100", "Latias ex", "Double Rare", "Stellar Foil", 65.00, setCode = "CSV9C"),
        SetCardItem("002/100", "Latios ex", "Double Rare", "Stellar Foil", 65.00, setCode = "CSV9C"),
        SetCardItem("095/100", "Latias & Latios Duo SIR", "Special Art Rare", "Foil Textured", 1950.00, setCode = "CSV9C")
    )
    private val stellarCrystalPokeItems: List<SetCardItem> = listOf(
        SetCardItem("001/100", "Latias (Poké Ball Reverse)", "Rara", "Poké Ball Pattern", 45.00, patternVariant = "Poké Ball Pattern", setCode = "CSV9P"),
        SetCardItem("002/100", "Latios (Poké Ball Reverse)", "Rara", "Poké Ball Pattern", 45.00, patternVariant = "Poké Ball Pattern", setCode = "CSV9P")
    )
    private val stellarCrystalMasterItems: List<SetCardItem> = listOf(
        SetCardItem("001/100", "Latias (Master Ball Reverse)", "Rara", "Master Ball Pattern", 380.00, patternVariant = "Master Ball Pattern", setCode = "CSV9M"),
        SetCardItem("002/100", "Latios (Master Ball Reverse)", "Rara", "Master Ball Pattern", 380.00, patternVariant = "Master Ball Pattern", setCode = "CSV9M")
    )
    private val gemPack5Items: List<SetCardItem> = listOf(
        SetCardItem("001/030", "Glaceon ex (Crystal Gem)", "Double Rare", "Gem Texture", 85.00, setCode = "CBB5C"),
        SetCardItem("002/030", "Leafeon ex (Crystal Gem)", "Double Rare", "Gem Texture", 85.00, setCode = "CBB5C")
    )

    // 9. Perfect Order (POR) & Sparkling Fable Trio (CSV8C, CSV8m, CSV8p) & Ninja Spinner (M4) & Gem Pack 4 (CBB4C)
    private val perfectOrderItems: List<SetCardItem> = listOf(
        SetCardItem("012/085", "Zygarde Complete ex", "Double Rare", "Order Foil", 95.00, setCode = "POR"),
        SetCardItem("080/085", "Zygarde Complete ex SIR", "Special Art Rare", "Foil", 1250.00, setCode = "POR")
    )
    private val sparklingFableItems: List<SetCardItem> = listOf(
        SetCardItem("020/064", "Pecharunt ex", "Double Rare", "Foil", 65.00, setCode = "CSV8C"),
        SetCardItem("088/064", "Pecharunt ex SIR", "Special Art Rare", "Foil", 580.00, setCode = "CSV8C")
    )
    private val sparklingFableMasterItems: List<SetCardItem> = listOf(
        SetCardItem("020/064", "Pecharunt (Master Ball Mirror)", "Double Rare", "Master Ball Reverse Holo", 390.00, patternVariant = "Master Ball Pattern", setCode = "CSV8m"),
        SetCardItem("012/064", "Okidogi (Master Ball Mirror)", "Rara", "Master Ball Reverse Holo", 180.00, patternVariant = "Master Ball Pattern", setCode = "CSV8m")
    )
    private val sparklingFablePokeItems: List<SetCardItem> = listOf(
        SetCardItem("020/064", "Pecharunt (Poké Ball Mirror)", "Double Rare", "Poké Ball Reverse Holo", 45.00, patternVariant = "Poké Ball Pattern", setCode = "CSV8p"),
        SetCardItem("012/064", "Okidogi (Poké Ball Mirror)", "Rara", "Poké Ball Reverse Holo", 25.00, patternVariant = "Poké Ball Pattern", setCode = "CSV8p")
    )
    private val ninjaSpinnerItems: List<SetCardItem> = listOf(
        SetCardItem("010/075", "Greninja ex (Ninja Spinner Strike)", "Double Rare", "Spin Foil", 120.00, setCode = "M4"),
        SetCardItem("074/075", "Greninja ex SIR (Spinner Art)", "Special Art Rare", "Foil Textured", 2200.00, setCode = "M4")
    )
    private val gemPack4Items: List<SetCardItem> = listOf(
        SetCardItem("001/030", "Sylveon ex (Gem Spark)", "Double Rare", "Gem Foil", 110.00, setCode = "CBB4C"),
        SetCardItem("005/030", "Umbreon ex (Gem Spark)", "Double Rare", "Gem Foil", 140.00, setCode = "CBB4C")
    )

    // 10. Ascended Heroes Quartet (ASC, Team Rocket ASCTR, Poké Ball ASCPB, Energy Symbol ASCES)
    private val ascendedHeroesItems: List<SetCardItem> = listOf(
        SetCardItem("025/195", "Mega Lucario ex", "Double Rare", "Hero Foil", 85.00, setCode = "ASC"),
        SetCardItem("045/195", "Mega Mewtwo X ex", "Double Rare", "Hero Foil", 140.00, setCode = "ASC"),
        SetCardItem("046/195", "Mega Mewtwo Y ex", "Double Rare", "Hero Foil", 140.00, setCode = "ASC"),
        SetCardItem("190/195", "Mega Lucario ex SIR", "Special Art Rare", "Foil Textured", 1750.00, setCode = "ASC"),
        SetCardItem("195/195", "Mega Mewtwo X SIR Gold", "Hyper Rare", "Gold Foil", 2400.00, setCode = "ASC")
    )
    private val ascendedTeamRocketItems: List<SetCardItem> = listOf(
        SetCardItem("025/195", "Mega Lucario (Team Rocket Stamp Pattern)", "Rara", "Team Rocket Silhouette Foil", 95.00, patternVariant = "Team Rocket Pattern", setCode = "ASCTR"),
        SetCardItem("045/195", "Mega Mewtwo (Team Rocket Stamp Pattern)", "Rara", "Team Rocket Silhouette Foil", 160.00, patternVariant = "Team Rocket Pattern", setCode = "ASCTR"),
        SetCardItem("004/195", "Charmander (Team Rocket Stamp Pattern)", "Comum", "Team Rocket Silhouette Foil", 55.00, patternVariant = "Team Rocket Pattern", setCode = "ASCTR"),
        SetCardItem("025/195", "Pikachu (Team Rocket Stamp Pattern)", "Comum", "Team Rocket Silhouette Foil", 85.00, patternVariant = "Team Rocket Pattern", setCode = "ASCTR")
    )
    private val ascendedPokeBallItems: List<SetCardItem> = listOf(
        SetCardItem("001/195", "Bulbasaur (Poké Ball's Pattern)", "Comum", "Poké Ball Holo Foil", 25.00, patternVariant = "Poké Ball Pattern", setCode = "ASCPB"),
        SetCardItem("004/195", "Charmander (Poké Ball's Pattern)", "Comum", "Poké Ball Holo Foil", 38.00, patternVariant = "Poké Ball Pattern", setCode = "ASCPB"),
        SetCardItem("025/195", "Pikachu (Poké Ball's Pattern)", "Comum", "Poké Ball Holo Foil", 68.00, patternVariant = "Poké Ball Pattern", setCode = "ASCPB")
    )
    private val ascendedEnergySymbolItems: List<SetCardItem> = listOf(
        SetCardItem("001/195", "Bulbasaur (Grass Energy Pattern)", "Comum", "Energy Symbol Pattern Foil", 22.00, patternVariant = "Energy Symbol Pattern", setCode = "ASCES"),
        SetCardItem("004/195", "Charmander (Fire Energy Pattern)", "Comum", "Energy Symbol Pattern Foil", 35.00, patternVariant = "Energy Symbol Pattern", setCode = "ASCES"),
        SetCardItem("025/195", "Pikachu (Lightning Energy Pattern)", "Comum", "Energy Symbol Pattern Foil", 65.00, patternVariant = "Energy Symbol Pattern", setCode = "ASCES"),
        SetCardItem("045/195", "Mewtwo (Psychic Energy Pattern)", "Rara", "Energy Symbol Pattern Foil", 110.00, patternVariant = "Energy Symbol Pattern", setCode = "ASCES")
    )

    // 11. Nihil Zero (M3) & Blade Awakening Trio (CSV7C, CSV7m, CSV7p)
    private val nihilZeroItems: List<SetCardItem> = listOf(
        SetCardItem("010/070", "Necrozma Ultra ex", "Double Rare", "Nihil Foil", 95.00, setCode = "M3"),
        SetCardItem("069/070", "Necrozma Ultra ex SIR", "Special Art Rare", "Foil Textured", 1350.00, setCode = "M3")
    )
    private val bladeAwakeningItems: List<SetCardItem> = listOf(
        SetCardItem("018/090", "Ceruledge ex (Blade Fire)", "Double Rare", "Blade Foil", 55.00, setCode = "CSV7C"),
        SetCardItem("032/090", "Armarouge ex (Blade Cannon)", "Double Rare", "Blade Foil", 45.00, setCode = "CSV7C"),
        SetCardItem("088/090", "Ceruledge ex SIR", "Special Art Rare", "Foil Textured", 780.00, setCode = "CSV7C")
    )
    private val bladeMasterBallItems: List<SetCardItem> = listOf(
        SetCardItem("018/090", "Ceruledge (Master Ball Mirror)", "Double Rare", "Master Ball Reverse Holo", 350.00, patternVariant = "Master Ball Pattern", setCode = "CSV7m"),
        SetCardItem("032/090", "Armarouge (Master Ball Mirror)", "Double Rare", "Master Ball Reverse Holo", 260.00, patternVariant = "Master Ball Pattern", setCode = "CSV7m")
    )
    private val bladePokeBallItems: List<SetCardItem> = listOf(
        SetCardItem("018/090", "Ceruledge (Poké Ball Mirror)", "Double Rare", "Poké Ball Reverse Holo", 45.00, patternVariant = "Poké Ball Pattern", setCode = "CSV7p"),
        SetCardItem("032/090", "Armarouge (Poké Ball Mirror)", "Double Rare", "Poké Ball Reverse Holo", 35.00, patternVariant = "Poké Ball Pattern", setCode = "CSV7p")
    )

    // =========================================================================
    // SCARLET & VIOLET ERA (2023 - 2025)
    // =========================================================================

    // Scarlet & Violet: Prismatic Evolutions (PRE - 2025)
    private val prismaticEvolutionsItems: List<SetCardItem> = listOf(
        SetCardItem("001/175", "Eevee ex", "Double Rare", "Stellar Rainbow Foil", 65.00, setCode = "PRE"),
        SetCardItem("010/175", "Vaporeon ex", "Double Rare", "Stellar Foil", 55.00, setCode = "PRE"),
        SetCardItem("022/175", "Jolteon ex", "Double Rare", "Stellar Foil", 55.00, setCode = "PRE"),
        SetCardItem("034/175", "Flareon ex", "Double Rare", "Stellar Foil", 55.00, setCode = "PRE"),
        SetCardItem("048/175", "Espeon ex", "Double Rare", "Stellar Foil", 75.00, setCode = "PRE"),
        SetCardItem("060/175", "Umbreon ex", "Double Rare", "Stellar Foil", 95.00, setCode = "PRE"),
        SetCardItem("072/175", "Leafeon ex", "Double Rare", "Stellar Foil", 50.00, setCode = "PRE"),
        SetCardItem("085/175", "Glaceon ex", "Double Rare", "Stellar Foil", 50.00, setCode = "PRE"),
        SetCardItem("098/175", "Sylveon ex", "Double Rare", "Stellar Foil", 85.00, setCode = "PRE"),
        SetCardItem("005/175", "Leafeon", "Incomum", "Normal", 6.00, setCode = "PRE"),
        SetCardItem("014/175", "Flareon", "Incomum", "Normal", 8.00, setCode = "PRE"),
        SetCardItem("020/175", "Vaporeon", "Incomum", "Normal", 8.00, setCode = "PRE"),
        SetCardItem("028/175", "Glaceon", "Incomum", "Normal", 7.00, setCode = "PRE"),
        SetCardItem("038/175", "Jolteon", "Incomum", "Normal", 7.50, setCode = "PRE"),
        SetCardItem("044/175", "Espeon", "Incomum", "Normal", 9.00, setCode = "PRE"),
        SetCardItem("055/175", "Sylveon", "Incomum", "Normal", 10.00, setCode = "PRE"),
        SetCardItem("062/175", "Umbreon", "Incomum", "Normal", 12.00, setCode = "PRE"),
        SetCardItem("076/175", "Eevee", "Comum", "Normal", 5.00, setCode = "PRE"),
        SetCardItem("160/175", "Umbreon ex (Special Illustration Rare)", "Special Art Rare", "Stellar Foil Textured", 2400.00, setCode = "PRE"),
        SetCardItem("161/175", "Espeon ex SIR", "Special Art Rare", "Foil Textured", 1100.00, setCode = "PRE"),
        SetCardItem("162/175", "Sylveon ex SIR", "Special Art Rare", "Foil Textured", 1450.00, setCode = "PRE"),
        SetCardItem("163/175", "Eevee ex SIR", "Special Art Rare", "Foil Textured", 980.00, setCode = "PRE"),
        SetCardItem("175/175", "Eevee ex Gold Hyper Rare", "Hyper Rare", "Pure Gold Foil", 1600.00, setCode = "PRE")
    )

    // Scarlet & Violet: Surging Sparks (SSP - 2024)
    private val surgingSparksItems: List<SetCardItem> = listOf(
        SetCardItem("018/191", "Pikachu ex (Stellar Tera)", "Double Rare", "Stellar Foil", 130.00, setCode = "SSP"),
        SetCardItem("034/191", "Milotic ex", "Double Rare", "Foil", 45.00, setCode = "SSP"),
        SetCardItem("057/191", "Pikachu", "Comum", "Normal", 4.00, setCode = "SSP"),
        SetCardItem("050/191", "Magnemite", "Comum", "Normal", 3.00, setCode = "SSP"),
        SetCardItem("051/191", "Magneton", "Incomum", "Normal", 4.50, setCode = "SSP"),
        SetCardItem("075/191", "Togepi", "Comum", "Normal", 3.50, setCode = "SSP"),
        SetCardItem("087/191", "Latias ex", "Double Rare", "Foil", 65.00, setCode = "SSP"),
        SetCardItem("090/191", "Trapinch", "Comum", "Normal", 2.50, setCode = "SSP"),
        SetCardItem("115/191", "Deino", "Comum", "Normal", 3.00, setCode = "SSP"),
        SetCardItem("120/191", "Archaludon ex", "Double Rare", "Foil", 50.00, setCode = "SSP"),
        SetCardItem("130/191", "Hydreigon ex", "Double Rare", "Foil", 40.00, setCode = "SSP"),
        SetCardItem("140/191", "Tatsugiri", "Comum", "Normal", 3.50, setCode = "SSP"),
        SetCardItem("156/191", "Drayton (Full Art)", "Ultra Rare", "Foil", 75.00, setCode = "SSP"),
        SetCardItem("158/191", "Lisia's Appeal (Full Art)", "Ultra Rare", "Foil", 160.00, setCode = "SSP"),
        SetCardItem("204/191", "Pikachu ex (Special Illustration Rare)", "Special Art Rare", "Stellar Rainbow Foil", 1450.00, setCode = "SSP"),
        SetCardItem("205/191", "Latias ex SIR", "Special Art Rare", "Foil", 580.00, setCode = "SSP"),
        SetCardItem("206/191", "Milotic ex SIR", "Special Art Rare", "Foil", 320.00, setCode = "SSP"),
        SetCardItem("214/191", "Lisia's Appeal SIR", "Special Art Rare", "Foil", 620.00, setCode = "SSP"),
        SetCardItem("220/191", "Pikachu ex Gold Hyper Rare", "Hyper Rare", "Gold Foil", 690.00, setCode = "SSP")
    )

    // Scarlet & Violet: 151 (MEW / SV3pt5 - 2023)
    private val pokemon151Items: List<SetCardItem> = listOf(
        SetCardItem("001/165", "Bulbasaur", "Comum", "Normal", 5.00, setCode = "151"),
        SetCardItem("002/165", "Ivysaur", "Incomum", "Normal", 7.00, setCode = "151"),
        SetCardItem("003/165", "Venusaur ex", "Double Rare", "Foil", 55.00, setCode = "151"),
        SetCardItem("004/165", "Charmander", "Comum", "Normal", 6.00, setCode = "151"),
        SetCardItem("005/165", "Charmeleon", "Incomum", "Normal", 8.50, setCode = "151"),
        SetCardItem("006/165", "Charizard ex", "Double Rare", "Foil", 210.00, setCode = "151"),
        SetCardItem("007/165", "Squirtle", "Comum", "Normal", 6.00, setCode = "151"),
        SetCardItem("008/165", "Wartortle", "Incomum", "Normal", 7.50, setCode = "151"),
        SetCardItem("009/165", "Blastoise ex", "Double Rare", "Foil", 85.00, setCode = "151"),
        SetCardItem("010/165", "Caterpie", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("011/165", "Metapod", "Incomum", "Normal", 4.00, setCode = "151"),
        SetCardItem("012/165", "Butterfree", "Rara", "Holo", 8.00, setCode = "151"),
        SetCardItem("013/165", "Weedle", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("014/165", "Kakuna", "Incomum", "Normal", 3.50, setCode = "151"),
        SetCardItem("015/165", "Beedrill", "Rara", "Holo", 7.50, setCode = "151"),
        SetCardItem("016/165", "Pidgey", "Comum", "Normal", 3.50, setCode = "151"),
        SetCardItem("017/165", "Pidgeotto", "Incomum", "Normal", 4.50, setCode = "151"),
        SetCardItem("018/165", "Pidgeot ex", "Double Rare", "Foil", 22.00, setCode = "151"),
        SetCardItem("019/165", "Rattata", "Comum", "Normal", 2.50, setCode = "151"),
        SetCardItem("020/165", "Raticate", "Incomum", "Normal", 3.50, setCode = "151"),
        SetCardItem("021/165", "Spearow", "Comum", "Normal", 2.50, setCode = "151"),
        SetCardItem("022/165", "Fearow", "Incomum", "Normal", 3.50, setCode = "151"),
        SetCardItem("023/165", "Ekans", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("024/165", "Arbok ex", "Double Rare", "Foil", 28.00, setCode = "151"),
        SetCardItem("025/165", "Pikachu", "Comum", "Normal", 15.00, setCode = "151"),
        SetCardItem("026/165", "Raichu", "Rara", "Holo", 18.00, setCode = "151"),
        SetCardItem("027/165", "Sandshrew", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("028/165", "Sandslash", "Incomum", "Normal", 4.00, setCode = "151"),
        SetCardItem("035/165", "Clefairy", "Comum", "Normal", 4.00, setCode = "151"),
        SetCardItem("037/165", "Vulpix", "Comum", "Normal", 4.50, setCode = "151"),
        SetCardItem("038/165", "Ninetales ex", "Double Rare", "Foil", 35.00, setCode = "151"),
        SetCardItem("039/165", "Jigglypuff", "Comum", "Normal", 3.50, setCode = "151"),
        SetCardItem("041/165", "Zubat", "Comum", "Normal", 2.50, setCode = "151"),
        SetCardItem("043/165", "Oddish", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("050/165", "Diglett", "Comum", "Normal", 2.50, setCode = "151"),
        SetCardItem("052/165", "Meowth", "Comum", "Normal", 4.00, setCode = "151"),
        SetCardItem("054/165", "Psyduck", "Comum", "Normal", 5.00, setCode = "151"),
        SetCardItem("056/165", "Mankey", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("058/165", "Growlithe", "Comum", "Normal", 4.50, setCode = "151"),
        SetCardItem("060/165", "Poliwag", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("063/165", "Abra", "Comum", "Normal", 3.50, setCode = "151"),
        SetCardItem("064/165", "Kadabra", "Incomum", "Normal", 6.00, setCode = "151"),
        SetCardItem("065/165", "Alakazam ex", "Double Rare", "Foil", 45.00, setCode = "151"),
        SetCardItem("066/165", "Machop", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("067/165", "Machoke", "Incomum", "Normal", 4.00, setCode = "151"),
        SetCardItem("068/165", "Machamp", "Rara", "Holo", 15.00, setCode = "151"),
        SetCardItem("069/165", "Bellsprout", "Comum", "Normal", 2.50, setCode = "151"),
        SetCardItem("074/165", "Geodude", "Comum", "Normal", 2.50, setCode = "151"),
        SetCardItem("077/165", "Ponyta", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("079/165", "Slowpoke", "Comum", "Normal", 4.00, setCode = "151"),
        SetCardItem("081/165", "Magnemite", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("092/165", "Gastly", "Comum", "Normal", 4.50, setCode = "151"),
        SetCardItem("093/165", "Haunter", "Incomum", "Normal", 6.00, setCode = "151"),
        SetCardItem("094/165", "Gengar", "Rara", "Holo", 38.00, setCode = "151"),
        SetCardItem("095/165", "Onix", "Incomum", "Normal", 5.00, setCode = "151"),
        SetCardItem("096/165", "Drowzee", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("109/165", "Koffing", "Comum", "Normal", 3.00, setCode = "151"),
        SetCardItem("129/165", "Magikarp", "Comum", "Normal", 5.00, setCode = "151"),
        SetCardItem("130/165", "Gyarados", "Rara", "Holo", 32.00, setCode = "151"),
        SetCardItem("131/165", "Lapras", "Rara", "Holo", 16.00, setCode = "151"),
        SetCardItem("132/165", "Ditto", "Rara", "Holo", 14.00, setCode = "151"),
        SetCardItem("133/165", "Eevee", "Comum", "Normal", 8.00, setCode = "151"),
        SetCardItem("134/165", "Vaporeon", "Rara", "Holo", 20.00, setCode = "151"),
        SetCardItem("135/165", "Jolteon", "Rara", "Holo", 18.00, setCode = "151"),
        SetCardItem("136/165", "Flareon", "Rara", "Holo", 18.00, setCode = "151"),
        SetCardItem("143/165", "Snorlax", "Rara", "Holo", 25.00, setCode = "151"),
        SetCardItem("144/165", "Articuno", "Rara", "Holo", 22.00, setCode = "151"),
        SetCardItem("145/165", "Zapdos ex", "Double Rare", "Foil", 70.00, setCode = "151"),
        SetCardItem("146/165", "Moltres", "Rara", "Holo", 24.00, setCode = "151"),
        SetCardItem("147/165", "Dratini", "Comum", "Normal", 4.00, setCode = "151"),
        SetCardItem("148/165", "Dragonair", "Incomum", "Normal", 7.00, setCode = "151"),
        SetCardItem("149/165", "Dragonite", "Rara", "Holo", 28.00, setCode = "151"),
        SetCardItem("150/165", "Mewtwo", "Rara", "Holo", 48.00, setCode = "151"),
        SetCardItem("151/165", "Mew ex", "Double Rare", "Foil", 95.00, setCode = "151"),
        SetCardItem("166/165", "Bulbasaur (Illustration Rare)", "Illustration Rare", "Foil", 140.00, setCode = "151"),
        SetCardItem("168/165", "Charmander (Illustration Rare)", "Illustration Rare", "Foil", 185.00, setCode = "151"),
        SetCardItem("170/165", "Squirtle (Illustration Rare)", "Illustration Rare", "Foil", 160.00, setCode = "151"),
        SetCardItem("173/165", "Pikachu (Illustration Rare)", "Illustration Rare", "Foil", 190.00, setCode = "151"),
        SetCardItem("183/165", "Charizard ex (Ultra Rare Full Art)", "Ultra Rare", "Full Art Foil", 220.00, setCode = "151"),
        SetCardItem("198/165", "Venusaur ex SIR", "Special Art Rare", "Foil", 280.00, setCode = "151"),
        SetCardItem("199/165", "Charizard ex SIR", "Special Art Rare", "Foil", 850.00, setCode = "151"),
        SetCardItem("200/165", "Blastoise ex SIR", "Special Art Rare", "Foil", 320.00, setCode = "151"),
        SetCardItem("201/165", "Alakazam ex SIR", "Special Art Rare", "Foil", 210.00, setCode = "151"),
        SetCardItem("202/165", "Zapdos ex SIR", "Special Art Rare", "Foil", 260.00, setCode = "151"),
        SetCardItem("205/165", "Mew ex Gold Hyper Rare", "Hyper Rare", "Gold Foil", 350.00, setCode = "151")
    )

    // Scarlet & Violet: Twilight Masquerade (TWM - 2024)
    private val twilightMasqueradeItems: List<SetCardItem> = listOf(
        SetCardItem("025/167", "Ogerpon Teal Mask ex", "Double Rare", "Foil", 55.00, setCode = "TWM"),
        SetCardItem("040/167", "Ogerpon Hearthflame Mask ex", "Double Rare", "Foil", 45.00, setCode = "TWM"),
        SetCardItem("064/167", "Ogerpon Wellspring Mask ex", "Double Rare", "Foil", 50.00, setCode = "TWM"),
        SetCardItem("112/167", "Ogerpon Cornerstone Mask ex", "Double Rare", "Foil", 40.00, setCode = "TWM"),
        SetCardItem("106/167", "Greninja ex", "Double Rare", "Foil", 65.00, setCode = "TWM"),
        SetCardItem("141/167", "Bloodmoon Ursaluna ex", "Double Rare", "Foil", 60.00, setCode = "TWM"),
        SetCardItem("186/167", "Carmine (Ultra Rare)", "Ultra Rare", "Full Art Foil", 220.00, setCode = "TWM"),
        SetCardItem("214/167", "Greninja ex (Special Illustration Rare)", "Special Art Rare", "Foil", 1750.00, setCode = "TWM"),
        SetCardItem("217/167", "Carmine SIR", "Special Art Rare", "Foil", 550.00, setCode = "TWM"),
        SetCardItem("218/167", "Perrin SIR", "Special Art Rare", "Foil", 420.00, setCode = "TWM")
    )

    // Scarlet & Violet: Temporal Forces (TEF - 2024)
    private val temporalForcesItems: List<SetCardItem> = listOf(
        SetCardItem("024/162", "Walking Wake ex", "Double Rare", "Foil", 45.00, setCode = "TEF"),
        SetCardItem("077/162", "Raging Bolt ex", "Double Rare", "Foil", 65.00, setCode = "TEF"),
        SetCardItem("110/162", "Iron Crown ex", "Double Rare", "Foil", 55.00, setCode = "TEF"),
        SetCardItem("152/162", "Prime Catcher (ACE SPEC)", "ACE SPEC", "Special Foil", 85.00, setCode = "TEF"),
        SetCardItem("205/162", "Raging Bolt ex SIR", "Special Art Rare", "Foil", 650.00, setCode = "TEF"),
        SetCardItem("206/162", "Iron Crown ex SIR", "Special Art Rare", "Foil", 580.00, setCode = "TEF")
    )

    // Scarlet & Violet: Paldean Fates (PAF - 2024)
    private val paldeanFatesItems: List<SetCardItem> = listOf(
        SetCardItem("054/091", "Charizard ex (Shiny Darkness)", "Double Rare", "Foil", 85.00, setCode = "PAF"),
        SetCardItem("089/091", "Iono (Special Illustration Rare Shiny)", "Special Art Rare", "Foil", 420.00, setCode = "PAF"),
        SetCardItem("234/091", "Charizard ex (Shiny Treasure SIR)", "Special Art Rare", "Textured Rainbow Foil", 920.00, setCode = "PAF"),
        SetCardItem("232/091", "Gardevoir ex Shiny SIR", "Special Art Rare", "Foil", 480.00, setCode = "PAF"),
        SetCardItem("238/091", "Mew ex Shiny SIR", "Special Art Rare", "Foil", 680.00, setCode = "PAF")
    )

    // Scarlet & Violet: Paldea Evolved (PAL - 2023)
    private val paldeaEvolvedItems: List<SetCardItem> = listOf(
        SetCardItem("015/193", "Meowscarada ex", "Double Rare", "Foil", 35.00, setCode = "PAL"),
        SetCardItem("037/193", "Skeledirge ex", "Double Rare", "Foil", 28.00, setCode = "PAL"),
        SetCardItem("052/193", "Quaquaval ex", "Double Rare", "Foil", 22.00, setCode = "PAL"),
        SetCardItem("061/193", "Chien-Pao ex", "Double Rare", "Foil", 55.00, setCode = "PAL"),
        SetCardItem("123/193", "Ting-Lu ex", "Double Rare", "Foil", 30.00, setCode = "PAL"),
        SetCardItem("185/193", "Iono (Full Art)", "Ultra Rare", "Foil", 220.00, setCode = "PAL"),
        SetCardItem("269/193", "Iono SIR", "Special Art Rare", "Foil", 480.00, setCode = "PAL"),
        SetCardItem("270/193", "Magikarp (Illustration Rare)", "Illustration Rare", "Foil", 650.00, setCode = "PAL")
    )

    // Scarlet & Violet Base Set (SVI - 2023)
    private val scarletVioletBaseItems: List<SetCardItem> = listOf(
        SetCardItem("025/198", "Koraidon ex", "Double Rare", "Foil", 35.00, setCode = "SVI"),
        SetCardItem("081/198", "Miraidon ex", "Double Rare", "Foil", 45.00, setCode = "SVI"),
        SetCardItem("086/198", "Gardevoir ex", "Double Rare", "Foil", 40.00, setCode = "SVI"),
        SetCardItem("244/198", "Miriam (Special Illustration Rare)", "Special Art Rare", "Foil", 280.00, setCode = "SVI"),
        SetCardItem("245/198", "Gardevoir ex SIR", "Special Art Rare", "Foil", 340.00, setCode = "SVI"),
        SetCardItem("247/198", "Miraidon ex SIR", "Special Art Rare", "Foil", 290.00, setCode = "SVI")
    )

    // =========================================================================
    // SWORD & SHIELD ERA (2020 - 2023)
    // =========================================================================

    // Crown Zenith (CRZ - 2023)
    private val crownZenithItems: List<SetCardItem> = listOf(
        SetCardItem("GG68/GG70", "Origin Forme Dialga VSTAR (Gold Alt)", "Galarian Gallery Gold", "Gold Foil", 380.00, setCode = "CRZ"),
        SetCardItem("GG69/GG70", "Giratina VSTAR (Galarian Gallery Gold)", "Galarian Gallery Gold", "Gold Foil", 750.00, setCode = "CRZ"),
        SetCardItem("GG70/GG70", "Arceus VSTAR (Galarian Gallery Gold)", "Galarian Gallery Gold", "Gold Foil", 490.00, setCode = "CRZ"),
        SetCardItem("GG44/GG70", "Mewtwo VSTAR (Galarian Gallery)", "Galarian Gallery", "Foil", 360.00, setCode = "CRZ")
    )

    // Evolving Skies (EVS - 2021)
    private val evolvingSkiesItems: List<SetCardItem> = listOf(
        SetCardItem("215/203", "Umbreon VMAX (Alternate Art Secret / Moonbreon)", "Secret Rare Alt", "Textured Rainbow Foil", 5200.00, setCode = "EVS"),
        SetCardItem("218/203", "Rayquaza VMAX (Alternate Art Secret)", "Secret Rare Alt", "Foil", 2100.00, setCode = "EVS"),
        SetCardItem("212/203", "Sylveon VMAX (Alternate Art Secret)", "Secret Rare Alt", "Foil", 1250.00, setCode = "EVS"),
        SetCardItem("205/203", "Leafeon VMAX (Alternate Art Secret)", "Secret Rare Alt", "Foil", 1150.00, setCode = "EVS"),
        SetCardItem("209/203", "Glaceon VMAX (Alternate Art Secret)", "Secret Rare Alt", "Foil", 1100.00, setCode = "EVS")
    )

    // Celebrations 25th (CEL - 2021)
    private val celebrations25Items: List<SetCardItem> = listOf(
        SetCardItem("001/025", "Ho-Oh", "Holo Rare", "25th Stamp Foil", 10.00, setCode = "CEL"),
        SetCardItem("025/025", "Mew (Gold Secret)", "Secret Rare", "Gold Foil", 290.00, setCode = "CEL"),
        SetCardItem("004/025-C", "Charizard (Base Set Reprint)", "Classic Collection", "Holo Confetti", 490.00, setCode = "CEL"),
        SetCardItem("017/025-C", "Umbreon Star (Reprint)", "Classic Collection", "Gold Star Foil", 180.00, setCode = "CEL")
    )

    // =========================================================================
    // SUN & MOON ERA (2017 - 2019)
    // =========================================================================

    // Cosmic Eclipse (CEC - 2019)
    private val cosmicEclipseItems: List<SetCardItem> = listOf(
        SetCardItem("075/236", "Arceus & Dialga & Palkia GX", "Ultra Rare", "Foil", 65.00, setCode = "CEC"),
        SetCardItem("221/236", "Arceus & Dialga & Palkia GX (Alt Art)", "Secret Rare Alt", "Tag Team Foil", 850.00, setCode = "CEC"),
        SetCardItem("241/236", "Pikachu (Character Secret Rare / Red)", "Secret Rare", "Character Art Foil", 320.00, setCode = "CEC")
    )

    // Team Up (TEU - 2019)
    private val teamUpItems: List<SetCardItem> = listOf(
        SetCardItem("033/181", "Pikachu & Zekrom GX", "Ultra Rare", "Tag Team Foil", 75.00, setCode = "TEU"),
        SetCardItem("170/181", "Latios & Latias GX (Heart Alternate Art)", "Secret Rare Alt", "Tag Team Foil", 4900.00, setCode = "TEU"),
        SetCardItem("184/181", "Pikachu & Zekrom GX (Rainbow Secret)", "Hyper Rare", "Rainbow Foil", 380.00, setCode = "TEU")
    )

    // =========================================================================
    // VINTAGE & CLASSIC POKÉMON (1999 - 2004)
    // =========================================================================

    // Base Set 1999 (BS)
    private val baseSet1999Items: List<SetCardItem> = listOf(
        SetCardItem("001/102", "Alakazam (Holo)", "Holo Rare", "Holo 1st Ed/Shadowless", 1200.00, setCode = "BS"),
        SetCardItem("002/102", "Blastoise (Holo)", "Holo Rare", "Holo", 2200.00, setCode = "BS"),
        SetCardItem("003/102", "Chansey (Holo)", "Holo Rare", "Holo", 850.00, setCode = "BS"),
        SetCardItem("004/102", "Charizard (Holo)", "Holo Rare", "Holo 1st Ed/Unlimited", 8500.00, setCode = "BS"),
        SetCardItem("005/102", "Clefairy (Holo)", "Holo Rare", "Holo", 450.00, setCode = "BS"),
        SetCardItem("006/102", "Gyarados (Holo)", "Holo Rare", "Holo", 750.00, setCode = "BS"),
        SetCardItem("007/102", "Hitmonchan (Holo)", "Holo Rare", "Holo", 420.00, setCode = "BS"),
        SetCardItem("008/102", "Machamp (Holo 1st Edition)", "Holo Rare", "Holo 1st Edition", 550.00, setCode = "BS"),
        SetCardItem("009/102", "Magneton (Holo)", "Holo Rare", "Holo", 480.00, setCode = "BS"),
        SetCardItem("010/102", "Mewtwo (Holo)", "Holo Rare", "Holo", 1100.00, setCode = "BS"),
        SetCardItem("011/102", "Nidoking (Holo)", "Holo Rare", "Holo", 620.00, setCode = "BS"),
        SetCardItem("012/102", "Ninetales (Holo)", "Holo Rare", "Holo", 680.00, setCode = "BS"),
        SetCardItem("013/102", "Poliwrath (Holo)", "Holo Rare", "Holo", 500.00, setCode = "BS"),
        SetCardItem("014/102", "Raichu (Holo)", "Holo Rare", "Holo", 780.00, setCode = "BS"),
        SetCardItem("015/102", "Venusaur (Holo)", "Holo Rare", "Holo", 2400.00, setCode = "BS"),
        SetCardItem("016/102", "Zapdos (Holo)", "Holo Rare", "Holo", 820.00, setCode = "BS"),
        SetCardItem("023/102", "Arcanine", "Incomum", "Normal", 35.00, setCode = "BS"),
        SetCardItem("024/102", "Charmeleon", "Incomum", "Normal", 45.00, setCode = "BS"),
        SetCardItem("029/102", "Haunter", "Incomum", "Normal", 30.00, setCode = "BS"),
        SetCardItem("030/102", "Ivysaur", "Incomum", "Normal", 38.00, setCode = "BS"),
        SetCardItem("032/102", "Kadabra", "Incomum", "Normal", 28.00, setCode = "BS"),
        SetCardItem("034/102", "Machoke", "Incomum", "Normal", 25.00, setCode = "BS"),
        SetCardItem("042/102", "Wartortle", "Incomum", "Normal", 40.00, setCode = "BS"),
        SetCardItem("044/102", "Bulbasaur", "Comum", "Normal 1st/Unlimited", 35.00, setCode = "BS"),
        SetCardItem("045/102", "Caterpie", "Comum", "Normal", 15.00, setCode = "BS"),
        SetCardItem("046/102", "Charmander", "Comum", "Normal 1st/Unlimited", 45.00, setCode = "BS"),
        SetCardItem("047/102", "Diglett", "Comum", "Normal", 12.00, setCode = "BS"),
        SetCardItem("049/102", "Drowzee", "Comum", "Normal", 12.00, setCode = "BS"),
        SetCardItem("050/102", "Gastly", "Comum", "Normal", 18.00, setCode = "BS"),
        SetCardItem("052/102", "Machop", "Comum", "Normal", 12.00, setCode = "BS"),
        SetCardItem("053/102", "Magnemite", "Comum", "Normal", 14.00, setCode = "BS"),
        SetCardItem("058/102", "Pikachu (Red Cheeks Shadowless)", "Comum", "Normal Shadowless", 380.00, setCode = "BS"),
        SetCardItem("058/102", "Pikachu (Yellow Cheeks)", "Comum", "Normal", 90.00, setCode = "BS"),
        SetCardItem("059/102", "Poliwag", "Comum", "Normal", 12.00, setCode = "BS"),
        SetCardItem("060/102", "Ponyta", "Comum", "Normal", 14.00, setCode = "BS"),
        SetCardItem("061/102", "Rattata", "Comum", "Normal", 10.00, setCode = "BS"),
        SetCardItem("063/102", "Squirtle", "Comum", "Normal 1st/Unlimited", 40.00, setCode = "BS"),
        SetCardItem("064/102", "Starmie", "Comum", "Normal", 12.00, setCode = "BS"),
        SetCardItem("065/102", "Staryu", "Comum", "Normal", 10.00, setCode = "BS"),
        SetCardItem("066/102", "Tangela", "Comum", "Normal", 12.00, setCode = "BS"),
        SetCardItem("088/102", "Professor Oak", "Incomum", "Trainer", 25.00, setCode = "BS"),
        SetCardItem("091/102", "Bill", "Comum", "Trainer", 15.00, setCode = "BS"),
        SetCardItem("092/102", "Energy Removal", "Comum", "Trainer", 10.00, setCode = "BS"),
        SetCardItem("093/102", "Gust of Wind", "Comum", "Trainer", 14.00, setCode = "BS"),
        SetCardItem("094/102", "Potion", "Comum", "Trainer", 8.00, setCode = "BS"),
        SetCardItem("095/102", "Switch", "Comum", "Trainer", 10.00, setCode = "BS")
    )

    // Team Rocket (TR - 2000)
    private val teamRocketItems: List<SetCardItem> = listOf(
        SetCardItem("001/082", "Dark Alakazam (Holo 1st Ed)", "Holo Rare", "1st Edition Foil", 450.00, setCode = "TR"),
        SetCardItem("002/082", "Dark Blastoise (Holo 1st Ed)", "Holo Rare", "1st Edition Foil", 950.00, setCode = "TR"),
        SetCardItem("003/082", "Dark Charizard (Holo 1st Ed)", "Holo Rare", "1st Edition Foil", 2800.00, setCode = "TR"),
        SetCardItem("004/082", "Dark Dragonite (Holo 1st Ed)", "Holo Rare", "1st Edition Foil", 1100.00, setCode = "TR"),
        SetCardItem("008/082", "Dark Gyarados (Holo 1st Ed)", "Holo Rare", "1st Edition Foil", 420.00, setCode = "TR"),
        SetCardItem("083/082", "Dark Raichu (Secret Rare 1st Ed)", "Secret Rare", "1st Edition Foil", 1650.00, setCode = "TR")
    )

    // Neo Destiny (N4 - 2002)
    private val neoDestinyItems: List<SetCardItem> = listOf(
        SetCardItem("107/105", "Shining Charizard (1st Edition)", "Secret Shining", "Reflective 1st Ed Foil", 9500.00, setCode = "N4"),
        SetCardItem("108/105", "Shining Mewtwo (1st Edition)", "Secret Shining", "Reflective 1st Ed Foil", 3200.00, setCode = "N4"),
        SetCardItem("109/105", "Shining Tyranitar (1st Edition)", "Secret Shining", "Reflective 1st Ed Foil", 2800.00, setCode = "N4"),
        SetCardItem("113/105", "Shining Celebi (1st Edition)", "Secret Shining", "Reflective 1st Ed Foil", 2100.00, setCode = "N4")
    )

    // =========================================================================
    // MAGIC: THE GATHERING (1993 - 2026)
    // =========================================================================

    // Bloomburrow (BLB - 2024)
    private val mtgBloomburrowItems: List<SetCardItem> = listOf(
        SetCardItem("001/281", "Bello, Bard of the Brambles", "Mítica", "Anime Borderless Foil", 220.00, patternVariant = "Anime Art", setCode = "BLB"),
        SetCardItem("018/281", "Lumra, Bellow of the Woods", "Mítica", "Raised Foil Woodland Showcase", 480.00, patternVariant = "Raised Foil", setCode = "BLB"),
        SetCardItem("045/281", "Bria, Riptide Rogue", "Mítica", "Borderless Anime Foil", 310.00, patternVariant = "Anime Art", setCode = "BLB"),
        SetCardItem("065/281", "Maha, Its Feathers Night", "Mítica", "Showcase Woodland", 185.00, patternVariant = "Showcase Frame", setCode = "BLB"),
        SetCardItem("098/281", "Dragonhawk, Fate's Tempest", "Mítica", "Borderless Foil", 140.00, patternVariant = "Borderless", setCode = "BLB"),
        SetCardItem("261/281", "Three Tree City", "Rara", "Seasonal Autumn Foil Borderless", 240.00, patternVariant = "Borderless Land", setCode = "BLB")
    )

    // Duskmourn: House of Horror (DSK - 2024)
    private val mtgDuskmournItems: List<SetCardItem> = listOf(
        SetCardItem("082/276", "Valgavoth, Terror Eater", "Mítica", "Fracture Foil Nightmare Frame", 590.00, patternVariant = "Fracture Foil", setCode = "DSK"),
        SetCardItem("198/276", "Overlord of the Hauntwoods", "Mítica", "Paranormal Showcase Foil", 260.00, patternVariant = "Showcase Frame", setCode = "DSK"),
        SetCardItem("010/276", "Enduring Innocence", "Rara", "Nightmare Borderless", 110.00, patternVariant = "Borderless", setCode = "DSK"),
        SetCardItem("143/276", "Leyline of Resonance", "Rara", "Extended Art Foil", 95.00, patternVariant = "Extended Art", setCode = "DSK"),
        SetCardItem("260/276", "Verge Lands Cycle (Gloomlake Verge)", "Rara", "Borderless Foil", 85.00, patternVariant = "Borderless", setCode = "DSK")
    )

    // Foundations (FDN - 2024 / 2025)
    private val mtgFoundationsItems: List<SetCardItem> = listOf(
        SetCardItem("001/271", "Sire of Seven Deaths", "Mítica", "Borderless Foil", 195.00, patternVariant = "Borderless", setCode = "FDN"),
        SetCardItem("015/271", "Day of Judgment", "Rara", "Retro Frame Foil", 75.00, patternVariant = "Retro Frame", setCode = "FDN"),
        SetCardItem("088/271", "Llanowar Elves (Classic Art Special)", "Comum", "Retro Foil Borderless", 45.00, patternVariant = "Retro Frame", setCode = "FDN"),
        SetCardItem("145/271", "Twinflame Tyrant", "Mítica", "Special Guests Borderless", 280.00, patternVariant = "Special Guests", setCode = "FDN")
    )

    // Modern Horizons 3 (MH3 - 2024)
    private val mh3Items: List<SetCardItem> = listOf(
        SetCardItem("015/303", "Ulamog, the Defiler", "Mítica", "Borderless Textured Foil", 520.00, patternVariant = "Textured Foil", setCode = "MH3"),
        SetCardItem("004/303", "Ajani, Nacatl Pariah // Ajani Avenger", "Mítica", "Foil Double-Faced", 185.00, patternVariant = "Double-Faced", setCode = "MH3"),
        SetCardItem("044/303", "Flare of Denial", "Rara", "Retro Frame Foil", 110.00, patternVariant = "Retro Frame", setCode = "MH3"),
        SetCardItem("045/303", "Tamiyo, Inquisitive Student", "Mítica", "Borderless Double-Faced Foil", 240.00, patternVariant = "Borderless", setCode = "MH3"),
        SetCardItem("080/303", "Sorin of House Markov", "Mítica", "Foil Double-Faced Showcase", 195.00, patternVariant = "Showcase Frame", setCode = "MH3"),
        SetCardItem("103/303", "Nethergoyf", "Rara", "Retro Frame", 85.00, patternVariant = "Retro Frame", setCode = "MH3"),
        SetCardItem("247/303", "Bloodstained Mire", "Rara", "Retro Frame Fetchland Foil", 160.00, patternVariant = "Retro Fetchland", setCode = "MH3"),
        SetCardItem("249/303", "Polluted Delta", "Rara", "Retro Frame Fetchland Foil", 195.00, patternVariant = "Retro Fetchland", setCode = "MH3"),
        SetCardItem("250/303", "Wooded Foothills", "Rara", "Retro Frame Fetchland Foil", 145.00, patternVariant = "Retro Fetchland", setCode = "MH3"),
        SetCardItem("251/303", "Flooded Strand", "Rara", "Retro Frame Fetchland Foil", 175.00, patternVariant = "Retro Fetchland", setCode = "MH3")
    )

    // Commander Masters (CMM - 2023)
    private val mtgCommanderMastersItems: List<SetCardItem> = listOf(
        SetCardItem("688/436", "Jeweled Lotus", "Mítica", "Borderless Frame Break Foil", 850.00, patternVariant = "Frame Break Foil", setCode = "CMM"),
        SetCardItem("690/436", "The Ur-Dragon", "Mítica", "Profile Showcase Foil", 420.00, patternVariant = "Profile Showcase", setCode = "CMM"),
        SetCardItem("695/436", "Demonic Tutor", "Mítica", "Textured Foil Borderless", 680.00, patternVariant = "Textured Foil", setCode = "CMM"),
        SetCardItem("101/436", "Fierce Guardianship", "Rara", "Borderless Foil", 230.00, patternVariant = "Borderless", setCode = "CMM"),
        SetCardItem("145/436", "Deflecting Swat", "Rara", "Borderless Foil", 210.00, patternVariant = "Borderless", setCode = "CMM"),
        SetCardItem("288/436", "Cyclonic Rift", "Rara", "Retro Frame Foil", 180.00, patternVariant = "Retro Frame", setCode = "CMM")
    )

    // The Lord of the Rings: Tales of Middle-earth (LTR - 2023)
    private val lotrItems: List<SetCardItem> = listOf(
        SetCardItem("001/001", "The One Ring (Serialized 001/001 Post Malone)", "Única 1/1", "Serialized Pure Gold Black Speech", 12000000.00, patternVariant = "Serialized 1/1", setCode = "LTR"),
        SetCardItem("213/281", "The One Ring", "Mítica", "Extended Art Foil", 520.00, patternVariant = "Extended Art", setCode = "LTR"),
        SetCardItem("405/281", "The One Ring (Borderless Poster Foil)", "Mítica", "Psychedelic Poster Foil", 1150.00, patternVariant = "Poster Frame", setCode = "LTR"),
        SetCardItem("103/281", "Orcish Bowmasters", "Rara", "Borderless Foil", 340.00, patternVariant = "Borderless", setCode = "LTR"),
        SetCardItem("331/281", "Nazgûl (Art 1 a 9 Complete Collection)", "Incomum", "Full Foil Set", 480.00, patternVariant = "Showcase Frame", setCode = "LTR"),
        SetCardItem("412/281", "Sol Ring (Elven Serialized /300)", "Mítica", "Serialized Double Rainbow Foil", 6500.00, patternVariant = "Serialized", setCode = "LTR")
    )

    // Phyrexia: All Will Be One (ONE - 2023)
    private val mtgPhyrexiaItems: List<SetCardItem> = listOf(
        SetCardItem("325/271", "Elesh Norn, Mother of Machines (Junji Ito Manga)", "Mítica", "Step-and-Compleat Foil Manga", 1150.00, patternVariant = "Step-and-Compleat Foil", setCode = "ONE"),
        SetCardItem("333/271", "Atraxa, Grand Unifier", "Mítica", "Step-and-Compleat Foil Borderless", 480.00, patternVariant = "Step-and-Compleat Foil", setCode = "ONE"),
        SetCardItem("340/271", "Sheoldred, the Apocalypse", "Mítica", "Oil Slick Raised Foil", 690.00, patternVariant = "Oil Slick Raised Foil", setCode = "ONE"),
        SetCardItem("018/271", "Mondrak, Glory Dominus", "Mítica", "Phyrexian Language Foil", 240.00, patternVariant = "Phyrexian Text", setCode = "ONE"),
        SetCardItem("315/271", "Sword of Forge and Frontier", "Mítica", "Oil Slick Foil", 195.00, patternVariant = "Oil Slick Raised Foil", setCode = "ONE")
    )

    // Kamigawa: Neon Dynasty (NEO - 2022)
    private val mtgKamigawaItems: List<SetCardItem> = listOf(
        SetCardItem("428/302", "Hidetsugu, Devouring Chaos (Neon Ink Red /001-100)", "Mítica", "Neon Ink Red Foil", 6800.00, patternVariant = "Neon Ink Red", setCode = "NEO"),
        SetCardItem("429/302", "Hidetsugu, Devouring Chaos (Neon Ink Blue)", "Mítica", "Neon Ink Blue Foil", 2400.00, patternVariant = "Neon Ink Blue", setCode = "NEO"),
        SetCardItem("430/302", "Hidetsugu, Devouring Chaos (Neon Ink Green)", "Mítica", "Neon Ink Green Foil", 1300.00, patternVariant = "Neon Ink Green", setCode = "NEO"),
        SetCardItem("042/302", "The Wandering Emperor", "Mítica", "Showcase Anime Foil", 950.00, patternVariant = "Showcase Anime", setCode = "NEO"),
        SetCardItem("266/302", "Boseiju, Who Endures", "Rara", "Borderless Foil", 320.00, patternVariant = "Borderless", setCode = "NEO"),
        SetCardItem("272/302", "Otawara, Soaring City", "Rara", "Borderless Foil", 160.00, patternVariant = "Borderless", setCode = "NEO"),
        SetCardItem("141/302", "Fable of the Mirror-Breaker", "Rara", "Showcase Frame Foil", 120.00, patternVariant = "Showcase Frame", setCode = "NEO")
    )

    // Double Masters 2022 (2X2 - 2022)
    private val mtgDoubleMastersItems: List<SetCardItem> = listOf(
        SetCardItem("350/331", "Imperial Seal", "Mítica", "Borderless Foil", 650.00, patternVariant = "Borderless", setCode = "2X2"),
        SetCardItem("399/331", "Mana Vault", "Mítica", "Textured Foil Borderless", 1250.00, patternVariant = "Textured Foil", setCode = "2X2"),
        SetCardItem("337/331", "Force of Will", "Mítica", "Borderless Textured Foil", 980.00, patternVariant = "Textured Foil", setCode = "2X2"),
        SetCardItem("342/331", "Dockside Extortionist", "Mítica", "Borderless Foil", 420.00, patternVariant = "Borderless", setCode = "2X2"),
        SetCardItem("383/331", "Cavern of Souls", "Mítica", "Borderless Foil", 320.00, patternVariant = "Borderless", setCode = "2X2"),
        SetCardItem("381/331", "City of Brass", "Rara", "Borderless Foil", 160.00, patternVariant = "Borderless", setCode = "2X2")
    )

    // Modern Horizons 2 (MH2 - 2021)
    private val mtgMh2Items: List<SetCardItem> = listOf(
        SetCardItem("138/303", "Ragavan, Nimble Pilferer", "Mítica", "Borderless Foil", 390.00, patternVariant = "Borderless", setCode = "MH2"),
        SetCardItem("259/303", "Urza's Saga", "Rara", "Sketch Retro Foil", 280.00, patternVariant = "Retro Frame", setCode = "MH2"),
        SetCardItem("031/303", "Solitude", "Mítica", "Borderless Foil", 210.00, patternVariant = "Borderless", setCode = "MH2"),
        SetCardItem("087/303", "Grief", "Mítica", "Borderless Foil", 140.00, patternVariant = "Borderless", setCode = "MH2"),
        SetCardItem("052/303", "Murktide Regent", "Mítica", "Retro Frame Foil", 160.00, patternVariant = "Retro Frame", setCode = "MH2"),
        SetCardItem("253/303", "Misty Rainforest (Retro Foil)", "Rara", "Retro Frame Foil", 185.00, patternVariant = "Retro Fetchland", setCode = "MH2"),
        SetCardItem("254/303", "Scalding Tarn (Retro Foil)", "Rara", "Retro Frame Foil", 195.00, patternVariant = "Retro Fetchland", setCode = "MH2")
    )

    // Dominaria (DOM - 2018)
    private val mtgDominariaItems: List<SetCardItem> = listOf(
        SetCardItem("207/269", "Teferi, Hero of Dominaria", "Mítica", "Mythic Foil", 190.00, patternVariant = "Foil", setCode = "DOM"),
        SetCardItem("224/269", "Mox Amber", "Mítica", "Mythic Foil", 230.00, patternVariant = "Foil", setCode = "DOM"),
        SetCardItem("001/269", "Karn, Scion of Urza", "Mítica", "Mythic Foil", 120.00, patternVariant = "Foil", setCode = "DOM"),
        SetCardItem("026/269", "Lyra Dawnbringer", "Mítica", "Mythic Foil", 85.00, patternVariant = "Foil", setCode = "DOM")
    )

    // Innistrad (ISD - 2011)
    private val mtgInnistradItems: List<SetCardItem> = listOf(
        SetCardItem("105/264", "Liliana of the Veil", "Mítica", "Original Innistrad Foil", 420.00, patternVariant = "Original Foil", setCode = "ISD"),
        SetCardItem("078/264", "Snapcaster Mage", "Rara", "Original Innistrad Foil", 260.00, patternVariant = "Original Foil", setCode = "ISD"),
        SetCardItem("213/264", "Geist of Saint Traft", "Mítica", "Original Foil", 95.00, patternVariant = "Original Foil", setCode = "ISD"),
        SetCardItem("051/264", "Delver of Secrets // Insectile Aberration", "Comum", "Original Foil", 35.00, patternVariant = "Double-Faced", setCode = "ISD")
    )

    // Zendikar & Worldwake (ZEN / WWK - 2009-2010)
    private val mtgZendikarItems: List<SetCardItem> = listOf(
        SetCardItem("WWK-031", "Jace, the Mind Sculptor", "Mítica", "Worldwake Original Foil", 780.00, patternVariant = "Original Foil", setCode = "WWK"),
        SetCardItem("ZEN-223", "Scalding Tarn", "Rara", "Zendikar Original Foil", 380.00, patternVariant = "Original Fetchland", setCode = "ZEN"),
        SetCardItem("ZEN-220", "Misty Rainforest", "Rara", "Zendikar Original Foil", 350.00, patternVariant = "Original Fetchland", setCode = "ZEN"),
        SetCardItem("ZEN-229", "Verdant Catacombs", "Rara", "Zendikar Original Foil", 320.00, patternVariant = "Original Fetchland", setCode = "ZEN"),
        SetCardItem("ZEN-217", "Arid Mesa", "Rara", "Zendikar Original Foil", 280.00, patternVariant = "Original Fetchland", setCode = "ZEN"),
        SetCardItem("ZEN-218", "Marsh Flats", "Rara", "Zendikar Original Foil", 260.00, patternVariant = "Original Fetchland", setCode = "ZEN")
    )

    // Ravnica: City of Guilds (RAV - 2005)
    private val mtgRavnicaItems: List<SetCardItem> = listOf(
        SetCardItem("RAV-081", "Dark Confidant (Bob Maher)", "Rara", "Original Foil", 480.00, patternVariant = "Original Foil", setCode = "RAV"),
        SetCardItem("RAV-158", "Doubling Season", "Rara", "Original Foil", 390.00, patternVariant = "Original Foil", setCode = "RAV"),
        SetCardItem("RAV-286", "Watery Grave", "Rara", "Original Shockland Foil", 320.00, patternVariant = "Original Shockland", setCode = "RAV"),
        SetCardItem("RAV-281", "Temple Garden", "Rara", "Original Shockland Foil", 280.00, patternVariant = "Original Shockland", setCode = "RAV"),
        SetCardItem("RAV-279", "Sacred Foundry", "Rara", "Original Shockland Foil", 290.00, patternVariant = "Original Shockland", setCode = "RAV"),
        SetCardItem("RAV-278", "Overgrown Tomb", "Rara", "Original Shockland Foil", 310.00, patternVariant = "Original Shockland", setCode = "RAV")
    )

    // Urza's Saga (USG - 1998)
    private val mtgUrzaSagaItems: List<SetCardItem> = listOf(
        SetCardItem("USG-321", "Gaea's Cradle", "Rara", "Reserved List Mint", 5800.00, patternVariant = "Reserved List", setCode = "USG"),
        SetCardItem("USG-330", "Tolarian Academy", "Rara", "Reserved List Mint", 650.00, patternVariant = "Reserved List", setCode = "USG"),
        SetCardItem("USG-171", "Yawgmoth's Will", "Rara", "Reserved List Mint", 850.00, patternVariant = "Reserved List", setCode = "USG"),
        SetCardItem("USG-103", "Time Spiral", "Rara", "Reserved List Mint", 620.00, patternVariant = "Reserved List", setCode = "USG"),
        SetCardItem("USG-218", "Sneak Attack", "Rara", "Original Mint", 190.00, patternVariant = "Original", setCode = "USG"),
        SetCardItem("USG-299", "Phyrexian Tower", "Rara", "Original Mint", 160.00, patternVariant = "Original", setCode = "USG")
    )

    // Arabian Nights & Legends (ARN / LEG - 1993-1994)
    private val mtgArabianLegendsItems: List<SetCardItem> = listOf(
        SetCardItem("ARN-084", "Bazaar of Baghdad", "Incomum Arabian", "Arabian Nights Mint", 14500.00, patternVariant = "Reserved List", setCode = "ARN"),
        SetCardItem("ARN-086", "Library of Alexandria", "Incomum Arabian", "Arabian Nights Mint", 9800.00, patternVariant = "Reserved List", setCode = "ARN"),
        SetCardItem("ARN-034", "Juzám Djinn", "Rara Arabian", "Arabian Nights Mint", 8200.00, patternVariant = "Reserved List", setCode = "ARN"),
        SetCardItem("LEG-307", "The Tabernacle at Pendrell Vale", "Rara Legends", "Legends Mint", 18500.00, patternVariant = "Reserved List", setCode = "LEG"),
        SetCardItem("LEG-097", "Chains of Mephistopheles", "Rara Legends", "Legends Mint", 4200.00, patternVariant = "Reserved List", setCode = "LEG"),
        SetCardItem("LEG-112", "Nether Void", "Rara Legends", "Legends Mint", 3800.00, patternVariant = "Reserved List", setCode = "LEG"),
        SetCardItem("LEG-027", "Moat", "Rara Legends", "Legends Mint", 3900.00, patternVariant = "Reserved List", setCode = "LEG")
    )

    // Alpha & Beta (LEA / LEB - 1993)
    private val mtgAlphaItems: List<SetCardItem> = listOf(
        SetCardItem("LEA-232", "Black Lotus", "Mítica Alpha", "Alpha Mint Power 9", 350000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-266", "Mox Sapphire", "Rara Alpha", "Alpha Mint Power 9", 75000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-264", "Mox Jet", "Rara Alpha", "Alpha Mint Power 9", 68000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-265", "Mox Ruby", "Rara Alpha", "Alpha Mint Power 9", 65000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-263", "Mox Emerald", "Rara Alpha", "Alpha Mint Power 9", 62000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-267", "Mox Pearl", "Rara Alpha", "Alpha Mint Power 9", 58000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-088", "Time Walk", "Rara Alpha", "Alpha Mint Power 9", 90000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-047", "Ancestral Recall", "Rara Alpha", "Alpha Mint Power 9", 85000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-089", "Timetwister", "Rara Alpha", "Alpha Mint Power 9", 45000.00, patternVariant = "Power Nine", setCode = "LEA"),
        SetCardItem("LEA-287", "Underground Sea", "Rara Alpha", "Alpha Dual Land Mint", 48000.00, patternVariant = "Alpha Dual Land", setCode = "LEA"),
        SetCardItem("LEB-290", "Volcanic Island", "Rara Beta", "Beta Dual Land Mint", 42000.00, patternVariant = "Beta Dual Land", setCode = "LEB")
    )

    // =========================================================================
    // YU-GI-OH! (2002 - 2026)
    // =========================================================================

    // Legend of Blue Eyes (LOB - 2002)
    private val yugiohLobItems: List<SetCardItem> = listOf(
        SetCardItem("LOB-001", "Blue-Eyes White Dragon", "Ultra Rare", "1st Edition Foil", 4500.00, patternVariant = "1st Edition Vintage", setCode = "LOB"),
        SetCardItem("LOB-005", "Dark Magician", "Ultra Rare", "1st Edition Foil", 2800.00, patternVariant = "1st Edition Vintage", setCode = "LOB"),
        SetCardItem("LOB-070", "Red-Eyes B. Dragon", "Ultra Rare", "1st Edition Foil", 3200.00, patternVariant = "1st Edition Vintage", setCode = "LOB"),
        SetCardItem("LOB-124", "Exodia the Forbidden One", "Ultra Rare", "1st Edition Foil", 2600.00, patternVariant = "1st Edition Vintage", setCode = "LOB"),
        SetCardItem("LOB-120", "Right Leg of the Forbidden One", "Ultra Rare", "1st Edition Foil", 850.00, patternVariant = "1st Edition Vintage", setCode = "LOB"),
        SetCardItem("LOB-121", "Left Leg of the Forbidden One", "Ultra Rare", "1st Edition Foil", 850.00, patternVariant = "1st Edition Vintage", setCode = "LOB")
    )

    // Metal Raiders & Invasion of Chaos (MRD / IOC - 2002-2004)
    private val yugiohMrdIocItems: List<SetCardItem> = listOf(
        SetCardItem("MRD-138", "Mirror Force", "Secret Rare", "1st Edition Secret Foil", 1400.00, patternVariant = "1st Edition Secret", setCode = "MRD"),
        SetCardItem("MRD-127", "Solemn Judgment", "Ultra Rare", "1st Edition Foil", 950.00, patternVariant = "1st Edition Vintage", setCode = "MRD"),
        SetCardItem("MRD-065", "Time Wizard", "Ultra Rare", "1st Edition Foil", 720.00, patternVariant = "1st Edition Vintage", setCode = "MRD"),
        SetCardItem("IOC-000", "Chaos Emperor Dragon - Envoy of the End", "Secret Rare", "1st Edition Secret Foil", 2200.00, patternVariant = "1st Edition Secret", setCode = "IOC"),
        SetCardItem("IOC-025", "Black Luster Soldier - Envoy of the Beginning", "Ultra Rare", "1st Edition Ultra Foil", 1850.00, patternVariant = "1st Edition Vintage", setCode = "IOC"),
        SetCardItem("IOC-065", "Dark Magician of Chaos", "Ultra Rare", "1st Edition Ultra Foil", 1200.00, patternVariant = "1st Edition Vintage", setCode = "IOC")
    )

    // 25th Anniversary Rarity Collection I & II (RA01 / RA02 - 2023-2024)
    private val yugiohRarity25Items: List<SetCardItem> = listOf(
        SetCardItem("RA01-EN001", "Blue-Eyes White Dragon (Quarter Century)", "Quarter Century Secret", "QCR Holographic Stamp", 390.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA01"),
        SetCardItem("RA01-EN005", "Ash Blossom & Joyous Spring (QCR)", "Quarter Century Secret", "QCR Holographic Stamp", 320.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA01"),
        SetCardItem("RA01-EN030", "Baronne de Fleur (QCR)", "Quarter Century Secret", "QCR Holographic Stamp", 240.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA01"),
        SetCardItem("RA01-EN065", "Nibiru, the Primal Being (QCR)", "Quarter Century Secret", "QCR Holographic Stamp", 195.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA01"),
        SetCardItem("RA01-EN078", "Infinite Impermanence (QCR)", "Quarter Century Secret", "QCR Holographic Stamp", 260.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA01"),
        SetCardItem("RA02-EN010", "Accesscode Talker (QCR)", "Quarter Century Secret", "QCR Holographic Stamp", 290.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA02"),
        SetCardItem("RA02-EN025", "Apollousa, Bow of the Goddess (QCR Alt Art)", "Quarter Century Secret", "QCR Holographic Stamp", 350.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA02"),
        SetCardItem("RA02-EN040", "I:P Masquerena (QCR Alt Art)", "Quarter Century Secret", "QCR Holographic Stamp", 380.00, patternVariant = "Quarter Century Secret Rare", setCode = "RA02")
    )

    // =========================================================================
    // ONE PIECE CARD GAME (2022 - 2026)
    // =========================================================================

    // OP-01: Romance Dawn (OP-01 - 2022)
    private val onePieceOp01Items: List<SetCardItem> = listOf(
        SetCardItem("OP01-120", "Shanks (Manga Parallel SEC)", "Manga Rare", "Manga Holographic Foil", 6500.00, patternVariant = "Manga Rare", setCode = "OP-01"),
        SetCardItem("OP01-001", "Roronoa Zoro (Leader Alt Art)", "Leader Parallel", "Foil Textured", 480.00, patternVariant = "Leader Parallel", setCode = "OP-01"),
        SetCardItem("OP01-016", "Nami (Parallel Rare)", "Super Rare Parallel", "Foil Textured", 850.00, patternVariant = "Alt Art Parallel", setCode = "OP-01"),
        SetCardItem("OP01-047", "Trafalgar Law (Leader Alt Art)", "Leader Parallel", "Foil Textured", 380.00, patternVariant = "Leader Parallel", setCode = "OP-01")
    )

    // OP-02 & OP-03: Paramount War & Pillars of Strength (OP-02 / OP-03 - 2023)
    private val onePieceOp0203Items: List<SetCardItem> = listOf(
        SetCardItem("OP02-013", "Portgas.D.Ace (Manga Parallel SEC)", "Manga Rare", "Manga Holographic Foil", 7200.00, patternVariant = "Manga Rare", setCode = "OP-02"),
        SetCardItem("OP02-001", "Edward.Newgate (Whitebeard Leader Parallel)", "Leader Parallel", "Foil Textured", 340.00, patternVariant = "Leader Parallel", setCode = "OP-02"),
        SetCardItem("OP03-122", "Sogeking (Manga Parallel SEC)", "Manga Rare", "Manga Holographic Foil", 4900.00, patternVariant = "Manga Rare", setCode = "OP-03"),
        SetCardItem("OP03-123", "Charlotte Katakuri (Secret Alt Art)", "Secret Rare Parallel", "Foil Textured", 390.00, patternVariant = "Alt Art Parallel", setCode = "OP-03")
    )

    // OP-05: Awakening of the New Era (OP-05 - 2023)
    private val onePieceOp05Items: List<SetCardItem> = listOf(
        SetCardItem("OP05-119", "Monkey.D.Luffy (Gear 5 Manga Rare)", "Manga Rare", "Manga Holographic Texture", 18500.00, patternVariant = "Manga Rare", setCode = "OP-05"),
        SetCardItem("OP05-060-S", "Monkey.D.Luffy (Eiichiro Oda Signed Gold Stamp)", "Special Rare", "Gold Stamped Signature", 9800.00, patternVariant = "Oda Signed Gold Stamp", setCode = "OP-05"),
        SetCardItem("OP05-060", "Monkey.D.Luffy (Gear 5 Secret Rare)", "Secret Rare", "Foil", 280.00, patternVariant = "Secret Rare", setCode = "OP-05"),
        SetCardItem("OP05-069", "Trafalgar Law (Leader Alt Art)", "Leader Parallel", "Foil Textured", 450.00, patternVariant = "Leader Parallel", setCode = "OP-05"),
        SetCardItem("OP05-100", "Enel (Leader Parallel)", "Leader Parallel", "Foil Textured", 520.00, patternVariant = "Leader Parallel", setCode = "OP-05")
    )

    // OP-06 / OP-07 / OP-08 & PRB-01 (2024 - 2025)
    private val onePieceModernItems: List<SetCardItem> = listOf(
        SetCardItem("OP06-118", "Roronoa Zoro (Manga Parallel SEC)", "Manga Rare", "Manga Holographic Texture", 8500.00, patternVariant = "Manga Rare", setCode = "OP-06"),
        SetCardItem("OP06-022", "Yamato (Leader Parallel)", "Leader Parallel", "Foil Textured", 420.00, patternVariant = "Leader Parallel", setCode = "OP-06"),
        SetCardItem("OP07-119", "Boa Hancock (Manga Parallel SEC)", "Manga Rare", "Manga Holographic Texture", 9200.00, patternVariant = "Manga Rare", setCode = "OP-07"),
        SetCardItem("OP08-118", "Silvers Rayleigh (Manga Parallel SEC)", "Manga Rare", "Manga Holographic Texture", 6800.00, patternVariant = "Manga Rare", setCode = "OP-08"),
        SetCardItem("PRB01-001", "Don!! Card (Gold Super Parallel)", "Special Super Parallel", "Pure Gold Foil Finish", 1850.00, patternVariant = "Gold Foil Parallel", setCode = "PRB-01")
    )

    // =========================================================================
    // DISNEY LORCANA (2023 - 2026)
    // =========================================================================

    // The First Chapter & Rise of the Floodborn (TFC / ROTF - 2023)
    private val lorcanaChapter12Items: List<SetCardItem> = listOf(
        SetCardItem("207/204", "Elsa - Spirit of Winter (Enchanted)", "Enchanted Rare", "Holo Foil Textured", 5800.00, patternVariant = "Enchanted Rare", setCode = "TFC"),
        SetCardItem("208/204", "Mickey Mouse - Brave Little Tailor (Enchanted)", "Enchanted Rare", "Foil Textured", 1450.00, patternVariant = "Enchanted Rare", setCode = "TFC"),
        SetCardItem("206/204", "Tinker Bell - Giant Fairy (Enchanted)", "Enchanted Rare", "Foil Textured", 2200.00, patternVariant = "Enchanted Rare", setCode = "TFC"),
        SetCardItem("209/204", "Stitch - Carefree Surfer (Enchanted)", "Enchanted Rare", "Foil Textured", 1800.00, patternVariant = "Enchanted Rare", setCode = "TFC"),
        SetCardItem("205/204", "Cinderella - Stouthearted (Enchanted)", "Enchanted Rare", "Foil Textured", 1950.00, patternVariant = "Enchanted Rare", setCode = "ROTF"),
        SetCardItem("206/204", "Alice - Growing Girl (Enchanted)", "Enchanted Rare", "Foil Textured", 1350.00, patternVariant = "Enchanted Rare", setCode = "ROTF")
    )

    // Into the Inklands & Ursula's Return / Azurite Sea (ITI / URR / AZS - 2024-2025)
    private val lorcanaModernItems: List<SetCardItem> = listOf(
        SetCardItem("207/204", "Ursula - Deceiver of All (Enchanted)", "Enchanted Rare", "Foil Textured", 1650.00, patternVariant = "Enchanted Rare", setCode = "ITI"),
        SetCardItem("208/204", "Maleficent - Monstrous Dragon (Enchanted)", "Enchanted Rare", "Foil Textured", 1550.00, patternVariant = "Enchanted Rare", setCode = "ITI"),
        SetCardItem("205/204", "Ariel - Sonic Warrior (Enchanted)", "Enchanted Rare", "Foil Textured", 1400.00, patternVariant = "Enchanted Rare", setCode = "URR"),
        SetCardItem("209/204", "Diablo - Devoted Herald (Enchanted)", "Enchanted Rare", "Foil Textured", 1250.00, patternVariant = "Enchanted Rare", setCode = "URR"),
        SetCardItem("207/204", "Stitch - Alien Buccaneer (Enchanted)", "Enchanted Rare", "Foil Textured", 1800.00, patternVariant = "Enchanted Rare", setCode = "AZS")
    )

    // =========================================================================
    // STAR WARS: UNLIMITED & DRAGON BALL SUPER (2024 - 2026)
    // =========================================================================

    private val starWarsUnlimitedItems: List<SetCardItem> = listOf(
        SetCardItem("SOR-010", "Darth Vader - Command Leader (Showcase)", "Showcase Leader", "Foil Showcase Hyperspace", 3800.00, patternVariant = "Showcase Frame", setCode = "SOR"),
        SetCardItem("SOR-005", "Luke Skywalker - Faithful Friend (Showcase)", "Showcase Leader", "Foil Showcase Hyperspace", 2400.00, patternVariant = "Showcase Frame", setCode = "SOR"),
        SetCardItem("SOR-015", "Boba Fett - Collecting the Bounty (Showcase)", "Showcase Leader", "Foil Showcase Hyperspace", 2900.00, patternVariant = "Showcase Frame", setCode = "SOR"),
        SetCardItem("SHD-001", "The Mandalorian - Sworn to the Creed (Showcase)", "Showcase Leader", "Foil Showcase Hyperspace", 2600.00, patternVariant = "Showcase Frame", setCode = "SHD"),
        SetCardItem("TWI-003", "Ahsoka Tano - Former Jedi (Showcase)", "Showcase Leader", "Foil Showcase Hyperspace", 2800.00, patternVariant = "Showcase Frame", setCode = "TWI")
    )

    private val dragonBallFusionItems: List<SetCardItem> = listOf(
        SetCardItem("FB01-139", "Son Goku (God Rare GDR Parallel)", "God Rare", "Pure Gold Foil Holographic", 8900.00, patternVariant = "God Rare GDR", setCode = "FB01"),
        SetCardItem("FB01-035", "Vegeta (Super Alternate Art)", "Super Rare Parallel", "Textured Foil", 680.00, patternVariant = "Super Alt Art", setCode = "FB01"),
        SetCardItem("FB02-140", "Vegito (God Rare GDR Parallel)", "God Rare", "Pure Gold Foil Holographic", 9500.00, patternVariant = "God Rare GDR", setCode = "FB02")
    )

    // =========================================================================
    // HOT WHEELS (1968 - 2026)
    // =========================================================================

    // Hot Wheels Mainline 2026 (HW-2026)
    private val hw2026Items: List<SetCardItem> = listOf(
        SetCardItem("#001/250", "Pagani Utopia Roadster", "Super Treasure Hunt", "Spectraflame Prata / Real Riders", 390.00, setCode = "HW-2026"),
        SetCardItem("#015/250", "Porsche 911 GT3 RS (992.2)", "Treasure Hunt", "Foil Flame Logo", 120.00, setCode = "HW-2026"),
        SetCardItem("#045/250", "Toyota GR Yaris Rally1", "Básico", "Mainline", 18.00, setCode = "HW-2026"),
        SetCardItem("#088/250", "Nissan Skyline GT-R R32 Nismo", "Super Treasure Hunt", "Spectraflame Gunmetal", 420.00, setCode = "HW-2026")
    )

    // Hot Wheels Mainline 2025 (HW-2025)
    private val hw2025Items: List<SetCardItem> = listOf(
        SetCardItem("#001/250", "Porsche 911 GT3 RS (992)", "Super Treasure Hunt", "Spectraflame Azul / Real Riders", 380.00, setCode = "HW-2025"),
        SetCardItem("#024/250", "Nissan Skyline GT-R (BCNR33)", "Treasure Hunt", "Foil Flame Logo", 95.00, setCode = "HW-2025"),
        SetCardItem("#035/250", "'73 BMW 3.0 CSL Race Car", "Super Treasure Hunt", "Spectraflame Branco / Real Riders", 340.00, setCode = "HW-2025"),
        SetCardItem("#110/250", "'69 Dodge Charger 500", "Super Treasure Hunt", "Spectraflame Roxo / Real Riders", 320.00, setCode = "HW-2025"),
        SetCardItem("#215/250", "Honda Civic Type R (EK9) Spoon", "Treasure Hunt", "Foil Tampo", 110.00, setCode = "HW-2025")
    )

    // =========================================================================
    // MOEDAS & NUMISMÁTICA (1724 - 2024)
    // =========================================================================

    private val moedasItems: List<SetCardItem> = listOf(
        SetCardItem("MOED-1998", "1 Real 1998 - 50 Anos Direitos Humanos", "Flor de Cunho / Rara", "Bimetálica", 480.00, setCode = "MOED-BR"),
        SetCardItem("MOED-2002", "1 Real 2002 - JK Juscelino Kubitschek", "Flor de Cunho", "Bimetálica", 120.00, setCode = "MOED-BR"),
        SetCardItem("MOED-2012", "1 Real 2012 - Entrega Bandeira Olímpica", "Flor de Cunho", "Bimetálica", 350.00, setCode = "MOED-BR"),
        SetCardItem("MOED-2016", "1 Real 2016 - Mascotes Rio 2016 (Vinícius & Tom)", "Flor de Cunho", "Bimetálica", 150.00, setCode = "MOED-BR"),
        SetCardItem("MOED-1818", "960 Réis 1818 'Patacão' Prata D. João VI", "MBC / Rara", "Prata Lei 896", 950.00, setCode = "MOED-HIST"),
        SetCardItem("MOED-1724", "20.000 Réis 1724 'Dobrão de Minas' D. João V", "Flor de Cunho Ouro", "Ouro 917 (53.8g)", 85000.00, setCode = "MOED-HIST")
    )

    // =========================================================================
    // ALL COLLECTIBLE SETS REGISTRY ACROSS ALL ERAS & YEARS
    // =========================================================================

    val popularSets = listOf(
        // --- 2026 MEGA ERA & 30TH CELEBRATION ---
        CollectibleSet(
            id = "pokemon_30th_celebration",
            name = "30th Celebration",
            code = "30C",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 30,
            iconCategory = "pokemon",
            bannerColor = 0xFFF59E0B,
            description = "A histórica celebração de 30 Anos de Pokémon TCG com cards lendários de todas as gerações e Ash & Pikachu SIR.",
            items = set30thCelebrationItems
        ),
        CollectibleSet(
            id = "pokemon_30th_classic",
            name = "30th Celebration Classic Cards",
            code = "30C-C",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 25,
            iconCategory = "pokemon",
            bannerColor = 0xFFDC2626,
            description = "Reprints icônicos clássicos com selo dos 30 anos: Charizard 1996, Umbreon Star, Shining Gyarados e Mew ex.",
            items = set30thClassicItems
        ),
        CollectibleSet(
            id = "pokemon_starter_mem",
            name = "ex Starter Set Sprigatito & Meowscarada ex",
            code = "MEM",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 20,
            iconCategory = "pokemon",
            bannerColor = 0xFF10B981,
            description = "Deck inicial competitivo de Grama com Meowscarada ex e mecânicas Mega.",
            items = starterMemItems
        ),
        CollectibleSet(
            id = "pokemon_starter_mez",
            name = "ex Starter Set Zorua & Zoroark ex",
            code = "MEZ",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 20,
            iconCategory = "pokemon",
            bannerColor = 0xFF6366F1,
            description = "Deck inicial competitivo de Escuridão com Hisuian Zoroark ex.",
            items = starterMezItems
        ),
        CollectibleSet(
            id = "pokemon_starter_mee",
            name = "ex Starter Set Eevee ex",
            code = "MEE",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 20,
            iconCategory = "pokemon",
            bannerColor = 0xFFF59E0B,
            description = "Deck especial com Eevee ex prismático e itens de evolução de todas as Eeveelutions.",
            items = starterMeeItems
        ),
        CollectibleSet(
            id = "pokemon_storm_emeralda",
            name = "Storm Emeralda",
            code = "M6",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 90,
            iconCategory = "pokemon",
            bannerColor = 0xFF059669,
            description = "Expansão focada na tempestade esmeralda de Rayquaza ex e Mega Sceptile.",
            items = stormEmeraldaItems
        ),
        CollectibleSet(
            id = "pokemon_pitch_black",
            name = "Pitch Black",
            code = "PBL",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / 30th Celebration & Mega Era",
            totalItems = 90,
            iconCategory = "pokemon",
            bannerColor = 0xFF1E1E2E,
            description = "Coleção sombria trazendo Darkrai ex SIR e o temido Mega Gengar ex.",
            items = pitchBlackItems
        ),
        CollectibleSet(
            id = "pokemon_prize_pack_9",
            name = "Play! Pokémon Prize Pack Series Nine",
            code = "PPPS9",
            franchise = "Pokémon TCG",
            year = "2026",
            era = "2026 / Play! Pokémon Prize Packs",
            totalItems = 80,
            iconCategory = "pokemon",
            bannerColor = 0xFF2563EB,
            description = "Pacotes oficiais de premiação competitiva de ligas e torneios com selo Play! Pokémon Series Nine.",
            items = prizePack9Items
        ),
        CollectibleSet(
            id = "pokemon_terastal_gathering_c",
            name = "Terastal Gathering",
            code = "C9.5C",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 187,
            iconCategory = "pokemon",
            bannerColor = 0xFF8B5CF6,
            description = "Coleção principal de reunião Terastal com cards normais e Double Rares.",
            items = terastalGatheringItems
        ),
        CollectibleSet(
            id = "pokemon_terastal_pokeball",
            name = "Terastal Gathering: Poké Ball",
            code = "C9.5P",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 187,
            iconCategory = "pokemon",
            bannerColor = 0xFFEF4444,
            description = "Edição com padrão Reverse Holo de Poké Ball em todas as cartas comuns, incomuns e raras.",
            items = terastalPokeBallItems
        ),
        CollectibleSet(
            id = "pokemon_terastal_masterball",
            name = "Terastal Gathering: Master Ball",
            code = "C9.5M",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 187,
            iconCategory = "pokemon",
            bannerColor = 0xFF7C3AED,
            description = "A raríssima e cobiçada versão Master Ball Mirror Foil de Terastal Gathering.",
            items = terastalMasterBallItems
        ),
        CollectibleSet(
            id = "pokemon_abyss_eye",
            name = "Abyss Eye",
            code = "M5",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 80,
            iconCategory = "pokemon",
            bannerColor = 0xFF0284C7,
            description = "O despertar do abismo marinho com Kyogre ex SIR.",
            items = abyssEyeItems
        ),
        CollectibleSet(
            id = "pokemon_chaos_rising",
            name = "Chaos Rising",
            code = "CRI",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 95,
            iconCategory = "pokemon",
            bannerColor = 0xFFDC2626,
            description = "A fúria de magma e caos com Groudon ex SIR.",
            items = chaosRisingItems
        ),
        CollectibleSet(
            id = "pokemon_stellar_crystal",
            name = "Stellar Crystal",
            code = "CSV9C",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 100,
            iconCategory = "pokemon",
            bannerColor = 0xFF38BDF8,
            description = "Expansão cristalina apresentando a dupla Latias & Latios Duo SIR.",
            items = stellarCrystalItems
        ),
        CollectibleSet(
            id = "pokemon_stellar_crystal_pb",
            name = "Stellar Crystal: Poké Ball",
            code = "CSV9P",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 100,
            iconCategory = "pokemon",
            bannerColor = 0xFF0EA5E9,
            description = "Versão com padrão Poké Ball de Stellar Crystal.",
            items = stellarCrystalPokeItems
        ),
        CollectibleSet(
            id = "pokemon_stellar_crystal_mb",
            name = "Stellar Crystal: Master Ball",
            code = "CSV9M",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 100,
            iconCategory = "pokemon",
            bannerColor = 0xFF9333EA,
            description = "Versão espelhada Master Ball Foil de Stellar Crystal.",
            items = stellarCrystalMasterItems
        ),
        CollectibleSet(
            id = "pokemon_gem_pack_5",
            name = "Gem Pack Volume 5",
            code = "CBB5C",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 30,
            iconCategory = "pokemon",
            bannerColor = 0xFF06B6D4,
            description = "Coleção de joias e gemas com Glaceon ex e Leafeon ex com acabamento cristalino.",
            items = gemPack5Items
        ),
        CollectibleSet(
            id = "pokemon_perfect_order",
            name = "Perfect Order",
            code = "POR",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 85,
            iconCategory = "pokemon",
            bannerColor = 0xFF16A34A,
            description = "A ordem absoluta de Kalos liderada por Zygarde Complete Forme ex SIR.",
            items = perfectOrderItems
        ),
        CollectibleSet(
            id = "pokemon_sparkling_fable",
            name = "Sparkling Fable",
            code = "CSV8C",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 64,
            iconCategory = "pokemon",
            bannerColor = 0xFF9333EA,
            description = "Coleção especial da fábula venenosa de Pecharunt e os Três Leais.",
            items = sparklingFableItems
        ),
        CollectibleSet(
            id = "pokemon_sparkling_fable_mb",
            name = "Sparkling Fable: Master Ball",
            code = "CSV8m",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 64,
            iconCategory = "pokemon",
            bannerColor = 0xFF7E22CE,
            description = "Padrão Master Ball Mirror de Sparkling Fable.",
            items = sparklingFableMasterItems
        ),
        CollectibleSet(
            id = "pokemon_sparkling_fable_pb",
            name = "Sparkling Fable: Poké Ball",
            code = "CSV8p",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 64,
            iconCategory = "pokemon",
            bannerColor = 0xFFC084FC,
            description = "Padrão Poké Ball Mirror de Sparkling Fable.",
            items = sparklingFablePokeItems
        ),
        CollectibleSet(
            id = "pokemon_ninja_spinner",
            name = "Ninja Spinner",
            code = "M4",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 75,
            iconCategory = "pokemon",
            bannerColor = 0xFF2563EB,
            description = "Coleção ninja de alta velocidade com Greninja ex Ninja Spinner SIR.",
            items = ninjaSpinnerItems
        ),
        CollectibleSet(
            id = "pokemon_gem_pack_4",
            name = "Gem Pack Volume 4",
            code = "CBB4C",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 30,
            iconCategory = "pokemon",
            bannerColor = 0xFFEC4899,
            description = "Edição de gemas com Sylveon ex e Umbreon ex.",
            items = gemPack4Items
        ),
        CollectibleSet(
            id = "pokemon_ascended_heroes",
            name = "Ascended Heroes",
            code = "ASC",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 195,
            iconCategory = "pokemon",
            bannerColor = 0xFFF59E0B,
            description = "O ápice dos heróis com Mega Lucario ex e Mega Mewtwo X/Y ex.",
            items = ascendedHeroesItems
        ),
        CollectibleSet(
            id = "pokemon_ascended_rocket",
            name = "Ascended Heroes: Team Rocket Pattern",
            code = "ASCTR",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 195,
            iconCategory = "pokemon",
            bannerColor = 0xFF18181B,
            description = "Cards exclusivos de Ascended Heroes com textura e silhueta clássica do 'R' da Equipe Rocket.",
            items = ascendedTeamRocketItems
        ),
        CollectibleSet(
            id = "pokemon_ascended_pokeball",
            name = "Ascended Heroes: Poké Ball’s Pattern",
            code = "ASCPB",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 195,
            iconCategory = "pokemon",
            bannerColor = 0xFFEF4444,
            description = "Padrão holográfico de Poké Ball de Ascended Heroes.",
            items = ascendedPokeBallItems
        ),
        CollectibleSet(
            id = "pokemon_ascended_energy",
            name = "Ascended Heroes: Energy Symbol Pattern",
            code = "ASCES",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 195,
            iconCategory = "pokemon",
            bannerColor = 0xFF10B981,
            description = "Padrão de símbolos elementais de Energia (Fogo, Água, Grama, Elétrico, Psíquico).",
            items = ascendedEnergySymbolItems
        ),
        CollectibleSet(
            id = "pokemon_nihil_zero",
            name = "Nihil Zero",
            code = "M3",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 70,
            iconCategory = "pokemon",
            bannerColor = 0xFF6B7280,
            description = "O ponto zero da escuridão e do vácuo com Ultra Necrozma ex SIR.",
            items = nihilZeroItems
        ),
        CollectibleSet(
            id = "pokemon_blade_awakening",
            name = "Blade Awakening",
            code = "CSV7C",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 90,
            iconCategory = "pokemon",
            bannerColor = 0xFFEA580C,
            description = "O despertar das lâminas com Ceruledge ex e Armarouge ex.",
            items = bladeAwakeningItems
        ),
        CollectibleSet(
            id = "pokemon_blade_awakening_mb",
            name = "Blade Awakening: Master Ball",
            code = "CSV7m",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 90,
            iconCategory = "pokemon",
            bannerColor = 0xFF9333EA,
            description = "Padrão Master Ball Mirror de Blade Awakening.",
            items = bladeMasterBallItems
        ),
        CollectibleSet(
            id = "pokemon_blade_awakening_pb",
            name = "Blade Awakening: Poké Ball",
            code = "CSV7p",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 90,
            iconCategory = "pokemon",
            bannerColor = 0xFFFB923C,
            description = "Padrão Poké Ball de Blade Awakening.",
            items = bladePokeBallItems
        ),
        CollectibleSet(
            id = "pokemon_prize_pack_8",
            name = "Play! Pokémon Prize Pack Series Eight",
            code = "PPPS8",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "2026 / Play! Pokémon Prize Packs",
            totalItems = 80,
            iconCategory = "pokemon",
            bannerColor = 0xFF1D4ED8,
            description = "Pacotes de premiação competitiva de ligas oficiais de 2025 com Greninja ex e Carmine com selo Play! Pokémon.",
            items = prizePack8Items
        ),
        // --- SCARLET & VIOLET CLASSICS (2023-2025) ---
        CollectibleSet(
            id = "pokemon_prismatic",
            name = "Scarlet & Violet: Prismatic Evolutions",
            code = "PRE",
            franchise = "Pokémon TCG",
            year = "2025",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 175,
            iconCategory = "pokemon",
            bannerColor = 0xFF8B5CF6,
            description = "O ápice de Eevee e suas 8 evoluções em versões Stellar Tera e Special Illustration Rares.",
            items = prismaticEvolutionsItems
        ),
        CollectibleSet(
            id = "pokemon_surging_sparks",
            name = "Scarlet & Violet: Surging Sparks",
            code = "SSP",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 191,
            iconCategory = "pokemon",
            bannerColor = 0xFFFBBF24,
            description = "Pikachu ex Stellar Tera, Latias ex, Milotic ex e Lisia's Appeal SIR.",
            items = surgingSparksItems
        ),
        CollectibleSet(
            id = "pokemon_151",
            name = "Scarlet & Violet: 151",
            code = "151",
            franchise = "Pokémon TCG",
            year = "2023",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 165,
            iconCategory = "pokemon",
            bannerColor = 0xFFEF4444,
            description = "O icônico set com os 151 Pokémons originais de Kanto em versões modernas, ex e Secret Rares.",
            items = pokemon151Items
        ),
        CollectibleSet(
            id = "pokemon_twilight",
            name = "Scarlet & Violet: Twilight Masquerade",
            code = "TWM",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 167,
            iconCategory = "pokemon",
            bannerColor = 0xFF065F46,
            description = "Apresenta as 4 Máscaras de Ogerpon, Carmine SIR, Perrin e o cobiçado Greninja ex SIR.",
            items = twilightMasqueradeItems
        ),
        CollectibleSet(
            id = "pokemon_temporal",
            name = "Scarlet & Violet: Temporal Forces",
            code = "TEF",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 162,
            iconCategory = "pokemon",
            bannerColor = 0xFF0284C7,
            description = "Retorno dos cards ACE SPEC com Raging Bolt ex e Iron Crown ex.",
            items = temporalForcesItems
        ),
        CollectibleSet(
            id = "pokemon_paldean_fates",
            name = "Scarlet & Violet: Paldean Fates",
            code = "PAF",
            franchise = "Pokémon TCG",
            year = "2024",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 91,
            iconCategory = "pokemon",
            bannerColor = 0xFF6366F1,
            description = "Coleção Shiny com Charizard ex Shiny SIR, Gardevoir ex Shiny e Mew ex Shiny.",
            items = paldeanFatesItems
        ),
        CollectibleSet(
            id = "pokemon_paldea",
            name = "Scarlet & Violet: Paldea Evolved",
            code = "PAL",
            franchise = "Pokémon TCG",
            year = "2023",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 193,
            iconCategory = "pokemon",
            bannerColor = 0xFF10B981,
            description = "Expansão de Paldea com Iono Full Art & SIR, Chien-Pao ex e Magikarp IR.",
            items = paldeaEvolvedItems
        ),
        CollectibleSet(
            id = "pokemon_sv_base",
            name = "Scarlet & Violet Base Set",
            code = "SVI",
            franchise = "Pokémon TCG",
            year = "2023",
            era = "Scarlet & Violet (2023-2025)",
            totalItems = 198,
            iconCategory = "pokemon",
            bannerColor = 0xFFDC2626,
            description = "A estreia da 9ª geração nos cards com Miriam SIR e Koraidon / Miraidon ex.",
            items = scarletVioletBaseItems
        ),
        // --- SWORD & SHIELD ERA (2020-2023) ---
        CollectibleSet(
            id = "pokemon_crown_zenith",
            name = "Crown Zenith",
            code = "CRZ",
            franchise = "Pokémon TCG",
            year = "2023",
            era = "Sword & Shield (2020-2023)",
            totalItems = 160,
            iconCategory = "pokemon",
            bannerColor = 0xFFEAB308,
            description = "O grandioso encerramento de Sword & Shield com a Galarian Gallery Dourada de Giratina, Arceus, Dialga e Palkia.",
            items = crownZenithItems
        ),
        CollectibleSet(
            id = "pokemon_evolving_skies",
            name = "Evolving Skies",
            code = "EVS",
            franchise = "Pokémon TCG",
            year = "2021",
            era = "Sword & Shield (2020-2023)",
            totalItems = 203,
            iconCategory = "pokemon",
            bannerColor = 0xFF059669,
            description = "O set mais valorizado da era moderna com o lendário Umbreon VMAX Alt Art (Moonbreon) e Rayquaza VMAX Alt.",
            items = evolvingSkiesItems
        ),
        CollectibleSet(
            id = "pokemon_celebrations_25",
            name = "Celebrations 25th Anniversary",
            code = "CEL",
            franchise = "Pokémon TCG",
            year = "2021",
            era = "Sword & Shield (2020-2023)",
            totalItems = 25,
            iconCategory = "pokemon",
            bannerColor = 0xFFF59E0B,
            description = "A coleção comemorativa dos 25 anos de Pokémon com Mew Gold e Charizard Base Reprint.",
            items = celebrations25Items
        ),
        // --- SUN & MOON ERA (2017-2019) ---
        CollectibleSet(
            id = "pokemon_cosmic_eclipse",
            name = "Cosmic Eclipse",
            code = "CEC",
            franchise = "Pokémon TCG",
            year = "2019",
            era = "Sun & Moon (2017-2019)",
            totalItems = 236,
            iconCategory = "pokemon",
            bannerColor = 0xFF4338CA,
            description = "O clímax de Sun & Moon com Arceus & Dialga & Palkia GX e os primeiros Character Secret Rares.",
            items = cosmicEclipseItems
        ),
        CollectibleSet(
            id = "pokemon_team_up",
            name = "Team Up",
            code = "TEU",
            franchise = "Pokémon TCG",
            year = "2019",
            era = "Sun & Moon (2017-2019)",
            totalItems = 181,
            iconCategory = "pokemon",
            bannerColor = 0xFFEA580C,
            description = "A estreia histórica dos cards TAG TEAM GX com o cobiçado Latios & Latias GX Alternate Art.",
            items = teamUpItems
        ),
        // --- VINTAGE & CLASSIC POKÉMON (1999-2002) ---
        CollectibleSet(
            id = "pokemon_base_1999",
            name = "Base Set Original 1999",
            code = "BS",
            franchise = "Pokémon TCG",
            year = "1999",
            era = "Vintage Wizards of the Coast (1999-2003)",
            totalItems = 102,
            iconCategory = "pokemon",
            bannerColor = 0xFFF59E0B,
            description = "A histórica primeira coleção ocidental com Charizard Holo, Blastoise e Venusaur.",
            items = baseSet1999Items
        ),
        CollectibleSet(
            id = "pokemon_team_rocket_2000",
            name = "Team Rocket Original",
            code = "TR",
            franchise = "Pokémon TCG",
            year = "2000",
            era = "Vintage Wizards of the Coast (1999-2003)",
            totalItems = 82,
            iconCategory = "pokemon",
            bannerColor = 0xFF18181B,
            description = "A estreia dos Dark Pokémon com Dark Charizard, Dark Dragonite e Dark Blastoise.",
            items = teamRocketItems
        ),
        CollectibleSet(
            id = "pokemon_neo_destiny",
            name = "Neo Destiny",
            code = "N4",
            franchise = "Pokémon TCG",
            year = "2002",
            era = "Vintage Wizards of the Coast (1999-2003)",
            totalItems = 105,
            iconCategory = "pokemon",
            bannerColor = 0xFF4B5563,
            description = "A lendária coleção Neo com Shining Charizard, Shining Mewtwo e Shining Tyranitar.",
            items = neoDestinyItems
        ),
        // --- MAGIC: THE GATHERING (1993 - 2026) ---
        CollectibleSet(
            id = "mtg_bloomburrow",
            name = "Bloomburrow",
            code = "BLB",
            franchise = "Magic: The Gathering",
            year = "2024",
            era = "Modern Standard (2024-2026)",
            totalItems = 281,
            iconCategory = "magic",
            bannerColor = 0xFF059669,
            description = "Bello Anime Foil, Lumra Raised Foil, Bria Anime e Three Tree City Borderless.",
            items = mtgBloomburrowItems
        ),
        CollectibleSet(
            id = "mtg_duskmourn",
            name = "Duskmourn: House of Horror",
            code = "DSK",
            franchise = "Magic: The Gathering",
            year = "2024",
            era = "Modern Standard (2024-2026)",
            totalItems = 276,
            iconCategory = "magic",
            bannerColor = 0xFF4C0519,
            description = "Valgavoth Fracture Foil, Overlord of the Hauntwoods e Leyline of Resonance.",
            items = mtgDuskmournItems
        ),
        CollectibleSet(
            id = "mtg_foundations",
            name = "Magic: The Gathering Foundations",
            code = "FDN",
            franchise = "Magic: The Gathering",
            year = "2024",
            era = "Modern Standard (2024-2026)",
            totalItems = 271,
            iconCategory = "magic",
            bannerColor = 0xFFD97706,
            description = "Coleção base definitiva até 2029 com Sire of Seven Deaths, Twinflame Tyrant e Llanowar Elves retro.",
            items = mtgFoundationsItems
        ),
        CollectibleSet(
            id = "mtg_mh3",
            name = "Modern Horizons 3",
            code = "MH3",
            franchise = "Magic: The Gathering",
            year = "2024",
            era = "Modern Horizons & Masters (2020-2026)",
            totalItems = 303,
            iconCategory = "magic",
            bannerColor = 0xFF8B5CF6,
            description = "Ulamog Textured Foil, Tamiyo, Sorin, Ajani e Fetchlands Retro Frame.",
            items = mh3Items
        ),
        CollectibleSet(
            id = "mtg_cmm",
            name = "Commander Masters",
            code = "CMM",
            franchise = "Magic: The Gathering",
            year = "2023",
            era = "Modern Horizons & Masters (2020-2026)",
            totalItems = 436,
            iconCategory = "magic",
            bannerColor = 0xFFE11D48,
            description = "Jeweled Lotus Frame Break, The Ur-Dragon Profile Foil e Demonic Tutor Textured.",
            items = mtgCommanderMastersItems
        ),
        CollectibleSet(
            id = "mtg_lotr",
            name = "The Lord of the Rings: Tales of Middle-earth",
            code = "LTR",
            franchise = "Magic: The Gathering",
            year = "2023",
            era = "Universes Beyond (2023-2025)",
            totalItems = 281,
            iconCategory = "magic",
            bannerColor = 0xFFD97706,
            description = "The One Ring (incluindo o exemplar único 001/001 Post Malone), Orcish Bowmasters e Sol Rings Seriais.",
            items = lotrItems
        ),
        CollectibleSet(
            id = "mtg_phyrexia_one",
            name = "Phyrexia: All Will Be One",
            code = "ONE",
            franchise = "Magic: The Gathering",
            year = "2023",
            era = "Phyrexian Arc (2022-2023)",
            totalItems = 271,
            iconCategory = "magic",
            bannerColor = 0xFF991B1B,
            description = "Elesh Norn Junji Ito Manga, Atraxa Step-and-Compleat Foil e Sheoldred Oil Slick Foil.",
            items = mtgPhyrexiaItems
        ),
        CollectibleSet(
            id = "mtg_kamigawa_neo",
            name = "Kamigawa: Neon Dynasty",
            code = "NEO",
            franchise = "Magic: The Gathering",
            year = "2022",
            era = "Modern Eras (2020-2023)",
            totalItems = 302,
            iconCategory = "magic",
            bannerColor = 0xFFEC4899,
            description = "Hidetsugu Neon Ink Foil (Red/Blue/Green), The Wandering Emperor Anime e Boseiju Borderless.",
            items = mtgKamigawaItems
        ),
        CollectibleSet(
            id = "mtg_double_masters_2022",
            name = "Double Masters 2022",
            code = "2X2",
            franchise = "Magic: The Gathering",
            year = "2022",
            era = "Modern Horizons & Masters (2020-2026)",
            totalItems = 331,
            iconCategory = "magic",
            bannerColor = 0xFFF97316,
            description = "Imperial Seal Borderless, Mana Vault Textured Foil e Force of Will Borderless.",
            items = mtgDoubleMastersItems
        ),
        CollectibleSet(
            id = "mtg_mh2",
            name = "Modern Horizons 2",
            code = "MH2",
            franchise = "Magic: The Gathering",
            year = "2021",
            era = "Modern Horizons & Masters (2020-2026)",
            totalItems = 303,
            iconCategory = "magic",
            bannerColor = 0xFF3B82F6,
            description = "Ragavan Nimble Pilferer Borderless, Urza's Saga Sketch/Retro e Solitude Borderless.",
            items = mtgMh2Items
        ),
        CollectibleSet(
            id = "mtg_dominaria",
            name = "Dominaria Original 2018",
            code = "DOM",
            franchise = "Magic: The Gathering",
            year = "2018",
            era = "Pioneer & Modern (2015-2019)",
            totalItems = 269,
            iconCategory = "magic",
            bannerColor = 0xFFCA8A04,
            description = "Teferi Hero of Dominaria, Mox Amber Foil e Karn Scion of Urza.",
            items = mtgDominariaItems
        ),
        CollectibleSet(
            id = "mtg_innistrad",
            name = "Innistrad Original 2011",
            code = "ISD",
            franchise = "Magic: The Gathering",
            year = "2011",
            era = "Modern Era (2010-2014)",
            totalItems = 264,
            iconCategory = "magic",
            bannerColor = 0xFF374151,
            description = "Liliana of the Veil Original Foil, Snapcaster Mage e Geist of Saint Traft.",
            items = mtgInnistradItems
        ),
        CollectibleSet(
            id = "mtg_zendikar",
            name = "Zendikar & Worldwake",
            code = "ZEN",
            franchise = "Magic: The Gathering",
            year = "2009",
            era = "Modern Era (2003-2009)",
            totalItems = 249,
            iconCategory = "magic",
            bannerColor = 0xFF0891B2,
            description = "Jace the Mind Sculptor, Scalding Tarn, Misty Rainforest e Fetchlands originais.",
            items = mtgZendikarItems
        ),
        CollectibleSet(
            id = "mtg_ravnica",
            name = "Ravnica: City of Guilds",
            code = "RAV",
            franchise = "Magic: The Gathering",
            year = "2005",
            era = "Modern Era (2003-2009)",
            totalItems = 306,
            iconCategory = "magic",
            bannerColor = 0xFF7C3AED,
            description = "Dark Confidant Bob Maher, Doubling Season e o ciclo original de Shocklands.",
            items = mtgRavnicaItems
        ),
        CollectibleSet(
            id = "mtg_urza_saga",
            name = "Urza's Saga (1998)",
            code = "USG",
            franchise = "Magic: The Gathering",
            year = "1998",
            era = "Vintage MTG (1993-1999)",
            totalItems = 350,
            iconCategory = "magic",
            bannerColor = 0xFFB45309,
            description = "Gaea's Cradle Reserved List, Tolarian Academy, Yawgmoth's Will e Time Spiral.",
            items = mtgUrzaSagaItems
        ),
        CollectibleSet(
            id = "mtg_arabian_legends",
            name = "Arabian Nights & Legends (1993-1994)",
            code = "ARN",
            franchise = "Magic: The Gathering",
            year = "1993",
            era = "Vintage MTG (1993-1999)",
            totalItems = 310,
            iconCategory = "magic",
            bannerColor = 0xFF78350F,
            description = "Bazaar of Baghdad, The Tabernacle at Pendrell Vale, Library of Alexandria e Juzám Djinn.",
            items = mtgArabianLegendsItems
        ),
        CollectibleSet(
            id = "mtg_alpha_1993",
            name = "Alpha / Limited Edition 1993",
            code = "LEA",
            franchise = "Magic: The Gathering",
            year = "1993",
            era = "Vintage MTG (1993-1999)",
            totalItems = 295,
            iconCategory = "magic",
            bannerColor = 0xFF4C1D95,
            description = "O lendário Power Nine original (Black Lotus, Moxes, Time Walk, Timetwister) e Dual Lands.",
            items = mtgAlphaItems
        ),
        // --- YU-GI-OH! ---
        CollectibleSet(
            id = "yugioh_lob_2002",
            name = "Legend of Blue Eyes White Dragon",
            code = "LOB",
            franchise = "Yu-Gi-Oh!",
            year = "2002",
            era = "Yu-Gi-Oh! Vintage (2002-2005)",
            totalItems = 126,
            iconCategory = "yugioh",
            bannerColor = 0xFF2563EB,
            description = "Blue-Eyes White Dragon 1st Ed, Dark Magician, Red-Eyes e as 5 partes de Exodia.",
            items = yugiohLobItems
        ),
        CollectibleSet(
            id = "yugioh_mrd_ioc",
            name = "Metal Raiders & Invasion of Chaos",
            code = "MRD",
            franchise = "Yu-Gi-Oh!",
            year = "2004",
            era = "Yu-Gi-Oh! Vintage (2002-2005)",
            totalItems = 144,
            iconCategory = "yugioh",
            bannerColor = 0xFF15803D,
            description = "Chaos Emperor Dragon, Black Luster Soldier, Mirror Force e Dark Magician of Chaos.",
            items = yugiohMrdIocItems
        ),
        CollectibleSet(
            id = "yugioh_rarity_25",
            name = "25th Anniversary Rarity Collection I & II",
            code = "RA01",
            franchise = "Yu-Gi-Oh!",
            year = "2024",
            era = "Yu-Gi-Oh! Modern (2020-2026)",
            totalItems = 158,
            iconCategory = "yugioh",
            bannerColor = 0xFFF59E0B,
            description = "Quarter Century Secret Rares: Ash Blossom, Baronne de Fleur, Accesscode Talker e I:P Masquerena.",
            items = yugiohRarity25Items
        ),
        // --- ONE PIECE CARD GAME ---
        CollectibleSet(
            id = "one_piece_op01",
            name = "OP-01: Romance Dawn",
            code = "OP-01",
            franchise = "One Piece Card Game",
            year = "2022",
            era = "One Piece TCG (2022-2026)",
            totalItems = 121,
            iconCategory = "onepiece",
            bannerColor = 0xFFEA580C,
            description = "A histórica estreia com Shanks Manga Parallel, Zoro Leader Alt e Nami Parallel.",
            items = onePieceOp01Items
        ),
        CollectibleSet(
            id = "one_piece_op0203",
            name = "OP-02 & OP-03: Paramount War / Pillars",
            code = "OP-02",
            franchise = "One Piece Card Game",
            year = "2023",
            era = "One Piece TCG (2022-2026)",
            totalItems = 127,
            iconCategory = "onepiece",
            bannerColor = 0xFF4338CA,
            description = "Ace Manga Parallel, Whitebeard Leader, Sogeking Manga e Katakuri Secret Alt Art.",
            items = onePieceOp0203Items
        ),
        CollectibleSet(
            id = "one_piece_op05",
            name = "OP-05: Awakening of the New Era",
            code = "OP-05",
            franchise = "One Piece Card Game",
            year = "2023",
            era = "One Piece TCG (2022-2026)",
            totalItems = 126,
            iconCategory = "onepiece",
            bannerColor = 0xFFDC2626,
            description = "1º Aniversário de One Piece com a lendária carta Manga Rare de Luffy Gear 5 e Oda Signed.",
            items = onePieceOp05Items
        ),
        CollectibleSet(
            id = "one_piece_modern",
            name = "OP-06 a OP-08 & Premium Booster",
            code = "OP-06",
            franchise = "One Piece Card Game",
            year = "2024",
            era = "One Piece TCG (2022-2026)",
            totalItems = 140,
            iconCategory = "onepiece",
            bannerColor = 0xFF0D9488,
            description = "Zoro Manga, Boa Hancock Manga, Silvers Rayleigh Manga e Don!! Super Ouro Especial.",
            items = onePieceModernItems
        ),
        // --- DISNEY LORCANA ---
        CollectibleSet(
            id = "lorcana_chapter_12",
            name = "The First Chapter & Rise of the Floodborn",
            code = "TFC",
            franchise = "Disney Lorcana",
            year = "2023",
            era = "Lorcana Chapters (2023-2026)",
            totalItems = 204,
            iconCategory = "lorcana",
            bannerColor = 0xFF7C3AED,
            description = "As cobiçadas cartas Enchanted de Elsa, Tinker Bell, Mickey Brave Little Tailor e Cinderella.",
            items = lorcanaChapter12Items
        ),
        CollectibleSet(
            id = "lorcana_modern",
            name = "Into the Inklands, Ursula's Return & Azurite Sea",
            code = "ITI",
            franchise = "Disney Lorcana",
            year = "2024",
            era = "Lorcana Chapters (2023-2026)",
            totalItems = 204,
            iconCategory = "lorcana",
            bannerColor = 0xFF0284C7,
            description = "Cartas Enchanted de Ursula, Maleficent, Ariel Sonic Warrior e Stitch Bucaneiro.",
            items = lorcanaModernItems
        ),
        // --- STAR WARS: UNLIMITED & DRAGON BALL ---
        CollectibleSet(
            id = "star_wars_unlimited",
            name = "Star Wars: Unlimited (Spark / Shadows / Twilight)",
            code = "SOR",
            franchise = "Star Wars: Unlimited",
            year = "2024",
            era = "Star Wars TCG (2024-2026)",
            totalItems = 252,
            iconCategory = "starwars",
            bannerColor = 0xFF1E293B,
            description = "Showcase Leaders de Darth Vader, Luke Skywalker, Boba Fett, O Mandaloriano e Ahsoka Tano.",
            items = starWarsUnlimitedItems
        ),
        CollectibleSet(
            id = "dragon_ball_fusion",
            name = "Dragon Ball Super: Fusion World",
            code = "FB01",
            franchise = "Dragon Ball Super",
            year = "2024",
            era = "Dragon Ball TCG (2024-2026)",
            totalItems = 140,
            iconCategory = "dragonball",
            bannerColor = 0xFFF97316,
            description = "As lendárias cartas God Rare GDR Douradas de Son Goku e Vegito.",
            items = dragonBallFusionItems
        ),
        // --- HOT WHEELS ---
        CollectibleSet(
            id = "hw_mainline_2026",
            name = "Hot Wheels Mainline 2026",
            code = "HW-2026",
            franchise = "Hot Wheels",
            year = "2026",
            era = "Hot Wheels Mainline (2026)",
            totalItems = 250,
            iconCategory = "diecast",
            bannerColor = 0xFFDC2626,
            description = "Coleção mundial 2026 com Pagani Utopia Roadster STH e Porsche 911 GT3 RS 992.2 TH.",
            items = hw2026Items
        ),
        CollectibleSet(
            id = "hw_mainline_2025",
            name = "Hot Wheels Mainline 2025",
            code = "HW-2025",
            franchise = "Hot Wheels",
            year = "2025",
            era = "Hot Wheels Mainline (2025)",
            totalItems = 250,
            iconCategory = "diecast",
            bannerColor = 0xFF2563EB,
            description = "Coleção 2025 com Porsche 911 GT3 RS STH, Skyline BCNR33 TH e BMW 3.0 CSL STH.",
            items = hw2025Items
        ),
        // --- MOEDAS ---
        CollectibleSet(
            id = "moedas_comemorativas",
            name = "Moedas Históricas e Comemorativas",
            code = "MOEDAS",
            franchise = "Moedas",
            year = "2026",
            era = "Numismática Brasileira (1724-2026)",
            totalItems = 20,
            iconCategory = "moedas",
            bannerColor = 0xFFEAB308,
            description = "Moedas raras do Real (Direitos Humanos 1998, Bandeira 2012, Mascotes) e coloniais (Patacão 1818 e Dobrão 1724).",
            items = moedasItems
        )
    )

    fun getSetById(id: String): CollectibleSet? = popularSets.firstOrNull { it.id == id }

    fun getSetByCode(code: String): CollectibleSet? = popularSets.firstOrNull { it.code.equals(code, ignoreCase = true) }
}

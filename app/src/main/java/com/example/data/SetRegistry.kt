package com.example.data

object SetRegistry {

    val popularSets = listOf(
        CollectibleSet(
            id = "pokemon_151",
            name = "Scarlet & Violet: 151",
            franchise = "Pokémon TCG",
            year = "2023",
            totalItems = 165,
            iconCategory = "pokemon",
            bannerColor = 0xFFEF4444,
            description = "O icônico set com os 151 Pokémons originais de Kanto em versões modernas, ex e Secret Rares.",
            items = listOf(
                SetCardItem("001/165", "Bulbasaur", "Comum", "Normal", 4.50),
                SetCardItem("002/165", "Ivysaur", "Incomum", "Normal", 6.00),
                SetCardItem("003/165", "Venusaur ex", "Double Rare", "Foil", 45.00),
                SetCardItem("004/165", "Charmander", "Comum", "Normal", 5.00),
                SetCardItem("005/165", "Charmeleon", "Incomum", "Normal", 7.50),
                SetCardItem("006/165", "Charizard ex", "Double Rare", "Foil", 185.00),
                SetCardItem("007/165", "Squirtle", "Comum", "Normal", 5.00),
                SetCardItem("008/165", "Wartortle", "Incomum", "Normal", 6.50),
                SetCardItem("009/165", "Blastoise ex", "Double Rare", "Foil", 75.00),
                SetCardItem("025/165", "Pikachu", "Comum", "Normal", 12.00),
                SetCardItem("026/165", "Raichu", "Rara", "Holo", 15.00),
                SetCardItem("059/165", "Arcanine", "Rara", "Holo", 14.00),
                SetCardItem("094/165", "Gengar", "Rara", "Holo", 32.00),
                SetCardItem("130/165", "Gyarados", "Rara", "Holo", 28.00),
                SetCardItem("131/165", "Lapras", "Rara", "Holo", 18.00),
                SetCardItem("133/165", "Eevee", "Comum", "Normal", 6.00),
                SetCardItem("143/165", "Snorlax", "Rara", "Holo", 22.00),
                SetCardItem("144/165", "Articuno", "Rara", "Holo", 25.00),
                SetCardItem("145/165", "Zapdos ex", "Double Rare", "Foil", 65.00),
                SetCardItem("146/165", "Moltres", "Rara", "Holo", 24.00),
                SetCardItem("149/165", "Dragonite", "Rara", "Holo", 38.00),
                SetCardItem("150/165", "Mewtwo", "Rara", "Holo", 40.00),
                SetCardItem("151/165", "Mew ex", "Double Rare", "Foil", 85.00),
                SetCardItem("165/165", "Cycling Road", "Incomum", "Normal", 3.00),
                SetCardItem("183/165", "Charizard ex Special Illustration", "Special Art Rare", "Foil", 650.00),
                SetCardItem("199/165", "Alakazam ex SIR", "Special Art Rare", "Foil", 210.00),
                SetCardItem("205/165", "Mew ex Hyper Rare Gold", "Hyper Rare", "Gold Foil", 340.00)
            )
        ),
        CollectibleSet(
            id = "pokemon_paldea",
            name = "Scarlet & Violet: Paldea Evolved",
            franchise = "Pokémon TCG",
            year = "2023",
            totalItems = 193,
            iconCategory = "pokemon",
            bannerColor = 0xFF10B981,
            description = "Expansão de Paldea apresentando os iniciais Meowscarada, Skeledirge, Quaquaval e Iono.",
            items = listOf(
                SetCardItem("015/193", "Meowscarada ex", "Double Rare", "Foil", 35.00),
                SetCardItem("037/193", "Skeledirge ex", "Double Rare", "Foil", 28.00),
                SetCardItem("052/193", "Quaquaval ex", "Double Rare", "Foil", 22.00),
                SetCardItem("061/193", "Chien-Pao ex", "Double Rare", "Foil", 55.00),
                SetCardItem("123/193", "Ting-Lu ex", "Double Rare", "Foil", 30.00),
                SetCardItem("185/193", "Iono (Full Art)", "Ultra Rare", "Foil", 220.00),
                SetCardItem("269/193", "Iono SIR", "Special Art Rare", "Foil", 480.00)
            )
        ),
        CollectibleSet(
            id = "hw_mainline_2024",
            name = "Hot Wheels Mainline 2024",
            franchise = "Hot Wheels",
            year = "2024",
            totalItems = 250,
            iconCategory = "diecast",
            bannerColor = 0xFF3B82F6,
            description = "Coleção básica anual da Hot Wheels incluindo 250 modelos numerados, Treasure Hunts e Super THs.",
            items = listOf(
                SetCardItem("#001/250", "Ford Mustang GTD", "Básico", "Normal", 15.00),
                SetCardItem("#045/250", "'71 Datsun 510 Wagon", "Super Treasure Hunt", "Spectraflame", 320.00),
                SetCardItem("#088/250", "Nissan Skyline GT-R (R34)", "Treasure Hunt", "Foil Tampo", 85.00),
                SetCardItem("#102/250", "Porsche 911 GT3 RS", "Básico", "Normal", 18.00),
                SetCardItem("#125/250", "Toyota Supra MK4", "Básico", "Normal", 22.00),
                SetCardItem("#177/250", "Mazda RX-7", "Básico", "Normal", 16.00),
                SetCardItem("#210/250", "'67 Camaro", "Super Treasure Hunt", "Real Riders", 290.00),
                SetCardItem("#250/250", "Koenigsegg Jesko", "Básico", "Normal", 20.00)
            )
        ),
        CollectibleSet(
            id = "mtg_dominaria",
            name = "Dominaria United",
            franchise = "Magic: The Gathering",
            year = "2022",
            totalItems = 281,
            iconCategory = "magic",
            bannerColor = 0xFF8B5CF6,
            description = "Retorno ao plano lendário de Dominaria com lendas clássicas e os Planeswalkers de proteção.",
            items = listOf(
                SetCardItem("001/281", "Danitha, Benalia's Hope", "Rara", "Normal", 12.00),
                SetCardItem("045/281", "Vodalian Hexcatcher", "Rara", "Normal", 35.00),
                SetCardItem("097/281", "Sheoldred, the Apocalypse", "Mítica", "Foil", 420.00),
                SetCardItem("120/281", "Squee, Dubious Monarch", "Rara", "Normal", 18.00),
                SetCardItem("165/281", "Llanowar Loamspeaker", "Rara", "Normal", 14.00),
                SetCardItem("215/281", "Karn, Living Legacy", "Mítica", "Normal", 45.00),
                SetCardItem("275/281", "Plaza of Heroes", "Rara", "Normal", 55.00)
            )
        ),
        CollectibleSet(
            id = "yugioh_rarity_25",
            name = "25th Anniversary Rarity Collection",
            franchise = "Yu-Gi-Oh!",
            year = "2023",
            totalItems = 79,
            iconCategory = "yugioh",
            bannerColor = 0xFFF59E0B,
            description = "Edição comemorativa com as cartas mais jogadas da história em 7 raridades diferentes (incluindo Quarter Century Secret Rare).",
            items = listOf(
                SetCardItem("RA01-EN001", "Blue-Eyes White Dragon", "Quarter Century Secret", "Starlight", 350.00),
                SetCardItem("RA01-EN005", "Ash Blossom & Joyous Spring", "Quarter Century Secret", "Foil", 280.00),
                SetCardItem("RA01-EN010", "Nibiru, the Primal Being", "Secret Rare", "Foil", 35.00),
                SetCardItem("RA01-EN025", "Infinite Impermanence", "Collector's Rare", "Textured", 95.00),
                SetCardItem("RA01-EN030", "Baronne de Fleur", "Quarter Century Secret", "Foil", 210.00),
                SetCardItem("RA01-EN050", "Pot of Prosperity", "Ultimate Rare", "Relief", 140.00)
            )
        )
    )

    fun getSetById(id: String): CollectibleSet? = popularSets.firstOrNull { it.id == id }
}

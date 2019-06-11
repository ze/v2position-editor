package com.zelkatani.model

import com.zelkatani.model.map.WorldMap
import java.io.File

data class Mod(val worldMap: WorldMap, val localization: Localization) {
    companion object : ModelBuilder<Mod> {
        override fun from(file: File): Mod {
            val modFolder = GameLocation.modFolder
            val gameFolder = GameLocation.gameFolder

            val worldMap = WorldMap.from(modFolder.resolve("map"))
            val localization = Localization.from(gameFolder.resolve("localisation"))
            return Mod(worldMap, localization)
        }
    }
}
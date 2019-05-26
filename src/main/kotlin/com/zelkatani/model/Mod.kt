package com.zelkatani.model

import com.zelkatani.model.map.WorldMap
import java.io.File

class Mod(val worldMap: WorldMap) {
    companion object : ModelBuilder<Mod> {
        override fun from(file: File): Mod {
            val modFolder = GameLocation.modFolder
            val worldMap = WorldMap.from(modFolder.resolve("map"))
            return Mod(worldMap)
        }
    }
}
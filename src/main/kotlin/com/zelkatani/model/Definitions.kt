package com.zelkatani.model

import javafx.scene.paint.Color
import java.io.File

/**
 * Definitions model that holds all records from definitions.csv
 */
data class Definitions(val provinces: Map<Int, DefinitionParser.ProvinceDefinitionRecord>,
                       val colors: Map<Color, DefinitionParser.ColorDefinitionRecord>) {
    companion object : ModelBuilder<Definitions> {
        override fun from(file: File): Definitions {
            val parser = DefinitionParser(file)
            return Definitions(parser.provinces, parser.colors)
        }
    }
}
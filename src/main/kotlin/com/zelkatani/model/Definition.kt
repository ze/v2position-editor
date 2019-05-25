package com.zelkatani.model

import javafx.scene.paint.Color
import java.io.File

/**
 * Definition model that holds all records from definition.csv
 */
data class Definition(
    val provinces: Map<Int, DefinitionParser.ProvinceDefinitionRecord>,
    val colors: Map<Color, DefinitionParser.ColorDefinitionRecord>
) {
    companion object : ModelBuilder<Definition> {
        override fun from(file: File): Definition {
            val parser = DefinitionParser(file)
            return Definition(parser.provinces, parser.colors)
        }
    }
}
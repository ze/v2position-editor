package com.zelkatani.model.map

import com.zelkatani.antlr.DefaultLexer
import com.zelkatani.antlr.DefaultParser
import com.zelkatani.model.ModelBuilder
import com.zelkatani.visitor.map.DefaultVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * The "default.map" file model.
 */
data class Default(
    val maxProvinces: Int,
    val seaStarts: List<Int>,
    val definitions: String,
    val provinces: String,
    val positions: String,
    val terrain: String,
    val rivers: String,
    val terrainDefinition: String,
    val treeDefinition: String,
    val continent: String,
    val adjacencies: String,
    val region: String,
    val regionSea: String,
    val provinceFlagSprites: String,
    val borderHeights: List<Int>,
    val terrainSheetHeights: Int,
    val tree: Int,
    val borderCutoff: Double
) {
    companion object : ModelBuilder<Default> {
        override fun from(file: File): Default {
            val defaultLexer = DefaultLexer(CharStreams.fromReader(file.reader()))
            val defaultParser = DefaultParser(CommonTokenStream(defaultLexer))

            val defaultsContext = defaultParser.defaults()
            val defaultVisitor = DefaultVisitor()

            return defaultVisitor.visitDefaults(defaultsContext)
        }
    }
}

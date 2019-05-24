package com.zelkatani.model

import com.zelkatani.antlr.DefaultsLexer
import com.zelkatani.antlr.DefaultsParser
import com.zelkatani.visitor.DefaultsVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * The "default.map" file model.
 */
data class Defaults(
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
    val borderHeights: Pair<Int, Int>,
    val terrainSheetHeights: Int,
    val tree: Int,
    val borderCutoff: Float
) {
    companion object : ModelBuilder<Defaults> {
        override fun from(file: File): Defaults {
            val defaultLexer = DefaultsLexer(CharStreams.fromReader(file.reader()))
            val defaultParser = DefaultsParser(CommonTokenStream(defaultLexer))

            val defaultsContext = defaultParser.defaults()
            val defaultVisitor = DefaultsVisitor()

            return defaultVisitor.visitDefaults(defaultsContext)
        }
    }
}

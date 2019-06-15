package com.zelkatani.model.map

import com.zelkatani.antlr.ContinentLexer
import com.zelkatani.antlr.ContinentParser
import com.zelkatani.model.ModelBuilder
import com.zelkatani.model.Modifiers
import com.zelkatani.visitor.map.ContinentVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * A model for `continent.txt`.
 */
data class Continent(val definitions: Map<String, ContinentInfo>) {
    companion object : ModelBuilder<Continent> {
        override fun from(file: File): Continent {
            val continentLexer = ContinentLexer(CharStreams.fromReader(file.reader()))
            val continentParser = ContinentParser(CommonTokenStream(continentLexer))

            val continentContext = continentParser.continents()
            val continentVisitor = ContinentVisitor()

            return continentVisitor.visitContinents(continentContext)
        }
    }

    /**
     * Get a [continent] from [definitions].
     */
    operator fun get(continent: String) = definitions[continent]
}

/**
 * Data class for specific continent information. i.e. # of [provinces] or [modifiers] such as assimilation_rate.
 */
data class ContinentInfo(val provinces: List<Int>, val modifiers: Modifiers)
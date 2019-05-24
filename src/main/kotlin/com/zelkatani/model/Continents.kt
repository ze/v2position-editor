package com.zelkatani.model

import com.zelkatani.antlr.ContinentLexer
import com.zelkatani.antlr.ContinentParser
import com.zelkatani.visitor.ContinentVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * Continents model that contains the information of all defined continents.
 * (This may be better as an object)
 */
data class Continents(val definitions: Map<String, ContinentInfo>) {
    companion object : ModelBuilder<Continents> {
        override fun from(file: File): Continents {
            val continentLexer = ContinentLexer(CharStreams.fromReader(file.reader()))
            val continentParser = ContinentParser(CommonTokenStream(continentLexer))

            val continentsContext = continentParser.continents()
            val continentsVisitor = ContinentVisitor()

            return continentsVisitor.visitContinents(continentsContext)
        }
    }

    operator fun get(continent: String) = definitions[continent]
}

/**
 * Data class for specific continent information. i.e. # of [provinces] or [modifiers] such as assimilation_rate.
 */
data class ContinentInfo(val provinces: List<Int>, val modifiers: Modifiers)
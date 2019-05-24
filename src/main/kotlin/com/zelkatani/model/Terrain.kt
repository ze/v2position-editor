package com.zelkatani.model

import com.zelkatani.antlr.TerrainLexer
import com.zelkatani.antlr.TerrainParser
import com.zelkatani.visitor.TerrainVisitor
import javafx.scene.paint.Color
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

data class Terrain(val number: Int,
                   val definitions: Map<String, TerrainInfo>,
                   val terrainTGAMapping: Map<String, TerrainType>) {
    companion object : ModelBuilder<Terrain> {
        override fun from(file: File): Terrain {
            val terrainLexer = TerrainLexer(CharStreams.fromReader(file.reader()))
            val terrainParser = TerrainParser(CommonTokenStream(terrainLexer))

            val terrainContext = terrainParser.terrain()
            val terrainVisitor = TerrainVisitor()

            return terrainVisitor.visitTerrain(terrainContext)
        }

    }
}

data class TerrainInfo(val color: Color, val modifiers: Modifiers)
data class TerrainType(val type: String, val colorTGAIndex: List<Int>, val modifiers: Modifiers)
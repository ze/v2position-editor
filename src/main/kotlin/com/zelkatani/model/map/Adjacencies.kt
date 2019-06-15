package com.zelkatani.model.map

import com.zelkatani.model.ModelBuilder
import java.io.File

/**
 * A model for `adjacencies.csv`.
 */
data class Adjacencies(val types: Map<Point, AdjacenciesParser.AdjacencyRecord>) {
    companion object : ModelBuilder<Adjacencies> {
        override fun from(file: File): Adjacencies {
            val parser = AdjacenciesParser(file)
            return Adjacencies(parser.adjacencies)
        }
    }
}
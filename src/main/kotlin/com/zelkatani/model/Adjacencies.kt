package com.zelkatani.model

import java.io.File

/**
 * Adjacencies model that holds all records from adjacencies.csv
 */
data class Adjacencies(val types: Map<Point, AdjacenciesParser.AdjacencyRecord>) {
    companion object : ModelBuilder<Adjacencies> {
        override fun from(file: File): Adjacencies {
            val parser = AdjacenciesParser(file)
            return Adjacencies(parser.adjacencies)
        }
    }
}
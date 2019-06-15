package com.zelkatani.model.map

import com.zelkatani.antlr.RegionLexer
import com.zelkatani.antlr.RegionParser
import com.zelkatani.model.ModelBuilder
import com.zelkatani.visitor.map.RegionVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * A model for `region.txt`. Stores all region [definitions] as a map.
 */
data class Region(val definitions: Map<String, ProvinceList>) {
    companion object : ModelBuilder<Region> {
        override fun from(file: File): Region {
            val regionLexer = RegionLexer(CharStreams.fromReader(file.reader()))
            val regionParser = RegionParser(CommonTokenStream(regionLexer))

            val regionContext = regionParser.region()
            val regionVisitor = RegionVisitor()

            return regionVisitor.visitRegion(regionContext)
        }
    }
}

/**
 * A list of provinces.
 */
typealias ProvinceList = List<Int>
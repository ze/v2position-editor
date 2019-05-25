package com.zelkatani.model

import com.zelkatani.antlr.RegionLexer
import com.zelkatani.antlr.RegionParser
import com.zelkatani.visitor.RegionVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * Regions model that holds all region [definitions] for region.txt.
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

typealias ProvinceList = List<Int>
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
data class Regions(val definitions: Map<String, Region>) {
    companion object : ModelBuilder<Regions> {
        override fun from(file: File): Regions {
            val regionLexer = RegionLexer(CharStreams.fromReader(file.reader()))
            val regionParser = RegionParser(CommonTokenStream(regionLexer))

            val regionContext = regionParser.region()
            val regionVisitor = RegionVisitor()

            return regionVisitor.visitRegion(regionContext)
        }

    }
}

typealias Region = List<Int>
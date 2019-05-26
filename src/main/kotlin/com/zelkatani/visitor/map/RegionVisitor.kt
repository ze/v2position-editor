package com.zelkatani.visitor.map

import com.zelkatani.MultiException
import com.zelkatani.antlr.RegionBaseVisitor
import com.zelkatani.antlr.RegionParser
import com.zelkatani.model.map.ProvinceList
import com.zelkatani.model.map.Region
import com.zelkatani.requireNoExceptions
import com.zelkatani.visitor.asInt
import com.zelkatani.visitor.line
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Visitor for region.txt parser.
 */
class RegionVisitor : RegionBaseVisitor<Any>() {
    private val regions = hashMapOf<String, ProvinceList>()
    private val provincesAndOwner = hashMapOf<Int, String>()

    private val multiException = MultiException()

    override fun visitRegion(ctx: RegionParser.RegionContext): Region {
        val regionData = ctx.regionData()
        regionData.forEach(::visitRegionData)

        requireNoExceptions(multiException)

        return Region(regions)
    }

    override fun visitRegionData(ctx: RegionParser.RegionDataContext) {
        val name = ctx.IDENTIFIER().text
        if (name in regions) {
            multiException += "Duplicate region definition: $name" to ctx.line
        }

        val items = ctx.INT().map(TerminalNode::asInt)

        val duplicates = items.filter {
            it in provincesAndOwner
        }

        duplicates.forEach {
            multiException += "Province $it is found in both region '$name' and '${provincesAndOwner[it]}'." to ctx.line
        }

        regions[name] = items
    }
}
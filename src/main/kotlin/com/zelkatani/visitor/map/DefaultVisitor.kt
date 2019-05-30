package com.zelkatani.visitor.map

import com.zelkatani.MultiException
import com.zelkatani.antlr.DefaultBaseVisitor
import com.zelkatani.antlr.DefaultParser
import com.zelkatani.model.map.Default
import com.zelkatani.requireNoExceptions
import com.zelkatani.visitor.asFloat
import com.zelkatani.visitor.asInt
import com.zelkatani.visitor.asUnquotedString
import com.zelkatani.visitor.line
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * A visitor for "default.map".
 */
class DefaultVisitor : DefaultBaseVisitor<Any>() {

    // not lateinit vars since we have primitives
    private var maxProvinces: Int? = null
    private var seaStarts: List<Int>? = null
    private var definitions: String? = null
    private var provinces: String? = null
    private var positions: String? = null
    private var terrain: String? = null
    private var rivers: String? = null
    private var terrainDefinition: String? = null
    private var treeDefinition: String? = null
    private var continent: String? = null
    private var adjacencies: String? = null
    private var region: String? = null
    private var regionSea: String? = null
    private var provinceFlagSprites: String? = null
    private var borderHeights: List<Int>? = null
    private var terrainSheetHeights: Int? = null
    private var tree: Int? = null
    private var borderCutoff: Float? = null

    override fun visitDefaults(ctx: DefaultParser.DefaultsContext): Default {
        val multiException = MultiException()

        val defaultData = ctx.defaultData()
        defaultData.forEach {
            visit(it)
        }

        val line = ctx.line
        maxProvinces ?: multiException.add("max_provinces is not defined.", line)
        seaStarts ?: multiException.add("sea_starts is not defined.", line)
        definitions ?: multiException.add("definition path is not defined.", line)
        provinces ?: multiException.add("provinces.bmp path is not defined.", line)
        positions ?: multiException.add("positions path is not defined.", line)
        terrain ?: multiException.add("terrain.bmp path is not defined.", line)
        rivers ?: multiException.add("rivers.bmp path is not defined.", line)
        terrainDefinition ?: multiException.add("terrain definition path is not defined.", line)
        treeDefinition ?: multiException.add("tree definition path is not defined.", line)
        continent ?: multiException.add("continent path is not defined.", line)
        adjacencies ?: multiException.add("adjacencies.csv path is not defined.", line)
        region ?: multiException.add("region path is not defined.", line)
        regionSea ?: multiException.add("region_sea path is not defined.", line)
        provinceFlagSprites ?: multiException.add("province_flag_sprite folder path is not defined.", line)
        borderHeights ?: multiException.add("border_heights is not defined.", line)
        terrainSheetHeights ?: multiException.add("terrain_sheet_heights is not defined.", line)
        tree ?: multiException.add("tree is not defined.", line)
        borderCutoff ?: multiException.add("border_cutoff is not defined.", line)

        requireNoExceptions(multiException)

        return Default(
            maxProvinces!!,
            seaStarts!!,
            definitions!!,
            provinces!!,
            positions!!,
            terrain!!,
            rivers!!,
            terrainDefinition!!,
            treeDefinition!!,
            continent!!,
            adjacencies!!,
            region!!,
            regionSea!!,
            provinceFlagSprites!!,
            borderHeights!!,
            terrainSheetHeights!!,
            tree!!,
            borderCutoff!!
        )
    }

    override fun visitMaxProvincesExpr(ctx: DefaultParser.MaxProvincesExprContext) {
        maxProvinces = ctx.INT().asInt()
    }

    override fun visitSeaStartsBlock(ctx: DefaultParser.SeaStartsBlockContext) {
        seaStarts = ctx.INT().map(TerminalNode::asInt)
    }

    override fun visitDefinitionsExpr(ctx: DefaultParser.DefinitionsExprContext) {
        definitions = ctx.STRING().asUnquotedString()
    }

    override fun visitProvincesExpr(ctx: DefaultParser.ProvincesExprContext) {
        provinces = ctx.STRING().asUnquotedString()
    }

    override fun visitPositionsExpr(ctx: DefaultParser.PositionsExprContext) {
        positions = ctx.STRING().asUnquotedString()
    }

    override fun visitTerrainExpr(ctx: DefaultParser.TerrainExprContext) {
        terrain = ctx.STRING().asUnquotedString()
    }

    override fun visitRiversExpr(ctx: DefaultParser.RiversExprContext) {
        rivers = ctx.STRING().asUnquotedString()
    }

    override fun visitTerrainDefinitionExpr(ctx: DefaultParser.TerrainDefinitionExprContext) {
        terrainDefinition = ctx.STRING().asUnquotedString()
    }

    override fun visitTreeDefinitionExpr(ctx: DefaultParser.TreeDefinitionExprContext) {
        treeDefinition = ctx.STRING().asUnquotedString()
    }

    override fun visitContinentExpr(ctx: DefaultParser.ContinentExprContext) {
        continent = ctx.STRING().asUnquotedString()
    }

    override fun visitAdjacenciesExpr(ctx: DefaultParser.AdjacenciesExprContext) {
        adjacencies = ctx.STRING().asUnquotedString()
    }

    override fun visitRegionExpr(ctx: DefaultParser.RegionExprContext) {
        region = ctx.STRING().asUnquotedString()
    }

    override fun visitRegionSeaExpr(ctx: DefaultParser.RegionSeaExprContext) {
        regionSea = ctx.STRING().asUnquotedString()
    }

    override fun visitProvinceFlagSpriteExpr(ctx: DefaultParser.ProvinceFlagSpriteExprContext) {
        provinceFlagSprites = ctx.STRING().asUnquotedString()
    }

    override fun visitBorderHeightsBlock(ctx: DefaultParser.BorderHeightsBlockContext) {
        borderHeights = ctx.INT().map(TerminalNode::asInt)
    }

    override fun visitTerrainSheetHeighstBlock(ctx: DefaultParser.TerrainSheetHeighstBlockContext) {
        terrainSheetHeights = ctx.INT().asInt()
    }

    override fun visitTreeExpr(ctx: DefaultParser.TreeExprContext) {
        tree = ctx.INT().asInt()
    }

    override fun visitBorderCutoffExpr(ctx: DefaultParser.BorderCutoffExprContext) {
        borderCutoff = (if (ctx.FLOAT() != null) {
            ctx.FLOAT()
        } else {
            ctx.INT()
        }).asFloat()
    }
}
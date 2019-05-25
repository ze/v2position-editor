package com.zelkatani.visitor

import com.zelkatani.MultiException
import com.zelkatani.antlr.DefaultBaseVisitor
import com.zelkatani.antlr.DefaultParser
import com.zelkatani.model.Default
import com.zelkatani.requireNoExceptions
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * A visitor for "default.map".
 */
class DefaultVisitor : DefaultBaseVisitor<Any>() {

    var maxProvinces: Int? = null
    var seaStarts: List<Int>? = null
    var definitions: String? = null
    var provinces: String? = null
    var positions: String? = null
    var terrain: String? = null
    var rivers: String? = null
    var terrainDefinition: String? = null
    var treeDefinition: String? = null
    var continent: String? = null
    var adjacencies: String? = null
    var region: String? = null
    var regionSea: String? = null
    var provinceFlagSprites: String? = null
    var borderHeights: Pair<Int, Int>? = null
    var terrainSheetHeights: Int? = null
    var tree: Int? = null
    var borderCutoff: Float? = null

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
        borderHeights = ctx.INT(0).asInt() to ctx.INT(1).asInt()
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
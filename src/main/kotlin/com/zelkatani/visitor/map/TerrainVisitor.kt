package com.zelkatani.visitor.map

import com.zelkatani.MultiException
import com.zelkatani.antlr.TerrainBaseVisitor
import com.zelkatani.antlr.TerrainParser
import com.zelkatani.antlr.TerrainParser.FLOAT
import com.zelkatani.antlr.TerrainParser.INT
import com.zelkatani.model.Modifier
import com.zelkatani.model.map.Terrain
import com.zelkatani.model.map.TerrainInfo
import com.zelkatani.model.map.TerrainType
import com.zelkatani.requireNoExceptions
import com.zelkatani.visitor.asInt
import com.zelkatani.visitor.getNumber
import com.zelkatani.visitor.line
import javafx.scene.paint.Color
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * A visitor for `terrain.txt`.
 */
class TerrainVisitor : TerrainBaseVisitor<Any>() {
    private val definitions = hashMapOf<String, TerrainInfo>()
    private val terrainTGAMapping = hashMapOf<String, TerrainType>()
    private val multiException = MultiException()

    override fun visitTerrain(ctx: TerrainParser.TerrainContext): Terrain {
        val terrainExpr = ctx.terrainExpr(0)
        val number = visitTerrainExpr(terrainExpr)

        val terrainTGAData = ctx.terrainTGAData()
        terrainTGAData.forEach(::visitTerrainTGAData)

        val categoriesBlock = ctx.categoriesBlock(0)
        visitCategoriesBlock(categoriesBlock)

        requireNoExceptions(multiException)

        return Terrain(number, definitions, terrainTGAMapping)
    }

    override fun visitTerrainExpr(ctx: TerrainParser.TerrainExprContext) = ctx.INT().asInt()

    override fun visitTerrainTGAData(ctx: TerrainParser.TerrainTGADataContext) {
        val name = ctx.IDENTIFIER().text

        val typeExpr = ctx.typeExpr(0)
        val type = visitTypeExpr(typeExpr)

        val colorBlock = ctx.colorBlock(0)
        val colorList = visitColorBlock(colorBlock)

        val modifierExpr = ctx.modifierExpr()
        val modifiers = modifierExpr.map(::visitModifierExpr)

        terrainTGAMapping[name] = TerrainType(type, colorList, modifiers)
    }

    override fun visitTypeExpr(ctx: TerrainParser.TypeExprContext): String = ctx.IDENTIFIER().text
    override fun visitModifierExpr(ctx: TerrainParser.ModifierExprContext): Modifier {
        return visit(ctx.getChild(0)) as Modifier
    }

    override fun visitPriorityExpr(ctx: TerrainParser.PriorityExprContext): Modifier {
        val name = ctx.PRIORITY().text
        val value = ctx.INT().asInt()
        return Modifier(name, value)
    }

    override fun visitHasTextureExpr(ctx: TerrainParser.HasTextureExprContext): Modifier {
        val name = ctx.HAS_TEXTURE().text
        val value = visitAffirmative(ctx.affirmative())
        return Modifier(name, value)
    }

    override fun visitAffirmative(ctx: TerrainParser.AffirmativeContext): Boolean {
        return ctx.YES() != null
    }

    override fun visitCategoriesBlock(ctx: TerrainParser.CategoriesBlockContext) {
        val categoriesData = ctx.categoriesData()
        categoriesData.forEach(::visitCategoriesData)
    }

    override fun visitCategoriesData(ctx: TerrainParser.CategoriesDataContext) {
        val name = ctx.IDENTIFIER().text

        val colorBlock = ctx.colorBlock(0)

        val colorList = visitColorBlock(colorBlock)
        if (colorList.size != 3) {
            multiException += "Color must have three values." to ctx.line
        }

        if (colorList.size < 3) {
            return // Avoid IndexOutOfBounds.
        }

        val colorFloat = colorList.map { it / 255.0 }
        val asColor = Color(colorFloat[0], colorFloat[1], colorFloat[2], 1.0)

        val categories = ctx.categoriesExpr()
        val modifiers = categories.map(::visitCategoriesExpr)

        definitions[name] = TerrainInfo(asColor, modifiers)
    }

    override fun visitCategoriesExpr(ctx: TerrainParser.CategoriesExprContext): Modifier {
        return visit(ctx.getChild(0)) as Modifier
    }

    override fun visitMovementCostExpr(ctx: TerrainParser.MovementCostExprContext): Modifier =
        Modifier(ctx.MOVEMENT_COST(), ctx.getNumber())

    override fun visitAssimilationRateExpr(ctx: TerrainParser.AssimilationRateExprContext): Modifier =
        Modifier(ctx.ASSIMILATION_RATE(), ctx.getNumber())

    override fun visitAttritionExpr(ctx: TerrainParser.AttritionExprContext): Modifier =
        Modifier(ctx.ATTRITION(), ctx.getNumber())

    override fun visitCombatWidthExpr(ctx: TerrainParser.CombatWidthExprContext): Modifier =
        Modifier(ctx.COMBAT_WIDTH(), ctx.getNumber())

    override fun visitDefenceExpr(ctx: TerrainParser.DefenceExprContext): Modifier =
        Modifier(ctx.DEFENCE(), ctx.getNumber())

    override fun visitFarmRGOEffExpr(ctx: TerrainParser.FarmRGOEffExprContext): Modifier =
        Modifier(ctx.FARM_RGO_EFF(), ctx.getNumber())

    override fun visitFarmRGOSizeExpr(ctx: TerrainParser.FarmRGOSizeExprContext): Modifier =
        Modifier(ctx.FARM_RGO_SIZE(), ctx.getNumber())

    override fun visitImmigrantAttractExpr(ctx: TerrainParser.ImmigrantAttractExprContext): Modifier =
        Modifier(ctx.IMMIGRANT_ATTRACT(), ctx.getNumber())

    override fun visitIsWaterExpr(ctx: TerrainParser.IsWaterExprContext): Modifier =
        Modifier(ctx.IS_WATER(), visitAffirmative(ctx.affirmative()))

    override fun visitMinBuildFortExpr(ctx: TerrainParser.MinBuildFortExprContext): Modifier =
        Modifier(ctx.MIN_BUILD_FORT(), ctx.INT().asInt())

    override fun visitMinBuildNavalBaseExpr(ctx: TerrainParser.MinBuildNavalBaseExprContext): Modifier =
        Modifier(ctx.MIN_BUILD_NAVAL_BASE(), ctx.INT().asInt())

    override fun visitMinBuildRailroadExpr(ctx: TerrainParser.MinBuildRailroadExprContext): Modifier =
        Modifier(ctx.MIN_BUILD_RAILROAD(), ctx.INT().asInt())

    override fun visitMineRGOEffExpr(ctx: TerrainParser.MineRGOEffExprContext): Modifier =
        Modifier(ctx.MINE_RGO_EFF(), ctx.getNumber())

    override fun visitMineRGOSizeExpr(ctx: TerrainParser.MineRGOSizeExprContext): Modifier =
        Modifier(ctx.MINE_RGO_SIZE(), ctx.getNumber())

    override fun visitSupplyLimitExpr(ctx: TerrainParser.SupplyLimitExprContext): Modifier =
        Modifier(ctx.SUPPLY_LIMIT(), ctx.INT().asInt())

    override fun visitColorBlock(ctx: TerrainParser.ColorBlockContext): List<Int> = ctx.INT().map(TerminalNode::asInt)

    private fun ParserRuleContext.getNumber() = getNumber(INT, FLOAT)
}
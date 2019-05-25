package com.zelkatani.visitor

import com.zelkatani.MultiException
import com.zelkatani.antlr.ClimateBaseVisitor
import com.zelkatani.antlr.ClimateParser
import com.zelkatani.antlr.ClimateParser.FLOAT
import com.zelkatani.antlr.ClimateParser.INT
import com.zelkatani.model.Climate
import com.zelkatani.model.Modifier
import com.zelkatani.model.Modifiers
import com.zelkatani.requireNoExceptions
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

class ClimateVisitor : ClimateBaseVisitor<Any>() {
    private val modifiers = HashMap<String, Modifiers>()
    private val provinces = HashMap<String, List<Int>>()
    private val multiException = MultiException()

    override fun visitClimate(ctx: ClimateParser.ClimateContext): Climate {
        ctx.climateData().map(::visitClimateData)

        if (modifiers.keys != provinces.keys) {
            multiException += "Every climate must have modifiers and a list of provinces defined." to ctx.line
        }

        requireNoExceptions(multiException)

        return Climate(modifiers, provinces)
    }

    override fun visitClimateData(ctx: ClimateParser.ClimateDataContext) {
        if (ctx.climateLandBlock() != null) {
            visitClimateLandBlock(ctx.climateLandBlock())
        } else {
            visitClimateProvincesBlock(ctx.climateProvincesBlock())
        }
    }

    override fun visitClimateLandBlock(ctx: ClimateParser.ClimateLandBlockContext) {
        val climate = ctx.IDENTIFIER().toString()
        if (climate in modifiers) {
            multiException += "`$climate` has a duplicate modifier definition" to ctx.line
        } else {
            val landData = ctx.landData().map(::visitLandData)
            modifiers[climate] = landData
        }
    }

    override fun visitClimateProvincesBlock(ctx: ClimateParser.ClimateProvincesBlockContext) {
        val climate = ctx.IDENTIFIER().toString()
        if (climate in provinces) {
            multiException += "`$climate` has a duplicate province list definition" to ctx.line
        } else {
            val provs = ctx.INT().map(TerminalNode::asInt)
            provinces[climate] = provs
        }
    }

    override fun visitLandData(ctx: ClimateParser.LandDataContext) =
        visit(ctx.getChild(0)) as Modifier

    override fun visitFarmRGOSizeExpr(ctx: ClimateParser.FarmRGOSizeExprContext) =
        Modifier(ctx.FARM_RGO_SIZE(), ctx.getNumber())

    override fun visitFarmRGOEffExpr(ctx: ClimateParser.FarmRGOEffExprContext) =
        Modifier(ctx.FARM_RGO_EFF(), ctx.getNumber())

    override fun visitMineRGOSizeExpr(ctx: ClimateParser.MineRGOSizeExprContext) =
        Modifier(ctx.MINE_RGO_SIZE(), ctx.getNumber())

    override fun visitMineRGOEffExpr(ctx: ClimateParser.MineRGOEffExprContext) =
        Modifier(ctx.MINE_RGO_EFF(), ctx.getNumber())

    override fun visitMaxAttritionExpr(ctx: ClimateParser.MaxAttritionExprContext) =
        Modifier(ctx.MAX_ATTRITION(), ctx.getNumber())

    private fun ParserRuleContext.getNumber() = getNumber(INT, FLOAT)
}
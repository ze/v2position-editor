package com.zelkatani.visitor

import com.zelkatani.MultiException
import com.zelkatani.antlr.ContinentBaseVisitor
import com.zelkatani.antlr.ContinentParser
import com.zelkatani.antlr.ContinentParser.FLOAT
import com.zelkatani.antlr.ContinentParser.INT
import com.zelkatani.model.ContinentInfo
import com.zelkatani.model.Continents
import com.zelkatani.model.Modifier
import com.zelkatani.requireNoExceptions
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Visitor for continents.txt parser.
 */
class ContinentVisitor : ContinentBaseVisitor<Any>() {
    private val multiException = MultiException()

    override fun visitContinents(ctx: ContinentParser.ContinentsContext): Continents {
        val definitions = hashMapOf<String, ContinentInfo>()

        val continentData = ctx.continentData()
        continentData.forEach {
            val visit = visitContinentData(it)
            val first = visit.first

            if (first in definitions) {
                multiException += "Duplicate continent definition `$first`" to ctx.line
            }

            definitions[first] = visit.second
        }

        requireNoExceptions(multiException)

        return Continents(definitions)
    }

    override fun visitContinentData(ctx: ContinentParser.ContinentDataContext): Pair<String, ContinentInfo> {
        val pbc = ctx.provincesBlock()
        val cec = ctx.continentExpr() // TODO test if duplicates break or override.

        val line = ctx.line
        if (pbc.size > 1) multiException += "Too many province definitions" to line
        if (pbc.size == 0) multiException += "No province definitions" to line

        val provinces = visitProvincesBlock(pbc[0])
        val effects = cec.map(::visitContinentExpr)

        val continentName = ctx.IDENTIFIER().text

        return continentName to ContinentInfo(provinces, effects)
    }

    override fun visitProvincesBlock(ctx: ContinentParser.ProvincesBlockContext) =
        ctx.INT().map(TerminalNode::asInt)

    override fun visitContinentExpr(ctx: ContinentParser.ContinentExprContext) =
        visit(ctx.getChild(0)) as Modifier

    override fun visitAssimilationRateExpr(ctx: ContinentParser.AssimilationRateExprContext) =
        Modifier(ctx.ASSIMILATION_RATE(), ctx.getNumber())

    override fun visitFarmRGOSizeExpr(ctx: ContinentParser.FarmRGOSizeExprContext) =
        Modifier(ctx.FARM_RGO_SIZE(), ctx.getNumber())

    override fun visitMineRGOSizeExpr(ctx: ContinentParser.MineRGOSizeExprContext) =
        Modifier(ctx.MINE_RGO_SIZE(), ctx.getNumber())

    private fun ParserRuleContext.getNumber() = getNumber(INT, FLOAT)
}
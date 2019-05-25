package com.zelkatani.visitor

import com.zelkatani.MultiException
import com.zelkatani.antlr.PositionsBaseVisitor
import com.zelkatani.antlr.PositionsParser
import com.zelkatani.antlr.PositionsParser.FLOAT
import com.zelkatani.antlr.PositionsParser.INT
import com.zelkatani.model.*
import com.zelkatani.model.ObjectCoordinate.ObjectType
import com.zelkatani.requireNoExceptions
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

class PositionsVisitor : PositionsBaseVisitor<Any>() {
    private val positions = HashMap<Int, PositionData>(5000)
    private val multiException = MultiException()

    override fun visitPositions(ctx: PositionsParser.PositionsContext): Positions {
        ctx.positionData().map(::visitPositionData)

        requireNoExceptions(multiException)

        return Positions(positions)
    }

    override fun visitPositionData(ctx: PositionsParser.PositionDataContext) {
        val province = ctx.INT().asInt()
        if (province in positions) {
            multiException += "`$province` has a duplicate definition" to ctx.line
        } else {
            val positionData = ctx.positionExpr().map(::visitPositionExpr)
            positions[province] = positionData
        }
    }

    override fun visitPositionExpr(ctx: PositionsParser.PositionExprContext) =
            visit(ctx.getChild(0)) as PositionInfo

    override fun visitUnitPositionBlock(ctx: PositionsParser.UnitPositionBlockContext) =
            ObjectCoordinate(ObjectType.UNIT, visitCoordinate(ctx.coordinate()))

    override fun visitTextPositionBlock(ctx: PositionsParser.TextPositionBlockContext) =
            ObjectCoordinate(ObjectType.TEXT, visitCoordinate(ctx.coordinate()))

    override fun visitBuildingPositionBlock(ctx: PositionsParser.BuildingPositionBlockContext) =
        BuildingPosition(ctx.objectPositionBlock().map(::visitObjectPositionBlock))

    override fun visitObjectPositionBlock(ctx: PositionsParser.ObjectPositionBlockContext) =
        visit(ctx.getChild(0)) as BuildingPositionData

    override fun visitFortPositionBlock(ctx: PositionsParser.FortPositionBlockContext) =
        BuildingPositionData(BuildingPositionData.PositionType.FORT, visitCoordinate(ctx.coordinate()))

    override fun visitNavalBasePositionBlock(ctx: PositionsParser.NavalBasePositionBlockContext) =
        BuildingPositionData(BuildingPositionData.PositionType.NAVAL_BASE, visitCoordinate(ctx.coordinate()))

    override fun visitRailroadPositionBlock(ctx: PositionsParser.RailroadPositionBlockContext) =
        BuildingPositionData(BuildingPositionData.PositionType.RAILROAD, visitCoordinate(ctx.coordinate()))

    override fun visitBuildingConstructionBlock(ctx: PositionsParser.BuildingConstructionBlockContext) =
        ObjectCoordinate(ObjectType.BUILDING_CONSTRUCTION, visitCoordinate(ctx.coordinate()))

    override fun visitBuildingNudgeBlock(ctx: PositionsParser.BuildingNudgeBlockContext) =
        BuildingNudgeBlock(ctx.objectValueExpr().map(::visitObjectValueExpr))

    override fun visitMilitaryConstructionBlock(ctx: PositionsParser.MilitaryConstructionBlockContext) =
        ObjectCoordinate(ObjectType.MILITARY_CONSTRUCTION, visitCoordinate(ctx.coordinate()))

    override fun visitSpawnRailwayTrackBlock(ctx: PositionsParser.SpawnRailwayTrackBlockContext) =
        SpawnRailwayTrack(ctx.railwayTrackData().map(::visitRailwayTrackData))

    override fun visitRailwayTrackData(ctx: PositionsParser.RailwayTrackDataContext) =
        visitCoordinate(ctx.coordinate())

    override fun visitRailroadVisibilityBlock(ctx: PositionsParser.RailroadVisibilityBlockContext) =
        RailroadVisibility(ctx.INT().map(TerminalNode::asInt))

    override fun visitFactoryBlock(ctx: PositionsParser.FactoryBlockContext) =
        ObjectCoordinate(ObjectType.FACTORY, visitCoordinate(ctx.coordinate()))


    override fun visitBuildingRotationBlock(ctx: PositionsParser.BuildingRotationBlockContext) =
        BuildingRotation(ctx.objectValueExpr().map(::visitObjectValueExpr))

    @Suppress("UNCHECKED_CAST")
    override fun visitObjectValueExpr(ctx: PositionsParser.ObjectValueExprContext) =
        visit(ctx.getChild(0)) as BuildingTransform

    override fun visitFortValueExpr(ctx: PositionsParser.FortValueExprContext) =
        BuildingTransform(BuildingType.FORT, ctx.getNumber())

    override fun visitNavalBaseValueExpr(ctx: PositionsParser.NavalBaseValueExprContext) =
        BuildingTransform(BuildingType.NAVAL_BASE, ctx.getNumber())

    override fun visitRailroadValueExpr(ctx: PositionsParser.RailroadValueExprContext) =
        BuildingTransform(BuildingType.RAILROAD, ctx.getNumber())

    override fun visitAeroplaneFactoryValueExpr(ctx: PositionsParser.AeroplaneFactoryValueExprContext) =
        BuildingTransform(BuildingType.AEROPLANE_FACTORY, ctx.getNumber())

    override fun visitTextRotationExpr(ctx: PositionsParser.TextRotationExprContext) =
        TextRotation(ctx.getNumber())

    override fun visitTextScaleExpr(ctx: PositionsParser.TextScaleExprContext) =
        TextScale(ctx.getNumber())

    override fun visitCityBlock(ctx: PositionsParser.CityBlockContext) =
        ObjectCoordinate(ObjectType.CITY, visitCoordinate(ctx.coordinate()))


    override fun visitTownBlock(ctx: PositionsParser.TownBlockContext) =
        ObjectCoordinate(ObjectType.TOWN, visitCoordinate(ctx.coordinate()))

    override fun visitCoordinate(ctx: PositionsParser.CoordinateContext): Coordinate {
        val x = visitXPosition(ctx.xPosition().last())
        val y = visitYPosition(ctx.yPosition().last())
        return Coordinate(x, y)
    }

    override fun visitXPosition(ctx: PositionsParser.XPositionContext) =
        ctx.getNumber().toFloat()

    override fun visitYPosition(ctx: PositionsParser.YPositionContext) =
        ctx.getNumber().toFloat()

    private fun ParserRuleContext.getNumber() = getNumber(INT, FLOAT)
}
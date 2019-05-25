package com.zelkatani.model

import com.zelkatani.antlr.PositionsLexer
import com.zelkatani.antlr.PositionsParser
import com.zelkatani.visitor.PositionsVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

typealias Coordinate = Pair<Float, Float>

data class Positions(val positions: Map<Int, PositionData>) {
    companion object : ModelBuilder<Positions> {
        override fun from(file: File): Positions {
            val positionsLexer = PositionsLexer(CharStreams.fromReader(file.reader()))
            val positionsParser = PositionsParser(CommonTokenStream(positionsLexer))

            val positionsContext = positionsParser.positions()
            val positionsVisitor = PositionsVisitor()

            return positionsVisitor.visitPositions(positionsContext)
        }
    }
}

typealias PositionData = List<PositionInfo>

sealed class PositionInfo

class ObjectCoordinate(val type: ObjectType, val coordinate: Coordinate) : PositionInfo() {
    enum class ObjectType {
        UNIT, TEXT, BUILDING_CONSTRUCTION, MILITARY_CONSTRUCTION, FACTORY, CITY, TOWN
    }
}

typealias BuildingTransform = Pair<BuildingType, Number>

class BuildingNudgeBlock(val transforms: List<BuildingTransform>) : PositionInfo()
class BuildingRotation(val transforms: List<BuildingTransform>) : PositionInfo()

enum class BuildingType {
    FORT, NAVAL_BASE, RAILROAD, AEROPLANE_FACTORY
}

class RailroadVisibility(val visibilities: List<Int>) : PositionInfo()
class BuildingPosition(val positions: List<BuildingPositionData>) : PositionInfo()
class SpawnRailwayTrack(val coordinates: List<Coordinate>) : PositionInfo()

class TextRotation(val rotation: Number) : PositionInfo()
class TextScale(val scale: Number) : PositionInfo()

data class BuildingPositionData(val positionType: PositionType, val coordinate: Coordinate) {
    enum class PositionType {
        FORT, NAVAL_BASE, RAILROAD
    }
}


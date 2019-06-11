package com.zelkatani.model.map

import com.zelkatani.antlr.PositionsLexer
import com.zelkatani.antlr.PositionsParser
import com.zelkatani.model.ModelBuilder
import com.zelkatani.visitor.map.PositionsVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

typealias Coordinate = Pair<Double, Double>

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

    operator fun get(provinceId: Int) = positions[provinceId]
}

typealias PositionData = List<PositionInfo>

sealed class PositionInfo

data class ObjectCoordinate(val type: ObjectType, val coordinate: Coordinate) : PositionInfo() {
    enum class ObjectType {
        UNIT, TEXT, BUILDING_CONSTRUCTION, MILITARY_CONSTRUCTION, FACTORY, CITY, TOWN
    }
}

typealias BuildingTransform = Pair<BuildingType, Double>

data class BuildingNudgeBlock(val transforms: List<BuildingTransform>) : PositionInfo()
data class BuildingRotation(val transforms: List<BuildingTransform>) : PositionInfo()

enum class BuildingType {
    FORT, NAVAL_BASE, RAILROAD, AEROPLANE_FACTORY
}

data class RailroadVisibility(val visibilities: List<Int>) : PositionInfo()
data class BuildingPosition(val positions: List<BuildingPositionData>) : PositionInfo()
data class SpawnRailwayTrack(val coordinates: List<Coordinate>) : PositionInfo()

data class TextRotation(val rotation: Double) : PositionInfo()
data class TextScale(val scale: Double) : PositionInfo()

data class BuildingPositionData(val positionType: PositionType, val coordinate: Coordinate) {
    enum class PositionType {
        FORT, NAVAL_BASE, RAILROAD
    }
}


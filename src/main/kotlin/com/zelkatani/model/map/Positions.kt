package com.zelkatani.model.map

import com.zelkatani.antlr.PositionsLexer
import com.zelkatani.antlr.PositionsParser
import com.zelkatani.model.ModelBuilder
import com.zelkatani.model.map.BuildingPositionData.PositionType
import com.zelkatani.model.map.ObjectCoordinate.ObjectType
import com.zelkatani.visitor.map.PositionsVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * A coordinate point for (x, y) bound double grids.
 */
typealias Coordinate = Pair<Double, Double>

/**
 * A model for `positions.txt`.
 */
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

    /**
     * Get a [PositionData] from a [provinceId].
     */
    operator fun get(provinceId: Int) = positions[provinceId]
}

/**
 * A list of [PositionInfo]
 */
typealias PositionData = List<PositionInfo>

/**
 * All varying types of position information.
 */
sealed class PositionInfo

/**
 * A [coordinate] for various [ObjectType]'s.
 */
data class ObjectCoordinate(val type: ObjectType, val coordinate: Coordinate) : PositionInfo() {
    enum class ObjectType {
        UNIT, TEXT, BUILDING_CONSTRUCTION, MILITARY_CONSTRUCTION, FACTORY, CITY, TOWN
    }
}

/**
 * A transform of [BuildingType] to [Double] values.
 */
typealias BuildingTransform = Pair<BuildingType, Double>

/**
 * Building nudge data.
 */
data class BuildingNudgeBlock(val transforms: List<BuildingTransform>) : PositionInfo()

/**
 * Buulding rotation data.
 */
data class BuildingRotation(val transforms: List<BuildingTransform>) : PositionInfo()

/**
 * Buildings that can be acted upon by a rotation or nudge.
 */
enum class BuildingType {
    FORT, NAVAL_BASE, RAILROAD, AEROPLANE_FACTORY
}

/**
 * Railroad visibility data.
 */
data class RailroadVisibility(val visibilities: List<Int>) : PositionInfo()

/**
 * Building positions data. A collection of [positions].
 */
data class BuildingPosition(val positions: List<BuildingPositionData>) : PositionInfo()

/**
 * Coordinate data for railway track spawns.
 */
data class SpawnRailwayTrack(val coordinates: List<Coordinate>) : PositionInfo()

/**
 * Text rotation data.
 */
data class TextRotation(val rotation: Double) : PositionInfo()

/**
 * Text scale data.
 */
data class TextScale(val scale: Double) : PositionInfo()

/**
 * Position data [coordinate] for various [PositionType]'s.
 */
data class BuildingPositionData(val positionType: PositionType, val coordinate: Coordinate) {
    enum class PositionType {
        FORT, NAVAL_BASE, RAILROAD
    }
}


package com.zelkatani.model.map

import com.zelkatani.antlr.PositionsLexer
import com.zelkatani.antlr.PositionsParser
import com.zelkatani.model.ModelBuilder
import com.zelkatani.model.map.BuildingPositionData.PositionType
import com.zelkatani.model.map.ObjectCoordinate.ObjectType
import com.zelkatani.model.map.Positions.Companion.format
import com.zelkatani.visitor.map.PositionsVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * A model for `positions.txt`.
 */
data class Positions(val positions: MutableMap<Int, PositionData>) {
    companion object : ModelBuilder<Positions> {
        private lateinit var file: File

        override fun from(file: File): Positions {
            this.file = file

            val positionsLexer = PositionsLexer(CharStreams.fromReader(file.reader()))
            val positionsParser = PositionsParser(CommonTokenStream(positionsLexer))

            val positionsContext = positionsParser.positions()
            val positionsVisitor = PositionsVisitor()

            return positionsVisitor.visitPositions(positionsContext)
        }

        /**
         * Export a [Positions] to the [file] that was read from.
         *
         * @param positions The positions data to export.
         */
        fun toFile(positions: Positions) {
            file.writeText(positions.toString())
        }

        /**
         * A converter for numbers that are good for `positions.txt`.
         * Scientific notation cannot be used no matter what, and the locale must be English since some locales use commas.
         * Four digits are all that is needed since any extra value is either impreciseness or negligible.
         *
         * Furthermore, file size is reduced by removing the need for "xyz" to display always as "xyz.0".
         */
        val format = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).apply {
            isDecimalSeparatorAlwaysShown = false
            isGroupingUsed = false
            maximumFractionDigits = 4
        }
    }

    /**
     * Get a [PositionData] from a [provinceId].
     */
    operator fun get(provinceId: Int) = positions[provinceId]

    /**
     * Set [positionData] to [provinceId].
     */
    operator fun set(provinceId: Int, positionData: PositionData) {
        positions[provinceId] = positionData
    }

    override fun toString() = buildString {
        positions.forEach { (prov, data) ->
            if (data.isNotEmpty()) {
                append(prov).appendlf(" = {")
                data.forEach {
                    appendlf(it.toPositionString(OFFSET_STEP))
                }
                appendlf("}")
            }
        }
    }
}

/**
 * A coordinate point for (x, y) bound double grids.
 */
typealias Coordinate = Pair<Double, Double>

/**
 * The quantity of spaces for each level of indentation.
 */
private const val OFFSET_STEP = 2

/**
 * Repeat the string containing just a space n times.
 */
private fun n(offset: Int) = " ".repeat(offset)

/**
 * Append a unix linefeed onto a normal string. This is necessary since [appendln] can append a Windows linefeed.
 */
private fun StringBuilder.appendlf(s: String) = append(s).append('\n')

/**
 * Convert a [Coordinate] to a string for `positions.txt`.
 * This assumes the starting '{' is next to an '=' with one spacing between them.
 *
 * @see PositionInfo.toPositionString
 */
private fun Coordinate.toPositionString(offset: Int) = buildString {
    val inner = n(offset + OFFSET_STEP)
    val outer = n(offset)

    appendlf("{")
    append(inner).append("x = ").appendlf(format.format(first))
    append(inner).append("y = ").appendlf(format.format(second))
    append(outer).append("}")
}

/**
 * A list of [PositionInfo]
 */
typealias PositionData = List<PositionInfo>

/**
 * All varying types of position information.
 */
sealed class PositionInfo {
    /**
     * Construct a string for `positions.txt`. The [String] should not end with a newline.
     *
     * @param offset The amount of indentation before every new line
     * @return A string that can be written to `positions.txt`.
     */
    abstract fun toPositionString(offset: Int): String
}

/**
 * A [coordinate] for various [ObjectType]'s.
 */
data class ObjectCoordinate(val type: ObjectType, val coordinate: Coordinate) : PositionInfo() {
    enum class ObjectType {
        UNIT, TEXT, BUILDING_CONSTRUCTION, MILITARY_CONSTRUCTION, FACTORY, CITY, TOWN
    }

    override fun toPositionString(offset: Int) = buildString {
        append(n(offset)).append(
            when (type) {
                ObjectType.UNIT -> "unit"
                ObjectType.TEXT -> "text_position"
                ObjectType.BUILDING_CONSTRUCTION -> "building_construction"
                ObjectType.MILITARY_CONSTRUCTION -> "military_construction"
                ObjectType.FACTORY -> "factory"
                ObjectType.CITY -> "city"
                ObjectType.TOWN -> "town"
            }
        ).append(" = ")
        append(coordinate.toPositionString(offset))
    }
}

/**
 * A transform of [BuildingType] to [Double] values.
 */
typealias BuildingTransform = Pair<BuildingType, Double>

/**
 * @see PositionInfo.toPositionString
 */
private fun List<BuildingTransform>.toPositionString(offset: Int) = buildString {
    this@toPositionString.forEach {
        append(n(offset)).append(
            when (it.first) {
                BuildingType.FORT -> "fort"
                BuildingType.NAVAL_BASE -> "naval_base"
                BuildingType.RAILROAD -> "railroad"
                BuildingType.AEROPLANE_FACTORY -> "aeroplane_factory"
            }
        ).append(" = ")
        appendlf(format.format(it.second))
    }
}

/**
 * Building nudge data.
 */
data class BuildingNudges(val transforms: List<BuildingTransform>) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        val outer = n(offset)
        append(outer).appendlf("building_nudge = {")
        appendlf(transforms.toPositionString(offset + OFFSET_STEP))
        append(outer).append("}")
    }
}

/**
 * Building rotation data.
 */
data class BuildingRotations(val transforms: List<BuildingTransform>) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        val outer = n(offset)
        append(outer).appendlf("building_rotation = {")
        append(transforms.toPositionString(offset + OFFSET_STEP))
        append(outer).append("}")
    }
}

/**
 * Buildings that can be acted upon by a rotation or nudge.
 */
enum class BuildingType {
    FORT, NAVAL_BASE, RAILROAD, AEROPLANE_FACTORY
}

/**
 * Railroad visibility data.
 */
data class RailroadVisibility(val visibilities: List<Int>) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        append(n(offset))
            .append("railroad_visibility = { ")
            .append(visibilities.joinToString(" "))
            .append(" }")
    }
}

/**
 * Building positions data. A collection of [BuildingPositionData].
 */
data class BuildingPositions(val positions: List<BuildingPositionData>) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        val outer = n(offset)
        val inner = n(offset + OFFSET_STEP)

        append(outer).appendlf("building_position = {")
        positions.forEach {
            append(inner).append(
                when (it.positionType) {
                    PositionType.FORT -> "fort"
                    PositionType.NAVAL_BASE -> "naval_base"
                    PositionType.RAILROAD -> "railroad"
                }
            ).append(" = ")
            appendlf(it.coordinate.toPositionString(offset + OFFSET_STEP))
        }
        append(outer).append("}")
    }
}

/**
 * Coordinate data for railway track spawns.
 */
data class SpawnRailwayTrack(val coordinates: List<Coordinate>) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        val outer = n(offset)
        val inner = n(offset + OFFSET_STEP)

        append(outer).appendlf("spawn_railway_track = {")
        coordinates.forEach {
            append(inner).appendlf(it.toPositionString(offset + OFFSET_STEP))
        }
        append(outer).append("}")
    }
}

/**
 * Text rotation data.
 */
data class TextRotation(val rotation: Double) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        append(n(offset)).append("text_rotation = ").append(format.format(rotation))
    }
}

/**
 * Text scale data.
 */
data class TextScale(val scale: Double) : PositionInfo() {
    override fun toPositionString(offset: Int) = buildString {
        append(n(offset)).append("text_scale = ").append(format.format(scale))
    }
}

/**
 * Position data [coordinate] for various [PositionType]'s.
 */
data class BuildingPositionData(val positionType: PositionType, val coordinate: Coordinate) {
    enum class PositionType {
        FORT, NAVAL_BASE, RAILROAD
    }
}


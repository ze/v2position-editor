package com.zelkatani.model.map

import com.zelkatani.MultiException
import com.zelkatani.requireNoExceptions
import javafx.scene.paint.Color
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.File
import kotlin.collections.set

/**
 * Get a list of all records without any comments.
 */
private fun CSVParser.removeComments() = filter {
    val first = it[0].trimStart()
    first.isNotBlank() && first[0] != '#'
}

/**
 * Performs the given [action] on every line that starts with a province.
 */
private inline fun CSVParser.forEachProvinceIndex(action: (CSVRecord) -> Unit) {
    for (element in removeComments()) {
        if (element[0].trim().toIntOrNull() != null) {
            action(element)
        }
    }
}

/**
 * Abstract parser for CSV files in the `map` folder.
 */
abstract class MapCSVParser(file: File) {
    private val format = CSVFormat.newFormat(';').withIgnoreSurroundingSpaces(true)
    protected val parser = CSVParser(file.reader(), format)

    /**
     * Close the parser after finishing use.
     */
    protected fun close() = parser.close()

    /**
     * Parse and construct the maps necessary.
     */
    protected abstract fun parse()

    /**
     * Make sure the parser is open when using. This makes sure the parser doesn't run twice.
     */
    protected fun assertParserOpen() = check(!parser.isClosed) {
        "The parser is closed. " +
                "This should only happen if there was an attempt to parse twice, " +
                "which there should be no reason to."
    }
}

/**
 * A parser for `adjacencies.csv` in the `map` folder.
 */
class AdjacenciesParser(file: File) : MapCSVParser(file) {
    // The game itself has 120 entries, so this should fit and not resize, while allowing for a few more entries.
    private val _adjacencies = HashMap<Point, AdjacencyRecord>(175)

    /**
     * All province adjacencies.
     */
    val adjacencies: Map<Point, AdjacencyRecord>
        get() = _adjacencies

    init {
        parse()
    }

    override fun parse() {
        assertParserOpen()

        val multiException = MultiException()

        parser.forEachProvinceIndex {
            val point = it[0].toInt() to it[1].toInt()
            val type: AdjacencyType = when (it[2]) {
                "sea" -> AdjacencyType.SEA
                "land" -> AdjacencyType.LAND
                "impassable" -> AdjacencyType.IMPASSABLE
                "canal" -> AdjacencyType.CANAL
                else -> {
                    multiException.add("Unknown adjacency positionType: ${it[2]}", it.recordNumber.toInt())
                    AdjacencyType.ERROR
                }
            }

            _adjacencies[point] =
                AdjacencyRecord(type, it[3].toInt(), it[4].toInt(), it[5])
        }

        close()
        requireNoExceptions(multiException)
    }

    data class AdjacencyRecord(val type: AdjacencyType, val through: Int, val data: Int, val comment: String) {
        init {
            require(through >= 0) {
                "Entry 'through' must be a positive integer."
            }
        }
    }

    enum class AdjacencyType {
        SEA,
        LAND,
        IMPASSABLE,
        CANAL,
        ERROR
    }
}

/**
 * A parser for `definition.csv` in the `map` folder.
 */
class DefinitionParser(file: File) : MapCSVParser(file) {
    /**
     * A comfortable base capacity for the game. The base game itself has ~3320 values, so this should allow
     * for no resizing and the addition of a few provinces if necessary.
     */
    private val mapCapacity = 5000

    private val _provinces = HashMap<Int, ProvinceDefinitionRecord>(mapCapacity)

    /**
     * All province definition.
     */
    val provinces: Map<Int, ProvinceDefinitionRecord>
        get() = _provinces

    private val _colors = HashMap<Color, ColorDefinitionRecord>(mapCapacity)

    /**
     * All color definition.
     */
    val colors: Map<Color, ColorDefinitionRecord>
        get() = _colors

    init {
        parse()
    }

    override fun parse() {
        assertParserOpen()

        parser.forEachProvinceIndex {
            val triple = parseCSVRecord(it)

            _provinces[triple.first] =
                ProvinceDefinitionRecord(triple.second, triple.third)
            _colors[triple.second] =
                ColorDefinitionRecord(triple.first, triple.third)
        }
    }

    /**
     * Parse a [CSVRecord] into a convenient [Triple].
     */
    private fun parseCSVRecord(record: CSVRecord): Triple<Int, Color, String> {
        val province = record[0].toInt()
        require(province >= 0) {
            "Entry 'through' must be a positive integer."
        }

        val color = Color(
            record[1].replace(".", "").toInt() / 255.0,
            record[2].replace(".", "").toInt() / 255.0,
            record[3].replace(".", "").toInt() / 255.0,
            1.0
        )
        val descriptor = record[4]

        return Triple(province, color, descriptor)
    }

    /**
     * The definition that are known by any province.
     */
    data class ProvinceDefinitionRecord(val color: Color, val descriptor: String)

    /**
     * The definition that are known by any province.
     */
    data class ColorDefinitionRecord(val province: Int, val descriptor: String)
}
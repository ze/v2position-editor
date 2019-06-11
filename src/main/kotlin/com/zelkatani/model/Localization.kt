package com.zelkatani.model

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.FileFilter

data class Localization(val records: Map<String, LocalizationRecord>) {
    companion object : ModelBuilder<Localization> {
        /**
         * Read the localisation directory from [GameLocation.gameFolder]
         */
        override fun from(file: File): Localization {
            val filter = FileFilter { it.extension == "csv" }
            val gameCSVFiles = file.listFiles(filter).orEmpty()
            val gameCSVFilenames = gameCSVFiles.map { it.nameWithoutExtension }

            gameCSVFiles.forEach {
                val modCSVFile = GameLocation.fileFromMod(it)
                LocalizationParser.parseFile(if (modCSVFile.exists()) modCSVFile else it)
            }

            val modLocalization = GameLocation.modFolder.resolve("localisation")
            val modCSVFiles = modLocalization.listFiles(filter).orEmpty()

            // Don't go over files already visited
            modCSVFiles.filter {
                it.name !in gameCSVFilenames
            }.forEach(LocalizationParser::parseFile)

            return Localization(LocalizationParser.records)
        }
    }

    operator fun get(key: String, language: LocalizationLanguage) =
        records[key]?.getEntryForLanguage(language)
}


/**
 * Object class to parse many localization files, held in the game's `localisation` folder.
 */
object LocalizationParser {
    /**
     * Mutable collection of records.
     */
    private val mutableRecords = HashMap<String, LocalizationRecord>(1000)

    /**
     * All localization entries that the game knows of.
     */
    val records: Map<String, LocalizationRecord>
        get() = mutableRecords

    private val format = CSVFormat.newFormat(';')

    /**
     * Parse a localization file.
     * @param file The [File] to store records of.
     */
    fun parseFile(file: File) {
        require(file.path.endsWith(".csv")) {
            "File must be a .csv file."
        }

        file.reader().use { isr ->
            val parser = CSVParser(isr, format)
            parser.removeComments().forEach { record ->
                val first = record[0]
                var count = 0
                var containsX = false

                val rest = record.toList().takeWhile {
                    containsX = it.length == 1 && it.toLowerCase() == "x"
                    count++ <= 14 && !containsX
                }

                val goodCount = count >= 15

                val warning = when {
                    !(goodCount || containsX) -> LocalizationState.BAD_END // Will cause errors for the game.
                    !goodCount && containsX -> LocalizationState.TOO_SHORT // Can cause errors for specific languages of the game.
                    else -> LocalizationState.UNUSED // Usage validation will be when used, will become OK if so.
                }

                mutableRecords[first] = LocalizationRecord(rest.drop(1), warning)
            }

            parser.close()
        }
    }
}

/**
 * A record containing the localization for a specific record.
 *
 * Every record starts off with an [LocalizationState.UNUSED] state until it is proven otherwise.
 */
data class LocalizationRecord(val entries: List<String>, var state: LocalizationState) {
    operator fun get(index: Int) = entries[index]

    fun getEntryForLanguage(language: LocalizationLanguage) = get(language.ordinal)
}

/**
 * State of a [LocalizationRecord].
 */
enum class LocalizationState {
    OK,
    UNUSED,
    TOO_SHORT,
    BAD_END
}

/**
 * Composition of a [LocalizationRecord].
 *
 * Can be used to explain TOO_SHORT warnings, and for selections.
 */
enum class LocalizationLanguage {
    ENGLISH,
    FRENCH,
    GERMAN,
    POLISH,
    SPANISH,
    ITALIAN,
    SWEDISH,
    CZECH,
    HUNGARIAN,
    DUTCH,
    PORTUGUESE,
    RUSSIAN,
    FINNISH
}
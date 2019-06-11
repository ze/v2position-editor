package com.zelkatani.model

import org.apache.commons.csv.CSVParser

/**
 * Get a list of all records without any comments.
 */
fun CSVParser.removeComments() = filter {
    val first = it[0].trimStart()
    first.isNotBlank() && first[0] != '#'
}
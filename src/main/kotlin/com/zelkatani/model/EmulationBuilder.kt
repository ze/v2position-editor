package com.zelkatani.model

import java.io.File

/**
 * Companion object builder interface for the models that will be created from a parsed file.
 */
interface EmulationBuilder<out T> {

    /**
     * Build the necessary [T] with [file].
     */
    fun from(file: File): T
}
package com.zelkatani.model

import java.io.File

/**
 * Store the location of the game and mod folder.
 */
object GameLocation {
    var gamePath: String? = null
    var modPath: String? = null

    // lazily create these files to avoid early access error.
    val gameFolder by lazy { File(gamePath) }
    val modFolder by lazy { File(modPath) }

    fun fileFromMod(file: File): File {
        val relativePath = file.toRelativeString(gameFolder)
        return modFolder.resolve(relativePath)
    }
}
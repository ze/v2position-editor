package com.zelkatani.model

import java.io.File

/**
 * An object for the storage of the game and mod folder.
 */
object GameLocation {
    // These can't be lateinit vars due to restrictions on the modifier.
    /**
     * The path for the game, null by default until set.
     */
    var gamePath: String = if (System.getProperty("os.name").contains("Windows")) {
        "C:\\Program Files (x86)\\Steam\\SteamApps\\common\\Victoria 2"
    } else {
        "~/Library/Containers/com.vpltd.Victoria2HeartOfDarkness/Data/Library/Application Support/com.vpltd.Victoria2HeartOfDarkness-MAS/"
    }

    /**
     * The path for the game, null by default until set.
     */
    var modPath: String? = null

    /**
     * The file for the [gamePath].
     */
    val gameFolder: File
        get() = File(gamePath)

    /**
     * The file for [modPath]
     */
    val modFolder: File
        get() = File(modPath)

    /**
     * Get a file from [modFolder] if it exists in [gameFolder] originally.
     */
    fun fileFromMod(file: File): File {
        val relativePath = file.toRelativeString(gameFolder)
        return modFolder.resolve(relativePath)
    }
}
package com.zelkatani.gui.controller

import com.zelkatani.gui.preferencesName
import com.zelkatani.gui.view.DirectoryView
import com.zelkatani.model.GameLocation
import tornadofx.Controller

class DirectoryController : Controller() {
    private val directoryView: DirectoryView by inject()

    /**
     * Load preferences if they exist.
     * If they exist, they are NOT committed, but loaded in from [DirectoryView].
     */
    fun init() {
        preferences(preferencesName) {
            val gamePath = get("game_path", null) ?: return@preferences
            val modPath = get("mod_path", null) ?: return@preferences

            GameLocation.gamePath = gamePath
            GameLocation.modPath = modPath
        }
    }

    /**
     * Commit changes to what was entered.
     * This parses the inputted files, TODO figure out storage, should Mod be an object?
     * Validation of directories is done here.
     */
    fun commitGameLocation() {

    }
}
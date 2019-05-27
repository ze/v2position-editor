package com.zelkatani.gui.controller

import com.zelkatani.gui.preferencesName
import com.zelkatani.gui.view.DirectoryView
import com.zelkatani.gui.view.EditorScope
import com.zelkatani.gui.view.EditorView
import com.zelkatani.model.GameLocation
import com.zelkatani.model.Mod
import tornadofx.Controller
import tornadofx.find

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
     * This parses the inputted files,
     * Validation of directories is done here.
     */
    fun commitGameLocation() {
        // TODO: handle errors
        val mod = Mod.from(GameLocation.modFolder)
        val editorScope = EditorScope(mod)

        val editorView = find<EditorView>(editorScope)
        directoryView.replaceWith(editorView, centerOnScreen = true, sizeToScene = true)
    }
}
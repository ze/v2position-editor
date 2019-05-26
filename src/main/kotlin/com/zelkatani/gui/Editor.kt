package com.zelkatani.gui

import com.zelkatani.gui.controller.DirectoryController
import com.zelkatani.gui.view.DirectoryView
import javafx.stage.Stage
import tornadofx.App

const val applicationName = "Victoria 2 - Map Editor"
const val preferencesName = "v2-map-editor"

/**
 * The main GUI application.
 * The first view is the directory selector, which will lead into the editor.
 */
class Editor : App(DirectoryView::class) {
    private val directoryController: DirectoryController by inject()

    init {
        System.setProperty("apple.awt.application.name", applicationName)
    }

    override fun start(stage: Stage) {
        stage.isResizable = false

        directoryController.init()
        super.start(stage)
    }
}
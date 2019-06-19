package com.zelkatani.gui.app

import com.zelkatani.gui.controller.DirectoryController
import com.zelkatani.gui.view.DirectoryView
import javafx.scene.paint.Color
import javafx.stage.Stage
import tornadofx.*

const val APPLICATION_NAME = "Victoria 2 - Map Editor"
const val PREFERENCES_NAME = "v2-map-editor"
const val GAME_PATH = "game_path"
const val MOD_PATH = "mod_path"

/**
 * The main GUI application.
 * The first view is the directory selector, which will lead into the editor.
 */
class Editor : App(DirectoryView::class, EditorStylesheet::class) {
    private val directoryController: DirectoryController by inject()

    init {
        System.setProperty("apple.awt.application.name", APPLICATION_NAME)
    }

    override fun start(stage: Stage) {
        stage.isResizable = false

        directoryController.init()
        super.start(stage)
    }
}

/**
 * The [Stylesheet] for the [Editor] application.
 */
class EditorStylesheet : Stylesheet() {
    companion object {
        val selected by cssclass()
        val unselected by cssclass()
        val pressedIcon by cssclass()

        // credits to paint.net for these colors.
        private val fieldBorderColor = c("#0078D7")
        private val fieldBackgroundColor = c("#BFDDF5")

        private val iconColor = Color.DEEPSKYBLUE
    }

    init {
        selected {
            borderColor += box(fieldBorderColor)
            padding = box(2.px)
            backgroundColor += fieldBackgroundColor
        }

        unselected {
            borderColor += box(Color.TRANSPARENT)
            padding = box(2.px)
        }

        pressedIcon {
            fill = iconColor
            and(pressed) {
                fill = iconColor.darker()
            }
        }
    }
}
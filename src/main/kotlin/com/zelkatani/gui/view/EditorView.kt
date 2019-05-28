package com.zelkatani.gui.view

import com.zelkatani.gui.applicationName
import com.zelkatani.gui.pane.mappane
import com.zelkatani.model.Mod
import javafx.stage.Screen
import tornadofx.Scope
import tornadofx.View
import tornadofx.borderpane
import tornadofx.scrollpane

class EditorScope(val mod: Mod) : Scope()

class EditorView : View(applicationName) {
    override val scope = super.scope as EditorScope
    private val mod = scope.mod

    override fun onDock() {
        currentStage?.apply {
            isResizable = true

            val screen = Screen.getPrimary()
            val bounds = screen.bounds

            width = bounds.width / 1.15
            height = bounds.height / 1.2
        }
    }

    override val root = borderpane {
        center = scrollpane {
            mappane(mod.worldMap)

            prefViewportWidthProperty().bind(primaryStage.widthProperty())
            prefViewportHeightProperty().bind(primaryStage.heightProperty())
        }
    }
}
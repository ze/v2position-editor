package com.zelkatani.gui.view

import com.zelkatani.gui.applicationName
import com.zelkatani.gui.component.Layer
import com.zelkatani.gui.component.mapCanvas
import com.zelkatani.model.Mod
import javafx.stage.Screen
import tornadofx.*

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
            val mapCanvas = mapCanvas {
                val worldMap = mod.worldMap

                width = worldMap.provincesBMP.width.toDouble()
                height = worldMap.provincesBMP.height.toDouble()

                // TODO: figure out transform layers (i.e. country view)
                // TODO: there is probably a better way to handle layers
                layers += Layer(worldMap.provincesBMP)
                layers += Layer(worldMap.terrainBMP, isVisible = false)
                layers += Layer(worldMap.riversBMP, isVisible = false)
            }

            runLater {
                mapCanvas.draw()
            }

            add(mapCanvas)
        }.apply {
            prefViewportWidthProperty().bind(primaryStage.widthProperty())
            prefViewportHeightProperty().bind(primaryStage.heightProperty())
        }
    }
}
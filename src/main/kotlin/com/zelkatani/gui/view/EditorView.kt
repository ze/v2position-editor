package com.zelkatani.gui.view

import com.zelkatani.gui.applicationName
import com.zelkatani.gui.fragment.OpacityFragment
import com.zelkatani.gui.fragment.OpacityScope
import com.zelkatani.gui.pane.MapPane
import com.zelkatani.model.Mod
import javafx.stage.Screen
import javafx.stage.StageStyle
import tornadofx.*

/**
 * The scope for [EditorView], contains the mod to construct visuals for.
 */
class EditorScope(val mod: Mod) : Scope()

/**
 * The view for the [mod] provided. Contains a layer view and control.
 */
class EditorView : View(applicationName) {
    override val scope = super.scope as EditorScope
    private val mod = scope.mod
    private val mapPane = MapPane(mod.worldMap)

    override fun onDock() {
        currentStage?.apply {
            isResizable = true

            val screen = Screen.getPrimary()
            val bounds = screen.bounds

            width = bounds.width / 1.15
            height = bounds.height / 1.2
        }

        val opacityScope = OpacityScope(
            mapPane.provincesOpacityProperty,
            mapPane.terrainOpacityProperty,
            mapPane.riversOpacityProperty,
            mapPane.provincesBlendModeProperty,
            mapPane.terrainBlendModeProperty,
            mapPane.riversBlendModeProperty
        )

        val opacityFragment = find<OpacityFragment>(opacityScope)
        opacityFragment.openWindow(
            stageStyle = StageStyle.UTILITY,
            escapeClosesWindow = false,
            block = false,
            resizable = false
        )
    }

    override val root = borderpane {
        center = scrollpane {
            add(mapPane)

            prefViewportWidthProperty().bind(primaryStage.widthProperty())
            prefViewportHeightProperty().bind(primaryStage.heightProperty())
        }
    }
}
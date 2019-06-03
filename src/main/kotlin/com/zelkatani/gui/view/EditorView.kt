package com.zelkatani.gui.view

import com.zelkatani.gui.applicationName
import com.zelkatani.gui.fragment.OpacityFragment
import com.zelkatani.gui.fragment.OpacityScope
import com.zelkatani.gui.fragment.PositionFragment
import com.zelkatani.gui.pane.MapPane
import com.zelkatani.model.Mod
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
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

    private val positionFragmentProperty: ObjectProperty<PositionFragment?> = SimpleObjectProperty()
    // its bad design to modify observed values, but I don't know any other way to do this.
    private val positionFragmentListener: ChangeListener<PositionFragment?> = ChangeListener { _, oldValue, newValue ->
        // TODO: find a way to prohibit triggering it multiple times. Make it blocking?
        // TODO: If blocking, allow for the opacity control to be used.
        // TODO: Ideally, commit changes. Close for now
        if (oldValue?.isDocked == true) {
            oldValue.close()
        }

        newValue?.openWindow(
            stageStyle = StageStyle.UTILITY,
            escapeClosesWindow = false,
            block = false,
            resizable = false
        )
    }

    private val mapPane = MapPane(mod.worldMap, positionFragmentProperty)

    override fun onDock() {
        currentStage?.apply {
            isResizable = true

            val screen = Screen.getPrimary()
            val bounds = screen.bounds

            width = bounds.width / 1.15
            height = bounds.height / 1.2
        }

        positionFragmentProperty.addListener(positionFragmentListener)

        val opacityScope = OpacityScope(
            mapPane.provincesOpacityProperty,
            mapPane.terrainOpacityProperty,
            mapPane.riversOpacityProperty,
            mapPane.provincesBlendModeProperty,
            mapPane.terrainBlendModeProperty,
            mapPane.riversBlendModeProperty,
            mapPane.children,
            mapPane.positionFragmentChildren
        )

        val opacityFragment = find<OpacityFragment>(opacityScope)
        opacityFragment.openWindow(
            stageStyle = StageStyle.UTILITY,
            escapeClosesWindow = false,
            block = false,
            resizable = false
        )
    }

    override fun onUndock() {
        positionFragmentProperty.removeListener(positionFragmentListener)
    }

    override val root = borderpane {
        center = scrollpane {
            add(mapPane)

            prefViewportWidthProperty().bind(primaryStage.widthProperty())
            prefViewportHeightProperty().bind(primaryStage.heightProperty())
        }
    }
}
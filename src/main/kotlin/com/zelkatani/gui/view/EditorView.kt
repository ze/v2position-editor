package com.zelkatani.gui.view

import com.zelkatani.gui.app.APPLICATION_NAME
import com.zelkatani.gui.component.MapPane
import com.zelkatani.gui.component.fragment.OpacityFragment
import com.zelkatani.gui.component.fragment.OpacityScope
import com.zelkatani.gui.component.fragment.PositionFragment
import com.zelkatani.model.Mod
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.stage.Screen
import javafx.stage.StageStyle
import tornadofx.*

/**
 * The [Scope] for [EditorView], contains the mod to construct visuals for.
 */
class EditorScope(val mod: Mod) : Scope()

/**
 * The view for the [mod] provided. Contains a layer view and control.
 */
class EditorView : View(APPLICATION_NAME) {
    override val scope = super.scope as EditorScope

    /**
     * The [Mod] for the editor to work with.
     */
    private val mod = scope.mod

    /**
     * The current [PositionFragment] bound by this [EditorView].
     */
    private val positionFragmentProperty: ObjectProperty<PositionFragment?> = SimpleObjectProperty()

    /**
     * A [ChangeListener] for [positionFragmentProperty]. Closes the previous [PositionFragment]
     * if exists and is docked, otherwise creates a new [PositionFragment].
     */
    private val positionFragmentListener: ChangeListener<PositionFragment?> = ChangeListener { _, oldValue, newValue ->
        if (oldValue?.isDocked == true) {
            oldValue.close()
        }

        newValue?.openWindow(
            stageStyle = StageStyle.UTILITY,
            resizable = false
        )
    }

    /**
     * The [MapPane] component for this [EditorView].
     */
    private val mapPane = MapPane(mod.worldMap, mod.localization, positionFragmentProperty)

    /**
     * Set the stage size and create an [OpacityFragment].
     * Attach [positionFragmentListener] to [positionFragmentProperty].
     */
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
            resizable = false
        )
    }

    /**
     * Remove the [positionFragmentListener] on [positionFragmentProperty].
     */
    override fun onUndock() {
        positionFragmentProperty.removeListener(positionFragmentListener)
    }

    override val root = scrollpane {
        add(mapPane)

        prefViewportWidthProperty().bind(primaryStage.widthProperty())
        prefViewportHeightProperty().bind(primaryStage.heightProperty())
    }
}
package com.zelkatani.gui.fragment

import com.zelkatani.gui.EditorStylesheet
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextFormatter
import javafx.scene.effect.BlendMode
import javafx.scene.input.MouseEvent
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import kotlin.math.round

/**
 * The scope for [OpacityFragment], contains properties to bind to.
 */
class OpacityScope(
    val provincesOpacityProperty: DoubleProperty,
    val terrainOpacityProperty: DoubleProperty,
    val riversOpacityProperty: DoubleProperty,
    val provincesBlendModeProperty: ObjectProperty<BlendMode>,
    val terrainBlendModeProperty: ObjectProperty<BlendMode>,
    val riversBlendModeProperty: ObjectProperty<BlendMode>,
    val mapPaneChildren: ObservableList<Node>,
    val positionFragmentChildren: ObjectProperty<MutableList<Node>>
) : Scope()

/**
 * A utility fragment to handle opacity for the map canvas.
 */
class OpacityFragment : Fragment("Layer Opacity") {
    override val scope = super.scope as OpacityScope

    private val provincesValue = SimpleDoubleProperty(255.0)
    private val terrainValue = SimpleDoubleProperty(255.0)
    private val riversValue = SimpleDoubleProperty(255.0)

    private val blendStringList = FXCollections.observableArrayList(
        "Normal", "Multiply", "Add",
        "Color Burn", "Color Dodge",
        "Overlay",
        "Soft Light", "Hard Light",
        "Difference",
        "Lighten", "Darken",
        "Screen", "Exclusion"
    )

    private val blendModeMap = mapOf(
        "Normal" to null, "Multiply" to BlendMode.MULTIPLY, "Add" to BlendMode.ADD,
        "Color Burn" to BlendMode.COLOR_BURN, "Color Dodge" to BlendMode.COLOR_DODGE,
        "Overlay" to BlendMode.OVERLAY,
        "Soft Light" to BlendMode.SOFT_LIGHT, "Hard Light" to BlendMode.HARD_LIGHT,
        "Difference" to BlendMode.DIFFERENCE,
        "Lighten" to BlendMode.LIGHTEN, "Darken" to BlendMode.DARKEN,
        "Screen" to BlendMode.SCREEN, "Exclusion" to BlendMode.EXCLUSION
    )

    private val sliderFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isInt() && it.toInt() in 0..255
        }
    }

    /**
     * Add a slider and textfield that can control each other with integer bounds.
     */
    private fun Field.sliderfield(property: DoubleProperty) {
        slider(0..255) {
            valueProperty().addListener { _, _, newValue ->
                value = round(newValue.toDouble())
            }

            bind(property)
        }

        textfield(property, NumberStringConverter()) {
            // have to set the text here to 100 otherwise it ignores property's default value...
            text = "255"
            prefWidth = 45.0
            filterInput(sliderFilter)
        }
    }

    private fun blendModeChangeListener(property: ObjectProperty<BlendMode>): ChangeListener<String> =
        ChangeListener { _, _, newValue ->
            property.value = blendModeMap[newValue]
        }

    private val provincesListener = blendModeChangeListener(scope.provincesBlendModeProperty)
    private val terrainListener = blendModeChangeListener(scope.terrainBlendModeProperty)
    private val riversListener = blendModeChangeListener(scope.riversBlendModeProperty)

    private fun ComboBox<String>.blendListener(listener: ChangeListener<String>) = apply {
        value = "Normal"
        prefWidth = 157.5
        valueProperty().addListener(listener)
    }

    private val provincesComboBox = ComboBox(blendStringList).blendListener(provincesListener)
    private val terrainComboBox = ComboBox(blendStringList).blendListener(terrainListener)
    private val riversComboBox = ComboBox(blendStringList).blendListener(riversListener)

    private fun EventTarget.faiconview(
        icon: FontAwesomeIcon,
        size: String,
        op: FontAwesomeIconView.() -> Unit = {}
    ): FontAwesomeIconView {
        val iconView = FontAwesomeIconView(icon)
        iconView.size = size
        return opcr(this, iconView, op)
    }

    private val selected: ObjectProperty<Field?> = SimpleObjectProperty()
    private val selectedListener: ChangeListener<Field?> = ChangeListener { _, oldValue, newValue ->
        oldValue?.removeClass(EditorStylesheet.selected)
        oldValue?.addClass(EditorStylesheet.unselected)

        newValue?.removeClass(EditorStylesheet.unselected)
        newValue?.addClass(EditorStylesheet.selected)
    }

    private inline fun opacitySwapHandler(swap: Int, crossinline idxNotEqual: ObservableList<Node>.() -> Int) =
        EventHandler<MouseEvent> {
            val selectedField =
                selected.value ?: throw RuntimeException("No field is selected, but one should be")

            val selectedParent = selectedField.parent as Fieldset

            val observableChildren = FXCollections.observableArrayList(selectedParent.children)
            val idx = observableChildren?.indexOf(selectedField)
            val notEqual = idxNotEqual(observableChildren)
            if (idx != null && idx != notEqual) {
                observableChildren.swap(idx, idx + swap)

                // index should be reversed
                val mapChildren = FXCollections.observableArrayList(scope.mapPaneChildren)
                val positionChildren = FXCollections.observableArrayList(scope.positionFragmentChildren.value)

                // this is just the last index before the canvas pops up.
                val trueLast = mapChildren.lastIndex - 1
                mapChildren.swap(trueLast - idx, trueLast - (idx + swap))
                positionChildren.swap(trueLast - idx, trueLast - (idx + swap))

                scope.positionFragmentChildren.value = positionChildren
                scope.mapPaneChildren.setAll(mapChildren)
            }

            selectedParent.children.setAll(observableChildren)
        }

    /**
     * Add the selected listener to the selected value.
     */
    override fun onDock() {
        selected.addListener(selectedListener)
    }

    /**
     * Remove all bindings when closed, they will be added when an instance is made.
     */
    override fun onUndock() {
        scope.provincesOpacityProperty.unbind()
        scope.terrainOpacityProperty.unbind()
        scope.riversOpacityProperty.unbind()

        provincesComboBox.valueProperty().removeListener(provincesListener)
        terrainComboBox.valueProperty().removeListener(terrainListener)
        riversComboBox.valueProperty().removeListener(riversListener)
        selected.removeListener(selectedListener)
    }

    override val root = borderpane {
        center = form {
            fieldset {
                field("Provinces") {
                    sliderfield(provincesValue)
                    scope.provincesOpacityProperty.bind(provincesValue.divide(255.0))
                    add(provincesComboBox)

                    addClass(EditorStylesheet.selected)
                    selected.value = this

                    setOnMouseClicked {
                        selected.value = this
                    }
                }

                field("Terrain") {
                    sliderfield(terrainValue)
                    scope.terrainOpacityProperty.bind(terrainValue.divide(255.0))
                    add(terrainComboBox)

                    addClass(EditorStylesheet.unselected)
                    setOnMouseClicked {
                        selected.value = this
                    }
                }

                field("Rivers") {
                    sliderfield(riversValue)
                    scope.riversOpacityProperty.bind(riversValue.divide(255.0))
                    add(riversComboBox)

                    addClass(EditorStylesheet.unselected)
                    setOnMouseClicked {
                        selected.value = this
                    }
                }
            }
        }

        bottom = vbox {
            separator()

            hbox {
                spacing = 5.0
                padding = insets(10, 5)
                faiconview(FontAwesomeIcon.ARROW_UP, "2em") {
                    addClass(EditorStylesheet.pressedIcon)

                    onMouseClicked = opacitySwapHandler(-1) { 0 }
                }

                faiconview(FontAwesomeIcon.ARROW_DOWN, "2em") {
                    addClass(EditorStylesheet.pressedIcon)

                    onMouseClicked = opacitySwapHandler(1) { lastIndex }
                }
            }
        }
    }
}
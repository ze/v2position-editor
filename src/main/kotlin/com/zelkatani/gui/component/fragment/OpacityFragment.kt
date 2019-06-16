package com.zelkatani.gui.component.fragment

import com.zelkatani.gui.app.EditorStylesheet
import com.zelkatani.gui.faiconview
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextFormatter
import javafx.scene.effect.BlendMode
import javafx.scene.input.KeyCode
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

    /**
     * The opacity value for provinces.bmp. Range is [range].
     */
    private val provincesValue = SimpleDoubleProperty(255.0)

    /**
     * The opacity value for terrain.bmp. Range is [range].
     */
    private val terrainValue = SimpleDoubleProperty(255.0)

    /**
     * The opacity value for rivers.bmp. Range is [range].
     */
    private val riversValue = SimpleDoubleProperty(255.0)

    /**
     * The range for opacity controls. Goes from 0 to 255.
     */
    private val range = 0..255

    /**
     * The name of every possible [BlendMode] type.
     * This is the clean name of every [BlendMode], with "Normal" equivalent to null.
     */
    private val blendStringList = listOf(
        "Normal", "Multiply", "Add",
        "Color Burn", "Color Dodge",
        "Overlay",
        "Soft Light", "Hard Light",
        "Difference",
        "Lighten", "Darken",
        "Screen", "Exclusion"
    ).observable()

    /**
     * A mapping from [String] to [BlendMode].
     */
    private val blendModeMap = mapOf(
        "Normal" to null, "Multiply" to BlendMode.MULTIPLY, "Add" to BlendMode.ADD,
        "Color Burn" to BlendMode.COLOR_BURN, "Color Dodge" to BlendMode.COLOR_DODGE,
        "Overlay" to BlendMode.OVERLAY,
        "Soft Light" to BlendMode.SOFT_LIGHT, "Hard Light" to BlendMode.HARD_LIGHT,
        "Difference" to BlendMode.DIFFERENCE,
        "Lighten" to BlendMode.LIGHTEN, "Darken" to BlendMode.DARKEN,
        "Screen" to BlendMode.SCREEN, "Exclusion" to BlendMode.EXCLUSION
    )

    /**
     * A filter for a [slider]. Only integers in the [range] are allowed.
     */
    private val sliderFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isInt() && it.toInt() in range
        }
    }

    /**
     * Add a slider and textfield that can control each other with integer bounds with [range].
     *
     * @param opacity The bound opacity property.
     */
    private fun Field.sliderfield(opacity: DoubleProperty) {
        slider(range) {
            valueProperty().onChange {
                value = round(it)
            }

            bind(opacity)
        }

        val converter = NumberStringConverter()
        textfield(opacity, converter) {
            // have to set the text here to 255 otherwise it ignores opacity's default value...
            text = "255"
            prefWidth = 45.0
            filterInput(sliderFilter)

            setOnKeyPressed {
                if (it.code == KeyCode.UP || it.code == KeyCode.DOWN) {
                    val initial = converter.fromString(text)?.toInt() ?: return@setOnKeyPressed

                    if (it.code == KeyCode.UP && initial != 255) {
                        text = (initial + 1).toString()
                    } else if (it.code == KeyCode.DOWN && initial != 0) {
                        text = (initial - 1).toString()
                    }
                }
            }
        }
    }

    /**
     * A [ChangeListener] for a [ComboBox]. Sets the [String] updated value
     * to a [BlendMode] for [blendMode]
     *
     * @param blendMode The property to set.
     */
    private fun blendModeChangeListener(blendMode: ObjectProperty<BlendMode>): ChangeListener<String> =
        ChangeListener { _, _, newValue ->
            blendMode.value = blendModeMap[newValue]
        }

    /**
     * A [ChangeListener] for provinces.bmp.
     */
    private val provincesListener = blendModeChangeListener(scope.provincesBlendModeProperty)

    /**
     * A [ChangeListener] for terrain.bmp.
     */
    private val terrainListener = blendModeChangeListener(scope.terrainBlendModeProperty)

    /**
     * A [ChangeListener] for rivers.bmp.
     */
    private val riversListener = blendModeChangeListener(scope.riversBlendModeProperty)

    /**
     * Apply the wanted preset values to a [ComboBox] for [BlendMode] controls.
     *
     * @param listener The listener to add.
     * @return The same object with presets.
     */
    private fun ComboBox<String>.blendListener(listener: ChangeListener<String>) = apply {
        value = "Normal"
        prefWidth = 157.5
        valueProperty().addListener(listener)
    }

    /**
     * A [ComboBox] for provinces.bmp. Has a [blendListener] applied onto it.
     */
    private val provincesComboBox = ComboBox(blendStringList).blendListener(provincesListener)

    /**
     * A [ComboBox] for terrain.bmp. Has a [blendListener] applied onto it.
     */
    private val terrainComboBox = ComboBox(blendStringList).blendListener(terrainListener)

    /**
     * A [ComboBox] for rivers.bmp. Has a [blendListener] applied onto it.
     */
    private val riversComboBox = ComboBox(blendStringList).blendListener(riversListener)

    /**
     * The selected [Field]. Must be nullable since the first time the field is set, the old value is null.
     */
    private val selected: ObjectProperty<Field?> = SimpleObjectProperty()

    /**
     * A [ChangeListener] for [selected]. Swaps around [EditorStylesheet.selected] and [EditorStylesheet.unselected].
     */
    private val selectedListener: ChangeListener<Field?> = ChangeListener { _, oldValue, newValue ->
        oldValue?.removeClass(EditorStylesheet.selected)
        oldValue?.addClass(EditorStylesheet.unselected)

        newValue?.removeClass(EditorStylesheet.unselected)
        newValue?.addClass(EditorStylesheet.selected)
    }

    /**
     * Create an [ObservableList] from [this]. TornadoFX doesn't have this defined, but should.
     *
     * @return An [ObservableList] from the contents of [this]
     */
    private fun <T> List<T>.observableArrayList(): ObservableList<T> = FXCollections.observableArrayList(this)

    /**
     * An [EventHandler] for swapping layer order.
     *
     * @param swap The offset index to swap with.
     * @param idxNotEqual The outer bounds index that cannot be swapped from without an [IndexOutOfBoundsException].
     *
     * @return An [EventHandler] for layer swapping.
     */
    private inline fun opacitySwapHandler(swap: Int, crossinline idxNotEqual: ObservableList<Node>.() -> Int) =
        EventHandler<MouseEvent> {
            val selectedField =
                selected.value ?: throw RuntimeException("No field is selected, but one should be")

            val selectedParent = selectedField.parent as Fieldset

            val observableChildren = selectedParent.children.observableArrayList()
            val idx = observableChildren.indexOf(selectedField)
            val notEqual = idxNotEqual(observableChildren)
            if (idx != notEqual) {
                observableChildren.swap(idx, idx + swap)

                // index should be reversed
                val mapChildren = scope.mapPaneChildren.observableArrayList()
                val positionChildren = scope.positionFragmentChildren.value.observableArrayList()

                val last = mapChildren.lastIndex
                mapChildren.swap(last - idx, last - (idx + swap))
                positionChildren.swap(last - idx, last - (idx + swap))

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
package com.zelkatani.gui.fragment

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.control.TextFormatter
import javafx.scene.effect.BlendMode
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
    val riversBlendModeProperty: ObjectProperty<BlendMode>
) : Scope()

/**
 * A utility fragment to handle opacity for the map canvas.
 */
class OpacityFragment : Fragment("Layer Opacity") {
    private val opacityScope = super.scope as OpacityScope

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
    private fun Field.sliderfield(property: SimpleDoubleProperty) {
        slider(0..255) {
            valueProperty().addListener { _, _, newValue ->
                value = round(newValue.toDouble())
            }

            property.bindBidirectional(valueProperty())
        }

        textfield(property, NumberStringConverter()) {
            // have to set the text here to 100 otherwise it ignores property's default value...
            text = "255"
            prefWidth = 45.0
            filterInput(sliderFilter)
        }
    }

    private val provincesListener = ChangeListener<String> { _, _, newValue ->
        opacityScope.provincesBlendModeProperty.value = blendModeMap[newValue]
    }

    private val terrainListener = ChangeListener<String> { _, _, newValue ->
        opacityScope.terrainBlendModeProperty.value = blendModeMap[newValue]
    }

    private val riversListener = ChangeListener<String> { _, _, newValue ->
        opacityScope.riversBlendModeProperty.value = blendModeMap[newValue]
    }

    private val provincesComboBox = ComboBox(blendStringList).apply {
        value = "Normal"
        prefWidth = 157.5
        valueProperty().addListener(provincesListener)
    }

    private val terrainComboBox = ComboBox(blendStringList).apply {
        value = "Normal"
        prefWidth = 157.5
        valueProperty().addListener(terrainListener)
    }

    private val riversComboBox = ComboBox(blendStringList).apply {
        value = "Normal"
        prefWidth = 157.5
        valueProperty().addListener(riversListener)
    }

    /**
     * Remove all bindings when closed, they will be added when an instance is made.
     */
    override fun onUndock() {
        opacityScope.provincesOpacityProperty.unbind()
        opacityScope.terrainOpacityProperty.unbind()
        opacityScope.riversOpacityProperty.unbind()

        provincesComboBox.valueProperty().removeListener(provincesListener)
        terrainComboBox.valueProperty().removeListener(terrainListener)
        riversComboBox.valueProperty().removeListener(riversListener)
    }

    override val root = form {
        fieldset {
            field("Provinces") {
                sliderfield(provincesValue)
                opacityScope.provincesOpacityProperty.bind(provincesValue.divide(255.0))
                add(provincesComboBox)
            }

            field("Terrain") {
                sliderfield(terrainValue)
                opacityScope.terrainOpacityProperty.bind(terrainValue.divide(255.0))
                add(terrainComboBox)
            }

            field("Rivers") {
                sliderfield(riversValue)
                opacityScope.riversOpacityProperty.bind(riversValue.divide(255.0))
                add(riversComboBox)
            }
        }
    }
}
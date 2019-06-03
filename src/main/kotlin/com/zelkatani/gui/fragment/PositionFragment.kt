package com.zelkatani.gui.fragment

import com.zelkatani.model.map.Coordinate
import com.zelkatani.model.map.PositionData
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.layout.StackPane
import tornadofx.*
import java.awt.Rectangle

class PositionScope(
    val nodes: ObjectProperty<MutableList<Node>>,
    val bounds: Rectangle,
    val provinceId: Int,
    val positionData: PositionData
) : Scope()

class PositionFragment : Fragment() {
    override val scope = super.scope as PositionScope

    private val textX = SimpleDoubleProperty()
    private val textY = SimpleDoubleProperty()

    private val unitX = SimpleDoubleProperty()
    private val unitY = SimpleDoubleProperty()

    private val cityX = SimpleDoubleProperty()
    private val cityY = SimpleDoubleProperty()

    private val buildingConstructionX = SimpleDoubleProperty()
    private val buildingConstructionY = SimpleDoubleProperty()

    private val militaryConstructionX = SimpleDoubleProperty()
    private val militaryConstructionY = SimpleDoubleProperty()

    private val factoryX = SimpleDoubleProperty()
    private val factoryY = SimpleDoubleProperty()

    private val townX = SimpleDoubleProperty()
    private val townY = SimpleDoubleProperty()

    private val nodesListener: ChangeListener<MutableList<Node>> = ChangeListener { _, _, newValue ->
        (root.center as StackPane).children.setAll(newValue)
    }

    override fun onUndock() {
        scope.nodes.removeListener(nodesListener)
    }

    override fun onDock() {
        scope.nodes.addListener(nodesListener)
        title = scope.provinceId.toString()
    }

    private fun relativeToBounds(coordinate: Coordinate) =
        Coordinate(coordinate.first - scope.bounds.x, coordinate.second - scope.bounds.y)

    private val coordinateFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isDouble() && it.toDouble() >= 0
        }
    }

    private fun Fieldset.coordinates(text: String, xProperty: DoubleProperty, yProperty: DoubleProperty) {
        field("$text:") {
            textfield(xProperty) {
                prefWidth = 100.0
                filterInput(coordinateFilter)
            }

            textfield(yProperty) {
                prefWidth = 100.0
                filterInput(coordinateFilter)
            }
        }
    }

    override val root = borderpane {
        left = form {
            fieldset("Object Positions", FontAwesomeIconView(FontAwesomeIcon.ARROWS_ALT)) {
                coordinates("Text", textX, textY)
                coordinates("Unit", unitX, unitY)
                coordinates("Building Construction", buildingConstructionX, buildingConstructionY)
                coordinates("Military Construction", militaryConstructionX, militaryConstructionY)
                coordinates("Factory", factoryX, factoryY)
                coordinates("City", cityX, cityY)
                coordinates("Town", townX, townY)
            }
        }

        center = stackpane {
            scope.nodes.value.forEach {
                add(it)
            }
        }
    }
}
package com.zelkatani.gui.component.fragment

import com.zelkatani.gui.faiconview
import com.zelkatani.model.map.*
import com.zelkatani.model.map.BuildingPositionData.PositionType
import com.zelkatani.model.map.ObjectCoordinate.ObjectType
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableNumberValue
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ButtonBar
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import java.awt.Rectangle
import java.text.NumberFormat
import java.util.concurrent.Callable
import kotlin.math.PI

/**
 * A number with many factors and is large enough.
 */
const val POSITION_FRAGMENT_HEIGHT = 720.0

/**
 * The [Scope] for [PositionFragment].
 */
class PositionScope(
    val nodes: ObjectProperty<MutableList<Node>>,
    val bounds: Rectangle,
    val ratio: Int,
    val provinceId: Int,
    val provinceName: String,
    val positions: Positions
) : Scope() {
    /**
     * The position data for the [provinceId].
     */
    val positionData = positions[provinceId]
        ?: throw RuntimeException("No province defined with province id `$provinceId`.")
}

/**
 * A coordinate property by (x, y, visibility flag).
 */
private typealias CoordinateProperty = Triple<DoubleProperty, DoubleProperty, BooleanProperty>

/**
 * A [Fragment] for editing [Positions].
 */
class PositionFragment : Fragment() {
    override val scope = super.scope as PositionScope

    /**
     * Create a new [CoordinateProperty].
     *
     * @return A [CoordinateProperty] with default values (0.0, 0.0, true).
     */
    private fun coordinateProperty() =
        CoordinateProperty(SimpleDoubleProperty(), SimpleDoubleProperty(), SimpleBooleanProperty(true))

    /**
     * The [Coordinate] contained by this property.
     */
    private val CoordinateProperty.coordinate: Coordinate
        get() = first.value to second.value

    /**
     * The value defining true if the [Coordinate] modeled by this property is at the origin (0, 0), false otherwise.
     */
    private val CoordinateProperty.isZero: Boolean
        get() = first.value == 0.0 && second.value == 0.0

    /**
     * Set the position values of a [CoordinateProperty] to that of a [Coordinate].
     *
     * @param coordinate The coordinates to set with.
     */
    private fun CoordinateProperty.setCoordinates(coordinate: Coordinate) {
        first.value = coordinate.first
        second.value = coordinate.second
    }

    private val textNameProperty = SimpleStringProperty(scope.provinceName)
    private val textPositionProperty = coordinateProperty()
    private val textRotationProperty = SimpleDoubleProperty()
    private val textScaleProperty = SimpleDoubleProperty()

    private val unitCoordinateProperty = coordinateProperty()
    private val cityCoordinateProperty = coordinateProperty()
    private val buildingConstructionCoordinateProperty = coordinateProperty()
    private val militaryConstructionCoordinateProperty = coordinateProperty()
    private val factoryCoordinateProperty = coordinateProperty()
    private val townCoordinateProperty = coordinateProperty()

    private val fortRotationProperty = SimpleDoubleProperty()
    private val navalBaseRotationProperty = SimpleDoubleProperty()
    private val railroadRotationProperty = SimpleDoubleProperty()
    private val aeroplaneFactoryRotationProperty = SimpleDoubleProperty() // UNUSED

    private val fortNudgeProperty = SimpleDoubleProperty()
    private val navalBaseNudgeProperty = SimpleDoubleProperty()
    private val railroadNudgeProperty = SimpleDoubleProperty()
    private val aeroplaneFactoryNudgeProperty = SimpleDoubleProperty() // UNUSED

    private val fortPositionProperty = coordinateProperty()
    private val navalBasePositionProperty = coordinateProperty()
    private val railroadPositionProperty = coordinateProperty()
    // The game NEVER mentions aeroplane_factory positioning... It should be a property but it'll stay unused for now

    /**
     * A [ChangeListener] for [PositionScope.nodes]. Sets the values of [stack]'s children to the new value.
     */
    private val nodesListener: ChangeListener<MutableList<Node>> = ChangeListener { _, _, newValue ->
        stack.children.setAll(newValue)
    }

    /**
     * A [NumberStringConverter] that does not group numbers with commas.
     */
    private val numberStringConverter = NumberStringConverter(NumberFormat.getNumberInstance().apply {
        isGroupingUsed = false
    })

    /**
     * Remove the [nodesListener] on [PositionScope.nodes].
     */
    override fun onUndock() {
        scope.nodes.removeListener(nodesListener)
    }

    /**
     * Bind the properties of [PositionFragment], attach [nodesListener], and set the [title].
     */
    override fun onDock() {
        scope.positionData.forEach(::visitPositionInfo)

        scope.nodes.addListener(nodesListener)
        title = "${scope.provinceId} - ${scope.provinceName}"
    }

    /**
     * Match on a [PositionInfo] to set the [PositionFragment] properties.
     *
     * @param info The property to match on.
     */
    private fun visitPositionInfo(info: PositionInfo) = when (info) {
        is ObjectCoordinate -> visitObjectCoordinate(info)
        is BuildingNudges -> {
            info.transforms.forEach(
                buildingTypeTransform(
                    fortNudgeProperty,
                    navalBaseNudgeProperty,
                    railroadNudgeProperty,
                    aeroplaneFactoryNudgeProperty
                )
            )
        }
        is BuildingRotations -> {
            info.transforms.forEach(
                buildingTypeTransform(
                    fortRotationProperty,
                    navalBaseRotationProperty,
                    railroadRotationProperty,
                    aeroplaneFactoryRotationProperty
                )
            )
        }
        is RailroadVisibility -> Unit
        is BuildingPositions -> info.positions.forEach(::visitBuildingPositionData)
        is SpawnRailwayTrack -> Unit
        is TextRotation -> textRotationProperty.set(info.rotation)
        is TextScale -> textScaleProperty.set(info.scale)
    }

    /**
     * Provide a function for a [BuildingTransform].
     *
     * @param fort The fort property.
     * @param naval The naval base property.
     * @param rail The railroad property.
     * @param aero The aeroplane property.
     */
    private fun buildingTypeTransform(
        fort: DoubleProperty,
        naval: DoubleProperty,
        rail: DoubleProperty,
        aero: DoubleProperty
    ): (BuildingTransform) -> Unit = { bt ->
        when (bt.first) {
            BuildingType.FORT -> fort
            BuildingType.NAVAL_BASE -> naval
            BuildingType.RAILROAD -> rail
            BuildingType.AEROPLANE_FACTORY -> aero
        }.value = bt.second
    }

    /**
     * Match on an [ObjectCoordinate] to set the [PositionFragment] properties.
     *
     * @param oc The property to match on.
     */
    private fun visitObjectCoordinate(oc: ObjectCoordinate) = when (oc.type) {
        ObjectType.UNIT -> unitCoordinateProperty
        ObjectType.TEXT -> textPositionProperty
        ObjectType.BUILDING_CONSTRUCTION -> buildingConstructionCoordinateProperty
        ObjectType.MILITARY_CONSTRUCTION -> militaryConstructionCoordinateProperty
        ObjectType.FACTORY -> factoryCoordinateProperty
        ObjectType.CITY -> cityCoordinateProperty
        ObjectType.TOWN -> townCoordinateProperty
    }.setCoordinates(oc.coordinate)

    /**
     * Match on an [BuildingPositionData] to set the [PositionFragment] properties.
     *
     * @param bpd The property to match on.
     */
    private fun visitBuildingPositionData(bpd: BuildingPositionData) = when (bpd.positionType) {
        PositionType.FORT -> fortPositionProperty
        PositionType.NAVAL_BASE -> navalBasePositionProperty
        PositionType.RAILROAD -> railroadPositionProperty
    }.setCoordinates(bpd.coordinate)

    /**
     * A change formatter for numbers. Disallows non-digits and negative numbers.
     */
    private val numberFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.let {
            it.isDouble() && it.toDouble() >= 0
        }
    }

    /**
     * Provide an [EventHandler] for key presses for the textfield provided.
     */
    private fun TextField.setKeyPressedEvent() {
        setOnKeyPressed {
            if (it.code == KeyCode.UP || it.code == KeyCode.DOWN) {
                val initial = numberStringConverter.fromString(text)?.toDouble() ?: return@setOnKeyPressed
                text = when (it.code) {
                    KeyCode.UP -> initial + 1
                    KeyCode.DOWN -> if (initial <= 1) 0.0 else initial - 1
                    else -> throw RuntimeException("Unreachable code")
                }.toString()
            }
        }
    }

    /**
     * Attach a series of fields for editing [CoordinateProperty]'s.
     *
     * @param name The [Field] name.
     * @param coordinateProperty The property to make fields for.
     */
    private fun Fieldset.coordinates(name: String, coordinateProperty: CoordinateProperty) {
        field("$name:") {
            textfield(coordinateProperty.first, numberStringConverter) {
                prefWidth = 100.0
                filterInput(numberFilter)

                setKeyPressedEvent()
            }

            textfield(coordinateProperty.second, numberStringConverter) {
                prefWidth = 100.0
                filterInput(numberFilter)

                setKeyPressedEvent()
            }

            checkbox(property = coordinateProperty.third)
        }
    }

    /**
     * Attach a field for transforming [DoubleProperty]'s.
     *
     * @param name The [Field] name.
     * @param transform The property to make a field for.
     */
    private fun Fieldset.transform(name: String, transform: DoubleProperty) {
        field("$name:") {
            textfield(transform, numberStringConverter) {
                filterInput(numberFilter)

                setKeyPressedEvent()
            }
        }
    }

    /**
     * Create a [DoubleBinding] defined by the transform of the value in [this] by [op].
     *
     * @param op The transformation function.
     * @return A [DoubleBinding] from the application of [op] onto [this].
     */
    private fun DoubleProperty.map(op: (Double) -> Double): DoubleBinding = Bindings.createDoubleBinding(Callable {
        op(get())
    }, this)

    /**
     * Attach a field for modifying [DoubleProperty]'s that define rotation in radians.
     *
     * @param name The [Field] name.
     * @param rotation The property to make a rotation field for.
     */
    private fun Fieldset.rotation(name: String, rotation: DoubleProperty) {
        field("$name:") {
            val c = circle(radius = 112.5) {
                fill = Color.WHITE
                stroke = Color.BLACK
                strokeWidth = 1.0
            }

            line {
                isManaged = false
                stroke = Color.CRIMSON

                startXProperty().bind(c.layoutXProperty())
                startYProperty().bind(c.layoutYProperty())

                val ex = startXProperty() + (c.radiusProperty() * rotation.map(Math::cos))
                val ey = startYProperty() - (c.radiusProperty() * rotation.map(Math::sin))
                endXProperty().bind(ex)
                endYProperty().bind(ey)

                val rotateEvent = EventHandler<MouseEvent> {
                    val rx = c.radius + it.x - startX
                    val ry = c.radius + it.y - startY
                    val rot = Math.atan2(ry, rx)
                    rotation.value = -rot
                }

                c.onMouseDragged = rotateEvent
                c.onMouseClicked = rotateEvent
            }
        }
    }

    /**
     * Correct the origin of [this] to point to the top left of the canvas.
     *
     * @param cb The bottom of the canvas.
     * @return The corrected bindings.
     */
    private fun CoordinateProperty.subtractBounds(cb: ObservableNumberValue): Pair<DoubleBinding, DoubleBinding> {
        val x = (first - scope.bounds.x) * scope.ratio
        val y = ((second - scope.bounds.y) * -scope.ratio) + cb

        return x to y
    }

    /**
     * A binding of the layoutWidth of a [Text.layoutBoundsProperty].
     */
    private val Text.layoutWidthProperty: DoubleBinding
        get() {
            val lb = layoutBoundsProperty()
            return Bindings.createDoubleBinding(Callable {
                lb.get().width
            }, lb)
        }

    /**
     * Attach text for viewing a [CoordinateProperty].
     *
     * @param position The coordinates to work with.
     * @param scale The scale of the text.
     * @param rotation The rotation of the text.
     * @param canvasBottom The bottom of the canvas.
     */
    private fun Parent.text(
        position: CoordinateProperty,
        scale: DoubleProperty,
        rotation: DoubleProperty,
        canvasBottom: ObservableNumberValue
    ) {
        val (x, y) = position.subtractBounds(canvasBottom)
        text(textNameProperty) {
            isManaged = false

            visibleProperty().bind(position.third)
            rotateProperty().bind(-(rotation * 180 / PI) + 360)

            layoutXProperty().bind(x - layoutWidthProperty / 2)
            layoutYProperty().bind(y)

            scale.onChange {
                style = "-fx-font-size: ${it * scope.ratio * 1.2}px;"
            }
        }
    }

    /**
     * Attach a point for editing a [CoordinateProperty] without text.
     *
     * @param position The coordinates to work with.
     * @param propertyName The name of the property.
     * @param icon The icon to display.
     * @param canvasBottom The bottom of the canvas.
     */
    private fun Parent.point(
        position: CoordinateProperty,
        propertyName: String,
        icon: FontAwesomeIcon,
        canvasBottom: ObservableNumberValue
    ) {
        val (x, y) = position.subtractBounds(canvasBottom)
        text(propertyName) {
            isManaged = false

            visibleProperty().bind(position.third)
            layoutXProperty().bind(x - layoutWidthProperty / 2)
            layoutYProperty().bind(y - font.size)
        }

        faiconview(icon, "2em") {
            isManaged = false

            visibleProperty().bind(position.third)

            val c = convert("2em").toDouble() / 4
            layoutXProperty().bind(x - c)
            layoutYProperty().bind(y + c)
        }
    }

    /**
     * Bind all position properties of [PositionFragment] to a [StackPane].
     *
     * @param canvasBottom The bottom of the canvas.
     */
    private fun StackPane.bindPositionProperties(canvasBottom: ObservableNumberValue) {
        point(unitCoordinateProperty, "Unit Position", FontAwesomeIcon.STREET_VIEW, canvasBottom)
        point(buildingConstructionCoordinateProperty, "Building Construction", FontAwesomeIcon.WRENCH, canvasBottom)
        point(militaryConstructionCoordinateProperty, "Military Construction", FontAwesomeIcon.TRUCK, canvasBottom)
        point(factoryCoordinateProperty, "Factory", FontAwesomeIcon.INDUSTRY, canvasBottom)
        point(cityCoordinateProperty, "City", FontAwesomeIcon.HOME, canvasBottom)
        point(townCoordinateProperty, "Town", FontAwesomeIcon.FLAG, canvasBottom)

        point(fortPositionProperty, "Fort", FontAwesomeIcon.FORT_AWESOME, canvasBottom)
        point(navalBasePositionProperty, "Naval Base", FontAwesomeIcon.ANCHOR, canvasBottom)
        point(railroadPositionProperty, "Railroad", FontAwesomeIcon.SUBWAY, canvasBottom)

        text(textPositionProperty, textScaleProperty, textRotationProperty, canvasBottom)
    }

    /**
     * The [StackPane] containing the [PositionScope.nodes].
     */
    private lateinit var stack: StackPane

    /**
     * Add an [ObjectCoordinate] if the [Coordinate] is non-zero
     *
     * @param objectType The type of coordinate.
     * @param coordinateProperty The property containing a [Coordinate].
     */
    private fun MutableList<PositionInfo>.addNonZero(objectType: ObjectType, coordinateProperty: CoordinateProperty) {
        if (!coordinateProperty.isZero) this += ObjectCoordinate(objectType, coordinateProperty.coordinate)
    }

    /**
     * Get a list of [BuildingTransform]'s that are all non-zero transformations.
     *
     * @param fort The [BuildingType.FORT] transform.
     * @param naval The [BuildingType.NAVAL_BASE] transform.
     * @param rail The [BuildingType.RAILROAD] transform.
     * @param aero The [BuildingType.AEROPLANE_FACTORY] transform.
     *
     * @return All transformations that are non-zero.
     */
    private fun getBuildingTransforms(
        fort: DoubleProperty,
        naval: DoubleProperty,
        rail: DoubleProperty,
        aero: DoubleProperty
    ): List<BuildingTransform> = mutableListOf<BuildingTransform>().apply {
        if (fort.value != 0.0) this += BuildingTransform(BuildingType.FORT, fort.value)
        if (naval.value != 0.0) this += BuildingTransform(BuildingType.NAVAL_BASE, naval.value)
        if (rail.value != 0.0) this += BuildingTransform(BuildingType.RAILROAD, rail.value)
        if (aero.value != 0.0) this += BuildingTransform(BuildingType.AEROPLANE_FACTORY, aero.value)
    }

    /**
     * Get a list of [BuildingPositionData]'s that are all non-zero positions.
     *
     * @return All building positions that are non-zero.
     */
    private fun getBuildingPositionData(): List<BuildingPositionData> =
        mutableListOf<BuildingPositionData>().apply {
            if (!fortPositionProperty.isZero) this += BuildingPositionData(
                PositionType.FORT,
                fortPositionProperty.coordinate
            )

            if (!navalBasePositionProperty.isZero) this += BuildingPositionData(
                PositionType.NAVAL_BASE,
                navalBasePositionProperty.coordinate
            )

            if (!railroadPositionProperty.isZero) this += BuildingPositionData(
                PositionType.RAILROAD,
                railroadPositionProperty.coordinate
            )
        }

    /**
     * Create a [PositionData] from the properties of [PositionFragment].
     *
     * @return A [PositionData] containing non-zero fields.
     */
    private fun buildPositionData(): PositionData = mutableListOf<PositionInfo>().apply {
        addNonZero(ObjectType.TEXT, textPositionProperty)
        if (textRotationProperty.value != 0.0) this += TextRotation(textRotationProperty.value)
        if (textScaleProperty.value != 0.0) this += TextScale(textScaleProperty.value)
        addNonZero(ObjectType.UNIT, unitCoordinateProperty)
        addNonZero(ObjectType.CITY, cityCoordinateProperty)
        addNonZero(ObjectType.BUILDING_CONSTRUCTION, buildingConstructionCoordinateProperty)
        addNonZero(ObjectType.FACTORY, factoryCoordinateProperty)
        addNonZero(ObjectType.TOWN, townCoordinateProperty)

        val buildingPositionData = getBuildingPositionData()
        if (buildingPositionData.isNotEmpty()) {
            this += BuildingPositions(buildingPositionData)
        }

        val rotationData = getBuildingTransforms(
            fortRotationProperty,
            navalBaseRotationProperty,
            railroadRotationProperty,
            aeroplaneFactoryRotationProperty
        )
        if (rotationData.isNotEmpty()) {
            this += BuildingRotations(rotationData)
        }

        val nudgeData = getBuildingTransforms(
            fortNudgeProperty,
            navalBaseNudgeProperty,
            railroadNudgeProperty,
            aeroplaneFactoryNudgeProperty
        )
        if (rotationData.isNotEmpty()) {
            this += BuildingNudges(nudgeData)
        }
    }

    override val root = borderpane {
        left = vbox {
            scrollpane(fitToWidth = true) {
                prefViewportHeight = POSITION_FRAGMENT_HEIGHT

                form {
                    fieldset("Text", FontAwesomeIconView(FontAwesomeIcon.TEXT_WIDTH)) {
                        field("Name:") {
                            textfield(textNameProperty)
                        }
                        coordinates("Position", textPositionProperty)
                        rotation("Rotation", textRotationProperty)
                        transform("Scale", textScaleProperty)
                    }

                    fieldset("Object Positions", FontAwesomeIconView(FontAwesomeIcon.ARROWS_ALT)) {
                        coordinates("Unit", unitCoordinateProperty)
                        coordinates("Building Construction", buildingConstructionCoordinateProperty)
                        coordinates("Military Construction", militaryConstructionCoordinateProperty)
                        coordinates("Factory", factoryCoordinateProperty)
                        coordinates("City", cityCoordinateProperty)
                        coordinates("Town", townCoordinateProperty)
                    }

                    fieldset("Building Position", FontAwesomeIconView(FontAwesomeIcon.MAP_MARKER)) {
                        coordinates("Fort", fortPositionProperty)
                        coordinates("Naval Base", navalBasePositionProperty)
                        coordinates("Railroad", railroadPositionProperty)
                    }

                    fieldset("Building Rotation", FontAwesomeIconView(FontAwesomeIcon.UNDO)) {
                        rotation("Fort", fortRotationProperty)
                        rotation("Naval Base", navalBaseRotationProperty)
                        rotation("Railroad", railroadRotationProperty)
                    }
                }
            }

            buttonbar {
                padding = Insets(10.0)

                button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE).action {
                    this@PositionFragment.close()
                }

                button("Save", ButtonBar.ButtonData.OK_DONE) {
                    isDefaultButton = true

                    action {
                        val newData = buildPositionData()
                        scope.positions[scope.provinceId] = newData
                        Positions.toFile(scope.positions)

                        this@PositionFragment.close()
                    }
                }
            }
        }

        center = scrollpane {
            if (scope.bounds.width * scope.ratio > POSITION_FRAGMENT_HEIGHT * 2) {
                prefViewportWidth = POSITION_FRAGMENT_HEIGHT * 2
            }

            // if it ever appears, its likely because of padding in the way
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER

            stackpane {
                stack = stackpane {
                    scope.nodes.value.forEach {
                        add(it)
                    }
                }

                bindPositionProperties(heightProperty())
            }
        }
    }
}
package com.zelkatani.gui.component

import com.zelkatani.gui.component.fragment.POSITION_FRAGMENT_HEIGHT
import com.zelkatani.gui.component.fragment.PositionFragment
import com.zelkatani.gui.component.fragment.PositionScope
import com.zelkatani.model.Localization
import com.zelkatani.model.LocalizationLanguage
import com.zelkatani.model.map.WorldMap
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.add
import tornadofx.find
import tornadofx.toProperty
import java.awt.Rectangle
import java.lang.Math.round
import kotlin.math.roundToInt

/**
 * The map component that visualizes [WorldMap] instances, with [Localization] names.
 */
class MapPane(
    private val worldMap: WorldMap,
    private val localization: Localization,
    private val positionFragmentProperty: ObjectProperty<PositionFragment?>
) : StackPane() {

    /**
     * An [ImageView] storing provinces.bmp.
     */
    private val provincesImageView = ImageView(worldMap.provincesBMP.image)

    /**
     * An [ImageView] storing terrain.bmp.
     */
    private val terrainImageView = ImageView(worldMap.terrainBMP.image)

    /**
     * An [ImageView] storing rivers.bmp.
     */
    private val riversImageView = ImageView(worldMap.riversBMP.image)

    /**
     * An [ImageView] for a [PositionFragment] containing a section of provinces.bmp
     */
    private val positionProvincesView = ImageView()

    /**
     * An [ImageView] for a [PositionFragment] containing a section of terrain.bmp
     */
    private val positionTerrainView = ImageView()

    /**
     * An [ImageView] for a [PositionFragment] containing a section of rivers.bmp
     */
    private val positionRiversView = ImageView()

    /**
     * An [ObjectProperty] containing a re-arrangeable order of [Node]'s for a [PositionFragment]
     */
    val positionFragmentChildren: ObjectProperty<MutableList<Node>> =
        mutableListOf<Node>(
            positionRiversView,
            positionTerrainView,
            positionProvincesView
        ).toProperty()


    /**
     * Copy an [ImageView]'s y-scale, [ImageView.opacityProperty], and [ImageView.blendModeProperty]
     *
     * @param iv The [ImageView] to mimic.
     */
    private fun ImageView.setPositionProperties(iv: ImageView) {
        scaleY = iv.scaleY
        opacityProperty().bind(iv.opacityProperty())
        blendModeProperty().bind(iv.blendModeProperty())
    }

    /**
     * The opacity property of [provincesImageView].
     */
    val provincesOpacityProperty: DoubleProperty = provincesImageView.opacityProperty()

    /**
     * The opacity property of [terrainImageView].
     */
    val terrainOpacityProperty: DoubleProperty = terrainImageView.opacityProperty()

    /**
     * The opacity property of [riversImageView].
     */
    val riversOpacityProperty: DoubleProperty = riversImageView.opacityProperty()

    /**
     * The blend mode property of [provincesImageView].
     */
    val provincesBlendModeProperty: ObjectProperty<BlendMode> = provincesImageView.blendModeProperty()

    /**
     * The blend mode property of [terrainImageView].
     */
    val terrainBlendModeProperty: ObjectProperty<BlendMode> = terrainImageView.blendModeProperty()

    /**
     * The blend mode property of [riversImageView].
     */
    val riversBlendModeProperty: ObjectProperty<BlendMode> = riversImageView.blendModeProperty()

    /**
     * An [EventHandler] for a [MouseEvent] when a province is clicked on the editor view.
     * It only triggers on double clicks and when given a province, defines the value of the
     * [positionFragmentProperty] by a call to [getPositionFragment].
     */
    private val provinceClickEvent = EventHandler<MouseEvent> {
        if (it.clickCount < 2) return@EventHandler

        val image = provincesImageView.image
        val provinceReader = image.pixelReader

        val x = round(it.x).toInt()
        val y = round(image.height - it.y).toInt()

        val color = provinceReader.getColor(x, y)
        positionFragmentProperty.value = getPositionFragment(color)
    }

    // Set correct scales, add in correct order to the stackpane
    init {
        provincesImageView.scaleY = -1.0
        terrainImageView.scaleY = -1.0
        riversImageView.scaleY = -1.0

        positionProvincesView.setPositionProperties(provincesImageView)
        positionTerrainView.setPositionProperties(terrainImageView)
        positionRiversView.setPositionProperties(riversImageView)

        add(riversImageView)
        add(terrainImageView)
        add(provincesImageView)

        onMouseClicked = provinceClickEvent
    }

    /**
     * Define a [Rectangle] that is the bounds of [color] in the map.
     *
     * @param color The color to get a bounded box of
     * @return A [Rectangle] containing the color's bounding box.
     */
    private fun getColorBounds(color: Color): Rectangle {
        var minX = Int.MAX_VALUE
        var maxX = 0
        var minY = Int.MAX_VALUE
        var maxY = 0

        val provinces = worldMap.provincesBMP
        provinces.forEach {
            val pColor = provinces[it]
            // this checks float equality... if this doesn't work for everything, write a different checker.
            if (pColor == color) {
                val (x, y) = it
                if (x < minX) minX = x
                if (x > maxX) maxX = x
                if (y < minY) minY = y
                if (y > maxY) maxY = y
            }
        }

        return Rectangle(
            minY,
            minX,
            maxY - minY,
            maxX - minX
        )
    }

    /**
     * Set the [ImageView.image] property by constructing a new image
     * from the [imageView] while taking a chunk that is [bounds] scaled by [zoom], with
     * 1 pixel going to [zoom] pixels.
     *
     * @param bounds The bounding rectangle for [imageView].
     * @param imageView The [ImageView] to read from.
     * @param zoom The zoom factor.
     */
    private fun ImageView.zoomBounds(bounds: Rectangle, imageView: ImageView, zoom: Int) {
        val readerImage = imageView.image
        val reader = readerImage.pixelReader
        val writableImage = WritableImage(bounds.width * zoom, bounds.height * zoom)
        val writer = writableImage.pixelWriter

        for (x in 0 until bounds.width) {
            for (y in 0 until bounds.height) {
                val xi = x + bounds.x
                val yi = y + bounds.y
                val argb =
                    if (xi < 0 || yi < 0 || xi >= readerImage.width.toInt() || yi >= readerImage.height.toInt()) {
                        Int.MIN_VALUE
                    } else {
                        reader.getArgb(xi, yi)
                    }
                for (i in 0 until zoom) {
                    for (j in 0 until zoom) {
                        writer.setArgb(x * zoom + i, y * zoom + j, argb)
                    }
                }
            }
        }

        image = writableImage
    }

    /**
     * Create a [PositionFragment] from the [color].
     *
     * @param color The color to construct a zoomed view of
     * @return A [PositionFragment] which is the up-scaled province bound to [color].
     */
    private fun getPositionFragment(color: Color): PositionFragment {
        val bounds = getColorBounds(color)
        bounds.grow(10, 10)

        val ratio = (POSITION_FRAGMENT_HEIGHT / bounds.height).roundToInt()

        positionProvincesView.zoomBounds(bounds, provincesImageView, ratio)
        positionTerrainView.zoomBounds(bounds, terrainImageView, ratio)
        positionRiversView.zoomBounds(bounds, riversImageView, ratio)

        val colorRecord = worldMap.definition.colors[color]
        colorRecord ?: throw RuntimeException("Color should have an associated province id, but does not have one.")

        val provinceId = colorRecord.province
        val positionData = worldMap.positions[provinceId]

        val provinceString = "PROV$provinceId"
        val provinceName = localization[provinceString, LocalizationLanguage.ENGLISH] ?: provinceString

        // TODO: is this actually required or is there just some default defined if none?
        positionData ?: throw RuntimeException("Province has no position data defined.")

        val positionScope =
            PositionScope(positionFragmentChildren, bounds, ratio, provinceId, provinceName, positionData)
        return find(positionScope)
    }
}

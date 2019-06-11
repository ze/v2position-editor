package com.zelkatani.gui.pane

import com.zelkatani.gui.fragment.POSITION_FRAGMENT_HEIGHT
import com.zelkatani.gui.fragment.PositionFragment
import com.zelkatani.gui.fragment.PositionScope
import com.zelkatani.model.Localization
import com.zelkatani.model.LocalizationLanguage
import com.zelkatani.model.map.WorldMap
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.add
import tornadofx.find
import java.awt.Rectangle
import java.lang.Math.round
import kotlin.math.roundToInt

/**
 * The map pane that visualizes [WorldMap] instances.
 */
class MapPane(
    private val worldMap: WorldMap,
    private val localization: Localization,
    private val positionFragmentProperty: ObjectProperty<PositionFragment?>
) :
    StackPane() {

    private val canvas = Canvas()

    private val provincesImageView = ImageView(worldMap.provincesBMP.image)
    private val terrainImageView = ImageView(worldMap.terrainBMP.image)
    private val riversImageView = ImageView(worldMap.riversBMP.image)

    // the same imageviews propagate through each PositionFragment
    // they are the same as their above counterpart but with a different
    // viewport associated with them. Other than that, they are effectively
    // the same.
    private val positionProvincesView = ImageView()
    private val positionTerrainView = ImageView()
    private val positionRiversView = ImageView()
    private val positionCanvas = Canvas()
    val positionFragmentChildren: ObjectProperty<MutableList<Node>> =
        SimpleObjectProperty(
            mutableListOf(
                positionRiversView,
                positionTerrainView,
                positionProvincesView,
                positionCanvas
            )
        )

    private fun ImageView.setPositionProperties(iv: ImageView) {
        scaleY = iv.scaleY
        opacityProperty().bind(iv.opacityProperty())
        blendModeProperty().bind(iv.blendModeProperty())
    }

    // Set correct scales
    init {
        provincesImageView.scaleY = -1.0
        terrainImageView.scaleY = -1.0
        riversImageView.scaleY = -1.0

        positionProvincesView.setPositionProperties(provincesImageView)
        positionTerrainView.setPositionProperties(terrainImageView)
        positionRiversView.setPositionProperties(riversImageView)
    }

    val provincesOpacityProperty: DoubleProperty = provincesImageView.opacityProperty()
    val terrainOpacityProperty: DoubleProperty = terrainImageView.opacityProperty()
    val riversOpacityProperty: DoubleProperty = riversImageView.opacityProperty()

    val provincesBlendModeProperty: ObjectProperty<BlendMode> = provincesImageView.blendModeProperty()
    val terrainBlendModeProperty: ObjectProperty<BlendMode> = terrainImageView.blendModeProperty()
    val riversBlendModeProperty: ObjectProperty<BlendMode> = riversImageView.blendModeProperty()

    // Add in correct order to the stackpane, but make sure canvas has right dimensions
    init {
        canvas.width = worldMap.provincesBMP.width.toDouble()
        canvas.height = worldMap.provincesBMP.height.toDouble()

        add(riversImageView)
        add(terrainImageView)
        add(provincesImageView)
        add(canvas)
    }

    private val provinceClickEvent = EventHandler<MouseEvent> {
        if (it.clickCount < 2) return@EventHandler

        val image = provincesImageView.image
        val provinceReader = image.pixelReader

        val x = round(it.x).toInt()
        val y = round(image.height - it.y).toInt()

        val color = provinceReader.getColor(x, y)
        positionFragmentProperty.value = getPositionFragment(color)
    }

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

    /**
     * Register click event to handle position editing.
     */
    init {
        onMouseClicked = provinceClickEvent
    }
}

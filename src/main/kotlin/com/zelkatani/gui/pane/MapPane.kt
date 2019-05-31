package com.zelkatani.gui.pane

import com.zelkatani.gui.fragment.PositionFragment
import com.zelkatani.gui.fragment.PositionScope
import com.zelkatani.model.map.WorldMap
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.add
import tornadofx.find
import java.lang.Math.round

/**
 * The map pane that visualizes [WorldMap] instances.
 */
class MapPane(private val worldMap: WorldMap, private val positionFragmentProperty: ObjectProperty<PositionFragment?>) :
    StackPane() {

    private val canvas = Canvas()

    private val provincesImageView = ImageView(worldMap.provincesBMP.image)
    private val terrainImageView = ImageView(worldMap.terrainBMP.image)
    private val riversImageView = ImageView(worldMap.riversBMP.image)

    // the same imageviews propagate through each PositionFragment
    // they are the same as their above counterpart but with a different
    // viewport associated with them. Other than that, they are effectively
    // the same.
    private val positionProvincesView = ImageView(worldMap.provincesBMP.image)
    private val positionTerrainView = ImageView(worldMap.terrainBMP.image)
    private val positionRiversView = ImageView(worldMap.riversBMP.image)
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

    private fun ImageView.mirrorProperties(iv: ImageView) {
        scaleY = iv.scaleY
        opacityProperty().bind(iv.opacityProperty())
        blendModeProperty().bind(iv.blendModeProperty())
    }

    // Set correct scales
    init {
        provincesImageView.scaleY = -1.0
        terrainImageView.scaleY = -1.0
        riversImageView.scaleY = -1.0

        positionProvincesView.mirrorProperties(provincesImageView)
        positionTerrainView.mirrorProperties(terrainImageView)
        positionRiversView.mirrorProperties(riversImageView)
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

    private fun getColorBounds(color: Color): Rectangle2D {
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

        return Rectangle2D(minY.toDouble(), minX.toDouble(), (maxY - minY).toDouble(), (maxX - minX).toDouble())
    }

    private fun getPositionFragment(color: Color): PositionFragment {
        val bounds = getColorBounds(color)

        positionProvincesView.viewport = bounds
        positionTerrainView.viewport = bounds
        positionRiversView.viewport = bounds

        val positionScope = PositionScope(positionFragmentChildren)
        return find(positionScope)
    }

    /**
     * Register click event to handle position editing.
     */
    init {
        onMouseClicked = provinceClickEvent
    }
}

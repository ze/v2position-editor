package com.zelkatani.gui.pane

import com.zelkatani.model.map.WorldMap
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import tornadofx.add
import java.lang.Math.round

/**
 * The map pane that visualizes [WorldMap] instances.
 */
class MapPane(worldMap: WorldMap) : StackPane() {
    private val canvas = Canvas()

    private val provincesImageView = ImageView(worldMap.provincesBMP.image)
    private val terrainImageView = ImageView(worldMap.terrainBMP.image)
    private val riversImageView = ImageView(worldMap.riversBMP.image)

    // Set correct scales
    init {
        provincesImageView.scaleY = -1.0
        terrainImageView.scaleY = -1.0
        riversImageView.scaleY = -1.0
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

    init {
        val image = provincesImageView.image
        val provinceReader = image.pixelReader
        setOnMouseClicked {
            val x = round(it.x).toInt()
            val y = round(image.height - it.y).toInt()
            val color = provinceReader.getColor(x, y)
            val provinceId = worldMap.definition.colors[color]
            println("$color -> ${provinceId?.province}")
        }
    }
}

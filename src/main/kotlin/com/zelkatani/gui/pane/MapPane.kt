package com.zelkatani.gui.pane

import com.zelkatani.model.map.WorldMap
import javafx.event.EventTarget
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import tornadofx.add
import tornadofx.imageview
import tornadofx.opcr


class MapPane(worldMap: WorldMap) : StackPane() {
    private val canvas = Canvas()

    private val scaleY = { imageView: ImageView ->
        imageView.scaleY = -1.0
    }

    private val terrainImageView = imageview(worldMap.terrainBMP.image, scaleY)
    private val riversImageView = imageview(worldMap.riversBMP.image, scaleY)
    private val provincesImageView = imageview(worldMap.provincesBMP.image, scaleY)

    val terrainOpacityProperty = terrainImageView.opacityProperty()
    val riversOpacityProperty = riversImageView.opacityProperty()
    val provincesOpacityProperty = provincesImageView.opacityProperty()

    init {
        canvas.width = worldMap.provincesBMP.width.toDouble()
        canvas.height = worldMap.provincesBMP.height.toDouble()

        add(terrainImageView)
        add(riversImageView)
        add(provincesImageView)
        add(canvas)
    }
}

fun EventTarget.mappane(worldMap: WorldMap, op: MapPane.() -> Unit = {}) =
    opcr(this, MapPane(worldMap), op)


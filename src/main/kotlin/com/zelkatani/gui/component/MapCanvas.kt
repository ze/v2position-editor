package com.zelkatani.gui.component

import com.zelkatani.model.map.Bitmap
import javafx.event.EventTarget
import javafx.scene.canvas.Canvas
import tornadofx.opcr

class MapCanvas : Canvas() {
    private val gc = graphicsContext2D
    val layers: MutableList<Layer> = mutableListOf()

    private fun clear() {
        gc.clearRect(0.0, 0.0, width, height)
    }

    fun draw() {
        clear()

        layers.map(::drawLayer)
    }

    private fun drawLayer(layer: Layer) {
        if (!layer.isVisible) return

        val bitmap = layer.bitmap
        gc.globalAlpha = layer.opacity
        bitmap.forEach {
            val color = bitmap[it]

            val (x, y) = it
            gc.fill = color
            gc.fillRect(y.toDouble(), bitmap.height - x.toDouble(), 1.0, 1.0)
        }
    }
}

class Layer(val bitmap: Bitmap, var isVisible: Boolean = true, var opacity: Double = 1.0)

fun EventTarget.mapCanvas(op: MapCanvas.() -> Unit = {}) =
    opcr(this, MapCanvas(), op)

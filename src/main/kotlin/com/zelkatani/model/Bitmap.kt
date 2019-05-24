package com.zelkatani.model

import javafx.scene.paint.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO

/**
 * A coordinate point for (x, y)  grids.
 */
typealias Point = Pair<Int, Int>

/**
 * A Bitmap model for .bmp images
 */
class Bitmap(bitmapFile: File) : Iterable<Point> {
    val bitmap: BufferedImage = ImageIO.read(bitmapFile)
    private val bitmapData = (bitmap.raster.dataBuffer as DataBufferByte).data

    private val width = bitmap.width
    private val height = bitmap.height

    private val colorMap = HashMap<Int, Color>(500)

    operator fun get(point: Point) = get(point.first, point.second)
    operator fun get(row: Int, col: Int): Color {
        require(row in 0 until height) {
            "Row $row is out of bounds. Bitmap height is: $height"
        }

        require(col in 0 until width) {
            "Column $col is out of bounds. Bitmap width is: $width"
        }

        val flatIndex = (col + row * width) * 3
        val blue = bitmapData[flatIndex].toInt() and 0xff
        val green = bitmapData[flatIndex + 1].toInt() and 0xff shl 8
        val red = bitmapData[flatIndex + 2].toInt() and 0xff shl 16

        val color = blue + green + red - 16777216

        return colorMap.computeIfAbsent(color) {
            val goodRed = red / (2 * Short.MAX_VALUE) / 255.0
            val goodGreen = green / 255 / 256.0
            val goodBlue = blue / 255.0
            Color(goodRed, goodGreen, goodBlue, 1.0)
        }
    }

    inline fun forEachColor(block: (Color) -> Unit) = forEach {
        val color = get(it)
        block(color)
    }

    override fun iterator() = BitmapIterator(width = width, height = height)
}

/**
 * An iterator for any rectangular grid, returning [Point]'s from the values in rows from left to right,
 * and top to bottom for each row.
 */
class BitmapIterator(
    private var row: Int = 0,
    private var col: Int = 0,
    private val width: Int,
    private val height: Int
) : Iterator<Point> {

    override fun next(): Point {
        val point = Point(row, col)

        col++
        if (col == width) {
            col = 0
            row++
        }

        return point
    }

    override fun hasNext() = (col + row * width) < width * height && row < height
}
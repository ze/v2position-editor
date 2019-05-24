package com.zelkatani

import com.zelkatani.model.Bitmap
import com.zelkatani.model.Defaults
import java.io.File
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    println("I'm a map editor!")

    require(args.isNotEmpty()) {
        "No default file provided"
    }

    val defaultsFile = File(args[0])
    val defaults = Defaults.from(defaultsFile)

    val provinces = Bitmap(File(defaults.provinces))

    provinces.forEachColor {
        println("${(it.red * 255).roundToInt()}, ${(it.green * 255).roundToInt()}, ${(it.blue * 255).roundToInt()}")
    }
}
package com.zelkatani

import com.zelkatani.model.WorldMap
import java.io.File

fun main(args: Array<String>) {
    require(args.isNotEmpty()) {
        "No arguments provided."
    }

    val mapFolder = File(args[0])
    WorldMap.from(mapFolder)
    println("map/ was successfully parsed.")
}
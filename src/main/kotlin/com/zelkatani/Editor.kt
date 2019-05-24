package com.zelkatani

import com.zelkatani.model.Defaults
import java.io.File

fun main(args: Array<String>) {
    println("I'm a map editor!")

    require(args.isNotEmpty()) {
        "No default file provided"
    }

    val defaultsFile = File(args[0])
    val defaults = Defaults.from(defaultsFile)
    println(defaults.provinces)
}
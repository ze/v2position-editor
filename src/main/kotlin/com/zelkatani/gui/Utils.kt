package com.zelkatani.gui

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.event.EventTarget
import tornadofx.opcr

fun EventTarget.faiconview(
    icon: FontAwesomeIcon,
    size: String,
    op: FontAwesomeIconView.() -> Unit = {}
): FontAwesomeIconView {
    val iconView = FontAwesomeIconView(icon)
    iconView.size = size
    return opcr(this, iconView, op)
}
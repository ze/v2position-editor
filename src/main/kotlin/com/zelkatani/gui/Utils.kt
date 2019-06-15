package com.zelkatani.gui

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.event.EventTarget
import tornadofx.opcr

/**
 * Create a [FontAwesomeIconView].
 *
 * @param icon The icon to attach.
 * @param size The size of the icon.
 * @param op Any extra configurations.
 *
 * @return A [FontAwesomeIconView] with presets defined attached to [EventTarget].
 */
fun EventTarget.faiconview(
    icon: FontAwesomeIcon,
    size: String,
    op: FontAwesomeIconView.() -> Unit = {}
): FontAwesomeIconView {
    val iconView = FontAwesomeIconView(icon)
    iconView.size = size
    return opcr(this, iconView, op)
}
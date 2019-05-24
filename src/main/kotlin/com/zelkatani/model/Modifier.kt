package com.zelkatani.model

import org.antlr.v4.runtime.tree.TerminalNode

/**
 * A modifier with a [name] that contains some [value].
 */
data class Modifier(val name: String, val value: Any) {
    constructor(terminalNode: TerminalNode, value: Any) : this(terminalNode.text, value)
}

/**
 * A collection of [Modifier]'s.
 */
typealias Modifiers = List<Modifier>
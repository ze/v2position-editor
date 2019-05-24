package com.zelkatani.visitor

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Get the representation of this [TerminalNode] as a string without `"`.
 *
 * It may be simpler to just substring instead, but this ensures that bad calls leave it as is.
 */
fun TerminalNode.asUnquotedString() = text.removeSurrounding("\"")

/**
 * Get the representation of this [TerminalNode] as an integer.
 */
fun TerminalNode.asInt() = text.toInt()

/**
 * Get the representation of this [TerminalNode] as a float.
 */
fun TerminalNode.asFloat() = text.toFloat()

/**
 * Get a [Number] from a specified [intToken] and [floatToken].
 */
fun ParserRuleContext.getNumber(intToken: Int, floatToken: Int): Number {
    val int = getToken(intToken, 0)
    val float = getToken(floatToken, 0)
    check(float != null || int != null) {
        "There must be a FLOAT or INT token."
    }

    return float?.asFloat() ?: int.asInt()
}

val ParserRuleContext.line
    get() = start.line
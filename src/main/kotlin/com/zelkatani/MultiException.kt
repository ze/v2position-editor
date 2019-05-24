package com.zelkatani

/**
 * Exception holder class for cases in which we want to keep record of all exceptions that would have been thrown,
 * rather than the first thrown exception.
 */
class MultiException : Exception(), Iterable<LineException> {
    private val exceptions = mutableListOf<LineException>()

    val size
        get() = exceptions.size

    fun add(string: String, line: Int) {
        exceptions += LineException(string, line)
    }

    operator fun plusAssign(exception: Pair<String, Int>) {
        exceptions += LineException(exception.first, exception.second)
    }

    override fun iterator() = exceptions.iterator()

    override fun toString() = buildString {
        exceptions.forEach {
            appendln(it)
        }
    }
}

/**
 * Require no exception to be held in the [multiException].
 */
fun requireNoExceptions(multiException: MultiException) {
    if (multiException.size != 0) {
        throw multiException
    }
}

/**
 * An exception associated with a line number.
 */
class LineException(message: String, private val line: Int) : Exception(message) {
    override fun toString() = "Error at line $line: $message"
}
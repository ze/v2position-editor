package com.zelkatani

/**
 * Exception holder class for cases in which we want to keep record of all exceptions that would have been thrown,
 * rather than the first thrown exception.
 */
class MultiException : Exception(), Iterable<LineException> {
    /**
     * All exceptions stored so far.
     */
    private val exceptions = mutableListOf<LineException>()

    /**
     * The quantity of exceptions retained.
     */
    val size
        get() = exceptions.size

    /**
     * Add an exception with [message] on [line].
     */
    fun add(message: String, line: Int) {
        exceptions += LineException(message, line)
    }

    /**
     * Create a new exception with message to line.
     */
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
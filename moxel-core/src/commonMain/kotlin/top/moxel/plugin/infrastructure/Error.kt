package top.moxel.plugin.infrastructure

fun systemAssert(condition: Boolean, message: () -> String) {
    if (condition) {
        throw SystemException(message())
    }
}

fun nonFatalAssert(condition: Boolean, message: () -> String) {
    if (condition) {
        throw NonFatalException(message())
    }
}

fun ignorableAssert(condition: Boolean, message: () -> String) {
    if (condition) {
        throw IgnorableException(message())
    }
}

/**
 * SystemException should exit the program immediately
 */
open class SystemException(message: String) : Exception(message)

/**
 * NonFatalException should be caught and handled
 */
open class NonFatalException(message: String) : RuntimeException(message)

/**
 * IgnorableException could be ignored
 */
open class IgnorableException(message: String) : RuntimeException(message)
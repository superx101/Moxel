package top.moxel.plugin.infrastructure

/**
 * SystemException should exit the program immediately
 */
open class SystemException(message: String) : Exception(message)

/**
 * NonFatalException should be caught and handled
 */
open class NonFatalException(message: String) : RuntimeException(message)

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
package top.moxel.plugin.infrastructure

/**
 * SystemException should exit the program immediately
 */
open class SystemException(message: String) : Exception(message)

open class WarningException(message: String) : SystemException(message)

fun systemAssert(condition: Boolean, message: () -> String) {
    if (condition) {
        throw SystemException(message())
    }
}

fun warning(message: () -> String): Unit = throw WarningException(message())

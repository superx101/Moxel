package top.moxel.plugin.annotation.lua

import io.github.oshai.kotlinlogging.KotlinLogging

typealias LuaBindingFunction = (Array<Any?>) -> Any?

object LuaBindingGroup {
    const val EXTENSION = "extension"
    const val EXPRESSION = "expression"
}

/**
 * auto generate Lua bindings list
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class LuaBinding(
    val group: String,
    val name: String = ""
)

fun checkParameters(hasVararg: Boolean, expectNumber: Int, actualNumber: Int) {
    if(hasVararg)
        return
    if (expectNumber == actualNumber)
        return

    val logger = KotlinLogging.logger {}
    logger.error {
        "The number of parameters does not match, " +
                "expected to be $expectNumber but actually $actualNumber"
    }
}

package top.moxel.plugin.annotation.lua

import io.github.oshai.kotlinlogging.KotlinLogging

typealias LuaBindingFunction = (Array<Any?>) -> Any?

data class LuaBinding(val name: String, val function: LuaBindingFunction)

data class LuaLibDeclaration(
    val type: LuaEngineType,
    val group: String,
    val bindings: List<LuaBinding>
)

enum class LuaEngineType {
    EXTENSION,
    SCRIPT
}

/**
 * auto generate Lua bindings list
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class LuaLibFunction(
    val type: LuaEngineType,
    val group: String = "",
    val name: String = ""
)

fun checkParameters(hasVararg: Boolean, expectNumber: Int, actualNumber: Int) {
    if (hasVararg)
        return
    if (expectNumber == actualNumber)
        return

    val logger = KotlinLogging.logger {}
    logger.error {
        "The number of parameters does not match, " +
                "expected to be $expectNumber but actually $actualNumber"
    }
}

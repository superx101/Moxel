package top.moxel.plugin.annotation

import io.github.oshai.kotlinlogging.KotlinLogging

enum class LuaBindingGroup {
    EXTENSION,
    EXPRESSION
}

/**
 * auto generate Lua bindings list
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class LuaBinding(
    val group: LuaBindingGroup
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

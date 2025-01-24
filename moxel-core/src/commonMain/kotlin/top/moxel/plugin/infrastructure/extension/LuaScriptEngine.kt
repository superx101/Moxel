package top.moxel.plugin.infrastructure.extension

import org.koin.core.annotation.Single
import top.moxel.plugin.ksp.generated.LuaBindingList

expect class LuaScriptEngine() {
    /**
     * bind a Kotlin function to Lua
     */
    fun bindFunction(functionName: String, function: (Array<Any?>) -> Any?)

    /**
     * execute Lua code
     */
    fun execute(code: String): Any?

    /**
     * refresh Lua state
     */
    fun newState()

    /**
     * close Lua engine
     */
    fun close()
}

@Single
class LuaScriptFactory {
    private val luaExtensionScriptEngine = LuaScriptEngine()
    private val luaScriptEngine = LuaScriptEngine()

    fun initialize() {
        LuaBindingList.list_extension.forEach {
            luaExtensionScriptEngine.bindFunction(it.first, it.second)
        }

        LuaBindingList.list_expression.forEach {
            luaScriptEngine.bindFunction(it.first, it.second)
        }
    }

    fun getLuaExtensionEngine(): LuaScriptEngine {
        return luaExtensionScriptEngine
    }

    fun getLuaExpressionEngine(): LuaScriptEngine {
        return luaScriptEngine
    }
}
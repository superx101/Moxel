package top.moxel.plugin.infrastructure.extension

import org.koin.core.annotation.Single
import top.moxel.plugin.annotation.lua.LuaBindingFunction
import top.moxel.plugin.ksp.generated.LuaBindingList

expect open class LuaEngine() {
    /**
     * bind a Kotlin function to Lua
     */
    fun bindFunction(functionName: String, function: LuaBindingFunction)

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
class LuaExtensionEngine : LuaEngine() {
    init {
        LuaBindingList.list_extension.forEach {
            bindFunction(it.first, it.second)
        }
    }
}

@Single
class LuaScriptEngine : LuaEngine() {
    init {
        LuaBindingList.list_expression.forEach {
            bindFunction(it.first, it.second)
        }
    }
}
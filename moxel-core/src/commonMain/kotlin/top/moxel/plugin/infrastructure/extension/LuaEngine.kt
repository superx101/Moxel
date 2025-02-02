package top.moxel.plugin.infrastructure.extension

import top.moxel.plugin.annotation.lua.LuaBinding
import top.moxel.plugin.annotation.lua.LuaEngineType

expect class LuaCFunctionRef

data class LuaLibFunction(
    val name: String,
    val luaCFunctionRef: LuaCFunctionRef
)

data class LuaLib(
    val name: String,
    val luaLibFunctions: List<LuaLibFunction>,
    val isGlobal: Boolean = false
)

data class LuaEngineId(
    val type: LuaEngineType,
    val id: String
)

expect open class LuaEngine() {
    /**
     * set a set of functions to a table
     */
    fun newLib(lib: LuaLib)

    /**
     * set Lua functions
     */
    fun newLibs(libList: List<LuaLib>)

    /**
     * execute Lua code
     * if it has multiple returns, return them as Array<Any?>
     */
    fun eval(code: String): Any?

    /**
     * close Lua engine
     */
    fun close()

    companion object {
        fun buildLuaFunctions(bindingList: List<LuaBinding>): List<LuaLibFunction>
        fun disposeLibs(libs: List<LuaLib>)
    }
}

internal inline fun LuaEngine.commonNewLibs(libList: List<LuaLib>) {
    for (lib in libList) {
        newLib(lib)
    }
}
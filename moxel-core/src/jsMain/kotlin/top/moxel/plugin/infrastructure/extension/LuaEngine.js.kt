package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import top.moxel.plugin.annotation.lua.LuaBinding
import top.moxel.plugin.annotation.lua.LuaBindingFunction
import top.moxel.plugin.infrastructure.common.ActualWrapper

typealias LuaCFunction = (LuaState) -> Int

data class LuaCFunctionWrapper(
    override var value: LuaCFunction
) : ActualWrapper<LuaCFunction>

actual typealias LuaCFunctionRef = LuaCFunctionWrapper

actual open class LuaEngine {
    private val logger = KotlinLogging.logger {}
    private var luaState = createState()

    actual companion object {
        private fun createLuaCFunction(bindingFunction: LuaBindingFunction): LuaCFunction {
            return { luaState ->
                val argCount = lua.lua_gettop(luaState)
                val args = mutableListOf<Any?>()
                for (i in 1..argCount) {
                    args.add(luaToKotlin(luaState, i))
                }

                var result: Any? = null
                try {
                    result = bindingFunction(args.toTypedArray())
                } catch (e: Exception) {
                    val logger = KotlinLogging.logger {}
                    logger.error(e) { "Failed to execute Kotlin function: $bindingFunction" }
                }
                pushKotlinValue(luaState, result)

                1 // return 1
            }
        }

        private fun luaToKotlin(luaState: LuaState, index: Int): Any? {
            return when (val type = lua.lua_type(luaState, index)) {
                lua.LUA_TNIL -> null
                lua.LUA_TBOOLEAN -> lua.lua_toboolean(luaState, index)
                lua.LUA_TLIGHTUSERDATA -> {
                    lua.lua_touserdata(luaState, index)?.toLong()
                }
                lua.LUA_TNUMBER -> lua.lua_tonumber(luaState, index)
                lua.LUA_TSTRING -> to_jsstring(lua.lua_tostring(luaState, index))
                lua.LUA_TTABLE -> {
                    val table = mutableMapOf<Any?, Any?>()
                    lua.lua_pushnil(luaState)
                    while (lua.lua_next(luaState, index) != 0) {
                        val key = luaToKotlin(luaState, -2)
                        val value = luaToKotlin(luaState, -1)
                        if (key != null) {
                            table[key] = value
                        }
                        lua.lua_pop(luaState, 1)
                    }
                    table
                }

                lua.LUA_TFUNCTION -> {
                    lua.lua_topointer(luaState, index)
                }

                lua.LUA_TUSERDATA -> {
                    lua.lua_touserdata(luaState, index)
                }

                lua.LUA_TTHREAD -> {
                    lua.lua_tothread(luaState, index)
                }

                else -> error("Unsupported Lua type: $type")
            }
        }

        private fun pushKotlinValue(luaState: LuaState, value: Any?) {
            when (value) {
                null -> lua.lua_pushnil(luaState)
                is Boolean -> lua.lua_pushboolean(luaState, if (value) 1 else 0)
                is Number -> lua.lua_pushnumber(luaState, value.toDouble())
                is String -> lua.lua_pushstring(luaState, to_luastring(value))
                is Map<*, *> -> {
                    lua.lua_createtable(luaState, 0, 0)
                    for ((key, mapValue) in value) {
                        pushKotlinValue(luaState, key)
                        pushKotlinValue(luaState, mapValue)
                        lua.lua_settable(luaState, -3)
                    }
                }

                is Function<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    value as LuaBindingFunction
                    val luaCFunction = createLuaCFunction(value)
                    val fnRef = lua.lua_pushcfunction(luaState, luaCFunction)
                    lua.lua_pushlightuserdata(luaState, fnRef)
                }

                else -> {
                    lua.lua_pushlightuserdata(luaState, value)
                }
            }
        }

        actual fun buildLuaFunctions(bindingList: List<LuaBinding>): List<LuaLibFunction> {
            return bindingList.map {
                val luaCFunction = createLuaCFunction(it.function)
                LuaLibFunction(
                    it.name,
                    LuaCFunctionRef(luaCFunction)
                )
            }
        }

        actual fun disposeLibs(libs: List<LuaLib>) {
            // do nothing
        }
    }

    private fun createState(): LuaState {
        val state = lauxlib.luaL_newstate()
        lualib.luaL_openlibs(state)
        return state
    }

    actual fun newLib(lib: LuaLib) {
        if (lib.isGlobal) {
            for (luaLibFun in lib.luaLibFunctions) {
                val fnRef = luaLibFun.luaCFunctionRef.value
                lua.lua_pushcfunction(luaState, fnRef)
                lua.lua_setglobal(luaState, luaLibFun.name)
            }
            return
        }

        lua.lua_getglobal(luaState, lib.name)
        if (lua.lua_type(luaState, -1) == lua.LUA_TNIL) {
            lua.lua_pop(luaState, 1)
            lua.lua_newtable(luaState)
            lua.lua_setglobal(luaState, lib.name)

            lua.lua_getglobal(luaState, lib.name)
        }
        for (luaLibFun in lib.luaLibFunctions) {
            val fnRef = luaLibFun.luaCFunctionRef.value
            lua.lua_pushcfunction(luaState, fnRef)
            lua.lua_setfield(luaState, -2, luaLibFun.name)
        }
        lua.lua_settop(luaState, 0)
    }

    actual fun newLibs(libList: List<LuaLib>) = commonNewLibs(libList)

    actual fun eval(code: String): Any? {
        if (lauxlib.luaL_dostring(luaState, to_luastring(code)) != lua.LUA_OK) {
            error(
                "Failed to run Lua code: ${
                    to_jsstring(lua.lua_tostring(luaState, -1))
                }"
            )
        }

        val returnValues = mutableListOf<Any?>()
        val result = when (val returnCount = lua.lua_gettop(luaState)) {
            0 -> null
            1 -> luaToKotlin(luaState, 1)
            else -> {
                for (i in 1..returnCount) {
                    val returnValue = luaToKotlin(luaState, i)
                    returnValues.add(returnValue)
                }
                returnValues.toTypedArray()
            }
        }

        lua.lua_settop(luaState, 0)
        return result
    }

    actual fun close() {
        lua.lua_close(luaState)
    }
}


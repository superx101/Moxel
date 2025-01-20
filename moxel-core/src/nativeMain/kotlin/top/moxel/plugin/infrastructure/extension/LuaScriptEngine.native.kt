package top.moxel.plugin.infrastructure.extension

import kotlinx.cinterop.*
import org.lua.*

@OptIn(ExperimentalForeignApi::class)
actual class LuaScriptEngine {
    private val luaState: CPointer<cnames.structs.lua_State> =
        luaL_newstate() ?: error("Failed to create Lua state")

    init {
        luaL_openlibs(luaState)
    }

    actual fun bindFunction(functionName: String, function: (Array<Any?>) -> Any?) {
        val stableRef = StableRef.create(function)

        lua_pushlightuserdata(luaState, stableRef.asCPointer())
        lua_pushcclosure(luaState, staticCFunction { state ->
            // get Kotlin function from StableRef
            val ref =
                lua_touserdata(state, luaUpValueIndex(1))?.asStableRef<(Array<Any?>) -> Any?>()
                    ?: error("Failed to get Kotlin function")

            val args = mutableListOf<Any?>()
            val argCount = lua_gettop(state)
            for (i in 1..argCount) {
                args.add(luaToKotlin(state, i))
            }

            val result = ref.get()(args.toTypedArray()) // call Kotlin function
            pushKotlinValue(state, result)

            return@staticCFunction 1
        }, 1)
        lua_setglobal(luaState, functionName)
    }

    actual fun execute(code: String): Any? {
        if (luaL_loadstring(luaState, code) != LUA_OK) {
            error("Failed to load Lua code: ${lua_tolstring(luaState, -1, null)?.toKString()}")
        }

        if (lua_pcallk(luaState, 0, LUA_MULTRET, 0, 0, null) != LUA_OK) {
            error("Failed to run Lua code: ${lua_tolstring(luaState, -1, null)?.toKString()}")
        }

        return luaToKotlin(luaState, -1)
    }

    actual fun close() {
        lua_close(luaState)
    }
}
package top.moxel.plugin.infrastructure.extension

import cnames.structs.lua_State
import kotlinx.cinterop.*
import org.lua.*

@OptIn(ExperimentalForeignApi::class)
inline fun luaUpValueIndex(i: Int): Int {
    return LUA_REGISTRYINDEX - i
}

@OptIn(ExperimentalForeignApi::class)
fun luaToKotlin(luaState: CPointer<lua_State>?, index: Int): Any? {
    return when (lua_type(luaState, index)) {
        LUA_TNUMBER -> lua_tonumberx(luaState, index, null)
        LUA_TSTRING -> lua_tolstring(luaState, index, null)?.toKString()
        LUA_TBOOLEAN -> lua_toboolean(luaState, index) != 0
        LUA_TNIL -> null
        else -> error("Unsupported Lua type: ${lua_type(luaState, index)}")
    }
}

@OptIn(ExperimentalForeignApi::class)
fun pushKotlinValue(luaState: CPointer<lua_State>?, value: Any?) {
    when (value) {
        is Number -> lua_pushnumber(luaState, value.toDouble())
        is String -> lua_pushstring(luaState, value)
        is Boolean -> lua_pushboolean(luaState, if (value) 1 else 0)
        null -> lua_pushnil(luaState)
        else -> error("Unsupported Kotlin type: ${value::class.simpleName}")
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class LuaScriptEngine {
    private var luaState: CPointer<lua_State> = createState()
    private val refList = mutableListOf<Pair<String, StableRef<(Array<Any?>) -> Any?>>>()

    private inline fun createState(): CPointer<lua_State> {
        val state = luaL_newstate() ?: error("Failed to create Lua state")
        luaL_openlibs(state)
        return state
    }

    private fun bindFunctionRef(functionName: String, funcRef: StableRef<(Array<Any?>) -> Any?>) {
        lua_pushlightuserdata(luaState, funcRef.asCPointer())
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

            // call Kotlin function
            val result = ref.get()(args.toTypedArray())
            pushKotlinValue(state, result)

            return@staticCFunction 1
        }, 1)
        lua_setglobal(luaState, functionName)
    }

    actual fun bindFunction(functionName: String, function: (Array<Any?>) -> Any?) {
        val funcRef = StableRef.create(function)
        bindFunctionRef(functionName, funcRef)
        refList.add(Pair(functionName, funcRef))
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

    actual fun newState() {
        lua_close(luaState)
        luaState = createState()

        for (ref in refList) {
            bindFunctionRef(ref.first, ref.second)
        }
    }

    actual fun close() {
        for (ref in refList) {
            ref.second.dispose()
        }
        lua_close(luaState)
    }
}
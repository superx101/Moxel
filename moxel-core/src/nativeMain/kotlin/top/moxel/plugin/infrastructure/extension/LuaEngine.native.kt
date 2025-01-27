package top.moxel.plugin.infrastructure.extension

import cnames.structs.lua_State
import kotlinx.cinterop.*
import org.lua.*
import top.moxel.plugin.annotation.lua.LuaBindingFunction

@OptIn(ExperimentalForeignApi::class)
internal inline fun luaUpValueIndex(i: Int): Int {
    return LUA_REGISTRYINDEX - i
}

@OptIn(ExperimentalForeignApi::class)
internal fun luaToKotlin(luaState: CPointer<lua_State>?, index: Int): Any? {
    return when (lua_type(luaState, index)) {
        LUA_TNUMBER -> lua_tonumberx(luaState, index, null)
        LUA_TSTRING -> lua_tolstring(luaState, index, null)?.toKString()
        LUA_TBOOLEAN -> lua_toboolean(luaState, index) != 0
        LUA_TNIL -> null
        else -> error("Unsupported Lua type: ${lua_type(luaState, index)}")
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun pushKotlinValue(luaState: CPointer<lua_State>?, value: Any?) {
    when (value) {
        is Number -> lua_pushnumber(luaState, value.toDouble())
        is String -> lua_pushstring(luaState, value)
        is Boolean -> lua_pushboolean(luaState, if (value) 1 else 0)
        null -> lua_pushnil(luaState)
        else -> error("Unsupported Kotlin type: ${value::class.simpleName}")
    }
}

@OptIn(ExperimentalForeignApi::class)
actual open class LuaEngine {
    private var luaState: CPointer<lua_State> = createState()
    private val fnStableRefList = mutableListOf<Pair<String, StableRef<LuaBindingFunction>>>()

    private inline fun createState(): CPointer<lua_State> {
        val state = luaL_newstate() ?: error("Failed to create Lua state")
        luaL_openlibs(state)
        return state
    }

    private fun bindLuaStableRef(functionName: String, ref: StableRef<LuaBindingFunction>) {
        lua_pushlightuserdata(luaState, ref.asCPointer())
        val luaCFunction = staticCFunction(
            fun(state: CPointer<lua_State>?): Int {
                val args = mutableListOf<Any?>()
                val argCount = lua_gettop(state)
                for (i in 1..argCount) {
                    args.add(luaToKotlin(state, i))
                }

                // call Kotlin function
                val fnRef = lua_touserdata(state, luaUpValueIndex(1))
                    ?.asStableRef<LuaBindingFunction>()
                    ?: error("Failed to get Kotlin function")
                val fn = fnRef.get()
                val result = fn(args.toTypedArray())
                pushKotlinValue(state, result)

                return 1
            }
        )

        // #define lua_pushcfunction(L,f)	lua_pushcclosure(L, (f), 0)
        lua_pushcclosure(luaState, luaCFunction, 1)
        lua_setglobal(luaState, functionName)
    }

    actual fun bindFunction(functionName: String, function: LuaBindingFunction) {
        val stableRef = StableRef.create(function)
        bindLuaStableRef(functionName, stableRef)

        fnStableRefList.add(functionName to stableRef)
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

        for (ref in fnStableRefList) {
            bindLuaStableRef(ref.first, ref.second)
        }
    }

    actual fun close() {
        lua_close(luaState)
        for (ref in fnStableRefList) {
            ref.second.dispose()
        }
    }
}
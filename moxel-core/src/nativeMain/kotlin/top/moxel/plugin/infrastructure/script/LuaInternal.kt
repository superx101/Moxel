package top.moxel.plugin.infrastructure.script

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import org.lua.*

@OptIn(ExperimentalForeignApi::class)
fun luaUpValueIndex(i: Int): Int {
    return LUA_REGISTRYINDEX - i
}

@OptIn(ExperimentalForeignApi::class)
fun luaToString(L: CPointer<cnames.structs.lua_State>?, index: Int): CPointer<ByteVar>? {
    return lua_tolstring(L, index, null)
}

@OptIn(ExperimentalForeignApi::class)
fun luaToKotlin(luaState: CPointer<cnames.structs.lua_State>?, index: Int): Any? {
    return when (lua_type(luaState, index)) {
        LUA_TNUMBER -> lua_tonumberx(luaState, index, null)
        LUA_TSTRING -> luaToString(luaState, index)?.toKString()
        LUA_TBOOLEAN -> lua_toboolean(luaState, index) != 0
        LUA_TNIL -> null
        else -> error("Unsupported Lua type: ${lua_type(luaState, index)}")
    }
}

@OptIn(ExperimentalForeignApi::class)
fun kotlinToLua(luaState: CPointer<cnames.structs.lua_State>?, value: Any?) {
    when (value) {
        is Number -> lua_pushnumber(luaState, value.toDouble())
        is String -> lua_pushstring(luaState, value)
        is Boolean -> lua_pushboolean(luaState, if (value) 1 else 0)
        null -> lua_pushnil(luaState)
        else -> error("Unsupported Kotlin type: ${value::class.simpleName}")
    }
}

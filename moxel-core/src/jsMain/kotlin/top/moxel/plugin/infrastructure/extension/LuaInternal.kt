package top.moxel.plugin.infrastructure.extension

fun luaToKotlin(luaState: LuaState, index: Int): Any? {
    return when (val type = lua.lua_type(luaState, index)) {
        lua.LUA_TNIL -> null
        lua.LUA_TNUMBER -> lua.lua_tonumber(luaState, index)
        lua.LUA_TBOOLEAN -> lua.lua_toboolean(luaState, index)
        lua.LUA_TSTRING -> to_jsstring(lua.lua_tostring(luaState, index))
        else -> error("Unsupported Lua type: $type")
    }
}

fun pushKotlinValue(luaState: LuaState, value: Any?) {
    when (value) {
        is Number -> lua.lua_pushnumber(luaState, value.toDouble())
        is Boolean -> lua.lua_pushboolean(luaState, if (value) 1 else 0)
        is String -> lua.lua_pushstring(luaState, to_luastring(value))
        null -> lua.lua_pushnil(luaState)
        else -> error("Unsupported Kotlin type: ${value::class.simpleName}")
    }
}
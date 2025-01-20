@file:JsModule("fengari-web")
@file:JsNonModule

package top.moxel.plugin.infrastructure.extension

external class LuaState;

external object lua {
    val LUA_OK: Int

    val LUA_TNIL: Int
    val LUA_TBOOLEAN: Int
    val LUA_TNUMBER: Int
    val LUA_TSTRING: Int

    val LUA_MULTRET: Int

    fun lua_type(L: LuaState, index: Int): Int
    fun lua_tonumber(L: LuaState, index: Int): Double
    fun lua_toboolean(L: LuaState, index: Int): Boolean
    fun lua_tostring(L: LuaState, index: Int): String?

    fun lua_pushnumber(L: LuaState, n: Double)
    fun lua_pushstring(L: LuaState, s: Any)
    fun lua_pushboolean(L: LuaState, b: Int)
    fun lua_pushnil(L: LuaState)

    fun lua_pushcfunction(L: LuaState, f: (LuaState) -> Int)
    fun lua_setglobal(L: LuaState, name: String)
    fun lua_gettop(L: LuaState): Int
    fun lua_pcall(L: LuaState, nargs: Int, nresults: Int, errfunc: Int): Int
    fun lua_pop(L: LuaState, n: Int)
    fun lua_close(L: LuaState)
}

external object lauxlib {
    fun luaL_newstate(): LuaState
    fun luaL_loadstring(L: LuaState, s: Any): Int
}

external object lualib {
    fun luaL_openlibs(L: LuaState)
}

external fun to_jsstring(value: String?): String?
external fun to_luastring(value: Any): Any
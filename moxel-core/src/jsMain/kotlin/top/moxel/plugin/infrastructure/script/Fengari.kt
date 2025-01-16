@file:JsModule("fengari-web")
@file:JsNonModule

package top.moxel.plugin.infrastructure.script

external class LuaState;

external object fengari {
    val lua: Lua
    val lauxlib: LuaAuxLib
    val lualib: LuaLib

    interface Lua {
        fun lua_newstate(): LuaState
        fun lua_close(L: LuaState)
        fun lua_pcall(L: LuaState, nargs: Int, nresults: Int, errfunc: Int): Int
        fun lua_setglobal(L: LuaState, name: String)
        fun lua_getglobal(L: LuaState, name: String): Int
        fun lua_pushstring(L: LuaState, s: String)
        fun lua_tostring(L: LuaState, index: Int): String?
        fun lua_pushlightuserdata(L: LuaState, p: dynamic)
        fun lua_pushcfunction(L: LuaState, fn: (LuaState) -> Int)
        fun lua_gettop(L: LuaState): Int
        fun lua_tolightuserdata(L: LuaState, index: Int): dynamic
    }

    interface LuaAuxLib {
        fun luaL_openlibs(L: LuaState)
        fun luaL_loadstring(L: LuaState, code: String): Int
    }

    interface LuaLib {
        fun luaL_newstate(): LuaState
    }
}
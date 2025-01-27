package top.moxel.plugin.infrastructure.extension

import top.moxel.plugin.annotation.lua.LuaBindingFunction

internal fun luaToKotlin(luaState: LuaState, index: Int): Any? {
    return when (val type = lua.lua_type(luaState, index)) {
        lua.LUA_TNIL -> null
        lua.LUA_TNUMBER -> lua.lua_tonumber(luaState, index)
        lua.LUA_TBOOLEAN -> lua.lua_toboolean(luaState, index)
        lua.LUA_TSTRING -> to_jsstring(lua.lua_tostring(luaState, index))
        else -> error("Unsupported Lua type: $type")
    }
}

internal fun pushKotlinValue(luaState: LuaState, value: Any?) {
    when (value) {
        is Number -> lua.lua_pushnumber(luaState, value.toDouble())
        is Boolean -> lua.lua_pushboolean(luaState, if (value) 1 else 0)
        is String -> lua.lua_pushstring(luaState, to_luastring(value))
        null -> lua.lua_pushnil(luaState)
        else -> error("Unsupported Kotlin type: ${value::class.simpleName}")
    }
}

typealias LuaCFunction = (LuaState) -> Int

actual open class LuaEngine {
    private var luaState = createState()
    private val funcList = mutableListOf<Pair<String, LuaCFunction>>()

    private fun createState(): LuaState {
        val state = lauxlib.luaL_newstate()
        lualib.luaL_openlibs(state)
        return state
    }

    private fun bindLuaCFunction(
        functionName: String,
        luaCFunction: LuaCFunction
    ) {
        lua.lua_pushcfunction(luaState, luaCFunction)
        lua.lua_setglobal(luaState, functionName)
    }

    actual fun bindFunction(
        functionName: String,
        function: LuaBindingFunction
    ) {
        val luaCFunction: LuaCFunction = { luaState ->
            val argCount = lua.lua_gettop(luaState)
            val args = mutableListOf<Any?>()
            for (i in 1..argCount) {
                args.add(luaToKotlin(luaState, i))
            }

            val result = function(args.toTypedArray())
            pushKotlinValue(luaState, result)

            1 // return 1
        }
        bindLuaCFunction(functionName, luaCFunction)

        funcList.add(functionName to luaCFunction)
}

actual fun execute(code: String): Any? {
    if (lauxlib.luaL_loadstring(luaState, to_luastring(code)) != lua.LUA_OK) {
        error(
            "Failed to load Lua code: ${
                to_jsstring(
                    lua.lua_tostring(luaState, -1)
                )
            }"
        )
    }

    if (lua.lua_pcall(luaState, 0, lua.LUA_MULTRET, 0) != lua.LUA_OK) {
        error(
            "Failed to run Lua code: ${
                to_jsstring(
                    lua.lua_tostring(
                        luaState,
                        -1
                    )
                )
            }"
        )
    }

    val result = luaToKotlin(luaState, -1)
    lua.lua_pop(luaState, 1)
    return result
}

actual fun newState() {
    lua.lua_close(luaState)
    luaState = createState()

    for (func in funcList) {
        bindLuaCFunction(func.first, func.second)
    }
}

actual fun close() {
    lua.lua_close(luaState)
}
}
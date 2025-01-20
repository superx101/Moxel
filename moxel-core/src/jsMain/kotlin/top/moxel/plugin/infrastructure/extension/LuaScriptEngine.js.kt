package top.moxel.plugin.infrastructure.extension

actual class LuaScriptEngine {
    private val luaState = lauxlib.luaL_newstate()

    init {
        lualib.luaL_openlibs(luaState)
    }

    actual fun bindFunction(
        functionName: String,
        function: (Array<Any?>) -> Any?
    ) {
        lua.lua_pushcfunction(luaState) { luaState ->
            val argCount = lua.lua_gettop(luaState)
            val args = mutableListOf<Any?>()
            for (i in 1..argCount) {
                args.add(luaToKotlin(luaState, i))
            }

            val result = function(args.toTypedArray())
            pushKotlinValue(luaState, result)

            1
        }

        lua.lua_setglobal(luaState, functionName)
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

    actual fun close() {
        lua.lua_close(luaState)
    }
}
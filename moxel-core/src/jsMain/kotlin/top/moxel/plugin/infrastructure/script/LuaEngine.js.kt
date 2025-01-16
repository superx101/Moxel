package top.moxel.plugin.infrastructure.script

actual class LuaEngine {
    private val luaState = fengari.lualib.luaL_newstate()

    init {
        fengari.lauxlib.luaL_openlibs(luaState)
    }

    actual fun bindFunction(
        functionName: String,
        function: (Array<Any?>) -> Any?
    ) {
        TODO("Not yet implemented")
    }

    actual fun execute(code: String): Any? {
        TODO("Not yet implemented")
    }

    actual fun close() {
        TODO("Not yet implemented")
    }
}
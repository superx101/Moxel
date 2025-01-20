package top.moxel.plugin.infrastructure.extension

expect class LuaScriptEngine {
    /**
     * bind a Kotlin function to Lua
     */
    fun bindFunction(functionName: String, function: (Array<Any?>) -> Any?)

    /**
     * execute Lua code
     */
    fun execute(code: String): Any?

    /**
     * close Lua engine
     */
    fun close()
}
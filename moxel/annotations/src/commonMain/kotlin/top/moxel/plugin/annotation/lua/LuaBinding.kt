package top.moxel.plugin.annotation.lua

enum class LuaEngineType {
    EXTENSION,
    SCRIPT,
}

typealias LuaBindingFunction = (Array<Any?>) -> Any?

data class LuaBinding(val name: String, val function: LuaBindingFunction)

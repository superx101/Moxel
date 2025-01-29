package top.moxel.plugin.infrastructure.extension

import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.annotation.lua.LuaLibFunction

/**
 * TODO: remove soon, keep this for developing
 */

@LuaLibFunction(type = LuaEngineType.EXTENSION, group = "mt")
fun test(a: Int, b: Int): Int {
    return a * b
}

@LuaLibFunction(type = LuaEngineType.EXTENSION, group = "mt")
fun test2(a: Int, b: Int, c: Double): Int {
    return a * b + c.toInt()
}

@LuaLibFunction(type = LuaEngineType.SCRIPT)
fun test3(vararg str: String): String {
    return str[0]
}
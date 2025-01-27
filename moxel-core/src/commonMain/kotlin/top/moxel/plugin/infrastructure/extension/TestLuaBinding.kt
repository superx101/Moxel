package top.moxel.plugin.infrastructure.extension

import top.moxel.plugin.annotation.lua.LuaBinding
import top.moxel.plugin.annotation.lua.LuaBindingGroup

/**
 * TODO: remove soon, keep this for developing
 */

@LuaBinding(group = LuaBindingGroup.EXTENSION)
fun test(a: Int, b: Int): Int {
    return a * b
}

@LuaBinding(group = LuaBindingGroup.EXTENSION)
fun test2(a: Int, b: Int, c: Double): Int {
    return a * b + c.toInt()
}

@LuaBinding(group = LuaBindingGroup.EXPRESSION)
fun test3(vararg str: String): String {
    return str[0]
}
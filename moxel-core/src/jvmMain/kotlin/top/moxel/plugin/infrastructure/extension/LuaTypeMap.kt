package top.moxel.plugin.infrastructure.extension

import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.jse.CoerceLuaToJava

fun luaToKotlin(args: Varargs, index: Int): Any? {
    val type = args.type(index)
    return when (type) {
        LuaValue.TNIL -> null
        LuaValue.TBOOLEAN -> args.toboolean(index)
        LuaValue.TNUMBER -> args.todouble(index)
        LuaValue.TSTRING -> args.tojstring(index)
        else -> CoerceLuaToJava.coerce(args.arg(index), Any::class.java)
    }
}

fun luaToKotlin(result: LuaValue): Any? {
    return when {
        result.isnil() -> null
        result.isboolean() -> result.toboolean()
        result.isnumber() -> result.todouble()
        result.isstring() -> result.tojstring()
        else -> CoerceLuaToJava.coerce(result, Any::class.java)
    }
}

fun kotlinToLua(value: Any?): LuaValue {
    return when (value) {
        is Boolean -> LuaValue.valueOf(value)
        is Int -> LuaValue.valueOf(value)
        is Double -> LuaValue.valueOf(value)
        is String -> LuaValue.valueOf(value)
        else -> LuaValue.NIL
    }
}

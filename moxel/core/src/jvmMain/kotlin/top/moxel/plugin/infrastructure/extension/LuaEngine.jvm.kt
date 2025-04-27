package top.moxel.plugin.infrastructure.extension

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import org.luaj.vm2.lib.jse.JsePlatform
import top.moxel.plugin.annotation.lua.LuaBinding
import top.moxel.plugin.annotation.lua.LuaBindingFunction
import top.moxel.plugin.infrastructure.common.ActualWrapper

actual typealias LuaCFunctionRef = LuaCFunctionWrapper

class LuaCFunctionWrapper(override var value: VarArgFunction) : ActualWrapper<VarArgFunction>

actual open class LuaEngine actual constructor() {
    private var globals: Globals = JsePlatform.standardGlobals()

    actual companion object {
        private fun createLuaCFunction(function: LuaBindingFunction): VarArgFunction {
            return object : VarArgFunction() {
                override fun invoke(args: Varargs): Varargs {
                    val arguments = Array(args.narg()) { i -> luaToKotlin(args.arg(i + 1)) }
                    val result = function(arguments)
                    return varargsOf(arrayOf(kotlinToLua(result)))
                }
            }
        }

        internal fun luaToKotlin(result: LuaValue): Any? {
            return when {
                result.isnil() -> null
                result.isboolean() -> result.toboolean()
                result.isnumber() -> result.todouble()
                result.isstring() -> result.tojstring()
                else -> CoerceLuaToJava.coerce(result, Any::class.java)
            }
        }

        internal fun kotlinToLua(value: Any?): LuaValue {
            return when (value) {
                null -> LuaValue.NIL
                is Boolean -> LuaValue.valueOf(value)
                is Int -> LuaValue.valueOf(value)
                is Double -> LuaValue.valueOf(value)
                is String -> LuaValue.valueOf(value)
                else -> CoerceJavaToLua.coerce(value)
            }
        }

        actual fun buildLuaFunctions(bindingList: List<LuaBinding>): List<LuaLibFunction> {
            return bindingList.map {
                val luaCFunction = createLuaCFunction(it.function)
                LuaLibFunction(it.name, LuaCFunctionRef(luaCFunction))
            }
        }

        actual fun disposeLibs(libs: List<LuaLib>) {
            // do nothing
        }
    }

    actual fun newLib(lib: LuaLib) {
        if (lib.isGlobal) {
            for (luaLibFun in lib.luaLibFunctions)
                globals.set(luaLibFun.name, luaLibFun.luaCFunctionRef.value)
            return
        }

        var table = globals.get(lib.name)
        if (table.isnil()) {
            table = LuaTable()
        }
        table as LuaTable

        for (luaLibFun in lib.luaLibFunctions)
            table.set(luaLibFun.name, luaLibFun.luaCFunctionRef.value)
        globals.set(lib.name, table)
    }

    actual fun newLibs(libList: List<LuaLib>) = commonNewLibs(libList)

    actual fun eval(code: String): Any? {
        return try {
            val result = globals.load(code).call()
            return luaToKotlin(result)
        } catch (e: Exception) {
            null
        }
    }

    actual fun close() {
        globals.get("collectgarbage")?.call()
    }
}

package top.moxel.plugin.infrastructure.extension

import org.luaj.vm2.Globals
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.JsePlatform

actual class LuaScriptEngine {
    private val globals: Globals = JsePlatform.standardGlobals()

    actual fun bindFunction(
        functionName: String,
        function: (Array<Any?>) -> Any?
    ) {
        val luaFunction = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val arguments = Array(args.narg()) { i -> luaToKotlin(args, i + 1) }
                val result = function(arguments)
                return varargsOf(arrayOf(kotlinToLua(result)))
            }
        }

        globals.set(functionName, luaFunction)
    }

    actual fun execute(code: String): Any? {
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
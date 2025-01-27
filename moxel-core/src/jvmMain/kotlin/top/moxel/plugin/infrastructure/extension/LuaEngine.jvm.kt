package top.moxel.plugin.infrastructure.extension

import org.luaj.vm2.Globals
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.JsePlatform
import top.moxel.plugin.annotation.lua.LuaBindingFunction

actual open class LuaEngine {
    private var globals: Globals = JsePlatform.standardGlobals()
    private val funcList = mutableListOf<Pair<String, VarArgFunction>>()

    private fun bindVarArgFunction(
        functionName: String,
        function: VarArgFunction
    ) {
        globals.set(functionName, function)
    }


    actual fun bindFunction(
        functionName: String,
        function: LuaBindingFunction
    ) {
        val luaFunction = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val arguments = Array(args.narg()) { i -> luaToKotlin(args, i + 1) }
                val result = function(arguments)
                return varargsOf(arrayOf(kotlinToLua(result)))
            }
        }
        bindVarArgFunction(functionName, luaFunction)

        funcList.add(functionName to luaFunction)
    }

    actual fun execute(code: String): Any? {
        return try {
            val result = globals.load(code).call()
            return luaToKotlin(result)
        } catch (e: Exception) {
            null
        }
    }

    actual fun newState() {
        globals = JsePlatform.standardGlobals()
        funcList.forEach {
            bindVarArgFunction(it.first, it.second)
        }
    }

    actual fun close() {
        globals.get("collectgarbage")?.call()
    }
}
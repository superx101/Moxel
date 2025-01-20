package script

import top.moxel.plugin.infrastructure.extension.LuaScriptEngine
import kotlin.test.Test
import kotlin.test.assertEquals

class LuaEngineTest {
    @Test
    fun testBindFunction() {
        console.log("LuaEngineTest.testBindFunction")
        val luaScriptEngine = LuaScriptEngine()
        luaScriptEngine.bindFunction("f") { args ->
            val a = args[0] as Int
            val b = args[1] as Int
            a * b
        }
        assertEquals("function", luaScriptEngine.execute("return type(f)"))
        assertEquals(10.0, luaScriptEngine.execute("return f(2, 5)"))
        luaScriptEngine.close()
    }
}
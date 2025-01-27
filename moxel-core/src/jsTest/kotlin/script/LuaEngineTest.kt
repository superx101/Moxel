package script

import top.moxel.plugin.infrastructure.extension.LuaEngine
import kotlin.test.Test
import kotlin.test.assertEquals

class LuaEngineTest {
    @Test
    fun testBindFunction() {
        console.log("LuaEngineTest.testBindFunction")
        val luaEngine = LuaEngine()
        luaEngine.bindFunction("f") { args ->
            val a = args[0] as Int
            val b = args[1] as Int
            a * b
        }
        assertEquals("function", luaEngine.execute("return type(f)"))
        assertEquals(10.0, luaEngine.execute("return f(2, 5)"))
        luaEngine.close()
    }
}
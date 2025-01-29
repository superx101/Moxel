package infrastructure

import top.moxel.plugin.infrastructure.extension.LuaEngine
import kotlin.test.Test
import kotlin.test.assertEquals

class LuaEngineTest {
    @Test
    fun testBindFunction() {
        val luaEngine = LuaEngine()

        luaEngine.bindFunction("f") { args ->
            args[0] as Double + args[1] as Double
        }
        luaEngine.bindFunction("g") { args ->
            args[0] as Double * args[1] as Double
        }

        assertEquals(7.0, luaEngine.eval("return f(2, 5)"))
        assertEquals(10.0, luaEngine.eval("return g(2, 5)"))
        assertEquals(7.0, luaEngine.eval("return f(2, 5)"))
    }

    @Test
    fun testScope() {
        val luaEngine = LuaEngine()
        luaEngine.eval("local a = 3")
        assertEquals(null, luaEngine.eval("return a"))

        luaEngine.eval("local b = 4")
        assertEquals(null, luaEngine.eval("return b"))
    }
}
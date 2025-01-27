package infrastructure

import top.moxel.plugin.infrastructure.extension.LuaEngine
import kotlin.test.Test
import kotlin.test.assertEquals

class LuaEngineTest {
    @Test
    fun testBindFunction() {
        val luaEngine = LuaEngine()
        luaEngine.bindFunction("f") {
            args -> (args[0] as Double + args[1] as Double)
        }
        assertEquals(3.0, luaEngine.execute("return f(1, 2)"))
    }

    @Test
    fun testScope() {
        val luaEngine = LuaEngine()
        luaEngine.execute("a = 3")
        luaEngine.newState()
        assertEquals(null, luaEngine.execute("return a"))
    }
}
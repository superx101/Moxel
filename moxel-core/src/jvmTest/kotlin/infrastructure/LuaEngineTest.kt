package infrastructure

import org.junit.Assert.assertEquals
import top.moxel.plugin.infrastructure.extension.LuaEngine
import kotlin.test.Test


class LuaEngineTest {
    @Test
    fun testBindFunction() {
        val luaEngine = LuaEngine()
        luaEngine.bindFunction("f") {
            args -> (args[0] as Double + args[1] as Double)
        }
        assertEquals(3.0, luaEngine.eval("return f(1, 2)"))
    }
}
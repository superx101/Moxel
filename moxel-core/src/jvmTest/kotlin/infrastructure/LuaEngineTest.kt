package infrastructure

import org.junit.Assert.assertEquals
import top.moxel.plugin.infrastructure.extension.LuaScriptEngine
import kotlin.test.Test


class LuaEngineTest {
    @Test
    fun testBindFunction() {
        val luaScriptEngine = LuaScriptEngine()
        luaScriptEngine.bindFunction("f") {
            args -> (args[0] as Double + args[1] as Double)
        }
        assertEquals(3.0, luaScriptEngine.execute("return f(1, 2)"))
    }
}
package infrastructure

import top.moxel.plugin.infrastructure.script.LuaEngine
import kotlin.test.Test

class LuaEngineTest {
    @Test
    fun testSimpleLua() {
        val result = luaEngine.eval("print('Hello, Lua!')")
        println { "Result: $result" }

        val exp = luaEngine.getCompiledScript<(x: Int, y: Int)->Int>("""
                function multiply(x, y)
                    return x * y
                end
            """.trimIndent(), "multiply")
        println { "Exp: ${exp(4, 5)}" }
    }
}
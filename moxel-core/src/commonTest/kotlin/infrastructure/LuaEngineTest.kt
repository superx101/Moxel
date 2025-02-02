package infrastructure

import top.moxel.plugin.annotation.lua.LuaBinding
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.annotation.lua.LuaLibDeclaration
import top.moxel.plugin.infrastructure.extension.LuaEngineId
import top.moxel.plugin.infrastructure.extension.LuaEngineManager
import kotlin.test.Test
import kotlin.test.assertEquals

object TestLibs {
    val mathLib = listOf(
        LuaBinding("add") { args -> args[0] as Double + args[1] as Double },
        LuaBinding("sub") { args -> args[0] as Double - args[1] as Double },
        LuaBinding("mul") { args -> args[0] as Double * args[1] as Double }
    )
}

abstract class LuaEngineTest {
    @Test
    abstract fun bindingTest()

    protected fun commonBindingTest() {
        val manager = LuaEngineManager()
        manager.registerLib(
            LuaLibDeclaration(
                LuaEngineType.SCRIPT,
                "mymath",
                TestLibs.mathLib
            )
        )
        val engine = manager.getOrCreate(LuaEngineId(LuaEngineType.SCRIPT, "TestEngine"))

        assertEquals(7.0, engine.eval("return mymath.add(2, 5)"))
        assertEquals(-3.0, engine.eval("return mymath.sub(2, 5)"))
        assertEquals(10.0, engine.eval("return mymath.mul(2, 5)"))
    }
}
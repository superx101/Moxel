package infrastructure

import kotlin.test.Test

class LuaEngineTestJvm : LuaEngineTest() {
    @Test
    override fun bindingTest() {
        super.commonBindingTest()
    }
}

package top.moxel.plugin.infrastructure.extension

import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class LuaExtensionLoader : KoinComponent {
    private val engineFactory by inject<LuaScriptFactory>()
    private val engine = engineFactory.luaExtensionScriptEngine

    fun loadAll() {
        engineFactory.bindExtensionFunctions()
    }
}
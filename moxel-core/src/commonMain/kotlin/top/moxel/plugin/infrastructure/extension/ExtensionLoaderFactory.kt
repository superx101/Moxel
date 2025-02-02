package top.moxel.plugin.infrastructure.extension

import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.common.AbstractFactory

@Single
class ExtensionLoaderFactory : AbstractFactory<ExtensionType, ExtensionLoader>, KoinComponent {
    private val nativeLoader by inject<NativeExtensionLoader>()
    private val luaLoader by inject<LuaExtensionLoader>()

    override fun getInstance(type: ExtensionType): ExtensionLoader = when (type) {
        ExtensionType.Native -> nativeLoader
        ExtensionType.Lua -> luaLoader
    }

    fun getAllInstance(): List<ExtensionLoader> = listOf(nativeLoader, luaLoader)
}

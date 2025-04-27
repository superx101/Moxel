package top.moxel.plugin.infrastructure.extension

import top.moxel.plugin.annotation.di.Singleton
import top.moxel.plugin.infrastructure.common.AbstractFactory
import top.moxel.plugin.infrastructure.di.inject

@Singleton
class ExtensionLoaderFactory : AbstractFactory<ExtensionType, ExtensionLoader> {
    private val nativeLoader by inject<NativeExtensionLoader>()
    private val luaLoader by inject<LuaExtensionLoader>()

    override fun getInstance(type: ExtensionType): ExtensionLoader = when (type) {
        ExtensionType.Native -> nativeLoader
        ExtensionType.Lua -> luaLoader
    }

    fun getAllInstance(): List<ExtensionLoader> = listOf(nativeLoader, luaLoader)
}

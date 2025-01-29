package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.infrastructure.common.AbstractFactory
import top.moxel.plugin.infrastructure.environment.Environment
import top.moxel.plugin.infrastructure.environment.ResourceManager
import top.moxel.plugin.infrastructure.io.VirtualFile

interface ExtensionLoader {
    /**
     * Load one extension to modify the behavior of the plugin
     */
    fun load(path: Path)

    /**
     * Load all extensions
     */
    fun loadAll()

    /**
     * Free memory
     */
    fun freeAll()
}

@Single
class LuaExtensionLoader : ExtensionLoader, KoinComponent {
    private val manager by inject<LuaEngineManager>()
    private val resources by inject<ResourceManager>()
    private val logger = KotlinLogging.logger {}

    override fun load(path: Path) {
        val code = VirtualFile(path).loadText()
        val engine = manager.getOrCreate(LuaEngineId(LuaEngineType.EXTENSION, path.name))
        engine.eval(code)
    }

    override fun loadAll() {
        VirtualFile(resources.extension)
            .listFiles()
            .forEach {
                try {
                    load(it.path)
                    logger.debug { "Lua extension: $it loaded" }
                }
                catch (e: Exception) {
                    logger.error { "Load lua extension error: $it" }
                }
            }
    }

    override fun freeAll() {
        manager.disposeExtensions()
        logger.debug { "Lua extension closed" }
    }
}

@Single
expect class NativeExtensionLoader() : ExtensionLoader, KoinComponent {
    override fun loadAll()
    override fun load(path: Path)
    override fun freeAll()
}

internal fun NativeExtensionLoader.commonLoadAll(suffix: String) {
    val env by inject<Environment>()
    val logger = KotlinLogging.logger {}
    val extensionDir = env.dataRoot.resolve("extension")
    val paths =
        VirtualFile(extensionDir).listFiles().filter { it.path.toString().endsWith(suffix) }
    paths.forEach {
        try {
            load(it.path)
        } catch (e: Throwable) {
            logger.error { e }
        }
    }
}

enum class ExtensionType {
    Native,
    Lua
}

@Single
class ExtensionLoaderFactory : AbstractFactory<ExtensionType, ExtensionLoader>, KoinComponent {
    private val nativeLoader by inject<NativeExtensionLoader>()
    private val luaLoader by inject<LuaExtensionLoader>()

    override fun getInstance(type: ExtensionType): ExtensionLoader {
        return when (type) {
            ExtensionType.Native -> nativeLoader
            ExtensionType.Lua -> luaLoader
        }
    }

    fun getAllInstance(): List<ExtensionLoader> {
        return listOf(nativeLoader, luaLoader)
    }
}
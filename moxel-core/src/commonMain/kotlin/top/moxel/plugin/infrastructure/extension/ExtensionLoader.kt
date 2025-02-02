package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.infrastructure.environment.PathStorage
import top.moxel.plugin.infrastructure.io.VirtualFile

enum class ExtensionType {
    Native,
    Lua
}

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
expect class NativeExtensionLoader() : ExtensionLoader, KoinComponent {
    override fun loadAll()
    override fun load(path: Path)
    override fun freeAll()
}

internal fun NativeExtensionLoader.commonLoadAll(suffix: String) {
    val pathStorage by inject<PathStorage>()
    val logger = KotlinLogging.logger {}
    val paths =
        VirtualFile(pathStorage.extension).listFiles()
            .filter { it.path.toString().endsWith(suffix) }
    paths.forEach {
        try {
            load(it.path)
        } catch (e: Throwable) {
            logger.error { e }
        }
    }
}

@Single
class LuaExtensionLoader : ExtensionLoader, KoinComponent {
    private val manager by inject<LuaEngineManager>()
    private val pathStorage by inject<PathStorage>()
    private val logger = KotlinLogging.logger {}

    override fun load(path: Path) {
        val code = VirtualFile(path).loadText()
        val engine = manager.getOrCreate(LuaEngineId(LuaEngineType.EXTENSION, path.name))
        engine.eval(code)
    }

    override fun loadAll() {
        VirtualFile(pathStorage.extension)
            .listFiles()
            .forEach {
                try {
                    load(it.path)
                    logger.debug { "Lua extension: $it loaded" }
                } catch (e: Exception) {
                    logger.error { "Load lua extension error: $it" }
                }
            }
    }

    override fun freeAll() {
        manager.disposeExtensions()
        logger.debug { "Lua extension closed" }
    }
}
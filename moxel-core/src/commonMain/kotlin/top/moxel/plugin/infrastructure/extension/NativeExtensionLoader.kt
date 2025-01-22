package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.environment.Environment
import top.moxel.plugin.infrastructure.io.FakeFile

@Single
expect open class NativeExtensionLoader() : KoinComponent {
    /**
     * Load platform specific extension to modify the behavior of the plugin
     */
    fun load(path: Path)

    /**
     * Load all platform specific extensions
     */
    fun loadAll()

    /**
     * Free memory
     */
    fun freeAll()
}

internal fun NativeExtensionLoader.commonLoadAll(suffix: String) {
    val env by inject<Environment>()
    val logger = KotlinLogging.logger {}
    val extensionDir = env.dataRoot.resolve("extension")
    val paths =
        FakeFile(extensionDir).listFiles().filter { it.path.toString().endsWith(suffix) }
    paths.forEach {
        try {
            load(it.path)
        }
        catch (e: Throwable) {
            logger.error { e }
        }
    }
}
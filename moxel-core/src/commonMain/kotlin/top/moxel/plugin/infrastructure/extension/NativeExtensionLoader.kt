package top.moxel.plugin.infrastructure.extension

import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.environment.Environment
import top.moxel.plugin.infrastructure.io.FakeFile

@Single
expect class NativeExtensionLoader() : KoinComponent {
    /**
     * Load platform specific extension to modify the behavior of the plugin
     */
    fun load(path: Path)

    /**
     * Load all extensions
     */
    fun loadAll()
}

fun NativeExtensionLoader.loadAllImpl(suffix: String) {
    val env by inject<Environment>()
    val extensionDir = env.dataRoot.resolve("extension")
    val paths =
        FakeFile(extensionDir).listFiles().filter { it.path.toString().endsWith(suffix) }
    paths.forEach {
        load(it.path)
    }
}
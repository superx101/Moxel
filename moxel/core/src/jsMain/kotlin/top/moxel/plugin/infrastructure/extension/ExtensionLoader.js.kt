package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import top.moxel.plugin.annotation.di.Singleton
import top.moxel.plugin.infrastructure.io.VirtualFile

@Singleton
actual open class NativeExtensionLoader : ExtensionLoader {
    private val logger = KotlinLogging.logger {}

    actual override fun load(path: Path) {
        try {
            val code = VirtualFile(path).loadText()
            eval(code)
            logger.info { "Extension $path loaded successfully" }
        } catch (e: Exception) {
            throw RuntimeException("Error loading extension $path: $e")
        }
    }

    actual override fun loadAll() {
        commonLoadAll(".js")
    }

    actual override fun freeAll() {
        // do nothing
    }
}

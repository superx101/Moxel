package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import top.moxel.plugin.infrastructure.NonFatalException
import top.moxel.plugin.infrastructure.io.VirtualFile

@Single
actual open class NativeExtensionLoader : ExtensionLoader, KoinComponent {
    private val logger = KotlinLogging.logger {}

    actual override fun load(path: Path) {
        try {
            val code = VirtualFile(path).loadText()
            eval(code)
            logger.info { "Extension $path loaded successfully" }
        } catch (e: Exception) {
            throw NonFatalException("Error loading extension $path: $e")
        }
    }

    actual override fun loadAll() {
        commonLoadAll(".js")
    }

    actual override fun freeAll() {
        // do nothing
    }
}
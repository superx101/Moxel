package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import top.moxel.plugin.infrastructure.io.FakeFile

@Single
actual class NativeExtensionLoader : KoinComponent {
    private val logger = KotlinLogging.logger {}

    actual fun load(path: Path) {
        try {
            val code = FakeFile(path).loadText()
            js(code)
            logger.info { "Extension $path loaded successfully" }
        } catch (e: Exception) {
            logger.error { "Error loading extension $path: $e" }
        }
    }

    actual fun loadAll() {
        loadAllImpl(".js")
    }
}
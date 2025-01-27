package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.cinterop.*
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import platform.posix.RTLD_LAZY
import platform.posix.dlclose
import platform.posix.dlopen
import platform.posix.dlsym
import top.moxel.plugin.infrastructure.NonFatalException

@Single
actual open class NativeExtensionLoader : ExtensionLoader, KoinComponent {
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalForeignApi::class)
    private val handleList = mutableListOf<CPointer<out CPointed>>()

    @OptIn(ExperimentalForeignApi::class)
    actual override fun load(path: Path) {
        val filepath = path.toString()
        val handle =
            dlopen(filepath, RTLD_LAZY) ?: throw NonFatalException("Failed to load $filepath")

        val funcPtr = dlsym(handle, "onStart") ?: throw NonFatalException(
            "Failed to load function onStart in $filepath"
        )
        val func = funcPtr.reinterpret<CFunction<()->Unit>>()
        func.invoke()

        handleList.add(handle)
        logger.info { "start extension: $filepath, successfully" }
    }

    actual override fun loadAll() {
        commonLoadAll(".so")
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun freeAll() {
        for (handle in handleList) {
            dlclose(handle)
        }
    }
}
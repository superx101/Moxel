package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.cinterop.*
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import platform.windows.FreeLibrary
import platform.windows.GetProcAddress
import platform.windows.HMODULE
import platform.windows.LoadLibraryW

@Single
actual open class NativeExtensionLoader : ExtensionLoader, KoinComponent {
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalForeignApi::class)
    private val handleList = mutableListOf<HMODULE>()

    @OptIn(ExperimentalForeignApi::class)
    actual override fun load(path: Path) {
        val filepath = path.toString()
        memScoped {
            val libHandle = LoadLibraryW(filepath.wcstr.ptr.toString())
                ?: error("Failed to load $filepath")

            val funcPtr = GetProcAddress(libHandle, "onStart")
                ?: error("Failed to load function onStart in $filepath")
            val func = funcPtr.reinterpret<CFunction<() -> Unit>>()
            func.invoke()

            handleList.add(libHandle)
            logger.info { "start extension: $filepath, successfully" }
        }
    }

    actual override fun loadAll() {
        commonLoadAll(".dll")
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun freeAll() {
        for (handle in handleList) {
            FreeLibrary(handle)
        }
    }
}

package top.moxel.plugin.infrastructure.extension

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.wcstr
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import platform.windows.LoadLibrary
import top.moxel.plugin.infrastructure.SystemException
import kotlin.experimental.ExperimentalNativeApi

class UnsupportedOSException(message: String) : SystemException(message)

@Single
actual class NativeExtensionLoader : KoinComponent {
    private val suffix = getSuffix()

    @OptIn(ExperimentalNativeApi::class)
    private fun getSuffix(): String {
        return when (Platform.osFamily) {
            OsFamily.LINUX -> ".so"
            OsFamily.ANDROID -> ".so"
            OsFamily.WINDOWS -> ".dll"
            else -> throw UnsupportedOSException("Unsupported OS: ${Platform.osFamily}")
        }
    }

    @OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)
    actual fun load(path: Path) {
        val filepath = path.toString()

        when (Platform.osFamily) {
            OsFamily.LINUX -> {
                // TODO
            }
            OsFamily.ANDROID -> {
                // TODO
            }
            OsFamily.WINDOWS -> {
                memScoped {
                    LoadLibrary?.let { it(filepath.wcstr.getPointer(this)) }
                }
            }
            else -> {
                throw UnsupportedOSException("Unsupported OS: ${Platform.osFamily}")
            }
        }
    }

    actual fun loadAll() {
        loadAllImpl(suffix)
    }
}
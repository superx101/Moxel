package top.moxel.plugin.infrastructure.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

actual object FileLoader {
    actual suspend fun loadFileAsync(path: String): String {
        return withContext(Dispatchers.IO) {
            try {
                 File(path).readText()
            } catch (e: IOException) {
                throw IOException("Failed to load file from path: $path", e)
            }
        }
    }
}
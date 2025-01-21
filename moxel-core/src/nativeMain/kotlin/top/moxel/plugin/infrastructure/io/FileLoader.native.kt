package top.moxel.plugin.infrastructure.io

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread

actual object FileLoader {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun loadFileAsync(path: String): String {
        return withContext(Dispatchers.IO) {
            val file =
                fopen(path, "r") ?: throw Exception("File not found or unable to open file: $path")

            try {
                val buffer = ByteArray(1024)
                val result = StringBuilder()

                // Read file contents into buffer
                var bytesRead: Int
                while (true) {
                    bytesRead = fread(
                        buffer.refTo(0),
                        1u,
                        buffer.size.convert(),
                        file
                    ).toInt()
                    if (bytesRead == 0) break
                    result.append(buffer.decodeToString(0, bytesRead))
                }

                result.toString()
            } finally {
                fclose(file)
            }
        }
    }
}
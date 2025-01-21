package top.moxel.plugin.infrastructure.io

import kotlinx.browser.window
import kotlinx.coroutines.await

actual object FileLoader {
    actual suspend fun loadFileAsync(path: String): String {
        val response = window.fetch(path).await()
        return response.text().await()
    }
}
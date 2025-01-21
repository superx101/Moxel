package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.browser.document
import top.moxel.plugin.infrastructure.io.FileLoader

actual object NativeExtensionLoader {
    private val logger = KotlinLogging.logger {}

    private fun getExtensionPathsFromCookie(): List<String> {
        val cookie = document.cookie
        val extensionsCookie = cookie.split(";").find {
            it.trim().startsWith("extensions=")
        }
        return extensionsCookie?.substringAfter("=")?.split(",")?.map { it.trim() } ?: emptyList()
    }

    /***
     * Load js file from network
     */
    actual suspend fun load(filepath: String) {
        val jsFilePath = getExtensionPathsFromCookie().find { it.endsWith(filepath) }
        jsFilePath?.let {
            try {
                val code = FileLoader.loadFileAsync(it)
                js(code)
                logger.info { "Extension $filepath loaded successfully" }
            }
            catch (e: Exception) {
                logger.error { "Error loading extension $filepath: $e" }
            }
        } ?: logger.error { "Extension $filepath not found in cookie" }
    }

    /***
     * Load all js files from network by cookie
     */
    actual suspend fun loadAll() {
        val paths = getExtensionPathsFromCookie()
        if (paths.isEmpty()) {
            logger.error { "No extensions found in cookie." }
        } else {
            for (path in paths) {
                load(path)
            }
        }
    }
}
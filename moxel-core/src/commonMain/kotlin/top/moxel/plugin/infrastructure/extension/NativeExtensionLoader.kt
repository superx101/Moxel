package top.moxel.plugin.infrastructure.extension

expect object NativeExtensionLoader {
    /**
     * Load platform specific extension to modify the behavior of the plugin
     */
    suspend fun load(filepath: String)

    /**
     * Load all extensions
     */
    suspend fun loadAll()
}
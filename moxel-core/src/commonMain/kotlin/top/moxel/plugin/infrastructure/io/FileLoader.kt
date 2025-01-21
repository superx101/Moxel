package top.moxel.plugin.infrastructure.io

expect object FileLoader {
    suspend fun loadFileAsync(path: String): String
}
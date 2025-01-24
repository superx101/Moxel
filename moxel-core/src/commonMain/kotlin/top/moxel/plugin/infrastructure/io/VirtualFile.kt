package top.moxel.plugin.infrastructure.io

import okio.Path

/**
 * abstract file tool
 */
expect class VirtualFile(path: Path) {
    val path: Path

    fun loadText(): String
    fun writeText(text: String)
    fun listFiles(): List<VirtualFile>
}
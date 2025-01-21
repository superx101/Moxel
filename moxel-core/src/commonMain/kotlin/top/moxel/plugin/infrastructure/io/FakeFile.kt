package top.moxel.plugin.infrastructure.io

import okio.Path

expect class FakeFile(path: Path) {
    val path: Path

    fun loadText(): String
    fun writeText(text: String)
    fun listFiles(): List<FakeFile>
}
package top.moxel.plugin.infrastructure.io

import okio.FileSystem
import okio.Path
import okio.buffer

actual class FakeFile actual constructor(actual val path: Path) {
    actual fun loadText(): String {
        val source = FileSystem.SYSTEM.source(path)
        return source.buffer().readUtf8()
    }

    actual fun writeText(text: String) {
        val sink = FileSystem.SYSTEM.sink(path)
        sink.buffer().writeUtf8(text)
    }

    actual fun listFiles(): List<FakeFile> {
        return FileSystem.SYSTEM.list(path)
            .map { FakeFile(it) }
    }
}
package top.moxel.plugin.infrastructure.io

import kotlinx.browser.localStorage
import okio.FileNotFoundException
import okio.Path
import okio.Path.Companion.toPath

object FilePrefix {
    const val FILE = "file://"
    const val DIR = "dir://"
}

actual class VirtualFile actual constructor(actual val path: Path) {
    actual fun loadText(): String {
        val filePath = FilePrefix.FILE + path.toString()
        val fileContent = localStorage.getItem(filePath)
        return fileContent
            ?: throw FileNotFoundException("File not found at path: $path")
    }

    actual fun writeText(text: String) {
        val filePath = FilePrefix.FILE + path.toString()
        localStorage.setItem(filePath, text)
    }

    actual fun listFiles(): List<VirtualFile> {
        val dirPath = FilePrefix.DIR + path.toString()
        val dirContent = localStorage.getItem(dirPath)

        return if (dirContent != null) {
            JSON.parse<Array<String>>(dirContent).toList().map { VirtualFile(it.toPath()) }
        } else {
            emptyList()
        }
    }
}
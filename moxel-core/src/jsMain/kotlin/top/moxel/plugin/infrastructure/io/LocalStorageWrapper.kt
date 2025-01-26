package top.moxel.plugin.infrastructure.io

import kotlinx.browser.localStorage
import org.khronos.webgl.Uint8Array
import top.moxel.plugin.infrastructure.NonFatalException

object LocalStorageWrapper {
    private val maxSize = 4 * 1024 * 1024 // 4MB

    /**
     * storage data with segment, exceeding browser limit
     */
    fun save(key: String, value: String) {
        val unit8Array = js("new TextEncoder().encode(value)") as Uint8Array
        val byteSize = unit8Array.length

        if (byteSize < maxSize) {
            return localStorage.setItem(key, value)
        }

        val segmentCount = (byteSize + maxSize - 1) / maxSize
        localStorage.setItem(key, "@Segment:$segmentCount")

        for (i in 0 until segmentCount) {
            val start = i * maxSize
            val end = minOf((i + 1) * maxSize, byteSize)

            val segmentArray = unit8Array.subarray(start, end)
            val base64Segment = js("btoa(String.fromCharCode.apply(null, segmentArray))") as String
            localStorage.setItem("${i}_$key", base64Segment)
        }
    }

    /**
     * load data, exceeding browser limit
     */
    fun load(key: String): String? {
        val segmentMetadata = localStorage.getItem(key)
        if (segmentMetadata == null || !segmentMetadata.startsWith("@Segment:"))
            return segmentMetadata

        val segmentCount = segmentMetadata.removePrefix("@Segment:").toInt()
        val segments = mutableListOf<String>()
        for (i in 0 until segmentCount) {
            val segment = localStorage.getItem("${i}_$key")
                ?: throw NonFatalException("Failed to load segment: $i")
            segments.add(segment)
        }
        return segments.joinToString("")
    }
}
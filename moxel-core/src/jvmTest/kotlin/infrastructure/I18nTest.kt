package infrastructure

import top.moxel.plugin.infrastructure.io.YamlConverter
import kotlin.test.Test
import kotlin.test.assertEquals

class I18nTest {
    @Test
    fun testYamlConverter() {
        javaClass.classLoader.getResourceAsStream("structure.yaml")?.let {
            val text = it.bufferedReader().use { it.readText() }
            val converter = YamlConverter()
            val map = converter.yaml2MutableMap(text)
            assertEquals(
                "{name=Test, version=1.0.0, array.1=a, array.2=b, array.3=c, object" +
                        ".key1=value1, object.key2.1=a, object.key2.2=b, object.key2.3=c}",
                map.toString()
            )
        }
    }
}
package top.moxel.plugin.infrastructure.io

import com.charleskorn.kaml.*
import top.moxel.plugin.infrastructure.systemAssert

class YamlConverter {
    private val map = mutableMapOf<String, String>()
    private var parentKeyStack: ArrayDeque<String> = ArrayDeque()

    private fun visitScalar(scalar: YamlScalar) {
        map[parentKeyStack.joinToString(".")] = scalar.content
    }

    private fun visitList(list: YamlList) {
        list.items.forEachIndexed { index, item ->
            parentKeyStack.addLast((index + 1).toString())
            visit(item)
            parentKeyStack.removeLast()
        }
    }

    private fun visitMap(map: YamlMap) {
        map.entries.forEach {
            parentKeyStack.addLast(it.key.content)
            visit(it.value)
            parentKeyStack.removeLast()
        }
    }

    private fun visit(node: YamlNode) {
        when(node) {
            is YamlScalar -> visitScalar(node)
            is YamlMap -> visitMap(node)
            is YamlList -> visitList(node)
            else -> {
                systemAssert(true) {
                    "Unsupported yaml node type"
                }
            }
        }
    }

    fun yaml2MutableMap(text: String): MutableMap<String, String> {
        val yamlNode = Yaml.default.parseToYamlNode(text)

        systemAssert(yamlNode !is YamlMap) {
            "Yaml text should be a map"
        }

        map.clear()
        parentKeyStack.clear()

        visitMap(yamlNode as YamlMap)
        return map.toMutableMap()
    }
}
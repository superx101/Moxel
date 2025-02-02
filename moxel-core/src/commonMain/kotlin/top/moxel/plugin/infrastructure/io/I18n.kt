package top.moxel.plugin.infrastructure.io

import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.annotation.lua.LuaLibFunction
import top.moxel.plugin.infrastructure.environment.PathStorage
import top.moxel.plugin.infrastructure.io.I18nContainer.addLanguageByYaml

object I18nContainer {
    private val languageMap = mutableMapOf<String, MutableMap<String, String>>()

    fun getValue(langCode: String, key: String): String {
        return languageMap[langCode]?.get(key) as String
    }

    fun addLanguage(langCode: String, i18nMap: Map<String, String>) {
        val mutableMap = languageMap[langCode] ?: mutableMapOf()
        i18nMap.forEach { (key, value) ->
            mutableMap[key] = value
        }
    }

    fun addLanguageByYaml(langCode: String, text: String) {
        val yamlConverter = YamlConverter()
        val i18nMap = yamlConverter.yaml2MutableMap(text)
        addLanguage(langCode, i18nMap)
    }

    fun String.i18n(langCode: String, vararg replacedValues: String): String {
        val valueString = getValue(langCode, this)

        val stringBuilder = StringBuilder(valueString)
        replacedValues.forEachIndexed { index, value ->
            val placeholder = "{$index}"
            var startIndex = stringBuilder.indexOf(placeholder)
            while (startIndex >= 0) {
                stringBuilder.replaceRange(startIndex, startIndex + placeholder.length, value)
                startIndex = stringBuilder.indexOf(placeholder, startIndex + value.length)
            }
        }
        return stringBuilder.toString()
    }
}

@Single
class I18nFileLoader : KoinComponent {
    private val pathStorage by inject<PathStorage>()

    private fun loadFile(path: Path) {
        val text = VirtualFile(path).loadText()
        val filename = path.name.substringBeforeLast(".")
        addLanguageByYaml(filename, text)
    }

    fun loadFiles() {
        val languagesDirPath = pathStorage.language
        val files = VirtualFile(languagesDirPath).listFiles()
        files.forEach {
            loadFile(it.path)
        }
    }
}

@LuaLibFunction(type = LuaEngineType.EXTENSION, group = "i18n")
fun addLanguage(langCode: String, i18nMap: Map<String, String>) =
    I18nContainer.addLanguage(langCode, i18nMap)
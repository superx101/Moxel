package top.moxel.plugin.infrastructure.io

import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.environment.Environment
import top.moxel.plugin.infrastructure.io.I18nContainer.addLanguageByYaml

typealias I18nMap = MutableMap<String, String>

object I18nContainer {
    private val languageMap = mutableMapOf<String, I18nMap>()

    fun getValue(langCode: String, key: String): String {
        return languageMap[langCode]?.get(key) as String
    }

    fun addLanguage(langCode: String, i18nMap: I18nMap) {
        languageMap[langCode] = i18nMap
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
    private val env by inject<Environment>()

    private fun loadFile(path: Path) {
        val text = FakeFile(path).loadText()
        val filename = path.name.substringBeforeLast(".")
        addLanguageByYaml(filename, text)
    }

    fun loadFiles() {
        val languagesDirPath = env.dataRoot.resolve("language")
        val files = FakeFile(languagesDirPath).listFiles()
        files.forEach {
            loadFile(it.path)
        }
    }
}

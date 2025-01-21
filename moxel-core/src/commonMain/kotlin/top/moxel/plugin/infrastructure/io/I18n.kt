package top.moxel.plugin.infrastructure.io

typealias I18nMap = MutableMap<String, String>

fun String.i18n(langCode: String, vararg replacedValues: String): String {
    val valueString = I18nContainer.getValue(langCode, this)

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

    suspend fun loadFile(langCode: String, path: String) {
        val text = FileLoader.loadFileAsync(path)
        addLanguageByYaml(langCode, text)
    }
}
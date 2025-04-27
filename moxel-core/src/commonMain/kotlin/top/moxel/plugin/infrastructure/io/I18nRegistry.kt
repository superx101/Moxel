package top.moxel.plugin.infrastructure.io

object I18nRegistry {
    private val languageMap = mutableMapOf<String, MutableMap<String, String>>()

    fun getValue(langCode: String, key: String): String = languageMap[langCode]?.get(key) as String

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

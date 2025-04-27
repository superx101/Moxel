package top.moxel.plugin.infrastructure.io

import okio.Path
import top.moxel.plugin.annotation.di.Singleton
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.annotation.lua.LuaLibFunction
import top.moxel.plugin.infrastructure.di.inject
import top.moxel.plugin.infrastructure.environment.PathStorage
import top.moxel.plugin.infrastructure.io.I18nRegistry.addLanguageByYaml

@Singleton
class I18nFileLoader {
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
    I18nRegistry.addLanguage(langCode, i18nMap)

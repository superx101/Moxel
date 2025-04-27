package top.moxel.plugin

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import top.moxel.plugin.infrastructure.di.inject
import top.moxel.plugin.infrastructure.extension.ExtensionLoaderFactory
import top.moxel.plugin.infrastructure.extension.ExtensionType
import top.moxel.plugin.infrastructure.io.I18nFileLoader
import top.moxel.plugin.infrastructure.io.LoggingConfiguration

object Moxel {
    private val logger = KotlinLogging.logger {}
    private val i18nFileLoader by inject<I18nFileLoader>()
    private val extensionLoaderFactory by inject<ExtensionLoaderFactory>()

    private fun loadConfiguration() {
//        TODO("other configurations")
        LoggingConfiguration()
    }

    /**
     * before run, start DI first
     */
    suspend fun run() = coroutineScope {
        logger.debug { "Starting Moxel" }

        loadConfiguration()
        logger.debug { "Configuration loaded" }

        i18nFileLoader.loadFiles()
        logger.debug { "I18n loaded" }

        // TODO test nativeExtensionLoader
        extensionLoaderFactory
            .getInstance(ExtensionType.Native)
            .loadAll()
        logger.debug { "Native extensions loaded" }

        extensionLoaderFactory
            .getInstance(ExtensionType.Lua)
            .loadAll()
        logger.debug { "Lua extensions loaded" }

//        TODO("start lua extension engine manager")

//        TODO("start lua expression engine manager")

        logger.debug { "Moxel started" }
    }

    suspend fun stop() = coroutineScope {
        extensionLoaderFactory.getAllInstance().forEach { it.freeAll() }
    }
}

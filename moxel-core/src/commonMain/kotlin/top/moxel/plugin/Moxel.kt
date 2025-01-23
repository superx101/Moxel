package top.moxel.plugin

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.extension.NativeExtensionLoader
import top.moxel.plugin.infrastructure.io.I18nFileLoader
import top.moxel.plugin.infrastructure.io.LoggingConfiguration

class Moxel : KoinComponent {
    private val logger = KotlinLogging.logger {}
    private val i18nFileLoader by inject<I18nFileLoader>()
    private val nativeExtensionLoader by inject<NativeExtensionLoader>()

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
        nativeExtensionLoader.loadAll()
        logger.debug { "Native extensions loaded" }

//        TODO("start lua extension engine manager")

//        TODO("start lua expression engine manager")

        logger.debug { "Moxel started" }
    }

    suspend fun stop() = coroutineScope {
        nativeExtensionLoader.freeAll()
    }
}
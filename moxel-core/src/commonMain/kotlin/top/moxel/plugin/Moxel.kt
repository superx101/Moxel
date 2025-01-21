package top.moxel.plugin

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import org.koin.core.module.Module
import top.moxel.plugin.infrastructure.DI
import top.moxel.plugin.infrastructure.extension.NativeExtensionLoader
import top.moxel.plugin.infrastructure.io.I18nFileLoader
import top.moxel.plugin.infrastructure.io.LoggingConfiguration

class Moxel {
    private val logger = KotlinLogging.logger {}

    private fun loadConfiguration() {
//        TODO("other configurations")
        LoggingConfiguration()
    }

    suspend fun run(module: Module) = coroutineScope {
        logger.debug { "Starting Moxel" }

        DI.registerModule(module)
        DI.startApplication()
        logger.debug { "DI started" }

        loadConfiguration()
        logger.debug { "Configuration loaded" }

        I18nFileLoader().loadFiles()
        logger.debug { "I18n loaded" }

        NativeExtensionLoader().loadAll()
        logger.debug { "Native extensions loaded" }

//        TODO("start lua extension engine manager")


//        TODO("start lua expression engine manager")

        logger.debug { "Moxel started" }
    }
}
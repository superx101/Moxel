package top.moxel.plugin

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.io.LoggingConfiguration
import top.moxel.plugin.infrastructure.platform.Platform


class MoxelCore : KoinComponent {
    private val logger = KotlinLogging.logger {}
    private val platform by inject<Platform>()

    init {
        LoggingConfiguration()
        logger.debug { "Moxel Core initialized" }

        logger.debug { "Moxel Core started" }
        logger.info { platform.name + " " + platform.target + " " + platform.edition }
    }
}
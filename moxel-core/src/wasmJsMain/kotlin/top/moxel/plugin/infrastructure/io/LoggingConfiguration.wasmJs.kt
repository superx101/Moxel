package top.moxel.plugin.infrastructure.io

import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level

actual class LoggingConfiguration actual constructor() {
    init {
        KotlinLoggingConfiguration.logLevel = Level.DEBUG
    }
}
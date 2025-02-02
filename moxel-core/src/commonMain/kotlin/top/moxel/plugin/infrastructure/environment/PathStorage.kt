package top.moxel.plugin.infrastructure.environment

import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PathStorage : KoinComponent {
    private val env by inject<Environment>()

    val extension = env.dataRoot.resolve("extension")
    val language = env.dataRoot.resolve("language")
}
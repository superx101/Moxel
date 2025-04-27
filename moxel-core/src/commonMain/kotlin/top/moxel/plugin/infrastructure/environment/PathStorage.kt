package top.moxel.plugin.infrastructure.environment

import top.moxel.plugin.annotation.di.Singleton
import top.moxel.plugin.infrastructure.di.inject

@Singleton
class PathStorage {
    private val env by inject<Environment>()

    val extension = env.dataRoot.resolve("extension")
    val language = env.dataRoot.resolve("language")
}

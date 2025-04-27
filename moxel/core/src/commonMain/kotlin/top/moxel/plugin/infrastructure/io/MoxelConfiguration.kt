package top.moxel.plugin.infrastructure.io

import kotlinx.serialization.Serializable

@Serializable
data class MoxelConfiguration(
    var debugMode: Boolean = false,
)

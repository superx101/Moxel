package top.moxel.plugin.infrastructure.environment

import top.moxel.plugin.annotation.di.ExpectedComponent

@ExpectedComponent
interface ModLoader {
    val name: String
    val target: PlatformTarget
    val minecraftType: MinecraftEditionType
    val version: String
}

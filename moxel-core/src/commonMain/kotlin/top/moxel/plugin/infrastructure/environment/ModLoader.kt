package top.moxel.plugin.infrastructure.environment

import top.moxel.plugin.infrastructure.SingleClass

@SingleClass
interface ModLoader {
    val name: String
    val target: PlatformTarget
    val minecraftType: MinecraftEditionType
    val version: String
}

package top.moxel.plugin.fabric.infrastructure

import top.moxel.plugin.infrastructure.platform.MinecraftEdition
import top.moxel.plugin.infrastructure.platform.Platform
import top.moxel.plugin.infrastructure.platform.PlatformTarget

class FabricPlatform : Platform {
    override val name: String
        get() = "fabric"

    override val target: PlatformTarget
        get() = PlatformTarget.Jvm

    override val edition: MinecraftEdition
        get() = MinecraftEdition.Java

    override val version: String
        get() = ""
}
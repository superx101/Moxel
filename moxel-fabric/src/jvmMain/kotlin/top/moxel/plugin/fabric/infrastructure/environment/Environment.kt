package top.moxel.plugin.fabric.infrastructure.environment

import org.koin.core.annotation.Single
import top.moxel.plugin.infrastructure.environment.Environment
import top.moxel.plugin.infrastructure.environment.MinecraftEdition
import top.moxel.plugin.infrastructure.environment.ModLoader
import top.moxel.plugin.infrastructure.environment.PlatformTarget

@Single
class FabricModLoader : ModLoader {
    override val name: String
        get() = TODO("Not yet implemented")
    override val target: PlatformTarget
        get() = TODO("Not yet implemented")
    override val edition: MinecraftEdition
        get() = TODO("Not yet implemented")
    override val version: String
        get() = TODO("Not yet implemented")
}

@Single
class FabricEnvironment : Environment {
    override val root: String
        get() = TODO("Not yet implemented")
    override val dataRoot: String
        get() = TODO("Not yet implemented")
}
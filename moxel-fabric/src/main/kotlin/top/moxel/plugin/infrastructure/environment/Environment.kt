package top.moxel.plugin.infrastructure.environment

import okio.Path
import org.koin.core.annotation.Single

@Single
class FabricModLoader : ModLoader {
    override val name: String
        get() = "Fabric"
    override val target: PlatformTarget
        get() = PlatformTarget.Jvm
    override val minecraftType: MinecraftEditionType
        get() = MinecraftEditionType.Java
    override val version: String
        get() = TODO("Not yet implemented")
}

@Single
class FabricEnvironment : Environment {
    override val root: Path
        get() = TODO("Not yet implemented")
    override val dataRoot: Path
        get() = TODO("Not yet implemented")
}

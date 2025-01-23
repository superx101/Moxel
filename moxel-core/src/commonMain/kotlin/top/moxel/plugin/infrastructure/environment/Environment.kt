package top.moxel.plugin.infrastructure.environment

import okio.Path
import top.moxel.plugin.infrastructure.SingleClass

enum class MinecraftEditionType {
    Bedrock,
    Java,
}

enum class PlatformTarget {
    Jvm,
    Web,
    Native,
}

@SingleClass
interface ModLoader {
    val name: String
    val target: PlatformTarget
    val minecraftType: MinecraftEditionType
    val version: String
}

@SingleClass
interface Environment {
    val root: Path
    val dataRoot: Path
}
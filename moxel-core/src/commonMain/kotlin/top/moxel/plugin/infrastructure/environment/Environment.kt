package top.moxel.plugin.infrastructure.environment

enum class MinecraftEdition {
    Bedrock,
    Java,
}

enum class PlatformTarget {
    Jvm,
    Web,
    Native,
}

interface ModLoader {
    val name: String
    val target: PlatformTarget
    val edition: MinecraftEdition
    val version: String
}

interface Environment {
    val root: String
    val dataRoot: String
}
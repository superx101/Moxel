package top.moxel.plugin.infrastructure.platform

enum class PlatformTarget {
    Jvm,
    Web,
    Native,
}

interface Platform {
    val name: String
    val target: PlatformTarget
    val edition: MinecraftEdition
    val version: String
}
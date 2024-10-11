package top.moxel.plugin.browser.infrastructure

import org.koin.core.annotation.Single
import top.moxel.plugin.infrastructure.platform.MinecraftEdition
import top.moxel.plugin.infrastructure.platform.Platform
import top.moxel.plugin.infrastructure.platform.PlatformTarget

@Single
class BrowserPlatform : Platform {
    override val name: String
        get() = "browser"

    override val target: PlatformTarget
        get() = PlatformTarget.Web

    override val edition: MinecraftEdition
        get() = MinecraftEdition.Bedrock

    override val version: String
        get() = ""
}
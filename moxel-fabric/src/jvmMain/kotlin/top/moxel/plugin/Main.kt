package top.moxel.plugin

import top.moxel.plugin.fabric.infrastructure.FabricPlatform
import top.moxel.plugin.infrastructure.platform.Platform
import top.moxel.plugin.infrastructure.di.DI

fun main() {
    DI.addSingleton<Platform>(FabricPlatform())
    DI.startApplication()

    MoxelCore()
}
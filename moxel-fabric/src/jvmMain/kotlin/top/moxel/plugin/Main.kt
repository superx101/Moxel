package top.moxel.plugin

import org.koin.ksp.generated.module
import top.moxel.plugin.fabric.infrastructure.di.FabricModules
import top.moxel.plugin.infrastructure.di.DI

fun main() {
    DI.addModule(FabricModules().module)
    DI.startApplication()

    MoxelCore()
}
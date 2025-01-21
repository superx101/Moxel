package top.moxel.plugin

import kotlinx.coroutines.runBlocking
import org.koin.ksp.generated.module
import top.moxel.plugin.fabric.infrastructure.di.FabricModules
import top.moxel.plugin.infrastructure.DI

fun main() = runBlocking {
    val moxel = Moxel()
    moxel.run(FabricModules().module)
}
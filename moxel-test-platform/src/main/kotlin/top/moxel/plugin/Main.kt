package top.moxel.plugin

import okio.Path
import okio.Path.Companion.toPath
import top.moxel.plugin.annotation.di.ActualComponent
import top.moxel.plugin.annotation.di.Singleton
import top.moxel.plugin.infrastructure.di.InstanceContainer
import top.moxel.plugin.infrastructure.di.inject
import top.moxel.plugin.infrastructure.environment.Environment
import top.moxel.plugin.infrastructure.environment.MinecraftEditionType
import top.moxel.plugin.infrastructure.environment.ModLoader
import top.moxel.plugin.infrastructure.environment.PlatformTarget
import top.moxel.plugin.ksp.generated.CoreComponentDefinitions
import top.moxel.plugin.ksp.generated.TestComponentDefinitions

@ActualComponent
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

@ActualComponent
class FabricEnvironment : Environment {
    override val root: Path
        get() = "test root".toPath()
    override val dataRoot: Path
        get() = TODO("Not yet implemented")
}

@Singleton
class A() {
    fun print() {
        println("A")
    }
}

fun main() {
    InstanceContainer.registerModules(
        CoreComponentDefinitions.module,
        TestComponentDefinitions.module,
    )

    InstanceContainer.showAll()

    val env = InstanceContainer.get<Environment>()
    println(env.root)

    val a by inject<A>()
    a.print()
}

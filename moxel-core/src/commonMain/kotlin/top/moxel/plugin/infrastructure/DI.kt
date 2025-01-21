package top.moxel.plugin.infrastructure

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

object DI {
    private val moduleList = mutableListOf<Module>()

    fun registerModule(module: Module) {
        moduleList.add(module)
    }

    fun startApplication() {
        startKoin {
            val parent = module {
                includes(moduleList)
            }
            modules(parent, Modules().module)
        }
    }
}
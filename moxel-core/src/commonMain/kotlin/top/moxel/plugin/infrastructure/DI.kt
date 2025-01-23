package top.moxel.plugin.infrastructure

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

/**
 * Indicates that the current interface will be implemented by subproject, as singleton
 */
annotation class SingleClass()

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
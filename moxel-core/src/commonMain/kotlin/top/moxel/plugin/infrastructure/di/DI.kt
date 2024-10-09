package top.moxel.plugin.infrastructure.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

class DI {
    companion object {
        val moduleList = mutableListOf<Module>()

        fun startApplication() {
            startKoin {
                val parent = module {
                    includes(moduleList)
                }
                val coreModules = Modules()
                modules(parent, coreModules.module)
            }
        }

        inline fun <reified T> addSingleton(component: T) {
            moduleList.add(module { single<T> { component } })
        }
    }
}
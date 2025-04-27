package top.moxel.plugin.infrastructure.di

import kotlin.reflect.KClass

class Module {
    val definitions = mutableListOf<ComponentDefinition<*>>()

    fun <T> addDefinition(definition: ComponentDefinition<T>) {
        definitions.add(definition)
    }

    inline fun <reified R> add(
        type: ComponentDefinition.Type,
        noinline constructor: InstanceConstructor<R>,
        identifier: String,
    ) {
        this.addDefinition(
            ComponentDefinition(
                identifier,
                InstanceFactory(constructor),
                type,
            ),
        )
    }

    inline fun <reified R> singleOf(
        noinline constructor: InstanceConstructor<R>,
        identifierClazz: KClass<*>? = null,
    ) = add(
        ComponentDefinition.Type.SINGLETON,
        constructor,
        identifierClazz?.getFullName()
            ?: R::class.getFullName()
    )

    inline fun <reified R> factoryOf(
        noinline constructor: InstanceConstructor<R>,
        identifierClazz: KClass<*>? = null,
    ) = this.add(
        ComponentDefinition.Type.FACTORY,
        constructor,
        identifierClazz?.getFullName()
            ?: R::class.getFullName()
    )
}

fun createModule(block: Module.() -> Unit): Module {
    val module = Module()
    module.block()
    return module
}

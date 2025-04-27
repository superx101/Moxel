package top.moxel.plugin.infrastructure.di

object InstanceContainer {
    private val factories = mutableMapOf<ComponentId, InstanceFactory<*>>()

    fun showAll() {
        println("Container instances:")
        for ((key) in factories) {
            println("  $key")
        }
    }

    fun register(identifier: ComponentId, factory: InstanceFactory<*>) {
        factories[identifier] = factory
    }

    fun registerModules(vararg modules: Module) {
        for (module in modules) {
            for (definition in module.definitions) {
                with(definition) {
                    // TODO: factory type injection
                    register(identifier, factory)
                }
            }
        }
    }

    fun <T : Any> get(identifier: String): T = factories[identifier]?.get() as? T
        ?: throw IllegalStateException("No dependency found for $identifier")

    inline fun <reified T : Any> get(): T = get(T::class.getFullName())
}

inline fun <reified T : Any> inject(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> =
    lazy(mode) { InstanceContainer.get() }

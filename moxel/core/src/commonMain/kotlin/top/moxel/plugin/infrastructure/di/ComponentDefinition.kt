package top.moxel.plugin.infrastructure.di

data class ComponentDefinition<T>(
    val identifier: String,
    val factory: InstanceFactory<T>,
    val
    type: Type,
) {
    enum class Type {
        SINGLETON,
        FACTORY,
    }
}

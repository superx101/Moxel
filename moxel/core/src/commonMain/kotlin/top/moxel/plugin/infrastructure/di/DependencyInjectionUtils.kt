package top.moxel.plugin.infrastructure.di

import kotlin.reflect.KClass

typealias ComponentId = String
typealias InstanceConstructor<T> = () -> T

fun getDefaultClassName(kClazz: KClass<*>): String = "KClass@${kClazz.hashCode()}"

fun KClass<*>.getFullName(): String {
    return this.qualifiedName ?: getDefaultClassName(this)
}

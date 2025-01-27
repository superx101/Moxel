package top.moxel.plugin.infrastructure.common

interface StaticFactory<T, R> {
    fun getInstance(type: T): R
}
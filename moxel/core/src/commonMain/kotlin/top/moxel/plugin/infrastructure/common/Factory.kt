package top.moxel.plugin.infrastructure.common

interface AbstractFactory<T, R> {
    fun getInstance(type: T): R
}

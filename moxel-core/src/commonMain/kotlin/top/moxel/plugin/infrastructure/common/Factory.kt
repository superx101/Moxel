package top.moxel.plugin.tool

interface StaticFactory<T, R> {
    fun getInstance(type: T): R
}
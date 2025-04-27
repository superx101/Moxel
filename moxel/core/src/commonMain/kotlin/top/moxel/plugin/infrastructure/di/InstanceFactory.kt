package top.moxel.plugin.infrastructure.di

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

class InstanceFactory<T>(private val constructor: () -> T) : SynchronizedObject() {
    private val instanceRef = atomic<T?>(null)

    fun get(): T = instanceRef.value ?: synchronized(this) {
        instanceRef.value ?: constructor().also { instanceRef.value = it }
    }
}

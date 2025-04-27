package top.moxel.plugin.infrastructure.common

/**
 * kotlin actual typealias cannot have type parameters, currently (2025.1.28)
 *
 * will remove this interface after kotlin update
 */
interface ActualWrapper<T> {
    var value: T
}

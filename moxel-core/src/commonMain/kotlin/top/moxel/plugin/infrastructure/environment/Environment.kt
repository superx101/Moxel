package top.moxel.plugin.infrastructure.environment

import okio.Path
import top.moxel.plugin.infrastructure.SingleClass

@SingleClass
interface Environment {
    val root: Path
    val dataRoot: Path
}
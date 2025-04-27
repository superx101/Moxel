package top.moxel.plugin.infrastructure.environment

import okio.Path
import top.moxel.plugin.annotation.di.ExpectedComponent

@ExpectedComponent
interface Environment {
    val root: Path
    val dataRoot: Path
}

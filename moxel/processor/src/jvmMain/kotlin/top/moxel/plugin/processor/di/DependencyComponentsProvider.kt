package top.moxel.plugin.processor.di

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class DependencyComponentsProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        DependencyComponentsProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options["module"]!!,
            environment.options["package"]!!,
        )
}

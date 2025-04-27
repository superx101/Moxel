package top.moxel.plugin.tool.di

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import top.moxel.plugin.annotation.di.ActualComponent
import top.moxel.plugin.annotation.di.ExpectedComponent
import top.moxel.plugin.annotation.di.Singleton

class DependencyComponentsProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val moduleName: String,
    private val packageName: String,
) : SymbolProcessor {
    private var invoked = false

    private fun collectSingletons(resolver: Resolver): List<String> {
        return resolver.getSymbolsWithAnnotation(Singleton::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { it.qualifiedName?.asString() }
            .toList()
    }

    private fun collectExpectedComponents(resolver: Resolver): List<String> {
        // TODO: use the expected component list for implementation check
        return resolver.getSymbolsWithAnnotation(ExpectedComponent::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { it.qualifiedName?.asString() }
            .toList()
    }

    private fun collectActualComponents(resolver: Resolver): List<Pair<String, String>> {
        return resolver.getSymbolsWithAnnotation(ActualComponent::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { implClass ->
                val implementedInterface = implClass.superTypes
                    .map { it.resolve().declaration }
                    .filterIsInstance<KSClassDeclaration>()
                    .firstOrNull()
                    ?: return@mapNotNull null

                val implName = implClass.qualifiedName?.asString() ?: return@mapNotNull null
                val interfaceName = implementedInterface.qualifiedName?.asString() ?: return@mapNotNull null

                interfaceName to implName
            }
            .toList()
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked)
            return emptyList()

        ComponentDefinitionsBuilder(packageName, moduleName)
            .buildList(collectSingletons(resolver), collectActualComponents(resolver))
            .build(codeGenerator)

        invoked = true
        return emptyList()
    }
}

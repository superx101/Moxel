package top.moxel.plugin.tool.di

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import top.moxel.plugin.tool.FileBuildHelper

class ComponentDefinitionsBuilder(private val packageName: String, projectName: String) {
    private val objectBuilder: TypeSpec.Builder
    private val blockBuilder: CodeBlock.Builder = CodeBlock.builder()
    private val className: String

    init {
        val name = projectName.lowercase().replaceFirstChar { it.uppercase() }
        className = "${name}ComponentDefinitions"
        objectBuilder = TypeSpec.objectBuilder(className)
    }

    fun buildList(
        singletonList: List<String>,
        actualPairList: List<Pair<String, String>>,
    ): ComponentDefinitionsBuilder {
        blockBuilder
            .addStatement(
                "%T {",
                ClassName("top.moxel.plugin.infrastructure.di", "createModule"),
            )
            .indent()

        for (singleton in singletonList) {
            blockBuilder.addStatement(
                "singleOf(::%T)",
                ClassName.bestGuess(singleton),
            )
        }

        for (pair in actualPairList) {
            blockBuilder.addStatement(
                "singleOf(::%T, %T::class)",
                ClassName.bestGuess(pair.second),
                ClassName.bestGuess(pair.first),
            )
        }

        blockBuilder.unindent()
            .addStatement("}")
            .build()

        return this
    }

    fun build(codeGenerator: CodeGenerator) {
        val moduleProperty = PropertySpec
            .builder(
                "module",
                ClassName("top.moxel.plugin.infrastructure.di", "Module"),
            )
            .initializer(blockBuilder.build())
            .build()

        objectBuilder.addProperty(moduleProperty)

        FileBuildHelper(codeGenerator)
            .buildClass(objectBuilder.build(), packageName, className)
    }
}

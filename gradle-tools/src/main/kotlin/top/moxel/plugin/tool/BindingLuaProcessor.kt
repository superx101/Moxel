package top.moxel.plugin.tool

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class BindingLuaProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator

    private val listType = List::class.asTypeName().parameterizedBy(
        Pair::class.asTypeName().parameterizedBy(
            String::class.asTypeName(),
            LambdaTypeName.get(
                parameters = arrayOf(
                    Array::class.asClassName().parameterizedBy(
                        listOf(
                            Any::class.asTypeName().copy(nullable = true)
                        )
                    )
                ),
                returnType = Any::class.asTypeName().copy(nullable = true)
            )
        )
    )
    private val annotationPackageName = "top.moxel.plugin.annotation.lua"
    private val annotationName = "LuaBinding"
    private val packageName = "top.moxel.plugin.ksp.generated"
    private val className = "LuaBindingList"
    private val objectBuilder = TypeSpec.objectBuilder(className)

    private fun generateBindingListByGroup(
        symbols: Sequence<KSFunctionDeclaration>,
        groupName: String
    ) {
        // build code
        val initCodeBuilder = CodeBlock
            .builder()
            .add("listOf (\n")
            .indent()
        symbols.forEach { symbol ->
            val functionName = symbol.simpleName.asString()
            val parameters = symbol.parameters
            val hasVararg = parameters.lastOrNull()?.isVararg == true
            val paramAccess = parameters.mapIndexed { index, value ->
                "it[$index] as ${value.type}"
            }.joinToString(", ")

            val customName = symbol.annotations
                .find { it.shortName.asString() == annotationName }?.arguments
                ?.find { it.name?.asString() == "name" }?.value as String?
                ?: functionName

            initCodeBuilder.add(
                """
                Pair("${customName.ifEmpty { functionName }}") { 
                    top.moxel.plugin.annotation.lua.checkParameters($hasVararg, ${parameters.size}, it.size);
                    return@Pair ${symbol.packageName.asString()}.$functionName($paramAccess);
                },
                
                """.trimIndent()
            )
        }
        initCodeBuilder
            .unindent()
            .add(")")

        objectBuilder.addProperty(
            PropertySpec.builder(
                "list_${groupName.lowercase()}",
                listType
            )
                .initializer(initCodeBuilder.build())
                .build()
        )
    }

    private fun writeToFile() {
        val builder = FileSpec
            .builder(packageName, className)
            .addType(objectBuilder.build())
            .build()
        codeGenerator.createNewFile(
            Dependencies(aggregating = false),
            packageName,
            className,
            "kt"
        ).writer().use { output ->
            builder.writeTo(output)
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver
                .getSymbolsWithAnnotation("$annotationPackageName.$annotationName")
                .filterIsInstance<KSFunctionDeclaration>()

        val groupedSymbols = symbols.groupBy { symbol ->
            val value = symbol.annotations
                .find { it.shortName.asString() == annotationName }?.arguments
                ?.find { it.name?.asString() == "group" }?.value as String
            value
        }
        groupedSymbols.forEach { (name, groupSymbols) ->
            generateBindingListByGroup(groupSymbols.asSequence(), name)
        }
        writeToFile()

        return emptyList()
    }
}

class BindingLuaProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        BindingLuaProcessor(environment)
}
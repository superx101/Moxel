package top.moxel.plugin.tool

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.annotation.lua.LuaLibDeclaration

class LuaLibBindingProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    private val listType = List::class.asTypeName().parameterizedBy(
        LuaLibDeclaration::class.asTypeName()
    )
    private val annotationPackageName = "top.moxel.plugin.annotation.lua"
    private val annotationName = "LuaLibFunction"
    private val packageName = "top.moxel.plugin.ksp.generated"
    private val className = "LuaBindingList"
    private val objectBuilder = TypeSpec.objectBuilder(className)

    private fun buildLuaBinding(codeBuilder: CodeBlock.Builder, symbol: KSFunctionDeclaration) {
        val callName = symbol.simpleName.asString()
        val parameters = symbol.parameters
        val hasVararg = parameters.lastOrNull()?.isVararg == true
        val paramAccess = parameters.mapIndexed { index, value ->
            val resolvedType = value.type.resolve()
            val type = StringBuilder()
            type.append(value.type)
            if (resolvedType.arguments.isNotEmpty()) {
                type.append("<")
                type.append(resolvedType.arguments.joinToString(", ") { arg ->
                    arg.type?.resolve()?.declaration?.qualifiedName?.asString() ?: "*"
                })
                type.append(">")
            }

            "it[$index] as $type"
        }.joinToString(", ")

        val customName = symbol.annotations
            .find { it.shortName.asString() == annotationName }?.arguments
            ?.find { it.name?.asString() == "name" }?.value as String?
            ?: callName

        codeBuilder.add(
            """
                $annotationPackageName.LuaBinding("${customName.ifEmpty { callName }}") { 
                    $annotationPackageName.checkParameters($hasVararg, ${parameters.size}, it.size);
                    ${symbol.packageName.asString()}.$callName($paramAccess);
                },
                
            """.trimIndent()
        )
    }

    private fun generateBindingDeclarationList(
        groupedSymbols: Map<Pair<LuaEngineType, String>, List<KSFunctionDeclaration>>
    ) {
        val codeBuilder = CodeBlock
            .builder()
            .addStatement("listOf (")
            .indent()

        groupedSymbols.forEach { (pair, list) ->
            codeBuilder.addStatement("$annotationPackageName.LuaLibDeclaration(")
                .indent()
                .addStatement(
                    "$annotationPackageName.LuaEngineType.${pair.first.name.uppercase()},"
                )
                .addStatement("\"${pair.second}\",")
                .addStatement("listOf(")
                .indent()

            list.forEach { symbol -> buildLuaBinding(codeBuilder, symbol) }

            codeBuilder.unindent()
                .addStatement(")")
                .unindent()
                .addStatement("),")
        }
        codeBuilder
            .unindent()
            .add(")")

        objectBuilder.addProperty(
            PropertySpec.builder(
                "list",
                listType
            )
                .initializer(codeBuilder.build())
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
            val arguments = symbol.annotations
                .find { it.shortName.asString() == annotationName }?.arguments!!

            val ksType = arguments.find { it.name?.asString() == "type" }!!.value as KSType
            val type = LuaEngineType.valueOf(ksType.declaration.simpleName.asString())
            val group = arguments.find { it.name?.asString() == "group" }?.value as String? ?: ""

            Pair(type, group)
        }
        generateBindingDeclarationList(groupedSymbols)
        writeToFile()

        return emptyList()
    }
}

class BindingLuaProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        LuaLibBindingProcessor(environment.logger, environment.codeGenerator)
}
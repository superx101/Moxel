package top.moxel.plugin.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

class FileBuildHelper(private val codeGenerator: CodeGenerator) {
    fun buildClass(typeSpec: TypeSpec, packageName: String, className: String) {
        val builder =
            FileSpec
                .builder(packageName, className)
                .addType(typeSpec)
                .build()
        codeGenerator.createNewFile(
            Dependencies(aggregating = false),
            packageName,
            className,
            "kt",
        ).writer().use { output ->
            builder.writeTo(output)
        }
    }
}

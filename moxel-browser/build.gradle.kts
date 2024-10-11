
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                mode = KotlinWebpackConfig.Mode.DEVELOPMENT
                devtool = "source-map"
            }
        }
        generateTypeScriptDefinitions()
    }

    sourceSets {
        val commonMain by getting
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":moxel-core"))

                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                api(libs.koin.annotations.annotations)

//                implementation(npm("wasmoon", "1.16.0"))
            }
        }
    }

    sourceSets.named("wasmJsMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/wasmJsMain/kotlin")
    }
}

dependencies {
    add("kspWasmJs", libs.koin.ksp.compiler)
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspKotlinWasmJs") {
        dependsOn("kspKotlinWasmJs")
    }
}
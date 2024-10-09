import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":moxel-core"))
//                implementation(npm("wasmoon", "1.16.0"))
            }
        }
    }
}

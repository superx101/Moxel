plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "top.moxel.plugin.tool"

kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":gradle-annotations"))

                implementation(libs.ksp.api)
                implementation(libs.kotlinpoet)
            }
        }
    }
}

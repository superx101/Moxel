plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":annotations"))

                implementation(libs.ksp.api)
                implementation(libs.kotlinpoet)
            }
        }
    }
}

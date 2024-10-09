plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvmToolchain(19)
    jvm {}

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":moxel-core"))
            }
        }
    }
}



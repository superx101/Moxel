plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "top.moxel.plugin.annotation"

kotlin {
    jvm()

    js(IR) {
        browser()
    }

    mingwX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.logging.common)
        }
    }
}
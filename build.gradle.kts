plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.ksp).apply(false)
}

group = "top.moxel.plugin"

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}
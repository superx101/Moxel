plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.ktlint)
}

group = "top.moxel.plugin"

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

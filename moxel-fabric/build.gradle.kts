import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain(19)
    jvm {}

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":moxel-core"))

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            api(libs.koin.annotations.annotations)
        }

        jvmTest.dependencies {
            implementation(project(":moxel-core"))

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            api(libs.koin.annotations.annotations)

            implementation(libs.kotlin.test)
        }
    }

    sourceSets.named("jvmMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/jvmMain/kotlin")
    }
}

dependencies {
    add("kspJvm", libs.koin.ksp.compiler)
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspKotlinJvm") {
        dependsOn("kspKotlinJvm")
    }
}

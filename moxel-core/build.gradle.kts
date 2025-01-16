import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain(19)
    jvm()

    js(IR) {
        browser()
        binaries.executable()
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        val main by compilations.getting
        main.cinterops.create("lua") {
            val resourceFile = project.file("src/nativeMain/resources").absolutePath
            val includePath = file("${resourceFile}/vendored/lua/include").absolutePath
            val libPath = file("${resourceFile}/vendored/lua").absolutePath

            defFile("${resourceFile}/cinterop/lua.def")
            compilerOpts("-I${resourceFile}")
            includeDirs.allHeaders(includePath)
            extraOpts("-libraryPath", libPath)
        }

        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlin.logging.common)

            implementation(libs.kotlinx.coroutines.core)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            api(libs.koin.annotations.annotations)

            implementation(libs.log4j.api)
            implementation(libs.logback.classic)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            dependencies {
                implementation(libs.kotlin.logging.jvm)
                implementation(libs.kotlinx.coroutines.core.jvm)
                implementation(libs.luaj.jse)
            }

        }

        jsMain.dependencies {
            implementation(libs.kotlin.stdlib.js)
            implementation(libs.kotlin.logging.js)
            implementation(libs.kotlinx.coroutines.core.js)

            implementation(npm("fengari-web", "0.1.4"))
        }

        nativeMain.dependencies {
            implementation(libs.kotlin.logging.mingwx64)
//                implementation(libs.kotlin.logging.linuxarm64)
        }
    }

    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

// KSP Tasks
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
}

// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

/**
 *  common
 *  |-- js
 *  |-- jvm
 *  '-- native
 *      |-- mingwX64
 *      '-- unix
 *          |-- [x] androidNativeArm64  // third-party library not fully supported
 *          '-- linuxArm64              // possible alternative to android

 */
kotlin {
    jvmToolchain(19)
    jvm()

    js(IR) {
        moduleName = "moxel-core"

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
            webpackTask {
                mainOutputFileName = "moxel-core.js"
                output.libraryTarget = "umd"
            }
        }
        binaries.executable()
    }

    listOf(
        linuxArm64(),
        mingwX64()
    ).forEach {
        it.apply {
            val main by compilations.getting
            main.cinterops.create("lua") {
                val resourceFile = project.file("src/nativeMain/resources").absolutePath
                val includePath = file("${resourceFile}/lua/include").absolutePath
                val libPath = file("${resourceFile}/lua").absolutePath

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
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.logging.common)

                implementation(libs.kotlinx.coroutines.core)

                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                api(libs.koin.annotations.annotations)

                implementation(libs.log4j.api)
                implementation(libs.logback.classic)

                implementation(libs.kaml)
                implementation(libs.okio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.luaj.jse)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("fengari-web", "0.1.4"))
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val unixMain by creating {
            dependsOn(nativeMain)
        }
        val linuxArm64Main by getting {
            dependsOn(unixMain)
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
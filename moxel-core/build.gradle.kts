import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
 *      |-- mingw (mingwX64)
 *      '-- androidNative (androidNativeArm64 or linuxArm64)
 *
 *  androidNativeArm64 third-party library not fully supported, use linuxArm64 replace temporarily
 *  (koin-annotations)
 */
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvmToolchain(21)
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
        mingwX64("mingw"),
        linuxArm64("androidNative"),
    ).forEach {
        it.apply {
            binaries {
                executable {
                    entryPoint = "main"
                }
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":gradle-annotations"))

                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.logging.common)

                implementation(libs.kotlinx.coroutines.core)

                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                api(libs.koin.annotations)

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
                implementation(devNpm("node-polyfill-webpack-plugin", "^2.0.1"))
                implementation(npm("fengari-web", "0.1.4"))
            }
        }

        val nativeMain by getting {
            dependsOn(commonMain)
        }
        val mingwMain by getting {
            dependsOn(nativeMain)
        }
        val androidNativeMain by getting {
            dependsOn(nativeMain)
        }
    }

    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    targets.withType<KotlinNativeTarget> {
        compilations["main"].cinterops.create("lua") {
            val resourceFile = project.file("src/nativeMain/resources").absolutePath
            val includePath = file("$resourceFile/lua/include").absolutePath
            val libPath = file("$resourceFile/lua").absolutePath

            defFile("$resourceFile/cinterop/lua.def")
            compilerOpts("-I$resourceFile")
            includeDirs.allHeaders(includePath)
            extraOpts("-libraryPath", libPath)
        }
    }
}

// KSP Tasks
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspCommonMainMetadata", project(":gradle-tools"))
}

tasks.register<Copy>("copyLuaDynamicLib") {
    from(project.file("src/nativeMain/resources/lua/lua52.dll"))
    into(project.file("build/bin/mingw/debugTest"))
}

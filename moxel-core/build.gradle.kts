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
 *      |-- mingw
 *      '-- androidNative
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
        androidNativeArm64("androidNative"),
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
                api(project(":gradle-annotations"))

                implementation(libs.kotlin.stdlib)
                api(libs.kotlin.logging.common)

                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.atomicfu)

                implementation(libs.log4j.api)
                implementation(libs.logback.classic)

                implementation(libs.kaml)
                api(libs.okio)
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
    add("kspCommonMainMetadata", project(":gradle-tools"))
//    add("kspJvm", project(":gradle-tools"))
}

ksp {
    arg("module", "core")
    arg("package", "top.moxel.plugin.ksp.generated")
}

tasks.register<Copy>("copyLuaDynamicLib") {
    from(project.file("src/nativeMain/resources/lua/lua52.dll"))
    into(project.file("build/bin/mingw/debugTest"))
}

tasks.register("generateKspCode") {
    group = "codegen"
    description = "Manually run KSP to generate code"
    dependsOn("kspCommonMainKotlinMetadata")
}

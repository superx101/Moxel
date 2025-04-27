pluginManagement {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Moxel"

include("processor")
include("annotations")
include("moxel-core")
include("moxel-fabric")
include("moxel-algorithmic-test")

project(":moxel-core").projectDir = file("moxel/core")
project(":moxel-fabric").projectDir = file("moxel/fabric")
project(":moxel-algorithmic-test").projectDir = file("moxel/test")
project(":annotations").projectDir = file("moxel/annotations")
project(":processor").projectDir = file("moxel/processor")

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.fabric.loom)
// 	id("maven-publish")
}

version = project.extra["fabric_mod_version"] as String
group = project.extra["maven_group"] as String

base {
    archivesName.set(
        "moxel-fabric-$version",
    )
}

repositories {
}

dependencies {
    implementation(project(":moxel-core"))

    minecraft(libs.minecraft)
    mappings(libs.fabric.yarn.get().toString()) // Need help
    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)

    implementation(libs.kotlinx.coroutines.core)
    implementation(project.dependencies.platform(libs.koin.bom))
//    implementation(libs.koin.core)
    implementation(libs.koin.annotations.jvm)

    implementation(libs.okio)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.the<BasePluginExtension>().archivesName.get()}" }
    }
}

// publishing {
// 	publications {
// 		create<MavenPublication>("mavenJava") {
// 			artifactId = project.the<BasePluginExtension>().archivesName.get()
// 			from(components["java"])
// 		}
// 	}
//
// 	repositories {}
// }

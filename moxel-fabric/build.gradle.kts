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

    implementation(project(":gradle-annotations"))
    annotationProcessor(libs.kotlinpoet)
    implementation(libs.kotlinpoet)
    ksp(project(":gradle-tools")) {
        exclude(group = "com.squareup", module = "kotlinpoet")
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.the<BasePluginExtension>().archivesName.get()}" }
    }
}

ksp {
    arg("module", "fabric")
    arg("package", "top.moxel.plugin.ksp.generated")
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

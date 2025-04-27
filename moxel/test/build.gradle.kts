plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":moxel-core"))
    ksp(project(":processor"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

ksp {
    arg("module", "test")
    arg("package", "top.moxel.plugin.ksp.generated")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    setDependsOn(dependsOn.filterNot { it.toString().contains("kspKotlin") })
}

tasks.register("generateKspCode") {
    group = "codegen"
    description = "Manually run KSP to generate code"
    dependsOn("kspKotlin")
}

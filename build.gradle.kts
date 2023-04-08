plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.versions)
    id("java-gradle-plugin")
}

group = "dev.limebeck"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.kotlin.plugin)
    implementation(libs.kotlinpoet)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    plugins {
        create("buildTimeConfig") {
            id = "dev.limebeck.build-time-config"
            implementationClass = "dev.limebeck.BuildTimeConfig"
        }
    }
}
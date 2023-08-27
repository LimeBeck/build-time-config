plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.versions)
    `java-gradle-plugin`
}

group = "dev.limebeck"
version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.kotlin.plugin)
    implementation(libs.kotlinpoet)
    testImplementation(libs.kotlin.stdlib)
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    website.set("https://github.com/LimeBeck/BuildTimeConfig")
    vcsUrl.set("https://github.com/LimeBeck/BuildTimeConfig.git")
    plugins {
        create("buildTimeConfig") {
            id = "dev.limebeck.build-time-config"
            displayName = "Kotlin Build-Time Config"
            description = "Gradle plugin for providing build-time configuration properties for kotlin application"
            tags.set(listOf("kotlin", "config"))
            implementationClass = "dev.limebeck.BuildTimeConfig"
        }
    }
}
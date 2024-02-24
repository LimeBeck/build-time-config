plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.versions)
    `java-gradle-plugin`
}

group = "dev.limebeck"
version = "2.2.1"

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

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    website.set("https://github.com/LimeBeck/BuildTimeConfig")
    vcsUrl.set("https://github.com/LimeBeck/BuildTimeConfig.git")
    val buildTimeConfig by plugins.creating {
        id = "dev.limebeck.build-time-config"
        displayName = "Kotlin Build-Time Config"
        description = "Gradle plugin for providing build-time configuration properties for kotlin application"
        tags.set(listOf("kotlin", "config"))
        implementationClass = "dev.limebeck.BuildTimeConfig"
    }
}

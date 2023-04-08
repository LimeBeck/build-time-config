package dev.limebeck

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import kotlin.reflect.KClass

data class Config(
    @Internal
    val configName: String,
    @Internal
    val packageName: String,
    @Internal
    val objectName: String,
    @Internal
    val properties: List<ConfigProperty<*>>,
    @OutputDirectory
    val destinationDir: File
)

data class ConfigProperty<T : Any>(
    @Internal
    val name: String,
    @Internal
    val type: KClass<T>,
    @Internal
    val value: T?
)
package dev.limebeck

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
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
    @Nested
    val properties: List<ConfigPropertyHolder>,
    @OutputDirectory
    val destinationDir: File
)

sealed class ConfigPropertyHolder(open val name: String)

data class ConfigProperty<T : Any>(
    @Internal
    override val name: String,
    @Internal
    val type: KClass<T>,
    @Internal
    val value: T?
) : ConfigPropertyHolder(name)

data class ConfigObject(
    @Internal
    override val name: String,
    @Nested
    val properties: List<ConfigPropertyHolder>,
) : ConfigPropertyHolder(name)


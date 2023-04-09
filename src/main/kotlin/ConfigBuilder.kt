package dev.limebeck

import org.gradle.api.Action
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import kotlin.reflect.KClass

open class ConfigBuilder(
    val name: String? = null,
    private val objectFactory: ObjectFactory
) {
    val packageName: Property<String> = objectFactory.property(String::class.java)
    val objectName: Property<String> = objectFactory.property(String::class.java)

    @OutputDirectory
    val destination: RegularFileProperty = objectFactory.fileProperty()
    val allProperties: MutableList<ConfigPropertyHolder> = mutableListOf()

    internal fun build(): Config {
        val name = name ?: "unnamed"
        return Config(
            configName = name,
            packageName = packageName.get(),
            objectName = objectName.get(),
            properties = allProperties,
            destinationDir = File(destination.get().asFile, name)
        )
    }

    @Suppress("UNUSED")
    fun configProperties(action: Action<ConfigPropertiesBuilder>) {
        val builder = ConfigPropertiesBuilder()
        action.execute(builder)
        allProperties.addAll(builder.allConfigProperties)
    }
}

open class ConfigPropertiesBuilder {
    val allConfigProperties: MutableList<ConfigPropertyHolder> = mutableListOf()

    fun <T : Any> property(name: String, type: KClass<T>): ConfigPropertyDefinition<T> {
        return ConfigPropertyDefinition(name, type)
    }

    @Suppress("UNUSED")
    inline fun <reified T : Any> property(name: String) = property(name, T::class)

    infix fun <T : Any> ConfigPropertyDefinition<T>.set(value: T?) {
        allConfigProperties.add(
            ConfigProperty(
                name = name,
                type = type,
                value = value
            )
        )
    }

    @Suppress("UNUSED")
    fun obj(name: String) = ConfigObjectDefinition(name)

    infix fun ConfigObjectDefinition.set(action: Action<ConfigPropertiesBuilder>) {
        val builder = ConfigPropertiesBuilder()
        action.execute(builder)
        allConfigProperties.add(ConfigObject(name, builder.allConfigProperties))
    }
}

data class ConfigPropertyDefinition<T : Any>(
    val name: String,
    val type: KClass<T>
)

data class ConfigObjectDefinition(
    val name: String
)
package dev.limebeck

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.io.File
import kotlin.reflect.KClass

class BuildTimeConfig : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(
            /* name = */ "buildTimeConfig",
            /* type = */ ConfigsHolder::class.java,
            /* ...constructionArguments = */ target
        )

        val kotlinExtension = target.extensions.getByType(KotlinJvmProjectExtension::class.java)
        val output = File(target.buildDir, "buildkonfig")

        target.afterEvaluate {
            val task = target.tasks.register("generateConfig", BuildTimeConfigTask::class.java) {
                println("<256f7cfc> Task run")
                it.configs = extension.configs
            }

            kotlinExtension.sourceSets.forEach {
                it.kotlin.srcDirs(task.map { it.destinations.values })
            }

        }
//
//        val task = target.tasks.register("generateConfig") {
//            it.doLast {
//                extension.configs.forEach { config ->
//                    val file = generateKotlinFile(config)
//                    println(file)
//                }
//                println("It`s Worked!")
//            }
//        }


    }
}

open class ConfigsHolder(
    private val project: Project
) {
    @Internal
    val configs: MutableList<Config> = mutableListOf()

    @Suppress("UNUSED")
    fun config(name: String? = null, action: Action<ConfigBuilder>) {
        val builder = ConfigBuilder(name, project.objects)
        action.execute(builder)
        configs.add(builder.build())
        println("<217d9e5f> ConfigsHolder called: $configs")
    }
}

open class ConfigBuilder(
    val name: String? = null,
    private val objectFactory: ObjectFactory
) {
    val packageName: Property<String> = objectFactory.property(String::class.java)
    val objectName: Property<String> = objectFactory.property(String::class.java)

    @OutputDirectory
    val destination: RegularFileProperty = objectFactory.fileProperty()
    val allProperties: MutableList<ConfigProperty<*>> = mutableListOf()

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
    val allConfigProperties: MutableList<dev.limebeck.ConfigProperty<*>> = mutableListOf()

    fun <T : Any> property(name: String, type: KClass<T>): ConfigPropertyDefinition<T> {
        return ConfigPropertyDefinition(name, type)
    }

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
}

data class ConfigPropertyDefinition<T : Any>(
    val name: String,
    val type: KClass<T>
)
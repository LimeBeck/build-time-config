package dev.limebeck

import dev.limebeck.delegates.LiteralTemplateConfigPropertyDelegate
import dev.limebeck.delegates.NumberTemplateConfigPropertyDelegate
import dev.limebeck.delegates.ObjectConfigPropertyDelegate
import org.gradle.api.Action
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import java.security.InvalidParameterException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

open class ConfigBuilder(
    @Input
    val name: String? = null,
    objectFactory: ObjectFactory
) {

    @Input
    val packageName: Property<String> = objectFactory.property(String::class.java)

    @Input
    val objectName: Property<String> = objectFactory.property(String::class.java)

    @OutputDirectory
    val destination: RegularFileProperty = objectFactory.fileProperty()

    @Input
    val allProperties: MutableList<ConfigProperty> = mutableListOf()

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
        val builder = ConfigPropertiesBuilder {
            action.execute(this)
        }
        allProperties.addAll(builder.allConfigProperties)
    }
}

open class ConfigPropertiesBuilder(initBlock: ConfigPropertiesBuilder.() -> Unit) {
    val allConfigProperties: MutableList<ConfigProperty> = mutableListOf()

    init {
        initBlock()
    }

    //Old style
    @Suppress("DEPRECATION")
    @Deprecated("Use new delegates API")
    fun property(name: String, type: KType): ConfigPropertyDefinition {
        return ConfigPropertyDefinition(name, type)
    }

    @Suppress("UNUSED", "DEPRECATION")
    @Deprecated("Use new delegates API")
    inline fun <reified T> property(name: String) = property(name, typeOf<T>())

    @Suppress("DEPRECATION")
    @Deprecated("Use new delegates API")
    infix fun <T : Any> ConfigPropertyDefinition.set(value: T) {
        allConfigProperties.add(
            LiteralTemplateConfigProperty(
                name = name,
                type = type,
                value = value,
                template = when {
                    type.isSubtypeOf(typeOf<Boolean>()) -> "%L"
                    type.isSubtypeOf(typeOf<Number>()) -> "%L"
                    type.isSubtypeOf(typeOf<String>()) -> "%S"
                    else -> throw InvalidParameterException("<4ac3a89c> Unknown property type $type")
                }
            )
        )
    }

    @Suppress("UNUSED", "DEPRECATION")
    @Deprecated("Use new delegates API")
    fun obj(name: String) = ConfigObjectDefinition(name)

    @Suppress("DEPRECATION")
    @Deprecated("Use new delegates API")

    infix fun ConfigObjectDefinition.set(action: Action<ConfigPropertiesBuilder>) {
        val builder = ConfigPropertiesBuilder {
            action.execute(this)
        }
        allConfigProperties.add(ObjectConfigProperty(name, builder.allConfigProperties))
    }

    @Deprecated("Use new delegates API")
    data class ConfigPropertyDefinition internal constructor(
        val name: String,
        val type: KType
    )

    @Deprecated("Use new delegates API")
    data class ConfigObjectDefinition internal constructor(
        val name: String
    )

    //New style
    fun <T : String?> string(value: T) = LiteralTemplateConfigPropertyDelegate(
        value = value,
        template = "%S",
        configPropertiesBuilder = this
    )

    fun <T : Boolean?> bool(value: T) = LiteralTemplateConfigPropertyDelegate(
        value = value,
        template = "%L",
        configPropertiesBuilder = this
    )

    @Suppress("UNUSED")
    fun <T : Boolean?> boolean(value: T) = bool(value)

    fun <T : Number?> number(value: T) = NumberTemplateConfigPropertyDelegate(
        value = value,
        configPropertiesBuilder = this
    )

    @Suppress("UNUSED")
    fun <T : Int?> int(value: T) = number(value)

    @Suppress("UNUSED")
    fun <T : Long?> long(value: T) = number(value)

    @Suppress("UNUSED")
    fun <T : Double?> double(value: T) = number(value)

    @Suppress("UNUSED")
    fun <T : Float?> float(value: Float?) = number(value)

    @Suppress("UNUSED")
    fun obj(valueBuilder: ConfigPropertiesBuilder.() -> Unit) =
        ObjectConfigPropertyDelegate(valueBuilder, this)
}

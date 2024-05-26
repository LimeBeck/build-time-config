package dev.limebeck

import com.squareup.kotlinpoet.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.Optional
import java.io.File
import java.util.*
import kotlin.reflect.KType


data class Config(
    @Input
    val configName: String,
    @Input
    val packageName: String,
    @Input
    val objectName: String,
    @Nested
    val properties: List<ConfigProperty>,
    @OutputDirectory
    val destinationDir: File
)

interface ConfigProperty {
    fun build(typeSpecBuilder: TypeSpec.Builder, fileSpecBuilder: FileSpec.Builder)
}


fun TypeSpec.Builder.applyProperties(fileSpecBuilder: FileSpec.Builder, properties: List<ConfigProperty>) = apply {
    properties.forEach { it.build(this, fileSpecBuilder) }
}

class ObjectConfigProperty(
    @Input
    val name: String,
    @Nested
    val properties: List<ConfigProperty>
) : ConfigProperty {
    override fun build(typeSpecBuilder: TypeSpec.Builder, fileSpecBuilder: FileSpec.Builder) {
        val capitalizedName = name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        val interfaceTypeName = ClassName("", capitalizedName)

        val interfaceType = TypeSpec.interfaceBuilder(capitalizedName)
            .applyProperties(fileSpecBuilder, properties)
            .also { b ->
                b.propertySpecs.replaceAll { p ->
                    val propBuilder = p.toBuilder()
                    propBuilder.initializer(null)
                    propBuilder.build()
                }
            }.build()

        typeSpecBuilder.addType(interfaceType)

        val typeObject = TypeSpec.anonymousClassBuilder()
            .addSuperinterfaces(listOf(interfaceTypeName))
            .applyProperties(fileSpecBuilder, properties)
            .also { b ->
                b.propertySpecs.replaceAll { p ->
                    val propBuilder = p.toBuilder()
                    propBuilder.addModifiers(KModifier.OVERRIDE)
                    propBuilder.build()
                }
            }
            .build()

        val property = PropertySpec
            .builder(name, interfaceTypeName)
            .initializer("%L", typeObject)
            .build()

        typeSpecBuilder.addProperty(property)
    }
}

open class LiteralTemplateConfigProperty<T>(
    @Input
    val name: String,
    @Internal
    val type: KType,
    @Input
    val template: String,
    @Input
    @Optional
    val value: T
) : ConfigProperty {
    override fun build(typeSpecBuilder: TypeSpec.Builder, fileSpecBuilder: FileSpec.Builder) {
        val prop = PropertySpec
            .builder(name, type.asTypeName())
            .initializer(template, value)
            .build()
        typeSpecBuilder.addProperty(prop)
    }
}

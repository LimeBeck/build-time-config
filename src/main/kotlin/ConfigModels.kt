package dev.limebeck

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.gradle.api.tasks.*
import java.io.File
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

class ObjectConfigProperty(
    @Input
    val name: String,
    @Nested
    val properties: List<ConfigProperty>
) : ConfigProperty {
    override fun build(typeSpecBuilder: TypeSpec.Builder, fileSpecBuilder: FileSpec.Builder) {
        val type = TypeSpec.objectBuilder(name).also { b ->
            properties.forEach { it.build(b, fileSpecBuilder) }
        }.build()
        typeSpecBuilder.addType(type)
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

package dev.limebeck

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.security.InvalidParameterException
import kotlin.reflect.full.isSubclassOf

fun TypeSpec.Builder.makeProperty(prop: ConfigPropertyHolder) {
    when (prop) {
        is ConfigObject -> {
            val type = TypeSpec.objectBuilder(prop.name).also { b ->
                prop.properties.forEach { b.makeProperty(it) }
            }.build()
            addType(type)
        }

        is ConfigProperty<*> -> {
            val template = when {
                prop.type.isSubclassOf(Boolean::class) -> "%L"
                prop.type.isSubclassOf(Number::class) -> "%L"
                prop.type.isSubclassOf(String::class) -> "%S"
                else -> throw InvalidParameterException("<4ac3a89c> Unknown property type ${prop.type}")
            }
            val prop = PropertySpec
                .builder(prop.name, prop.type)
                .initializer(template, prop.value)
                .build()
            addProperty(prop)
        }
    }
}

fun generateKotlinFile(config: Config): String {
    val propertyObj = TypeSpec
        .objectBuilder(config.objectName)
        .apply {
            config.properties.forEach { makeProperty(it) }
        }.build()

    val fileSpec = FileSpec
        .builder(config.packageName, config.objectName + ".kt")
        .addType(propertyObj)
        .build()

    return StringBuilder().also {
        fileSpec.writeTo(it)
    }.toString()
}

package dev.limebeck

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.security.InvalidParameterException
import kotlin.reflect.full.isSubclassOf

fun generateKotlinFile(config: Config): String {
    val properties = config.properties.map {
        val template = when {
            it.type.isSubclassOf(Boolean::class) -> "%L"
            it.type.isSubclassOf(Number::class) -> "%L"
            it.type.isSubclassOf(String::class) -> "%S"
            else -> throw InvalidParameterException("<4ac3a89c> Unknown property type ${it.type}")
        }
        PropertySpec
            .builder(it.name, it.type)
            .initializer(template, it.value)
            .build()
    }

    val propertyObj = TypeSpec
        .objectBuilder(config.objectName)
        .addProperties(properties)
        .build()

    val fileSpec = FileSpec
        .builder(config.packageName, config.objectName + ".kt")
        .addType(propertyObj)
        .build()

    return StringBuilder().also {
        fileSpec.writeTo(it)
    }.toString()
}
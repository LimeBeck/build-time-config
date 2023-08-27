package dev.limebeck

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

fun generateKotlinFile(config: Config): String {
    val fileSpecBuilder = FileSpec
        .builder(config.packageName, config.objectName + ".kt")

    val propertyObj = TypeSpec
        .objectBuilder(config.objectName)
        .also { typeSpecBuilder ->
            config.properties.forEach { it.build(typeSpecBuilder, fileSpecBuilder) }
        }.build()

    return StringBuilder().also {
        fileSpecBuilder
            .addType(propertyObj)
            .build()
            .writeTo(it)
    }.toString()
}

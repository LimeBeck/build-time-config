package dev.limebeck.delegates

import dev.limebeck.ConfigPropertiesBuilder
import dev.limebeck.LiteralTemplateConfigProperty
import dev.limebeck.ObjectConfigProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class LiteralTemplateConfigPropertyDelegate<T, R : T & Any>(
    val value: T,
    val type: KClass<R>,
    val template: String,
    val configPropertiesBuilder: ConfigPropertiesBuilder
) {
    operator fun provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Nothing?, T> {
        val prop = LiteralTemplateConfigProperty(
            name = prop.name,
            template = template,
            value = value,
            type = type,
            nullable = prop.returnType.isMarkedNullable
        )
        configPropertiesBuilder.allConfigProperties.add(prop)
        return ReadOnlyProperty { _, _ -> value }
    }
}

open class NumberTemplateConfigPropertyDelegate<T : Number?, R : T & Any>(
    value: T,
    type: KClass<R>,
    configPropertiesBuilder: ConfigPropertiesBuilder,
) : LiteralTemplateConfigPropertyDelegate<T, R>(
    value = value,
    type = type,
    template = "%L",
    configPropertiesBuilder = configPropertiesBuilder
)

open class ObjectConfigPropertyDelegate(
    val valueBuilder: ConfigPropertiesBuilder.() -> Unit,
    val configPropertiesBuilder: ConfigPropertiesBuilder,
) {
    operator fun provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Nothing?, ObjectConfigProperty> {
        val builder = ConfigPropertiesBuilder(valueBuilder)
        val prop = ObjectConfigProperty(
            name = prop.name,
            properties = builder.allConfigProperties
        )
        configPropertiesBuilder.allConfigProperties.add(prop)
        return ReadOnlyProperty { _, _ -> prop }
    }
}

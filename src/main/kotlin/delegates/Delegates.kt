package dev.limebeck.delegates;

import dev.limebeck.ConfigPropertiesBuilder
import dev.limebeck.LiteralTemplateConfigProperty
import dev.limebeck.ObjectConfigProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class LiteralTemplateConfigPropertyDelegate<T : Any>(
    val value: T,
    val type: KClass<T>,
    val template: String,
    val configPropertiesBuilder: ConfigPropertiesBuilder
) {
    operator fun provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Nothing?, LiteralTemplateConfigProperty<T>> {
        val prop = LiteralTemplateConfigProperty(
            name = prop.name,
            template = template,
            value = value,
            type = type
        )
        configPropertiesBuilder.allConfigProperties.add(prop)
        return ReadOnlyProperty { _, _ -> prop }
    }
}

open class NumberTemplateConfigPropertyDelegate<T : Number>(
    value: T,
    type: KClass<T>,
    configPropertiesBuilder: ConfigPropertiesBuilder,
) : LiteralTemplateConfigPropertyDelegate<T>(
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
        val builder = ConfigPropertiesBuilder().apply(valueBuilder)
        val prop = ObjectConfigProperty(
            name = prop.name,
            properties = builder.allConfigProperties
        )
        configPropertiesBuilder.allConfigProperties.add(prop)
        return ReadOnlyProperty { _, _ -> prop }
    }
}

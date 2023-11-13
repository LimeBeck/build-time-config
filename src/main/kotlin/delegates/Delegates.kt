package dev.limebeck.delegates

import dev.limebeck.ConfigPropertiesBuilder
import dev.limebeck.LiteralTemplateConfigProperty
import dev.limebeck.ObjectConfigProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class LiteralTemplateConfigPropertyDelegate<T>(
    val value: T,
    val template: String,
    val configPropertiesBuilder: ConfigPropertiesBuilder
) {
    operator fun provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Nothing?, T> {
        val property = LiteralTemplateConfigProperty(
            name = prop.name,
            template = template,
            value = value,
            type = prop.returnType
        )
        configPropertiesBuilder.allConfigProperties.add(property)
        return ReadOnlyProperty { _, _ -> value }
    }
}

open class NumberTemplateConfigPropertyDelegate<T : Number?>(
    value: T,
    configPropertiesBuilder: ConfigPropertiesBuilder,
) : LiteralTemplateConfigPropertyDelegate<T>(
    value = value,
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

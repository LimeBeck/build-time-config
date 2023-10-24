# Gradle plugin for providing build-time configuration properties

![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.limebeck.build-time-config)

## Usage

There is two API-styles available

1. New-style API (based on delegates)

    `build.gradle.kts`:
    
    ```kotlin
    plugins {
        kotlin("jvm") version "1.8.0"
        id("dev.limebeck.build-time-config") version "2.1.0"
    }
    ...
    buildTimeConfig {
        config {
            packageName.set("dev.limebeck.config")
            objectName.set("MyConfig")
            destination.set(project.buildDir)
    
            configProperties {
                val someProp: String by string("SomeValue")
                val somePropNullable: String? by string(null)
                val somePropNullableFilled: String? by string("null")
                val someProp2 by number(123)
                val someProp3 by number(123.0)
                val someProp4 by number(123L)
                val someProp5 by bool(true) //also can be boolean(true)
                val nested by obj {
                    val someProp by string("SomeValue")
                }
            }
        }
    }
    ```

2. Old-style API (DEPRECATED)

    `build.gradle.kts`:
    
    ```kotlin
    plugins {
        kotlin("jvm") version "1.8.0"
        id("dev.limebeck.build-time-config") version "2.0.0"
    }
    ...
    buildTimeConfig {
        config {
            packageName.set("dev.limebeck.config")
            objectName.set("MyConfig")
            destination.set(project.buildDir)
    
            configProperties {
                property<String>("someProp") set "SomeValue"
                property<Int>("someProp2") set 123
                property<Double>("someProp3") set 123.0
                property<Long>("someProp4") set 123L
                property<Boolean>("someProp5") set true
                obj("nested") set {
                    property<String>("someProp") set "SomeValue"
                }
            }
        }
    }
    ```

Both will generate code like this:

```kotlin
package dev.limebeck.config

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String

public object MyConfig {
    public val someProp: String = "SomeValue"

    public val someProp2: Int = 123

    public val someProp3: Double = 123.0

    public val someProp4: Long = 123

    public val someProp5: Boolean = true

    public object nested {
        public val someProp: String = "SomeValue"
    }
}
```

You can use config like this:

`Application.kt`

```kotlin
import dev.limebeck.config.MyConfig

class Application {
    val data: String = MyConfig.someProp
    val data2: Int = MyConfig.someProp2
    val data3: Double = MyConfig.someProp3
    val data4: Long = MyConfig.someProp4
    val data5: Boolean = MyConfig.someProp5
    val obj: String = MyConfig.nested.someProp
}
```

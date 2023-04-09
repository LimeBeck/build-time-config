# Gradle plugin for providing build-time configuration properties

## Usage

`build.gradle.kts`:
```kotlin
plugins {
    kotlin("jvm") version "1.8.0"
    id("dev.limebeck.build-time-config") version "1.0.0"
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
            property<Boolean>("someProp3") set true
            obj("nested") set {
                property<String>("someProp") set "SomeValue"
            }
        }
    }
}
```

`Application.kt`
```kotlin
import dev.limebeck.config.MyConfig

class Application {
    val data = MyConfig.someProp
    val data2 = MyConfig.someProp2
    val data3 = MyConfig.someProp3
}
```
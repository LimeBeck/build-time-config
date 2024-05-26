plugins {
    alias(libs.plugins.kotlin.jvm)
    id("dev.limebeck.build-time-config")
}

group = "dev.limebeck"
version = "2.2.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

buildTimeConfig {
    generateOnSync = true
    config("newStyle") {
        packageName.set("dev.limebeck.config")
        objectName.set("MyConfigNew")
        destination.set(project.buildDir)

        configProperties {
            val stringProp: String by string("SomeValue")
            val stringPropNullable: String? by string(null)
            val stringPropNullableFilled: String? by string("null")
            val intProp by number(123)
            val doubleProp by number(123.0)
            val longProp by number(123L)
            val boolProp by bool(true) //also can be boolean(true)
            val nested by obj {
                val stringProp by string("SomeValue")
            }
        }
    }

    config("oldStyle") {
        packageName.set("dev.limebeck.config")
        objectName.set("MyConfigOld")
        destination.set(project.buildDir)

        configProperties {
            configProperties {
                property<String>("stringProp") set "SomeValue"
                property<Int>("intProp") set 123
                property<Double>("doubleProp") set 123.0
                property<Long>("longProp") set 123L
                property<Boolean>("boolProp") set true
                obj("nested") set {
                    property<String>("stringProp") set "SomeValue"
                }
            }
        }
    }
}

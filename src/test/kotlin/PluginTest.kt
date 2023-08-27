import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.assertEquals

class PluginTest {

    @TempDir
    lateinit var testProjectDir: Path

    private lateinit var gradleRunner: GradleRunner

    @BeforeEach
    fun setup() {
        gradleRunner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.toFile())
            .withTestKitDir(testProjectDir.resolve("./testKit").createDirectories().toFile())
    }


    @Test
    fun `Generate v1 build time config`() {
        val buildGradleContent = """
            plugins {
                kotlin("jvm") version "1.8.0"
                id("dev.limebeck.build-time-config")
            }
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
        """.trimIndent()
        testProjectDir
            .resolve("build.gradle.kts")
            .createFile()
            .writeText(buildGradleContent)
        testProjectDir
            .resolve("settings.gradle.kts")
            .createFile()
            .writeText("rootProject.name = \"build-time-config-test\"")

        val codeGenerationResult = gradleRunner.withArguments("generateConfig").build()
        assertEquals(TaskOutcome.SUCCESS, codeGenerationResult.task(":generateConfig")!!.outcome)
        val resultFile = testProjectDir.resolve("./build/unnamed/MyConfig.kt").readText().trim()
        @Language("kotlin") val expectedGeneratedFileContent = """
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
        """.trimIndent()
        assertEquals(expectedGeneratedFileContent, resultFile)
    }

    @Test
    fun `Generate v2 build time config`() {
        val buildGradleContent = """
            plugins {
                kotlin("jvm") version "1.8.0"
                id("dev.limebeck.build-time-config")
            }
            buildTimeConfig {
                config {
                    packageName.set("dev.limebeck.config")
                    objectName.set("MyConfig")
                    destination.set(project.buildDir)

                    configProperties {
                        val someProp by string("SomeValue")
                        val someProp2 by number(123)
                        val someProp3 by number(123.0)
                        val someProp4 by number(123L)
                        val someProp5 by bool(true)
                        val nested by obj {
                            val someProp by string("SomeValue")
                        }
                    }
                }
            }
        """.trimIndent()
        testProjectDir
            .resolve("build.gradle.kts")
            .createFile()
            .writeText(buildGradleContent)
        testProjectDir
            .resolve("settings.gradle.kts")
            .createFile()
            .writeText("rootProject.name = \"build-time-config-test\"")

        val codeGenerationResult = gradleRunner.withArguments("generateConfig").build()
        assertEquals(TaskOutcome.SUCCESS, codeGenerationResult.task(":generateConfig")!!.outcome)

        val resultFile = testProjectDir.resolve("./build/unnamed/MyConfig.kt").readText().trim()
        @Language("kotlin") val expectedGeneratedFileContent = """
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
        """.trimIndent()
        assertEquals(expectedGeneratedFileContent, resultFile)
    }
}

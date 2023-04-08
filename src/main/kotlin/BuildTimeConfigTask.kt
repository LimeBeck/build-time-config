package dev.limebeck

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class BuildTimeConfigTask : DefaultTask() {
    @Suppress("unused")
    @get:OutputDirectories
    val destinations: Map<String, File>
        get() = configs.associate { it.configName to it.destinationDir }

    @get:Nested
    lateinit var configs: List<Config>

    @Suppress("unused")
    @TaskAction
    fun run() {
//        doLast {
        configs.forEach { config ->
            val file = generateKotlinFile(config)
            println(file)
            config.destinationDir.mkdirs()
            File(config.destinationDir, config.objectName + ".kt").writeText(file)
        }
        println("It`s Worked!")
//        }
    }
}
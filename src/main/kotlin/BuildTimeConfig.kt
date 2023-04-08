package dev.limebeck

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import kotlin.reflect.KClass

class BuildTimeConfig : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(
            /* name = */ "buildTimeConfig",
            /* type = */ ConfigsHolderExtension::class.java,
            /* ...constructionArguments = */ target
        )

        val kotlinExtension = target.extensions.getByType(KotlinJvmProjectExtension::class.java)

        target.afterEvaluate {
            val task = target.tasks.register("generateConfig", BuildTimeConfigTask::class.java) {
                println("<256f7cfc> Task run")
                it.configs = extension.configs
            }

            kotlinExtension.sourceSets.forEach {
                it.kotlin.srcDirs(task.map { it.destinations.values })
            }

        }
    }
}

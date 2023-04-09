package dev.limebeck

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class BuildTimeConfig : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(
            /* name = */ "buildTimeConfig",
            /* type = */ BuildTimeConfigProjectExtension::class.java,
            /* ...constructionArguments = */ target
        )

        target.afterEvaluate {
            val task = target.tasks.register("generateConfig", BuildTimeConfigTask::class.java) {
                it.configs = extension.configs
            }

            val kotlinJvmExtension = target.extensions.findByType(KotlinJvmProjectExtension::class.java)
            val kotlinMppExtension = target.extensions.findByType(KotlinMultiplatformExtension::class.java)

            val sourceSets = kotlinJvmExtension?.sourceSets
                ?: kotlinMppExtension?.sourceSets?.filter { it.name == "commonMain" }?.takeIf { it.isNotEmpty() }

            if (sourceSets == null) {
                target.logger.warn("BuildTimeConfig worked only with KotlinJvm or KotlinMultiplatform plugin. None of them found")
            }

            sourceSets?.forEach {
                it.kotlin.srcDirs(task.map { it.destinations.values })
            }
        }
    }
}

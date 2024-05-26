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

            val targetSourceSet = extension.targetSourceSet.orNull ?: kotlin.run {
                val kotlinJvmExtension = target.extensions.findByType(KotlinJvmProjectExtension::class.java)
                val kotlinMppExtension = target.extensions.findByType(KotlinMultiplatformExtension::class.java)

                kotlinJvmExtension?.sourceSets?.find { it.name == "main" }
                    ?: kotlinMppExtension?.sourceSets?.find { it.name == "commonMain" }
            }

            if (targetSourceSet == null) {
                target.logger.warn("BuildTimeConfig worked only with KotlinJvm or KotlinMultiplatform plugin. None of them found")
            } else {
                targetSourceSet.kotlin.srcDirs(task.map { it.destinations.values })
            }

            if (extension.generateOnSync.get()) {
                target.tasks.maybeCreate("prepareKotlinIdeaImport").dependsOn(task)
            }
        }
    }
}

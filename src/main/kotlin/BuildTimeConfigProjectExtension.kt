package dev.limebeck

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

open class BuildTimeConfigProjectExtension(
    private val project: Project
) {
    @Internal
    val configs: MutableList<Config> = mutableListOf()

    @Input
    val targetSourceSet: Property<KotlinSourceSet> = project.objects.property(KotlinSourceSet::class.java)

    @Input
    val generateOnSync: Property<Boolean> = project.objects.property(Boolean::class.java).convention(true)

    @Suppress("UNUSED")
    fun config(name: String? = null, action: Action<ConfigBuilder>) {
        val builder = ConfigBuilder(name, project.objects)
        action.execute(builder)
        configs.add(builder.build())
    }
}

package dev.limebeck

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Internal

open class ConfigsHolderExtension(
    private val project: Project
) {
    @Internal
    val configs: MutableList<Config> = mutableListOf()

    @Suppress("UNUSED")
    fun config(name: String? = null, action: Action<ConfigBuilder>) {
        val builder = ConfigBuilder(name, project.objects)
        action.execute(builder)
        configs.add(builder.build())
        println("<217d9e5f> ConfigsHolder called: $configs")
    }
}
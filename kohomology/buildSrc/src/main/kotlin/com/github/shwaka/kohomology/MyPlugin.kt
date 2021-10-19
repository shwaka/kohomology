package com.github.shwaka.kohomology

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

// [usage]
// apply<com.github.shwaka.kohomology.MyPlugin>()

class MyPlugin : Plugin<Project> {
    private val myTestListener = MyTestListener()

    override fun apply(project: Project) {
        project.tasks.withType<Test> {
            addTestListener(this@MyPlugin.myTestListener)
        }

        project.gradle.buildFinished {
            // this@MyPlugin.myTestListener.printSummary(project.logger)
            this@MyPlugin.myTestListener.printSummary(project.gradle as GradleInternal)
        }

        project.tasks.register("generateComponentN") {
            generateComponentN(30, project.rootDir)
        }
    }
}

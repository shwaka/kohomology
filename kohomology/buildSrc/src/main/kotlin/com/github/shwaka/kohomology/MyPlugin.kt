package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.depgraph.writeDepGraph
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
            doLast {
                generateComponentN(30, project.rootDir)
            }
        }

        project.tasks.register("writeDepGraph") {
            doLast {
                val propertyName = "umlPath"
                val umlPath: String? = System.getProperty(propertyName)
                if (umlPath == null) {
                    throw Exception("Please add option -D$propertyName=/path/to/depGraph.uml")
                }
                writeDepGraph(umlPath)
            }
        }
    }
}

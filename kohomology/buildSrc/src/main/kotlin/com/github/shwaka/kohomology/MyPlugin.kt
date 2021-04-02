package com.github.shwaka.kohomology

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.kotlin.dsl.withType

// based on https://github.com/radarsh/gradle-test-logger-plugin/issues/145

// [usage]
// apply<com.github.shwaka.kohomology.MyPlugin>()

class MyPlugin : Plugin<Project> {
    private val myTestListener = MyTestListener()

    override fun apply(project: Project) {
        project.tasks.withType<Test> {
            addTestListener(this@MyPlugin.myTestListener)
        }

        project.gradle.buildFinished {
            this@MyPlugin.myTestListener.printSummary(project.logger)
        }
    }
}

data class FailedTest(
    val className: String?,
    val name: String?
) {
    fun format(): String {
        return "[${this.shortClassName}] ${this.name}"
    }

    private val shortClassName: String
        get() {
            val splitClassName: List<String> = this.className?.split(".") ?: listOf("null")
            return if (splitClassName.isNotEmpty()) {
                splitClassName.last()
            } else {
                "null"
            }
        }
}

class MyTestListener : TestListener {
    // class ではなく object にすると、gradle の実行毎に object が再生成されず、
    // そのまま再利用されてしまうっぽい。
    // class にして自分で constructor を呼ぶなら大丈夫。
    private val failedTests: MutableList<FailedTest> = mutableListOf()

    override fun beforeSuite(suite: TestDescriptor?) = Unit
    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) = Unit
    override fun beforeTest(testDescriptor: TestDescriptor?) = Unit
    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (result?.resultType == TestResult.ResultType.FAILURE) {
            val failedTest = FailedTest(testDescriptor?.className, testDescriptor?.name)
            this.failedTests.add(failedTest)
        }
    }

    fun printSummary(logger: Logger) {
        if (failedTests.isNotEmpty()) {
            logger.error("==== FAILED TESTS ====")
            failedTests.forEach { logger.error(it.format()) }
        }
    }
}

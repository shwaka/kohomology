package com.github.shwaka.kohomology

import org.gradle.api.internal.GradleInternal
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory

// based on https://github.com/radarsh/gradle-test-logger-plugin/issues/145

data class FailedTest(
    val className: String,
    val name: String
) {
    // private val shortClassName: String
    //     get() {
    //         val splitClassName: List<String> = this.className.split(".")
    //         return if (splitClassName.isNotEmpty()) {
    //             splitClassName.last()
    //         } else {
    //             "null"
    //         }
    //     }
}

class MyTestListener : TestListener {
    // class ではなく object にすると、gradle の実行毎に object が再生成されず、
    // そのまま再利用されてしまうっぽい。
    // class にして自分で constructor を呼ぶなら大丈夫。
    private val failedTests: MutableMap<String, MutableList<FailedTest>> = mutableMapOf()

    override fun beforeSuite(suite: TestDescriptor?) = Unit
    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) = Unit
    override fun beforeTest(testDescriptor: TestDescriptor?) = Unit
    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (result?.resultType == TestResult.ResultType.FAILURE) {
            val className: String = testDescriptor?.className ?: "null"
            val name: String = testDescriptor?.name ?: "no name"
            val failedTest = FailedTest(className, name)
            this.failedTests
                .getOrPut(className, { mutableListOf() })
                .add(failedTest)
        }
    }

    fun printSummary(gradleInternal: GradleInternal) {
        // colorize output: https://stackoverflow.com/questions/14516693/gradle-color-output
        if (this.failedTests.isNotEmpty()) {
            val out = gradleInternal.services.get(StyledTextOutputFactory::class.java).create("an-output")
            out.style(StyledTextOutput.Style.FailureHeader).text("==== FAILED TESTS ====").println()
            for ((className, failedTestList) in this.failedTests) {
                out.style(StyledTextOutput.Style.FailureHeader).text(className).println()
                for (failedTest in failedTestList) {
                    out.style(StyledTextOutput.Style.Failure).text("  ${failedTest.name}").println()
                }
            }
        }
    }
}

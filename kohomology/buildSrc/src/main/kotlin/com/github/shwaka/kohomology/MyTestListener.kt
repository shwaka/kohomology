package com.github.shwaka.kohomology

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

// based on https://github.com/radarsh/gradle-test-logger-plugin/issues/145

// [usage]
// tasks.withType<Test> {
//     addTestListener(MyTestListener)
// }
// gradle.buildFinished {
//     MyTestListener.printSummary(logger)
// }

object MyTestListener : TestListener {
    private val failedTests: MutableList<String> = mutableListOf()

    override fun beforeSuite(suite: TestDescriptor?) = Unit
    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) = Unit
    override fun beforeTest(testDescriptor: TestDescriptor?) = Unit
    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (result?.resultType == TestResult.ResultType.FAILURE) {
            val message = testDescriptor!!.className + "\n  " + testDescriptor.name
            this.failedTests.add(message)
        }
    }

    fun printSummary(logger: Logger) {
        if (failedTests.isNotEmpty()) {
            logger.error("==== FAILED TESTS ====")
            failedTests.forEach { logger.error(it) }
        }
    }
}

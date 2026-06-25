package com.github.shwaka.kohomology.util.cancel

import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal object CancellationTestUtil {
    private const val sleepUnit: Long = 100L // ms
    private const val resultString = "Successfully finished"

    private fun cancellableSleep(
        sleepCount: Int,
        cancellationContext: CancellationContext,
    ): String {
        repeat(sleepCount) {
            Thread.sleep(sleepUnit)
            cancellationContext.check()
        }
        return resultString
    }

    suspend inline fun FreeScope.testAll(
        crossinline getContext: () -> CancellationContext,
    ) {
        val longer = 10
        val shorter = 5
        "timeout" {
            val cancellationContext = getContext()
            CancellationTestUtil.test(
                cancellationContext,
                sleepCount = longer,
                timeoutCount = shorter,
            )
        }
        "success" {
            val cancellationContext = getContext()
            CancellationTestUtil.test(
                cancellationContext,
                sleepCount = shorter,
                timeoutCount = longer,
            )
        }
        "no context" {
            val cancellationContext = getContext()
            val result = CancellationTestUtil.cancellableSleep(
                sleepCount = shorter,
                cancellationContext = cancellationContext,
            )
            result shouldBe CancellationTestUtil.resultString
        }
    }

    fun test(
        cancellationContext: CancellationContext,
        sleepCount: Int,
        timeoutCount: Int,
    ) {
        require(sleepCount != timeoutCount) {
            "sleepCount and timeoutCount must be different"
        }
        val timeout: Duration = (this.sleepUnit * timeoutCount).milliseconds
        val result: CancellationResult<String> =
            cancellationContext.runWithTimeout(timeout) {
                cancellableSleep(sleepCount, cancellationContext)
            }
        if (timeoutCount > sleepCount) {
            result.shouldBeInstanceOf<CancellationResult.Success<*>>()
            result.value shouldBe this.resultString
        } else {
            // timeoutCount < sleepCount
            result.shouldBeInstanceOf<CancellationResult.Cancelled>()
        }
    }
}

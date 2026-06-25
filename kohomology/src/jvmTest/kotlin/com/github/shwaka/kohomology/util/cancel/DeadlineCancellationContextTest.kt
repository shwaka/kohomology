package com.github.shwaka.kohomology.util.cancel

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.time.Duration.Companion.milliseconds

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

class DeadlineCancellationContextTest : FreeSpec({
    "test resulting cancel" {
        val context: CancellationContext = DeadlineCancellationContext()
        val sleepCount = 10
        val timeoutCount = 5
        val result: CancellationResult<String> =
            context.runWithTimeout((sleepUnit * timeoutCount).milliseconds) {
                cancellableSleep(sleepCount, context)
            }
        result.shouldBeInstanceOf<CancellationResult.Cancelled>()
    }

    "test resulting success" {
        val context: CancellationContext = DeadlineCancellationContext()
        val sleepCount = 5
        val timeoutCount = 10
        val result: CancellationResult<String> =
            context.runWithTimeout((sleepUnit * timeoutCount).milliseconds) {
                cancellableSleep(sleepCount, context)
            }
        result.shouldBeInstanceOf<CancellationResult.Success<*>>()
        result.value shouldBe resultString
    }
})

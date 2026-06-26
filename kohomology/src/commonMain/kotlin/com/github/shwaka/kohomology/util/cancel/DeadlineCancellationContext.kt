package com.github.shwaka.kohomology.util.cancel

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal class CancellationTimeoutException :
    RuntimeException("Computation timed out")

public class DeadlineCancellationChecker(
    timeout: Duration,
    timeSource: TimeSource = TimeSource.Monotonic,
) : CancellationChecker {
    private val deadline: TimeMark = timeSource.markNow() + timeout

    override fun check() {
        if (deadline.hasPassedNow()) {
            throw CancellationTimeoutException()
        }
    }
}

public class DeadlineCancellationContext(
    private val timeSource: TimeSource = TimeSource.Monotonic,
    storage: CancellationCheckerStorage =
        CancellationCheckerStorage.getDefault(),
) : CancellationContextBase(storage) {
    override fun <T> runWithTimeoutImpl(
        timeout: Duration,
        block: () -> T,
    ): CancellationResult<T> {
        val checker = DeadlineCancellationChecker(
            timeout = timeout,
            timeSource = timeSource,
        )

        return try {
            this.storage.withChecker(checker) {
                CancellationResult.Success(block())
            }
        } catch (_: CancellationTimeoutException) {
            CancellationResult.Cancelled
        }
    }
}

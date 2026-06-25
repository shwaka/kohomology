package com.github.shwaka.kohomology.util.cancel

import io.kotest.core.spec.style.FreeSpec

class DeadlineCancellationContextTest : FreeSpec({
    "test resulting cancel" {
        val context: CancellationContext = DeadlineCancellationContext()
        val sleepCount = 10
        val timeoutCount = 5
        CancellationTestUtil.test(context, sleepCount, timeoutCount)
    }

    "test resulting success" {
        val context: CancellationContext = DeadlineCancellationContext()
        val sleepCount = 5
        val timeoutCount = 10
        CancellationTestUtil.test(context, sleepCount, timeoutCount)
    }
})

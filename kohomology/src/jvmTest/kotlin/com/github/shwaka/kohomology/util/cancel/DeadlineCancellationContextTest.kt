package com.github.shwaka.kohomology.util.cancel

import io.kotest.core.spec.style.FreeSpec

class DeadlineCancellationContextTest : FreeSpec({
    "test with default storage" - {
        CancellationTestUtil.run {
            testAll { DeadlineCancellationContext() }
        }
    }

    "test with ThreadLocalCancellationCheckerStorage" - {
        CancellationTestUtil.run {
            val storage = ThreadLocalCancellationCheckerStorage()
            testAll { DeadlineCancellationContext(storage = storage) }
        }
    }

    "test with SingleThreadCancellationCheckerStorage" - {
        CancellationTestUtil.run {
            val storage = SingleThreadCancellationCheckerStorage()
            testAll { DeadlineCancellationContext(storage = storage) }
        }
    }
})

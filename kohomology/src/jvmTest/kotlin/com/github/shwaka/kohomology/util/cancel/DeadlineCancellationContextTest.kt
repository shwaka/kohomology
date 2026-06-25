package com.github.shwaka.kohomology.util.cancel

import io.kotest.core.spec.style.FreeSpec

class DeadlineCancellationContextTest : FreeSpec({
    "test with CancellationTestUtil" - {
        CancellationTestUtil.run {
            testAll { DeadlineCancellationContext() }
        }
    }
})

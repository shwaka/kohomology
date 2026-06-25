package com.github.shwaka.kohomology.util.cancel

import io.kotest.core.spec.style.FreeSpec

class CoroutineCancellationContextTest : FreeSpec({
    "test with default storage" - {
        CancellationTestUtil.run {
            testAll { CoroutineCancellationContext() }
        }
    }
})

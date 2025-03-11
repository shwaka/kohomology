package com.github.shwaka.kohomology.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize

class ShufflesTest : FreeSpec({
    "shuffles([p,q]) should have size (p+q)!/(p!q!)" {
        shuffles(listOf(2, 1)) shouldHaveSize 3
    }
})

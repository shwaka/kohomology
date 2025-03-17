package com.github.shwaka.kohomology.util.list

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class DuplicatesTest : FreeSpec({
    "[].duplicates() should be []" {
        emptyList<Int>().duplicates().shouldBeEmpty()
    }

    "[1, 2, 3].duplicates() should be []" {
        listOf(1, 2, 3).duplicates().shouldBeEmpty()
    }

    "[1, 1, 2].duplicates() should be [1]" {
        listOf(1, 1, 2).duplicates() shouldBe listOf(1)
    }

    "[1, 1, 2, 2].duplicates() should be [1, 2]" {
        listOf(1, 1, 2, 2).duplicates() shouldBe listOf(1, 2)
    }

    "[1, 2, 1].duplicates() should be [1]" {
        listOf(1, 2, 1).duplicates() shouldBe listOf(1)
    }
})

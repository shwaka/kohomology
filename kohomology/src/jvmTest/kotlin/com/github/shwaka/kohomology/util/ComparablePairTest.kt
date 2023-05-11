package com.github.shwaka.kohomology.util

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldNotBeGreaterThan
import io.kotest.matchers.comparables.shouldNotBeLessThan
import io.kotest.matchers.shouldBe

val comparablePairTag = NamedTag("ComparablePair")

class ComparablePairTest : FreeSpec({
    tags(comparablePairTag)

    "check comparison is correctly working" {
        forAll(
            row(ComparablePair(1, 3), ComparablePair(2, 4)),
            row(ComparablePair(1, 3), ComparablePair(1, 4)),
            row(ComparablePair(1, 3), ComparablePair(2, 3)),
            row(ComparablePair(1, 4), ComparablePair(2, 3)),
        ) { a, b ->
            a.shouldBeLessThan(b)
            a.shouldNotBeGreaterThan(b)
            b.shouldBeGreaterThan(a)
            b.shouldNotBeLessThan(a)
        }
    }

    "test directProductOf" {
        val actual = directProductOf(
            listOf(1, 2, 3),
            listOf(4, 5),
        )
        actual.shouldContainExactlyInAnyOrder(
            ComparablePair(1, 4),
            ComparablePair(1, 5),
            ComparablePair(2, 4),
            ComparablePair(2, 5),
            ComparablePair(3, 4),
            ComparablePair(3, 5),
        )
        actual.size shouldBe 6
    }
})

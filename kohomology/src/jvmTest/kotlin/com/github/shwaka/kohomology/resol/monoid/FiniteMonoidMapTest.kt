package com.github.shwaka.kohomology.resol.monoid

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val finiteMonoidMapTag = NamedTag("FiniteMonoidMap")

class FiniteMonoidMapTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    "test with cyclic groups" - {
        val source = CyclicGroup(6)
        val target = CyclicGroup(2)
        val values = (0 until 6).map { CyclicGroupElement(it % 2, 2) }
        val monoidMap = FiniteMonoidMap(source, target, values)

        "test invoke" {
            monoidMap(CyclicGroupElement(0, 6)) shouldBe CyclicGroupElement(0, 2)
            monoidMap(CyclicGroupElement(1, 6)) shouldBe CyclicGroupElement(1, 2)
            monoidMap(CyclicGroupElement(2, 6)) shouldBe CyclicGroupElement(0, 2)
            monoidMap(CyclicGroupElement(3, 6)) shouldBe CyclicGroupElement(1, 2)
        }
    }
})

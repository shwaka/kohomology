package com.github.shwaka.kohomology.resol.monoid.completion

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.resol.monoid.SimpleFiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.finiteMonoidTag
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

val groupCompletionTag = NamedTag("GroupCompletion")

class CommutativeGroupCompletionTest : FreeSpec({
    tags(finiteMonoidTag, groupCompletionTag)
    "group completion of Z/2 should be itself" {
        val cyclicGroup = CyclicGroup(2)
        val groupCompletion = GroupCompletion(cyclicGroup)
        groupCompletion.shouldBeInstanceOf<CommutativeGroupCompletion<CyclicGroupElement>>()
        groupCompletion.size shouldBe cyclicGroup.size
        shouldNotThrow<IllegalStateException> {
            groupCompletion.checkGroupAxioms()
        }
    }

    "group completion of Z/6 should be itself" {
        val cyclicGroup = CyclicGroup(6)
        val groupCompletion = GroupCompletion(cyclicGroup)
        groupCompletion.shouldBeInstanceOf<CommutativeGroupCompletion<CyclicGroupElement>>()
        groupCompletion.size shouldBe cyclicGroup.size
        shouldNotThrow<IllegalStateException> {
            groupCompletion.checkGroupAxioms()
        }
    }

    "group completion of monoid with 0 should be trivial" {
        val elements = listOf("1", "t", "0").map { SimpleFiniteMonoidElement(it) }
        val (one, t, zero) = elements
        val multiplicationTable = listOf(
            listOf(one, t, zero), // one*(-)
            listOf(t, zero, zero), // t*(-)
            listOf(zero, zero, zero), // zero*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable)
        val groupCompletion = GroupCompletion(monoid)
        groupCompletion.shouldBeInstanceOf<CommutativeGroupCompletion<CyclicGroupElement>>()
        groupCompletion.size shouldBe 1
        shouldNotThrow<IllegalStateException> {
            groupCompletion.checkGroupAxioms()
        }
    }
})

class LeftOreGroupCompletionTest : FreeSpec({
    tags(finiteMonoidTag, groupCompletionTag)

    "group completion of left Ore monoid" - {
        val elements = listOf("1", "x", "y").map { SimpleFiniteMonoidElement(it) }
        val (one, x, y) = elements
        val multiplicationTable = listOf(
            listOf(one, x, y), // one*(-)
            listOf(x, x, x), // x*(-)
            listOf(y, y, y), // y*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable)
        val groupCompletion = GroupCompletion(monoid)

        "groupCompletion should be an instance of LeftOreGroupCompletion" {
            groupCompletion.shouldBeInstanceOf<LeftOreGroupCompletion<SimpleFiniteMonoidElement<String>>>()
        }

        "groupCompletion.size should be 1 due to absorbing element" {
            groupCompletion.size shouldBe 1
        }

        "groupCompletion should satisfy the axioms of a group" {
            shouldNotThrow<IllegalStateException> {
                groupCompletion.checkGroupAxioms()
            }
        }
    }
})

class RightOreGroupCompletionTest : FreeSpec({
    tags(finiteMonoidTag, groupCompletionTag)

    "group completion of right Ore monoid" - {
        val elements = listOf("1", "x", "y").map { SimpleFiniteMonoidElement(it) }
        val (one, x, y) = elements
        val multiplicationTable = listOf(
            listOf(one, x, y), // one*(-)
            listOf(x, x, y), // x*(-)
            listOf(y, x, y), // y*(-)
        )
        val monoid = FiniteMonoidFromList(elements, multiplicationTable)
        val groupCompletion = GroupCompletion(monoid)

        "groupCompletion should be an instance of RightOreGroupCompletion" {
            groupCompletion.shouldBeInstanceOf<RightOreGroupCompletion<SimpleFiniteMonoidElement<String>>>()
        }

        "groupCompletion.size should be 1 due to absorbing element" {
            groupCompletion.size shouldBe 1
        }

        "groupCompletion should satisfy the axioms of a group" {
            shouldNotThrow<IllegalStateException> {
                groupCompletion.checkGroupAxioms()
            }
        }
    }
})

package com.github.shwaka.kohomology.resol.monoid

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val groupCompletionTag = NamedTag("GroupCompletion")

class GroupCompletionTest : FreeSpec({
    tags(groupCompletionTag)
    "group completion of Z/2 should be itself" {
        val cyclicGroup = CyclicGroup(2)
        GroupCompletion(cyclicGroup).size shouldBe cyclicGroup.size
    }

    "group completion of Z/6 should be itself" {
        val cyclicGroup = CyclicGroup(6)
        GroupCompletion(cyclicGroup).size shouldBe cyclicGroup.size
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
        GroupCompletion(monoid).size shouldBe 1
    }
})

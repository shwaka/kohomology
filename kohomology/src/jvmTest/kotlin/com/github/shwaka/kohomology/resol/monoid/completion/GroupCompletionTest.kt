package com.github.shwaka.kohomology.resol.monoid.completion

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.resol.monoid.SimpleFiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.finiteMonoidTag
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val groupCompletionTag = NamedTag("GroupCompletion")

class GroupCompletionTest : FreeSpec({
    tags(finiteMonoidTag, groupCompletionTag)
    "group completion of Z/2 should be itself" {
        val cyclicGroup = CyclicGroup(2)
        val groupCompletion = GroupCompletion(cyclicGroup)
        groupCompletion.size shouldBe cyclicGroup.size
        shouldNotThrow<IllegalStateException> {
            groupCompletion.checkGroupAxioms()
        }
    }

    "group completion of Z/6 should be itself" {
        val cyclicGroup = CyclicGroup(6)
        val groupCompletion = GroupCompletion(cyclicGroup)
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
        groupCompletion.size shouldBe 1
        shouldNotThrow<IllegalStateException> {
            groupCompletion.checkGroupAxioms()
        }
    }
})

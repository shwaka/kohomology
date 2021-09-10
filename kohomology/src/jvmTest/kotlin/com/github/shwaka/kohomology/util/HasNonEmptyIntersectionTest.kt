package com.github.shwaka.kohomology.util

import com.github.shwaka.kohomology.util.list.hasEmptyIntersection
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

val listUtilTag = NamedTag("ListUtil")

class HasNonEmptyIntersectionTest : FreeSpec({
    tags(listUtilTag)

    "hasEmptyIntersection() should return for the empty list" {
        val empty = emptyList<Int>()
        val nonEmpty = listOf(1, 3, 4)
        empty.hasEmptyIntersection(nonEmpty).shouldBeTrue()
        nonEmpty.hasEmptyIntersection(empty).shouldBeTrue()
        empty.hasEmptyIntersection(empty).shouldBeTrue()
    }

    "hasEmptyIntersection() should return false for non-empty list and itself" {
        val nonEmpty = listOf(1, 3, 4)
        nonEmpty.hasEmptyIntersection(nonEmpty).shouldBeFalse()
    }

    "hasEmptyIntersection() should return false for intersecting lists" {
        listOf(
            Pair(listOf(0, 1, 3), listOf(1, 2, 3)),
            Pair(listOf(0, 1, 3), listOf(3, 4, 5)),
            Pair(listOf(0, 1), listOf(1)),
            Pair(listOf(3), listOf(1, 3, 5)),
            Pair(listOf(0, 5, 6), listOf(3, 4, 5, 7)),
        ).forAll { (first, second) ->
            first.hasEmptyIntersection(second).shouldBeFalse()
            second.hasEmptyIntersection(first).shouldBeFalse()
        }
    }

    "hasEmptyIntersection() should return true for non-intersecting lists" {
        listOf(
            Pair(listOf(0, 1, 3), listOf(2, 4)),
            Pair(listOf(0), listOf(3, 4, 5)),
            Pair(listOf(3), listOf(4, 7)),
            Pair(listOf(0, 5, 6), listOf(3, 4, 7)),
        ).forAll { (first, second) ->
            first.hasEmptyIntersection(second).shouldBeTrue()
            second.hasEmptyIntersection(first).shouldBeTrue()
        }
    }
})

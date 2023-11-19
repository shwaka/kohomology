package com.github.shwaka.kohomology.util

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val directProductOfTag = NamedTag("DirectProductOf")

class DirectProductOfTest : FreeSpec({
    tags(directProductOfTag)

    "test directProductOf" {
        val list1 = listOf(1, 2, 3)
        val list2 = listOf("a", "b")
        val expected = listOf(
            Pair(1, "a"),
            Pair(1, "b"),
            Pair(2, "a"),
            Pair(2, "b"),
            Pair(3, "a"),
            Pair(3, "b"),
        )
        directProductOf(list1, list2) shouldBe expected
    }
})

class DirectProductOfFamilyTest : FreeSpec({
    tags(directProductOfTag)

    "assert that directProductOfFamily generalizes directProductOf" {
        val list1 = listOf(1, 2, 3)
        val list2 = listOf("a", "b")
        directProductOfFamily(listOf(list1, list2)) shouldBe
            directProductOf(list1, list2).map { (a, b) -> listOf(a, b) }
    }

    "test directProductOfFamily for 3 collections" {
        val list1 = listOf(1, 2)
        val list2 = listOf(3, 4)
        val list3 = listOf(5, 6)
        val expected = listOf(
            listOf(1, 3, 5),
            listOf(1, 3, 6),
            listOf(1, 4, 5),
            listOf(1, 4, 6),
            listOf(2, 3, 5),
            listOf(2, 3, 6),
            listOf(2, 4, 5),
            listOf(2, 4, 6),
        )
        directProductOfFamily(listOf(list1, list2, list3)) shouldBe expected
    }

    "directProductOfFamily(emptyList()) should be listOf(emptyList())" {
        directProductOfFamily<Int>(emptyList()) shouldBe listOf(emptyList())
    }
})

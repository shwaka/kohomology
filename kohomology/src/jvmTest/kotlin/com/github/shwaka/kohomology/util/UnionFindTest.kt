package com.github.shwaka.kohomology.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val unionFindTag = NamedTag("UnionFind")

class UnionFindTest : FreeSpec({
    tags(unionFindTag)

    "group integers by parity by using UnionFind" {
        val unionFind = UnionFind(5)
        unionFind.unite(0, 2)
        unionFind.unite(2, 4)
        unionFind.unite(1, 3)
        unionFind.groups().size shouldBe 2
        unionFind.same(0, 4).shouldBeTrue()
        unionFind.same(0, 1).shouldBeFalse()
    }
})

class GenericUnionFindTest : FreeSpec({
    tags(unionFindTag)

    "GenericUnionFind(elements) should throw IllegalArgumentException if elements contains duplicates" {
        shouldThrow<IllegalArgumentException> {
            GenericUnionFind(listOf("a", "b", "a"))
        }
    }

    "group alphabets by their homotopy types by using GenericUnionFind" {
        val unionFind = GenericUnionFind(listOf("a", "b", "c", "d", "i", "j"))
        unionFind.unite("a", "b")
        unionFind.unite("d", "b")
        unionFind.unite("i", "j")
        unionFind.groups().size shouldBe 3
        unionFind.same("a", "d").shouldBeTrue()
        unionFind.same("a", "i").shouldBeFalse()
    }
})

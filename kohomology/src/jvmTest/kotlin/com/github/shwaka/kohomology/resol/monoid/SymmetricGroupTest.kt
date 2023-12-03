package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val symmetricGroupTag = NamedTag("SymmetricGroup")

class SymmetricGroupTest : FreeSpec({
    tags(finiteMonoidTag, symmetricGroupTag)

    "test symmetric group of order 0" - {
        val symmetricGroup = SymmetricGroup(0)

        "size should be 1" {
            symmetricGroup.size shouldBe 1
        }

        "group axioms should be satisfied" {
            shouldNotThrow<IllegalStateException> {
                symmetricGroup.checkGroupAxioms()
            }
        }
    }

    "test symmetric group of order 1" - {
        val symmetricGroup = SymmetricGroup(1)

        "size should be 1" {
            symmetricGroup.size shouldBe 1
        }

        "group axioms should be satisfied" {
            shouldNotThrow<IllegalStateException> {
                symmetricGroup.checkGroupAxioms()
            }
        }
    }

    "test symmetric group of order 2" - {
        val symmetricGroup = SymmetricGroup(2)

        "size should be 2" {
            symmetricGroup.size shouldBe 2
        }

        "group axioms should be satisfied" {
            shouldNotThrow<IllegalStateException> {
                symmetricGroup.checkGroupAxioms()
            }
        }
    }

    "test symmetric group of order 3" - {
        val symmetricGroup = SymmetricGroup(3)

        "size should be 6" {
            symmetricGroup.size shouldBe 6
        }

        "all elements should have order 1, 2 or 3" {
            val unit = symmetricGroup.unit
            symmetricGroup.elements.forAll { permutation ->
                val square = symmetricGroup.multiply(permutation, permutation)
                val cube = symmetricGroup.multiply(square, permutation)
                ((permutation == unit) || (square == unit) || (cube == unit)).shouldBeTrue()
            }
        }

        "group axioms should be satisfied" {
            shouldNotThrow<IllegalStateException> {
                symmetricGroup.checkGroupAxioms()
            }
        }
    }
})

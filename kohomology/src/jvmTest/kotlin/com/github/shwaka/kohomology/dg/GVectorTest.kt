package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverRational
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

val gVectorTag = NamedTag("GVector")

fun <S : Scalar, V : NumVector<S>> gVectorTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "graded vector test" - {
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            (0 until degree).map { "v$it" }
        }
        gVectorSpace.context.run {
            "fromCoeffMap() should give the same gVector as fromCoeffList()" {
                val v1 = gVectorSpace.fromCoeffList(listOf(zero, one, two), 3)
                val v2 = gVectorSpace.fromCoeffMap(mapOf(1 to one, 2 to two), 3)
                v2 shouldBe v1
            }
            "GVectors with the same coefficients should have the same hashCode" {
                val v1 = gVectorSpace.fromCoeffList(listOf(one, two), 2)
                val v2 = gVectorSpace.fromCoeffList(listOf(one, two), 2)
                v1 shouldNotBeSameInstanceAs v2
                v1.hashCode() shouldBe v2.hashCode()
            }
            "addition test" {
                val v = gVectorSpace.fromCoeffList(listOf(one, zero), 2)
                val expected = gVectorSpace.fromCoeffList(listOf(two, zero), 2)
                (v + v) shouldBe expected
                (v + v) shouldNotBe v
            }
            "multiplication with Sign" {
                val v = gVectorSpace.fromCoeffList(listOf(one, -two, zero), 3)
                (v * Sign.PLUS) shouldBe v
                (Sign.PLUS * v) shouldBe v
                (v * Sign.MINUS) shouldBe -v
                (Sign.MINUS * v) shouldBe -v
            }
            "(0, 0).isZero() should be true" {
                val v = gVectorSpace.fromCoeffList(listOf(zero, zero), 2)
                v.isZero().shouldBeTrue()
            }
            "(0, 1).isZero() should be false" {
                val v = gVectorSpace.fromCoeffList(listOf(zero, one), 2)
                v.isZero().shouldBeFalse()
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>> gVectorSpaceTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
        (0 until degree).map { "v$it" }
    }

    gVectorSpace.context.run {
        "get() should return the cache if exists" {
            val vectorSpace1 = gVectorSpace[1]
            gVectorSpace[1] shouldBeSameInstanceAs vectorSpace1
        }

        "getBasis(deg) should return the correct basis" {
            val v0 = gVectorSpace.fromCoeffList(listOf(one, zero, zero), 3)
            val v1 = gVectorSpace.fromCoeffList(listOf(zero, one, zero), 3)
            val v2 = gVectorSpace.fromCoeffList(listOf(zero, zero, one), 3)
            gVectorSpace.getBasis(3) shouldBe listOf(v0, v1, v2)
        }

        "convertToGVector converts ZeroGVector to the correct zero vector" {
            val zeroGVector = ZeroGVector<IntDegree, StringBasisName, S, V>(gVectorSpace)
            val degree = 3
            val expected = gVectorSpace.getZero(degree)
            gVectorSpace.convertToGVector(zeroGVector, degree) shouldBe expected
        }

        "convertToGVector converts GVector to itself" {
            val degree = 3
            val v = gVectorSpace.getBasis(degree).reduce { x, y -> x + y }
            gVectorSpace.convertToGVector(v, degree) shouldBe v
        }

        "Iterable<GVector>.sum() should return sum of the elements" {
            val v0 = gVectorSpace.fromCoeffList(listOf(one, zero, zero), 3)
            val v1 = gVectorSpace.fromCoeffList(listOf(zero, one, zero), 3)
            val v2 = gVectorSpace.fromCoeffList(listOf(zero, zero, one), 3)
            listOf(v0, v1, v2).sum() shouldBe (v0 + v1 + v2)
            listOf(v0).sum() shouldBe v0
        }

        "emptyList().sum(degree) should be 0" {
            val degree = 3
            val z = gVectorSpace.getZero(degree)
            emptyList<GVector<IntDegree, StringBasisName, S, V>>().sum(degree) shouldBe z
        }

        "emptyList().sum() (without the argument 'degree') should throw an IllegalArgumentException" {
            shouldThrow<IllegalArgumentException> {
                emptyList<GVector<IntDegree, StringBasisName, S, V>>().sum()
            }
        }
    }
}

class RationalGVectorSpaceTest : FreeSpec({
    tags(gVectorTag, rationalTag)

    val numVectorSpace = DenseNumVectorSpaceOverRational
    include(gVectorTest(numVectorSpace))
    include(gVectorSpaceTest(numVectorSpace))
})

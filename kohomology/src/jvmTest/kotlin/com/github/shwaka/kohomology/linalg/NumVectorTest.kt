package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.intModpTag
import com.github.shwaka.kohomology.intRationalTag
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverF7
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverIntRational
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverF7
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverIntRational
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

val numVectorTag = NamedTag("NumVector")
val denseNumVectorTag = NamedTag("DenseNumVector")
val sparseNumVectorTag = NamedTag("SparseNumVector")

val kococoDebug = (System.getProperty("kococo.debug") != null)

fun <S : Scalar> denseNumVectorTest(numVectorSpace: DenseNumVectorSpace<S>) = freeSpec {
    "factory should return the cache if exists" {
        val field = numVectorSpace.field
        DenseNumVectorSpace.from(field) shouldBeSameInstanceAs numVectorSpace
    }
}

fun <S : Scalar> sparseNumVectorTest(numVectorSpace: SparseNumVectorSpace<S>) = freeSpec {
    "factory should return the cache if exists" {
        val field = numVectorSpace.field
        SparseNumVectorSpace.from(field) shouldBeSameInstanceAs numVectorSpace
    }

    "fromReducedValueMap should throw if zero is explicitly given and debug mode is enabled".config(enabled = kococoDebug) {
        numVectorSpace.field.context.run {
            val valueMap: Map<Int, S> = mapOf(
                1 to one,
                3 to -two,
                4 to zero,
            )
            shouldThrow<IllegalArgumentException> {
                numVectorSpace.fromReducedValueMap(valueMap, 7)
            }
        }
    }

    numVectorSpace.context.run {
        "valueMap for the vector (0, 0, 0) should be empty" {
            val v = numVectorSpace.fromValueList(listOf(zero, zero, zero))
            v.valueMap.size shouldBe 0
            v.dim shouldBe 3
        }
    }
}

fun <S : Scalar, V : NumVector<S>> numVectorTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    numVectorSpace.context.run {
        "numVectors with same values should have the same hashCode" {
            val v1 = numVectorSpace.fromValueList(listOf(one, two))
            val v2 = numVectorSpace.fromValueList(listOf(one, two))
            v1 shouldNotBeSameInstanceAs v2
            v1.hashCode() shouldBe v2.hashCode()
        }
        "(0, 1) + (0, 1) should be (0, 2)" {
            val v = numVectorSpace.fromValueList(listOf(zero, one))
            val w = numVectorSpace.fromValueList(listOf(zero, two))
            (v + v) shouldBe w
        }
        "(2, 0) + (-2, 0) should be (0, 0)" {
            val v = numVectorSpace.fromValueList(listOf(two, zero))
            val w = numVectorSpace.fromValueList(listOf(-two, zero))
            (v + w) shouldBe numVectorSpace.getZero(2)
        }
        "(-1, 0) - (-1, 0) should be (0, 0)" {
            val v = numVectorSpace.fromValueList(listOf(-one, zero))
            (v - v) shouldBe numVectorSpace.getZero(2)
        }
        "(1, 0) * 2 should be (2, 0)" {
            val v = numVectorSpace.fromValueList(listOf(one, zero))
            val w = numVectorSpace.fromValueList(listOf(two, zero))
            (v * two) shouldBe w
            (v * 2) shouldBe w
        }
        "2 * (1, 0) should be (2, 0)" {
            val v = numVectorSpace.fromValueList(listOf(one, zero))
            val w = numVectorSpace.fromValueList(listOf(two, zero))
            (two * v) shouldBe w
            (2 * v) shouldBe w
        }
        "(1, 0).dim should be 2" {
            val v = numVectorSpace.fromValueList(listOf(one, zero))
            v.dim shouldBe 2
        }
        "vectorSpace.getZero(3) should be (0, 0, 0)" {
            val v = numVectorSpace.getZero(3)
            val w = numVectorSpace.fromValueList(listOf(zero, zero, zero))
            v shouldBe w
        }
        "(0, 0, 0) should be different from (0, 0)" {
            val v = numVectorSpace.getZero(3)
            val w = numVectorSpace.getZero(2)
            v shouldNotBe w
        }
        "vectorSpace.get() and vectorSpace.getZero(0) should return the same element" {
            val v = numVectorSpace.fromValueList(listOf())
            val w = numVectorSpace.getZero(0)
            v shouldBe w
        }
        "(1,2) dot (-1, 3) should be 5" {
            val v = numVectorSpace.fromValueList(listOf(one, two))
            val w = numVectorSpace.fromValueList(listOf(-one, three))
            (v dot w) shouldBe five
        }
        "(0, 0, 0).isZero() should be true" {
            val v = numVectorSpace.fromValueList(listOf(zero, zero, zero))
            v.isZero().shouldBeTrue()
        }
        "(0, 1, 0).isZero() should be false" {
            val v = numVectorSpace.fromValueList(listOf(zero, one, zero))
            v.isZero().shouldBeFalse()
        }
        "fromValueMap should return the same numVector as fromValueList" {
            val v1 = numVectorSpace.fromValueMap(mapOf(1 to one, 2 to two), 4)
            val v2 = numVectorSpace.fromValueList(listOf(zero, one, two, zero))
            v1 shouldBe v2
        }
        "(0, 1, 2).toList() should be [0, 1, 2]" {
            val v = numVectorSpace.fromValueList(listOf(zero, one, two))
            v.toList() shouldBe listOf(zero, one, two)
        }
        "(0, 1, 2).toMap() should be {1: 1, 2: 2}" {
            val v = numVectorSpace.fromValueList(listOf(zero, one, two))
            v.toMap() shouldBe mapOf(1 to one, 2 to two)
            v.toMap()[0].shouldBeNull()
        }
    }
}

class IntRationalDenseNumVectorTest : FreeSpec({
    tags(numVectorTag, denseNumVectorTag, intRationalTag)
    include(denseNumVectorTest(DenseNumVectorSpaceOverIntRational))
    include(numVectorTest(DenseNumVectorSpaceOverIntRational))
})

class BigRationalDenseNumVectorTest : FreeSpec({
    tags(numVectorTag, denseNumVectorTag, bigRationalTag)
    include(denseNumVectorTest(DenseNumVectorSpaceOverBigRational))
    include(numVectorTest(DenseNumVectorSpaceOverBigRational))
})

class IntModpDenseNumVectorTest : FreeSpec({
    tags(numVectorTag, denseNumVectorTag, intModpTag)
    include(denseNumVectorTest(DenseNumVectorSpaceOverF7))
    include(numVectorTest(DenseNumVectorSpaceOverF7))
})

class IntRationalSparseNumVectorTest : FreeSpec({
    tags(numVectorTag, sparseNumVectorTag, intRationalTag)
    include(sparseNumVectorTest(SparseNumVectorSpaceOverIntRational))
    include(numVectorTest(SparseNumVectorSpaceOverIntRational))
})

class BigRationalSparseNumVectorTest : FreeSpec({
    tags(numVectorTag, sparseNumVectorTag, bigRationalTag)
    include(sparseNumVectorTest(SparseNumVectorSpaceOverBigRational))
    include(numVectorTest(SparseNumVectorSpaceOverBigRational))
})

class IntModpSparseNumVectorTest : FreeSpec({
    tags(numVectorTag, sparseNumVectorTag, intModpTag)
    include(sparseNumVectorTest(SparseNumVectorSpaceOverF7))
    include(numVectorTest(SparseNumVectorSpaceOverF7))
})

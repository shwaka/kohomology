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
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

val numVectorTag = NamedTag("NumVector")
val denseNumVectorTag = NamedTag("DenseNumVector")
val sparseNumVectorTag = NamedTag("SparseNumVector")

fun <S : Scalar> denseNumVectorTest(numVectorSpace: DenseNumVectorSpace<S>) = stringSpec {
    "factory should return the cache if exists" {
        val field = numVectorSpace.field
        DenseNumVectorSpace.from(field) shouldBeSameInstanceAs numVectorSpace
    }
}

fun <S : Scalar> sparseNumVectorTest(numVectorSpace: SparseNumVectorSpace<S>) = stringSpec {
    "factory should return the cache if exists" {
        val field = numVectorSpace.field
        SparseNumVectorSpace.from(field) shouldBeSameInstanceAs numVectorSpace
    }
}

fun <S : Scalar, V : NumVector<S>> numVectorTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    numVectorSpace.context.run {
        "(0, 1) + (0, 1) should be (0, 2)" {
            val v = numVectorSpace.fromValueList(listOf(zero, one))
            val w = numVectorSpace.fromValueList(listOf(zero, two))
            (v + v) shouldBe w
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
    }
}

class IntRationalDenseNumVectorTest : StringSpec({
    tags(numVectorTag, denseNumVectorTag, intRationalTag)
    include(denseNumVectorTest(DenseNumVectorSpaceOverIntRational))
    include(numVectorTest(DenseNumVectorSpaceOverIntRational))
})

class BigRationalDenseNumVectorTest : StringSpec({
    tags(numVectorTag, denseNumVectorTag, bigRationalTag)
    include(denseNumVectorTest(DenseNumVectorSpaceOverBigRational))
    include(numVectorTest(DenseNumVectorSpaceOverBigRational))
})

class IntModpDenseNumVectorTest : StringSpec({
    tags(numVectorTag, denseNumVectorTag, intModpTag)
    include(denseNumVectorTest(DenseNumVectorSpaceOverF7))
    include(numVectorTest(DenseNumVectorSpaceOverF7))
})

class IntRationalSparseNumVectorTest : StringSpec({
    tags(numVectorTag, sparseNumVectorTag, intRationalTag)
    include(sparseNumVectorTest(SparseNumVectorSpaceOverIntRational))
    include(numVectorTest(SparseNumVectorSpaceOverIntRational))
})

class BigRationalSparseNumVectorTest : StringSpec({
    tags(numVectorTag, sparseNumVectorTag, bigRationalTag)
    include(sparseNumVectorTest(SparseNumVectorSpaceOverBigRational))
    include(numVectorTest(SparseNumVectorSpaceOverBigRational))
})

class IntModpSparseNumVectorTest : StringSpec({
    tags(numVectorTag, sparseNumVectorTag, intModpTag)
    include(sparseNumVectorTest(SparseNumVectorSpaceOverF7))
    include(numVectorTest(SparseNumVectorSpaceOverF7))
})

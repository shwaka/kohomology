package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

val gVectorTag = NamedTag("GVector")

fun <S : Scalar<S>, V : NumVector<S, V>> gVectorSpaceTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    val field = numVectorSpace.field
    val zero = field.zero
    val one = field.one

    "get() should return the cache if exists" {
        val gVectorSpace = GVectorSpace(numVectorSpace) { deg ->
            val basis = (0 until deg).map { "v$it" }
            VectorSpace(numVectorSpace, basis)
        }
        val vectorSpace1 = gVectorSpace[1]
        gVectorSpace[1] shouldBeSameInstanceAs vectorSpace1
    }
}

class BigRationalGVectorSpaceTest : StringSpec({
    tags(gVectorTag, bigRationalTag)

    val numVectorSpace = DenseNumVectorSpace.from(BigRationalField)
    include(gVectorSpaceTest(numVectorSpace))
})

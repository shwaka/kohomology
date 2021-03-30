package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.field.DenseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val multipleVectorContextTag = NamedTag("MultipleVectorContext")

data class MyBasis(val name: String)

fun <S : Scalar, V : NumVector<S, V>> multipleVectorContextTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    "two vector spaces with the same basis class" {
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
        val context = MultipleVectorContext(numVectorSpace, listOf(vectorSpace1, vectorSpace2))

        val (a, b) = vectorSpace1.getBasis()
        val (x, y) = vectorSpace2.getBasis()

        context.run { a + b } shouldBe vectorSpace1.withContext { a + b }
        context.run { x + y } shouldBe vectorSpace2.withContext { x + y }
    }

    "two vector spaces with different basis classes" {
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf(MyBasis("x"), MyBasis("y")))
        val context = MultipleVectorContext(numVectorSpace, listOf(vectorSpace1, vectorSpace2))

        val (a, b) = vectorSpace1.getBasis()
        val (x, y) = vectorSpace2.getBasis()

        context.run { a + b } shouldBe vectorSpace1.withContext { a + b }
        context.run { x + y } shouldBe vectorSpace2.withContext { x + y }
    }
}

class BigRationalMultipleVectorContextTest : StringSpec({
    tags(multipleVectorContextTag)

    val numVectorSpace = DenseNumVectorSpaceOverBigRational
    include(multipleVectorContextTest(numVectorSpace))
})

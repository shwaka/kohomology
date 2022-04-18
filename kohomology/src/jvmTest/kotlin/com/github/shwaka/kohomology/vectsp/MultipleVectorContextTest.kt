package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val multipleVectorContextTag = NamedTag("MultipleVectorContext")

data class MyBasis(val name: String) : BasisName

fun <S : Scalar, V : NumVector<S>> multipleVectorContextTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "two vector spaces with the same basis class" {
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
        val context = MultipleVectorContext(numVectorSpace, listOf(vectorSpace1, vectorSpace2))

        val (a, b) = vectorSpace1.getBasis()
        val (x, y) = vectorSpace2.getBasis()

        context.run { a + b } shouldBe vectorSpace1.context.run { a + b }
        context.run { x + y } shouldBe vectorSpace2.context.run { x + y }
    }

    "two vector spaces with different basis classes" {
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf(MyBasis("x"), MyBasis("y")))
        val context = MultipleVectorContext(numVectorSpace, listOf(vectorSpace1, vectorSpace2))

        val (a, b) = vectorSpace1.getBasis()
        val (x, y) = vectorSpace2.getBasis()

        context.run { a + b } shouldBe vectorSpace1.context.run { a + b }
        context.run { x + y } shouldBe vectorSpace2.context.run { x + y }
    }
}

class RationalMultipleVectorContextTest : FreeSpec({
    tags(multipleVectorContextTag)

    val numVectorSpace = DenseNumVectorSpaceOverRational
    include(multipleVectorContextTest(numVectorSpace))
})

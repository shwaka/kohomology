package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val algebraTag = NamedTag("Algebra")

class AlgebraTest : FreeSpec({
    tags(algebraTag)

    "test with Q[Z/2]" - {
        val matrixSpace = SparseMatrixSpaceOverRational
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("e", "t"))
        val (e, t) = vectorSpace.getBasis()
        val multiplication = ValueBilinearMap(
            source1 = vectorSpace,
            source2 = vectorSpace,
            target = vectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(e, t), // e*(-)
                listOf(t, e)
            )
        )
        val algebra = Algebra(matrixSpace, vectorSpace, multiplication, unit = e)

        "test multiplication" {
            algebra.context.run {
                (e * e) shouldBe e
                (e * t) shouldBe t
                (t * e) shouldBe t
                (t * t) shouldBe e
                ((e + t) * (e + t)) shouldBe (2 * (e + t))
            }
        }
    }
})

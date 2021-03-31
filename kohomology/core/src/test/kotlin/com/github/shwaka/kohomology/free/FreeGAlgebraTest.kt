package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

val freeGAlgebraTag = NamedTag("FreeGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeGAlgebraTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val generatorList = listOf(
        Indeterminate("x", 2),
        Indeterminate("y", 2),
    )
    val freeGAlgebra = FreeGAlgebra(matrixSpace, generatorList)
    "check dimensions" {
        val gen = exhaustive(listOf(0, 1, 2, 3, 4, 5, 6))
        checkAll(gen) { i ->
            val degree = 2 * i
            freeGAlgebra[degree].dim shouldBe (i + 1)
        }
        checkAll(gen) { i ->
            val degree = 2 * i + 1
            freeGAlgebra[degree].dim shouldBe 0
        }
    }
    "check multiplication" {
        val (x, y) = freeGAlgebra.generatorList
        freeGAlgebra.withGAlgebraContext {
            (x + y).pow(0) shouldBe freeGAlgebra.unit
            (x + y).pow(1) shouldBe (x + y)
            (x + y).pow(2) shouldBe (x.pow(2) + 2 * x * y + y.pow(2))
            (x - y).pow(3) shouldBe (x.pow(3) - 3 * x.pow(2) * y + 3 * x * y.pow(2) - y.pow(3))
        }
    }
}

class FreeGAlgebraTest : StringSpec({
    tags(freeGAlgebraTag, bigRationalTag)

    include(freeGAlgebraTest(DenseMatrixSpaceOverBigRational))
})

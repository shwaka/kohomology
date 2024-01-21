package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Augmentation
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.math.min

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> idealSubModuleTestWithOneGeneratorAbsorbing(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    val monoid = FiniteMonoidFromList(
        elements = (0 until order).map { "x$it" },
        multiplicationTable = (0 until order).map { i ->
            (0 until order).map { j ->
                val k = min(i + j, order - 1)
                "x$k"
            }
        },
        name = "M_$order"
    )
    val coeffAlgebra = MonoidRing(monoid, matrixSpace)
    val totalModule = FreeModule(coeffAlgebra, listOf(StringBasisName("v")))
    val augmentation = Augmentation(coeffAlgebra)
    val ideal = augmentation.kernel()
    val subModule = SubModule(totalModule, ideal)

    "test SubModule constructed with Ideal for one generator monoid of order $order" - {
        "monoid should satisfy the axioms" {
            shouldNotThrow<IllegalStateException> {
                monoid.checkMonoidAxioms()
            }
        }

        "totalModule should have dimension $order" {
            totalModule.underlyingVectorSpace.dim shouldBe order
        }

        "subModule should have dimension ${order - 1}" {
            subModule.underlyingVectorSpace.dim shouldBe (order - 1)
        }

        "subModule.inclusion should be injective" {
            subModule.inclusion.underlyingLinearMap.isInjective().shouldBeTrue()
        }

        "subModule.retraction should be surjective" {
            subModule.retraction.isSurjective().shouldBeTrue()
        }
    }
}

class IdealSubModuleTest : FreeSpec({
    tags(moduleTag)

    include(idealSubModuleTestWithOneGeneratorAbsorbing(5, SparseMatrixSpaceOverRational))
})

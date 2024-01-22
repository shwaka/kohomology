package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.algebra.OpMonoidRing
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.resol.module.TrivialModule
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testWithFreeResolution(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    require(order > 1)
    val cyclicGroup = CyclicGroup(order)
    val coeffAlgebra = MonoidRing(cyclicGroup, matrixSpace)
    val complex = freeResolutionOverCyclicGroup(coeffAlgebra, matrixSpace)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Z/$order]" - {
        "getModule should return the same instance for Int and IntDegree" {
            (-10..10).forAll { n ->
                val degree = IntDegree(n)
                complex.getModule(n) shouldBeSameInstanceAs complex.getModule(degree)
            }
        }

        "getDifferential should return the same instance for Int and IntDegree" {
            (-10..10).forAll { n ->
                val degree = IntDegree(n)
                complex.getDifferential(n) shouldBeSameInstanceAs complex.getDifferential(degree)
            }
        }

        "getModule should return an instance of FreeModule" {
            (-10..10).forAll { n ->
                complex.getModule(n).shouldBeInstanceOf<FreeModule<*, *, *, *, *>>()
            }
        }

        "getDifferential should return an instance of FreeModule" {
            (-10..10).forAll { n ->
                complex.getDifferential(n).shouldBeInstanceOf<FreeModuleMap<*, *, *, *, *, *>>()
            }
        }

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-10..10).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        fun expectedCohomologyDim(degree: Int): Int {
            return when {
                (order == matrixSpace.field.characteristic) -> when {
                    (degree > 0) -> 0
                    else -> 1
                }
                else -> when (degree) {
                    0 -> 1
                    else -> 0
                }
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-10..10).forAll { degree ->
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe
                    expectedCohomologyDim(degree)
            }
        }

        "test cohomology of tensorWith(trivialModule)" {
            val opCoeffAlgebra = OpMonoidRing(coeffAlgebra)
            val oneDimensionalSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("1"))
            val trivialModule = TrivialModule(oneDimensionalSpace, opCoeffAlgebra)
            val tensorProductComplex = complex.tensorWith(trivialModule)
            (-10..10).forAll { degree ->
                tensorProductComplex.cohomology[degree].dim shouldBe
                    expectedCohomologyDim(degree)
            }
        }
    }
}

class FreeComplexTest : FreeSpec({
    tags(moduleTag)

    include(testWithFreeResolution(2, SparseMatrixSpaceOverRational))
    include(testWithFreeResolution(2, SparseMatrixSpaceOverF2))
    include(testWithFreeResolution(2, SparseMatrixSpaceOverF3))
    include(testWithFreeResolution(3, SparseMatrixSpaceOverRational))
    include(testWithFreeResolution(3, SparseMatrixSpaceOverF2))
    include(testWithFreeResolution(3, SparseMatrixSpaceOverF3))
})

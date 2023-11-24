package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeResolutionOverCyclicGroup(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
): ComplexOfFreeModules<IntDegree, CyclicGroupElement, StringBasisName, S, V, M> {
    val coeffAlgebra = MonoidRing(CyclicGroup(order), matrixSpace)
    val (e, t) = coeffAlgebra.getBasis()
    val unitMinusT = coeffAlgebra.context.run { e - t }
    val norm = coeffAlgebra.context.run { coeffAlgebra.getBasis().sum() }

    val zeroModule = FreeModule(coeffAlgebra, emptyList<StringBasisName>())

    val moduleAtEven = FreeModule(coeffAlgebra, listOf(StringBasisName("x")))
    val (x) = moduleAtEven.getGeneratingBasis()

    val moduleAtOdd = FreeModule(coeffAlgebra, listOf(StringBasisName("y")))
    val (y) = moduleAtOdd.getGeneratingBasis()

    val moduleMapFromEven = FreeModuleMap.fromValuesOnGeneratingBasis(
        source = moduleAtEven,
        target = moduleAtOdd,
        values = moduleAtOdd.context.run { listOf(norm * y) }
    )
    val moduleMapFromOdd = FreeModuleMap.fromValuesOnGeneratingBasis(
        source = moduleAtOdd,
        target = moduleAtEven,
        values = moduleAtEven.context.run { listOf(unitMinusT * x) }
    )
    val moduleMapFromZero = FreeModuleMap.fromValuesOnGeneratingBasis(
        source = moduleAtEven,
        target = zeroModule,
        values = listOf(zeroModule.underlyingVectorSpace.zeroVector),
    )
    val zeroModuleMap = FreeModuleMap.fromValuesOnGeneratingBasis(
        source = zeroModule,
        target = zeroModule,
        values = emptyList(),
    )

    val getModule = { degree: IntDegree ->
        val n = degree.value
        when {
            (n > 0) -> zeroModule
            (n.mod(2) == 0) -> moduleAtEven
            (n.mod(2) == 1) -> moduleAtOdd
            else -> throw Exception("This can't happen!")
        }
    }

    val getDifferential = { degree: IntDegree ->
        val n = degree.value
        when {
            (n > 0) -> zeroModuleMap
            (n == 0) -> moduleMapFromZero
            (n.mod(2) == 0) -> moduleMapFromEven
            (n.mod(2) == 1) -> moduleMapFromOdd
            else -> throw Exception("This can't happen!")
        }
    }

    return ComplexOfFreeModules(
        matrixSpace,
        IntDegreeGroup,
        "FreeResolution(Z/$order)",
        getModule,
        getDifferential,
    )
}

class ComplexOfFreeModulesTest : FreeSpec({
    tags(moduleTag)

    "test with free resolution of Z/2 over rational" - {
        val complex = freeResolutionOverCyclicGroup(2, SparseMatrixSpaceOverRational)

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-10..10).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
    }
})

package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.AlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.vectsp.LinearMap
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testWithCyclicGroupQuot(
    targetOrder: Int,
    orderFactor: Int,
    matrixSpace: MatrixSpace<S, V, M>,
    maxDeg: Int = 10,
) = freeSpec {
    val field = matrixSpace.field
    val p = field.characteristic

    val sourceOrder = targetOrder * orderFactor
    val sourceGroup = CyclicGroup(sourceOrder)
    val sourceAlgebra = MonoidRing(sourceGroup, matrixSpace)
    val sourceResol = freeResolutionOverCyclicGroup(sourceAlgebra, matrixSpace)

    val targetGroup = CyclicGroup(targetOrder)
    val targetAlgebra = MonoidRing(targetGroup, matrixSpace)
    val targetResol = freeResolutionOverCyclicGroup(targetAlgebra, matrixSpace)

    val groupMap = run {
        val values = sourceGroup.elements.map { cyclicGroupElement ->
            CyclicGroupElement(
                value = cyclicGroupElement.value % targetOrder,
                order = targetOrder,
            )
        }
        FiniteMonoidMap(sourceGroup, targetGroup, values)
    }
    val algebraMap = AlgebraMap.fromFiniteMonoidMap(
        groupMap,
        matrixSpace,
        source = sourceAlgebra,
        target = targetAlgebra,
    )

    val weightedNorm = targetAlgebra.context.run {
        val s = targetAlgebra.getBasis()[1]
        (0 until targetOrder).map { i ->
            s.pow(i * orderFactor)
        }.sum()
    }

    val chainMap = ChainMapAlongAlgebraMap(
        source = sourceResol,
        target = targetResol,
        algebraMap = algebraMap,
        name = "f",
    ) { degree: IntDegree ->
        val sourceModule = sourceResol.getModule(degree)
        val targetModule = targetResol.getModule(degree)
        val underlyingLinearMap = LinearMap.fromVectors(
            source = sourceModule.underlyingVectorSpace,
            target = targetModule.underlyingVectorSpace,
            matrixSpace = matrixSpace,
            vectors = sourceModule.underlyingVectorSpace.getBasis().indices.map { index ->
                val vector = targetModule.underlyingVectorSpace.getBasis()[index % targetOrder]
                targetModule.context.run {
                    weightedNorm * vector
                }
            }
        )
        ModuleMapAlongAlgebraMap(
            source = sourceModule,
            target = targetModule,
            algebraMap = algebraMap,
            underlyingLinearMap = underlyingLinearMap,
        )
    }

    "test ChainMapAlongAlgebraMap for the group hom Z/$sourceOrder → Z/$targetOrder over $field" - {
        "it should induce isomorphism on homology at degree 0" {
            chainMap.underlyingDGLinearMap.inducedMapOnCohomology[0].isIsomorphism().shouldBeTrue()
        }

        "test invoke" {
            (-maxDeg..0).forAll { n ->
                val x = sourceResol.underlyingDGVectorSpace.getBasis(n)[0]
                chainMap(x) shouldBe targetResol.underlyingDGVectorSpace.context.run {
                    val y = targetResol.underlyingDGVectorSpace.getBasis(n)[0]
                    val degree = IntDegree(n)
                    GVector(
                        vector = targetResol.getModule(degree).context.run {
                            weightedNorm * y.vector
                        },
                        degree = y.degree,
                        gVectorSpace = targetResol.underlyingDGVectorSpace,
                    )
                }
            }
        }
    }
}

class ChainMapAlongAlgebraMapTest : FreeSpec({
    tags(chainMapTag)

    include(testWithCyclicGroupQuot(targetOrder = 2, orderFactor = 3, SparseMatrixSpaceOverF3))
    include(testWithCyclicGroupQuot(targetOrder = 2, orderFactor = 3, SparseMatrixSpaceOverF2))
})

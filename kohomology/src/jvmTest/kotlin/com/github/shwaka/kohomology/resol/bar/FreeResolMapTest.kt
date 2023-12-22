package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.AlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.MonoidRing
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.util.isPrime
import com.github.shwaka.kohomology.vectsp.LinearMap
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testWithCyclicGroupQuot(
    targetOrder: Int,
    orderFactor: Int,
    matrixSpace: MatrixSpace<S, V, M>,
    maxDeg: Int = 10,
) = freeSpec {
    val field = matrixSpace.field
    val p = field.characteristic
    require(p.isPrime())
    require(targetOrder % p == 0)
    require(maxDeg > 0)

    val sourceOrder = targetOrder * orderFactor
    val sourceGroup = CyclicGroup(sourceOrder)
    val sourceAlgebra = MonoidRing(sourceGroup, matrixSpace)
    val sourceResol = FreeResol(sourceAlgebra)

    val targetGroup = CyclicGroup(targetOrder)
    val targetAlgebra = MonoidRing(targetGroup, matrixSpace)
    val targetResol = FreeResol(targetAlgebra)

    val groupMap = sourceGroup.getMonoidMap(targetGroup, targetGroup.elements[1])
    val algebraMap = AlgebraMap.fromFiniteMonoidMap(
        groupMap,
        matrixSpace,
        source = sourceAlgebra,
        target = targetAlgebra,
    )

    val moduleMap = ModuleMapAlongAlgebraMap(
        source = sourceResol.module,
        target = targetResol.module,
        algebraMap = algebraMap,
        underlyingLinearMap = LinearMap.fromVectors(
            source = sourceResol.module.underlyingVectorSpace,
            target = targetResol.module.underlyingVectorSpace,
            matrixSpace = matrixSpace,
            vectors = targetResol.module.underlyingVectorSpace.getBasis(),
        )
    )

    val freeResolMap = FreeResolMap(
        source = sourceResol,
        target = targetResol,
        moduleMap = moduleMap,
    )

    "test with Z/$sourceOrder→Z/$targetOrder over $field" - {
        "freeResolMap should be isomorphic at cohomology of degree 0" {
            freeResolMap.underlyingDGLinearMap.inducedMapOnCohomology[0].isIsomorphism().shouldBeTrue()
        }

        "freeResolMap should be isomorphic at cohomology of degree 1" {
            freeResolMap.underlyingDGLinearMap.inducedMapOnCohomology[1].isIsomorphism().shouldBeTrue()
        }

        "freeResolMap should be isomorphic or zero at cohomology of negative degree" {
            (-maxDeg until -1).forAll { degree ->
                if (orderFactor % p == 0) {
                    freeResolMap.underlyingDGLinearMap.inducedMapOnCohomology[degree].isZero().shouldBeTrue()
                } else {
                    freeResolMap.underlyingDGLinearMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
                }
            }
        }
    }
}

class FreeResolMapTest : FreeSpec({
    tags(moduleTag, freeResolTag)

    include(testWithCyclicGroupQuot(targetOrder = 2, orderFactor = 3, matrixSpace = SparseMatrixSpaceOverF2))
    include(testWithCyclicGroupQuot(targetOrder = 2, orderFactor = 2, matrixSpace = SparseMatrixSpaceOverF2))
    include(testWithCyclicGroupQuot(targetOrder = 3, orderFactor = 3, matrixSpace = SparseMatrixSpaceOverF3))
    include(testWithCyclicGroupQuot(targetOrder = 3, orderFactor = 2, matrixSpace = SparseMatrixSpaceOverF3))
    include(testWithCyclicGroupQuot(targetOrder = 3, orderFactor = 5, matrixSpace = SparseMatrixSpaceOverF3))
})

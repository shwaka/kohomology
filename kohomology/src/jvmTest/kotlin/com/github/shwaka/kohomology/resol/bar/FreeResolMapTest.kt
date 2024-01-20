package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRingMap
import com.github.shwaka.kohomology.resol.module.TrivialModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.util.isPrime
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
    val targetGroup = CyclicGroup(targetOrder)

    val groupMap = sourceGroup.getMonoidMap(targetGroup, targetGroup.elements[1])
    val algebraMap = MonoidRingMap(groupMap, matrixSpace)
    val moduleMap = TrivialModuleMapAlongAlgebraMap.baseField(algebraMap)

    val sourceResol = FreeResol(algebraMap.source, module = moduleMap.source)
    val targetResol = FreeResol(algebraMap.target, module = moduleMap.target)
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

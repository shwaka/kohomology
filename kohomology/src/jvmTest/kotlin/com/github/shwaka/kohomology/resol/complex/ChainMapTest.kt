package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue

val chainMapTag = NamedTag("ChainMap")

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testWithScalarMultiplication(
    order: Int,
    scalar: Int,
    matrixSpace: MatrixSpace<S, V, M>,
    maxDeg: Int = 10,
) = freeSpec {
    val field = matrixSpace.field
    val p = field.characteristic
    val freeResol = freeResolutionOverCyclicGroup(order, matrixSpace)
    val chainMap = ChainMap(
        source = freeResol,
        target = freeResol,
        name = "f",
    ) { degree: IntDegree ->
        val module = freeResol.getModule(degree)
        val vectors = module.underlyingVectorSpace.context.run {
            module.underlyingVectorSpace.getBasis().map {
                scalar * it
            }
        }
        ModuleMap.fromVectors(
            source = module,
            target = module,
            vectors = vectors,
        )
    }

    "test multiplication by $scalar on the free resolution over $field[Z/$order]" {
        (-maxDeg..maxDeg).forAll { n ->
            val degree = IntDegree(n)
            if (scalar % p == 0) {
                chainMap.getModuleMap(degree).underlyingLinearMap.isZero().shouldBeTrue()
            } else {
                chainMap.getModuleMap(degree).underlyingLinearMap.isIsomorphism().shouldBeTrue()
            }
        }
    }
}

class ChainMapTest : FreeSpec({
    tags(chainMapTag)

    include(testWithScalarMultiplication(order = 2, scalar = 3, matrixSpace = SparseMatrixSpaceOverF2))
    include(testWithScalarMultiplication(order = 3, scalar = 3, matrixSpace = SparseMatrixSpaceOverF3))
})

package com.github.shwaka.kohomology.resol

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorSpace
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.LinearMap

public fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> barComplex(
    finiteMonoid: FiniteMonoid<E>,
    matrixSpace: MatrixSpace<S, V, M>,
): DGVectorSpace<IntDegree, BarBasisName<E>, S, V, M> {
    val name = "B$finiteMonoid"
    val gVectorSpace = GVectorSpace.fromBasisNames(matrixSpace.numVectorSpace, name) { degree ->
        if (degree > 0) {
            emptyList()
        } else {
            finiteMonoid.getAllBarBasisName(-degree)
        }
    }
    val differential = GLinearMap(
        source = gVectorSpace,
        target = gVectorSpace,
        degree = 1,
        matrixSpace = matrixSpace,
        name = "d",
    ) { degree: IntDegree ->
        if (degree.value >= 0) {
            LinearMap.getZero(
                source = gVectorSpace[degree],
                target = gVectorSpace[degree.value + 1],
                matrixSpace = matrixSpace,
            )
        } else {
            val targetVectorSpace = gVectorSpace[degree.value + 1]
            val n: Int = -degree.value
            val vectors = gVectorSpace[degree].basisNames.map { barBasisName ->
                targetVectorSpace.context.run {
                    (0..n).map { i ->
                        targetVectorSpace.fromBasisName(barBasisName.boundary(i)) * Sign.fromParity(i)
                    }.sum()
                }
            }
            LinearMap.fromVectors(
                source = gVectorSpace[degree],
                target = targetVectorSpace,
                matrixSpace = matrixSpace,
                vectors = vectors
            )
        }
    }
    return DGVectorSpace(gVectorSpace, differential)
}

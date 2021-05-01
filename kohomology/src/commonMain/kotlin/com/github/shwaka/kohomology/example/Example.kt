package com.github.shwaka.kohomology.example

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.GeneralizedIndeterminate
import com.github.shwaka.kohomology.free.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> sphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<StringIndeterminateName, IntDegree, S, V, M> {
    if (dim <= 0)
        throw IllegalArgumentException("The dimension of a sphere must be positive")
    return if (dim % 2 == 1)
        oddSphere(matrixSpace, dim)
    else
        evenSphere(matrixSpace, dim)
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> oddSphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<StringIndeterminateName, IntDegree, S, V, M> {
    if (dim % 2 == 0)
        throw Exception("This can't happen!")
    val indeterminateList = listOf(
        GeneralizedIndeterminate("x", dim),
    )
    return FreeDGAlgebra(matrixSpace, indeterminateList) {
        listOf(zeroGVector)
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> evenSphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<StringIndeterminateName, IntDegree, S, V, M> {
    if (dim % 2 == 1)
        throw Exception("This can't happen!")
    val indeterminateList = listOf(
        GeneralizedIndeterminate("x", dim),
        GeneralizedIndeterminate("y", 2 * dim - 1)
    )
    return FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> complexProjectiveSpace(
    matrixSpace: MatrixSpace<S, V, M>,
    n: Int
): FreeDGAlgebra<StringIndeterminateName, IntDegree, S, V, M> {
    if (n <= 0)
        throw IllegalArgumentException("The complex dimension n of CP^n must be positive")
    val indeterminateList = listOf(
        GeneralizedIndeterminate("c", 2),
        GeneralizedIndeterminate("x", 2 * n + 1)
    )
    return FreeDGAlgebra(matrixSpace, indeterminateList) { (c, _) ->
        listOf(zeroGVector, c.pow(n + 1))
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pullbackOfHopfFibrationOverS4(
    matrixSpace: MatrixSpace<S, V, M>,
): FreeDGAlgebra<StringIndeterminateName, IntDegree, S, V, M> {
    val indeterminateList = listOf(
        GeneralizedIndeterminate("a", 2),
        GeneralizedIndeterminate("b", 2),
        GeneralizedIndeterminate("x", 3),
        GeneralizedIndeterminate("y", 3),
        GeneralizedIndeterminate("z", 3),
    )
    return FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, _, _, _) ->
        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))
    }
}

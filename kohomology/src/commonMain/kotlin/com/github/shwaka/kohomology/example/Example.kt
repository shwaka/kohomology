package com.github.shwaka.kohomology.example

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.free.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> sphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<StringIndeterminateName, S, V, M> {
    if (dim <= 0)
        throw IllegalArgumentException("The dimension of a sphere must be positive")
    if (dim % 2 == 1)
        TODO("Odd dimensional sphere is not yet implemented")
    else
        return evenSphere(matrixSpace, dim)
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> evenSphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<StringIndeterminateName, S, V, M> {
    if (dim % 2 == 1)
        throw Exception("This can't happen!")
    val indeterminateList = listOf(
        Indeterminate("x", dim),
        Indeterminate("y", 2 * dim - 1)
    )
    return FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }
}

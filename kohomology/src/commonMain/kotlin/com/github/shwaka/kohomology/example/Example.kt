package com.github.shwaka.kohomology.example

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> sphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
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
): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
    if (dim % 2 == 0)
        throw Exception("This can't happen!")
    val indeterminateList = listOf(
        Indeterminate("x", dim),
    )
    return FreeDGAlgebra.fromList(matrixSpace, indeterminateList) {
        listOf(zeroGVector)
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> evenSphere(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
    if (dim % 2 == 1)
        throw Exception("This can't happen!")
    val indeterminateList = listOf(
        Indeterminate("x", dim),
        Indeterminate("y", 2 * dim - 1)
    )
    return FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> sphereWithMultiDegree(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<MultiDegree, StringIndeterminateName, S, V, M> {
    if (dim <= 0)
        throw IllegalArgumentException("The dimension of a sphere must be positive")
    return if (dim % 2 == 1)
        oddSphereWithMultiDegree(matrixSpace, dim)
    else
        evenSphereWithMultiDegree(matrixSpace, dim)
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> oddSphereWithMultiDegree(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<MultiDegree, StringIndeterminateName, S, V, M> {
    if (dim % 2 == 0)
        throw Exception("This can't happen!")
    val degreeGroup = MultiDegreeGroup(
        listOf(
            DegreeIndeterminate("N", dim / 2),
        )
    )
    val (n) = degreeGroup.generatorList
    val indeterminateList = degreeGroup.context.run {
        listOf(
            Indeterminate("x", 2 * n + 1),
        )
    }
    return FreeDGAlgebra.fromList(matrixSpace, degreeGroup, indeterminateList) { (_) ->
        listOf(zeroGVector)
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> evenSphereWithMultiDegree(
    matrixSpace: MatrixSpace<S, V, M>,
    dim: Int
): FreeDGAlgebra<MultiDegree, StringIndeterminateName, S, V, M> {
    if (dim % 2 == 1)
        throw Exception("This can't happen!")
    val degreeGroup = MultiDegreeGroup(
        listOf(
            DegreeIndeterminate("N", dim / 2),
        )
    )
    val (n) = degreeGroup.generatorList
    val indeterminateList = degreeGroup.context.run {
        listOf(
            Indeterminate("x", 2 * n),
            Indeterminate("y", 4 * n - 1)
        )
    }
    return FreeDGAlgebra.fromList(matrixSpace, degreeGroup, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> complexProjectiveSpace(
    matrixSpace: MatrixSpace<S, V, M>,
    n: Int
): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
    if (n <= 0)
        throw IllegalArgumentException("The complex dimension n of CP^n must be positive")
    val indeterminateList = listOf(
        Indeterminate("c", 2),
        Indeterminate("x", 2 * n + 1)
    )
    return FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (c, _) ->
        listOf(zeroGVector, c.pow(n + 1))
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> complexProjectiveSpaceWithMultiDegree(
    matrixSpace: MatrixSpace<S, V, M>,
    n: Int
): FreeDGAlgebra<MultiDegree, StringIndeterminateName, S, V, M> {
    if (n <= 0)
        throw IllegalArgumentException("The complex dimension n of CP^n must be positive")
    val degreeGroup = MultiDegreeGroup(
        listOf(
            DegreeIndeterminate("K", 1),
        )
    )
    val (k) = degreeGroup.generatorList
    val indeterminateList = degreeGroup.context.run {
        listOf(
            Indeterminate("c", 2 * k),
            Indeterminate("x", 2 * k * (n + 1) - 1)
        )
    }
    return FreeDGAlgebra.fromList(matrixSpace, degreeGroup, indeterminateList) { (c, _) ->
        listOf(zeroGVector, c.pow(n + 1))
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pullbackOfHopfFibrationOverS4(
    matrixSpace: MatrixSpace<S, V, M>,
): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
    val indeterminateList = listOf(
        Indeterminate("a", 2),
        Indeterminate("b", 2),
        Indeterminate("x", 3),
        Indeterminate("y", 3),
        Indeterminate("z", 3),
    )
    return FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (a, b, _, _, _) ->
        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pullbackOfHopfFibrationOverS4WithMultiDegree(
    matrixSpace: MatrixSpace<S, V, M>,
): FreeDGAlgebra<MultiDegree, StringIndeterminateName, S, V, M> {
    val degreeGroup = MultiDegreeGroup(
        listOf(
            DegreeIndeterminate("N", 1),
            DegreeIndeterminate("M", 1),
        )
    )
    val (n, m) = degreeGroup.generatorList
    val indeterminateList = degreeGroup.context.run {
        listOf(
            Indeterminate("a", 2 * n),
            Indeterminate("b", 2 * m),
            Indeterminate("x", 4 * n - 1),
            Indeterminate("y", 2 * n + 2 * m - 1),
            Indeterminate("z", 4 * m - 1),
        )
    }
    return FreeDGAlgebra.fromList(matrixSpace, degreeGroup, indeterminateList) { (a, b, _, _, _) ->
        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))
    }
}

package com.github.shwaka.kohomology.profile.executable

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreeLoopSpace

class CohomologyOfFreeLoopSpace<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val degreeLimit: Int,
) : Executable() {
    override val description = "cohomology of free loop space of 2-sphere"
    override fun mainFun(): String {
        val sphereDim = 2
        val sphere = sphere(this.matrixSpace, sphereDim)
        val freeLoopSpace = FreeLoopSpace(sphere)

        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }
}

class CohomologyOfFreeLoopSpaceWithMultiDegree<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val degreeLimit: Int,
) : Executable() {
    override val description: String = "cohomology of free loop space of 2n-sphere (with MultiDegree)"
    override fun mainFun(): String {
        val degreeIndeterminateList = listOf(
            DegreeIndeterminate("n", 1),
        )
        val degreeMonoid = MultiDegreeGroup(degreeIndeterminateList)
        val (n) = degreeMonoid.generatorList
        val indeterminateList = degreeMonoid.context.run {
            listOf(
                Indeterminate("x", 2 * n),
                Indeterminate("y", 4 * n - 1)
            )
        }
        val sphere = FreeDGAlgebra.fromList(this.matrixSpace, degreeMonoid, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += freeLoopSpace.cohomology.getBasisForAugmentedDegree(degree).toString() + "\n"
        }
        return result
    }
}

class CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val degreeLimit: Int,
) : Executable() {
    override val description: String = "cohomology of free loop space of 2n-sphere (with MultiDegree and FreeLoopSpace.withShiftDegree)"
    override fun mainFun(): String {
        val degreeIndeterminateList = listOf(
            DegreeIndeterminate("n", 1),
        )
        val degreeMonoid = MultiDegreeGroup(degreeIndeterminateList)
        val (n) = degreeMonoid.generatorList
        val indeterminateList = degreeMonoid.context.run {
            listOf(
                Indeterminate("x", 2 * n),
                Indeterminate("y", 4 * n - 1)
            )
        }
        val sphere = FreeDGAlgebra.fromList(this.matrixSpace, degreeMonoid, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace.withShiftDegree(sphere)
        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += freeLoopSpace.cohomology.getBasisForAugmentedDegree(degree).toString() + "\n"
        }
        return result
    }
}

package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.example.pullbackOfHopfFibrationOverS4
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.model.FreePathSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

abstract class Executable {
    abstract val description: String
    protected open fun setupFun() {}
    private var setupFinished = false
    fun setup() {
        if (this.setupFinished)
            return
        this.setupFun()
        this.setupFinished = true
    }
    protected abstract fun mainFun(): String
    fun main(): String {
        this.setup()
        return this.mainFun()
    }
}

class CohomologyOfFreeLoopSpace(private val degreeLimit: Int) : Executable() {
    override val description = "cohomology of free loop space of 2-sphere"
    override fun mainFun(): String {
        val sphereDim = 2
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = sphere(matrixSpace, sphereDim)
        val freeLoopSpace = FreeLoopSpace(sphere)

        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }
}

class CohomologyOfFreeLoopSpaceWithMultiDegree(
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
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, degreeMonoid, indeterminateList) { (x, _) ->
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

class CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(
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
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, degreeMonoid, indeterminateList) { (x, _) ->
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

class IsomorphismToCohomologyOfFreePathSpace(val n: Int, val degreeLimit: Int) : Executable() {
    override val description: String = "cohomology of the free path space of CP^n"
    override fun mainFun(): String {
        val indeterminateList = listOf(
            Indeterminate("c", 2),
            Indeterminate("x", 2 * this.n + 1)
        )
        val sphere = FreeDGAlgebra(SparseMatrixSpaceOverBigRational, indeterminateList) { (c, _) ->
            listOf(zeroGVector, c.pow(this@IsomorphismToCohomologyOfFreePathSpace.n + 1))
        }
        val freePathSpace = FreePathSpace(sphere)

        val cohomologyInclusion1 = freePathSpace.inclusion1.inducedMapOnCohomology()
        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += cohomologyInclusion1[degree].isIsomorphism().toString()
        }
        return result
    }
}

class ComputeRowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>
) : Executable() {
    override val description: String = "compute row echelon form"

    private var matrix: M? = null

    override fun setupFun() {
        // val sphereDim = 2
        // val freeDGAlgebra = sphere(this.matrixSpace, sphereDim)
        val freeDGAlgebra = pullbackOfHopfFibrationOverS4(this.matrixSpace)
        val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
        val degree = 15
        this.matrix = freeLoopSpace.differential[degree].matrix
    }

    override fun mainFun(): String {
        return this.matrixSpace.context.run {
            println(this@ComputeRowEchelonForm.matrix?.let { "${it.rowCount}, ${it.colCount}" })
            this@ComputeRowEchelonForm.matrix?.rowEchelonForm?.reducedMatrix.toString()
        }
    }
}

package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.LinearDegreeMonoid
import com.github.shwaka.kohomology.example.pullbackOfHopfFibrationOverS4
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.GeneralizedIndeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    val executableList: List<Executable> = listOf(
        CohomologyOfFreeLoopSpace(150),
        CohomologyOfFreeLoopSpaceWithLinearDegree,
        ComputeRowEchelonForm(SparseMatrixSpaceOverBigRational)
    )
    println("Select script to profile: (default = 0)")
    executableList.mapIndexed { index, script ->
        println("$index: ${script.description}")
    }
    val index: Int = readLine()?.toIntOrNull() ?: 0
    val executable = executableList[index]
    println("Selected $index: ${executable.description}")
    executable.setup()
    print("Press ENTER to continue!!!")
    readLine()
    executable.main()
}

abstract class Executable {
    abstract val description: String
    protected open fun setupFun() {}
    var setupFinished = false
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

class CohomologyOfFreeLoopSpace(val degreeLimit: Int) : Executable() {
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

object CohomologyOfFreeLoopSpaceWithLinearDegree : Executable() {
    override val description: String = "cohomology of free loop space of 2n-sphere (with LinearDegree)"
    override fun mainFun(): String {
        val degreeIndeterminateList = listOf(
            DegreeIndeterminate("n", 1),
        )
        val degreeMonoid = LinearDegreeMonoid(degreeIndeterminateList)
        val (n) = degreeMonoid.generatorList
        val indeterminateList = degreeMonoid.context.run {
            listOf(
                GeneralizedIndeterminate("x", 2 * n),
                GeneralizedIndeterminate("y", 4 * n - 1)
            )
        }
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, degreeMonoid, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        var result = ""
        for (degree in 0 until 150) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        val limit = 200
        degreeMonoid.context.run {
            for (i in 1 until limit) {
                val degree = i * (2 * n - 1)
                result += freeLoopSpace.cohomology[degree].toString() + "\n"
            }
            for (i in 0 until limit) {
                val degree = 2 * n + i * (2 * n - 1)
                result += freeLoopSpace.cohomology[degree].toString() + "\n"
            }
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

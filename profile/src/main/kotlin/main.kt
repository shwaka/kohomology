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
    val scriptList: List<ProfiledScript> = listOf(
        CohomologyOfFreeLoopSpace,
        CohomologyOfFreeLoopSpaceWithLinearDegree,
        ComputeRowEchelonForm(SparseMatrixSpaceOverBigRational)
    )
    println("Select script to profile: (default = 0)")
    scriptList.mapIndexed { index, script ->
        println("$index: ${script.description}")
    }
    val index: Int = readLine()?.toIntOrNull() ?: 0
    val script = scriptList[index]
    println("Selected $index: ${script.description}")
    script.setup()
    print("Press ENTER to continue!!!")
    readLine()
    script.main()
}

interface ProfiledScript {
    val description: String
    fun setup() {}
    fun main(): String
}

object CohomologyOfFreeLoopSpace : ProfiledScript {
    override val description = "cohomology of free loop space of 2-sphere"
    override fun main(): String {
        val sphereDim = 2
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = sphere(matrixSpace, sphereDim)
        val freeLoopSpace = FreeLoopSpace(sphere)

        var result = ""
        for (degree in 0 until 150) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }
}

object CohomologyOfFreeLoopSpaceWithLinearDegree : ProfiledScript {
    override val description: String = "cohomology of free loop space of 2n-sphere (with LinearDegree)"
    override fun main(): String {
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
) : ProfiledScript {
    override val description: String = "compute row echelon form"

    private var matrix: M? = null

    override fun setup() {
        // val sphereDim = 2
        // val freeDGAlgebra = sphere(this.matrixSpace, sphereDim)
        val freeDGAlgebra = pullbackOfHopfFibrationOverS4(this.matrixSpace)
        val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
        val degree = 15
        this.matrix = freeLoopSpace.differential[degree].matrix
    }

    override fun main(): String {
        return this.matrixSpace.context.run {
            println(this@ComputeRowEchelonForm.matrix?.let { "${it.rowCount}, ${it.colCount}" })
            this@ComputeRowEchelonForm.matrix?.rowEchelonForm?.reducedMatrix.toString()
        }
    }
}

package com.github.shwaka.kohomology.profile.executable

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreeLoopSpace

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> arkowitzLupton(
    matrixSpace: MatrixSpace<S, V, M>,
): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
    val indeterminateList = listOf(
        Indeterminate("x1", 10),
        Indeterminate("x2", 12),
        Indeterminate("y1", 41),
        Indeterminate("y2", 43),
        Indeterminate("y3", 45),
        Indeterminate("z", 119),
    )
    return FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x1, x2, y1, y2, y3) ->
        listOf(
            zeroGVector, // d(x1)
            zeroGVector, // d(x2)
            x1.pow(3) * x2, // d(y1)
            x1.pow(2) * x2.pow(2), // d(y2)
            x1 * x2.pow(3), // d(y3)
            x2 * (y1 * x2 - x1 * y2) * (y2 * x2 - x1 * y3) + x1.pow(12) + x2.pow(10), // d(z)
        )
    }
}

class CohomologyOfFreeLoopSpaceOfArkowitzLupton<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val degreeLimit: Int,
) : Executable() {
    override val description = "H^*(L(Arkowitz-Lupton)) for *<$degreeLimit"
    override val filename: String = "cohom-LAL-$degreeLimit"
    override fun mainFun(): String {
        val freeDGAlgebra = arkowitzLupton(this.matrixSpace)
        val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)

        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }
}

class CohomologyOfFreeLoopSpaceOfArkowitzLuptonWithShiftDegree<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val degreeLimit: Int,
    label: String? = null,
) : Executable() {
    private val labelText = label?.let { " [$it]" } ?: ""
    private val filenameSuffix = label?.let { "-$it" } ?: ""
    override val description = "H^*(L(Arkowitz-Lupton)) for *<$degreeLimit (with FreeLoopSpace.withShiftDegree)$labelText"
    override val filename: String = "cohom-LAL-$degreeLimit-shift$filenameSuffix"
    override fun mainFun(): String {
        val freeDGAlgebra = arkowitzLupton(this.matrixSpace)
        val freeLoopSpace = FreeLoopSpace.withShiftDegree(freeDGAlgebra)

        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += freeLoopSpace.cohomology.getBasisForAugmentedDegree(degree).toString() + "\n"
        }
        return result
    }
}

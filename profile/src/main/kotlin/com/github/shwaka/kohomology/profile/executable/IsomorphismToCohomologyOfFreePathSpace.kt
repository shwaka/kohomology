package com.github.shwaka.kohomology.profile.executable

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreePathSpace

class IsomorphismToCohomologyOfFreePathSpace<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    val n: Int,
    val degreeLimit: Int,
) : Executable() {
    override val description: String = "cohomology of the free path space of CP^n"
    override fun mainFun(): String {
        val indeterminateList = listOf(
            Indeterminate("c", 2),
            Indeterminate("x", 2 * this.n + 1)
        )
        val sphere = FreeDGAlgebra.fromList(this.matrixSpace, indeterminateList) { (c, _) ->
            listOf(zeroGVector, c.pow(this@IsomorphismToCohomologyOfFreePathSpace.n + 1))
        }
        val freePathSpace = FreePathSpace(sphere)

        val cohomologyInclusion1 = freePathSpace.inclusion1.inducedMapOnCohomology
        var result = ""
        for (degree in 0 until this.degreeLimit) {
            result += cohomologyInclusion1[degree].isIsomorphism().toString()
        }
        return result
    }
}

package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.DualBasisName
import com.github.shwaka.kohomology.vectsp.DualVectorSpace
import com.github.shwaka.kohomology.vectsp.dual

public class DualGVectorContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, DualBasisName<B>, S, V>,
) : GVectorContext<D, DualBasisName<B>, S, V>(scalarOperations, numVectorOperations, gVectorOperations) {
    public operator fun GVector<D, DualBasisName<B>, S, V>.invoke(gVector: GVector<D, B, S, V>): S {
        val minusGVectorDegree = this.gVectorSpace.degreeGroup.context.run {
            -gVector.degree
        }
        return if (this.degree == minusGVectorDegree) {
            this.gVectorSpace[this.degree].context.run {
                TODO()
                // this@invoke.vector(gVector.vector)
            }
        } else {
            this.gVectorSpace.field.zero
        }
    }
}

public class DualGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    public val originalGVectorSpace: GVectorSpace<D, B, S, V>,
) : GVectorSpace<D, DualBasisName<B>, S, V>(
    numVectorSpace = originalGVectorSpace.numVectorSpace,
    degreeGroup = originalGVectorSpace.degreeGroup,
    name = "${originalGVectorSpace.name}^*",
    getInternalPrintConfig = { printConfig ->
        originalGVectorSpace.getInternalPrintConfig(printConfig).dual(printConfig)
    },
    listDegreesForAugmentedDegree = originalGVectorSpace.listDegreesForAugmentedDegree,
    getVectorSpace = { degree ->
        val minusDegree = originalGVectorSpace.degreeGroup.context.run { -degree }
        DualVectorSpace(originalGVectorSpace[minusDegree])
    },
) {
    override val context: GVectorContext<D, DualBasisName<B>, S, V> by lazy {
        DualGVectorContext(numVectorSpace.field, numVectorSpace, this)
    }
}

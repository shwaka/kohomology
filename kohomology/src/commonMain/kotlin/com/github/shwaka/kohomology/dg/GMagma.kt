package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName

public interface GMagmaOperations<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public fun multiply(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V>
    public fun multiply(a: GVectorOrZero<D, B, S, V>, b: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V>
}

public open class GMagmaContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
) : GVectorContext<D, B, S, V>(scalarOperations, numVectorOperations, gVectorOperations),
    GMagmaOperations<D, B, S, V, M> by gMagmaOperations {
    public operator fun GVector<D, B, S, V>.times(other: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this@GMagmaContext.multiply(this, other)
    }
    public operator fun GVectorOrZero<D, B, S, V>.times(other: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return this@GMagmaContext.multiply(this, other)
    }
}

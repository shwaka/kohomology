package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorContext
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface RightModuleContext<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    VectorContext<B, S, V> {
    public val module: RightModule<BA, B, S, V, M>

    public operator fun Vector<B, S, V>.times(other: Vector<BA, S, V>): Vector<B, S, V> {
        return this@RightModuleContext.module.act(this, other)
    }

    public companion object {
        public operator fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            module: RightModule<BA, B, S, V, M>,
        ): RightModuleContext<BA, B, S, V, M> {
            return RightModuleContextImpl(module)
        }
    }
}

private class RightModuleContextImpl<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val module: RightModule<BA, B, S, V, M>,
) : RightModuleContext<BA, B, S, V, M>,
    VectorContext<B, S, V> by VectorContext(module.underlyingVectorSpace)

public interface RightModule<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val underlyingVectorSpace: VectorSpace<B, S, V>
    public val coeffAlgebra: Algebra<BA, S, V, M>
    public val context: RightModuleContext<BA, B, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
    public val action: BilinearMap<B, BA, B, S, V, M>

    public fun act(a: Vector<B, S, V>, b: Vector<BA, S, V>): Vector<B, S, V> {
        return this.action(a, b)
    }

    public companion object {
        public operator fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            underlyingVectorSpace: VectorSpace<B, S, V>,
            coeffAlgebra: Algebra<BA, S, V, M>,
            action: BilinearMap<B, BA, B, S, V, M>,
        ): RightModule<BA, B, S, V, M> {
            return RightModuleImpl(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
        }
    }
}

private class RightModuleImpl<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val underlyingVectorSpace: VectorSpace<B, S, V>,
    override val coeffAlgebra: Algebra<BA, S, V, M>,
    override val action: BilinearMap<B, BA, B, S, V, M>,
) : RightModule<BA, B, S, V, M> {
    override val context: RightModuleContext<BA, B, S, V, M> = RightModuleContext(this)
}

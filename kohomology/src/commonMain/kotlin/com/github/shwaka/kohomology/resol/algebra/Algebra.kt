package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorContext
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface AlgebraContext<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    VectorContext<B, S, V> {
    public val algebra: Algebra<B, S, V, M>
    public val unit: Vector<B, S, V>
        get() = this.algebra.unit

    public operator fun Vector<B, S, V>.times(other: Vector<B, S, V>): Vector<B, S, V> {
        return this@AlgebraContext.algebra.multiply(this, other)
    }

    public fun Vector<B, S, V>.pow(exponent: Int): Vector<B, S, V> {
        val unit = this@AlgebraContext.algebra.unit
        return when {
            exponent == 0 -> unit
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else unit
                half * half * rem
            }
            exponent < 0 -> throw ArithmeticException("Negative power in an algebra is not defined")
            else -> throw Exception("This can't happen!")
        }
    }

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            algebra: Algebra<B, S, V, M>
        ): AlgebraContext<B, S, V, M> {
            return AlgebraContextImpl(algebra)
        }
    }
}

private class AlgebraContextImpl<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val algebra: Algebra<B, S, V, M>,
) : AlgebraContext<B, S, V, M>,
    VectorContext<B, S, V> by VectorContext(algebra)

public interface Algebra<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    VectorSpace<B, S, V> {
    public val unit: Vector<B, S, V>
    public val isCommutative: Boolean
    public override val context: AlgebraContext<B, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
    public val multiplication: BilinearMap<B, B, B, S, V, M>

    public fun multiply(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V> {
        return this.multiplication(a, b)
    }

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            vectorSpace: VectorSpace<B, S, V>,
            multiplication: BilinearMap<B, B, B, S, V, M>,
            unit: Vector<B, S, V>,
            isCommutative: Boolean = false,
        ): Algebra<B, S, V, M> {
            return AlgebraImpl(matrixSpace, vectorSpace, multiplication, unit, isCommutative)
        }
    }
}

private class AlgebraImpl<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    vectorSpace: VectorSpace<B, S, V>,
    override val multiplication: BilinearMap<B, B, B, S, V, M>,
    override val unit: Vector<B, S, V>,
    override val isCommutative: Boolean,
) : Algebra<B, S, V, M>,
    VectorSpace<B, S, V> by vectorSpace {
    override val context: AlgebraContext<B, S, V, M> = AlgebraContext(this)
}

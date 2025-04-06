package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public data class FieldProductBasis(val index: Int) : BasisName {
    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.TEX -> "e_${this.index}"
            else -> "e${this.index}"
        }
    }
}

public interface FieldProduct<
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Algebra<FieldProductBasis, S, V, M> {

    public companion object {
        public operator fun <
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            dim: Int,
            matrixSpace: MatrixSpace<S, V, M>,
        ): Algebra<FieldProductBasis, S, V, M> {
            return FieldProductImpl(dim, matrixSpace)
        }
    }
}

private class FieldProductImpl<
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    dim: Int,
    override val matrixSpace: MatrixSpace<S, V, M>,
) : Algebra<FieldProductBasis, S, V, M> {
    override val context: AlgebraContext<FieldProductBasis, S, V, M> by lazy {
        AlgebraContext(this)
    }
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<FieldProductBasis, S>
        get() = { InternalPrintConfig.default(it) }
    override val numVectorSpace: NumVectorSpace<S, V> = matrixSpace.numVectorSpace

    override val basisNames: List<FieldProductBasis> = (0 until dim).map { FieldProductBasis(it) }
    override fun indexOf(basisName: FieldProductBasis): Int {
        return this.basisNames.indexOf(basisName)
    }

    override val unit: Vector<FieldProductBasis, S, V> by lazy {
        this.context.run {
            this@FieldProductImpl.basisNames.map {
                this@FieldProductImpl.fromBasisName(it)
            }.sum()
        }
    }
    override val isCommutative: Boolean = true
    override val multiplication: BilinearMap<FieldProductBasis, FieldProductBasis, FieldProductBasis, S, V, M> by lazy {
        val values = this.basisNames.map { basisName1 ->
            this.basisNames.map { basisName2 ->
                if (basisName1 == basisName2) {
                    this.fromBasisName(basisName1)
                } else {
                    this.zeroVector
                }
            }
        }
        ValueBilinearMap(
            source1 = this,
            source2 = this,
            target = this,
            matrixSpace = matrixSpace,
            values = values,
        )
    }

    override fun toString(): String {
        return "${this.matrixSpace.field}^${this.dim}"
    }
}

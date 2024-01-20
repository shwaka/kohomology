package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public object TrivialAlgebraBasisName : BasisName {
    override fun toString(): String {
        return "1"
    }

    override fun toString(printConfig: PrintConfig): String {
        return "1"
    }
}

public class TrivialAlgebra<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
) : Algebra<TrivialAlgebraBasisName, S, V, M> {
    override val context: AlgebraContext<TrivialAlgebraBasisName, S, V, M> = AlgebraContext(this)
    override val basisNames: List<TrivialAlgebraBasisName> = listOf(TrivialAlgebraBasisName)
    override val numVectorSpace: NumVectorSpace<S, V>
        get() = matrixSpace.numVectorSpace
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<TrivialAlgebraBasisName, S>
        get() = { InternalPrintConfig.default(it) }

    override fun indexOf(basisName: TrivialAlgebraBasisName): Int {
        return this.basisNames.indexOf(basisName)
    }

    override val unit: Vector<TrivialAlgebraBasisName, S, V> = this.fromBasisName(TrivialAlgebraBasisName)
    override val isCommutative: Boolean
        get() = true
    override val multiplication: BilinearMap<TrivialAlgebraBasisName, TrivialAlgebraBasisName, TrivialAlgebraBasisName, S, V, M> = run {
        val values = listOf(listOf(this.unit))
        ValueBilinearMap(
            source1 = this,
            source2 = this,
            target = this,
            matrixSpace = matrixSpace,
            values = values,
        )
    }

    override fun toString(): String {
        return this.matrixSpace.field.toString()
    }
}

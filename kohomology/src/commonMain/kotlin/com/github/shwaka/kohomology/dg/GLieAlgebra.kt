package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace

// no additional operations for Lie algebra (magma is enough)

public interface GLieAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagmaContext<D, B, S, V, M> {
    public val gLieAlgebra: GLieAlgebra<D, B, S, V, M>

    public fun ad(gVector: GVector<D, B, S, V>): LieDerivation<D, B, S, V, M> {
        val matrixSpace = this.gLieAlgebra.matrixSpace
        val name = "ad($gVector)"
        return LieDerivation.fromGVectors(this.gLieAlgebra, gVector.degree, matrixSpace, name) { degree ->
            this.gLieAlgebra.getBasis(degree).map { basis -> gVector * basis }
        }
    }
}
public open class GLieAlgebraContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val gLieAlgebra: GLieAlgebra<D, B, S, V, M>,
) : GLieAlgebraContext<D, B, S, V, M>,
    GMagmaContext<D, B, S, V, M> by GMagmaContextImpl(gLieAlgebra)

public interface GLieAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagma<D, B, S, V, M> {
    override val context: GLieAlgebraContext<D, B, S, V, M>
    public val underlyingGLieAlgebra: GLieAlgebra<D, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getVectorSpace: (D) -> VectorSpace<B, S, V>,
            getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S> = { InternalPrintConfig.default(it) },
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
        ): GLieAlgebra<D, B, S, V, M> {
            val gVectorSpace = GVectorSpace(
                matrixSpace.numVectorSpace,
                degreeGroup,
                name,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
                getVectorSpace
            )
            val bilinearMapName = "LieBracket($name)"
            val multiplication = GBilinearMap(matrixSpace, gVectorSpace, gVectorSpace, gVectorSpace, 0, bilinearMapName) { p, q -> getMultiplication(p, q) }
            return GLieAlgebraImpl(matrixSpace, gVectorSpace, multiplication)
        }
    }
}

internal class GLieAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val multiplication: GBilinearMap<B, B, B, D, S, V, M>,
) : GLieAlgebra<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {
    override val context: GLieAlgebraContext<D, B, S, V, M> = GLieAlgebraContextImpl(this)
    override val underlyingGLieAlgebra: GLieAlgebra<D, B, S, V, M> = this
}

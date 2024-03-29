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

public interface GMagmaContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorContext<D, B, S, V> {
    public val gMagma: GMagma<D, B, S, V, M>

    public operator fun GVector<D, B, S, V>.times(other: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this@GMagmaContext.gMagma.multiply(this, other)
    }
    public operator fun GVectorOrZero<D, B, S, V>.times(other: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return this@GMagmaContext.gMagma.multiply(this, other)
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gMagma: GMagma<D, B, S, V, M>,
        ): GMagmaContext<D, B, S, V, M> {
            return GMagmaContextImpl(gMagma)
        }
    }
}

private class GMagmaContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val gMagma: GMagma<D, B, S, V, M>,
) : GMagmaContext<D, B, S, V, M>,
    GVectorContext<D, B, S, V> by GVectorContext(gMagma)

public interface GMagma<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorSpace<D, B, S, V> {
    public override val context: GMagmaContext<D, B, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
    public val multiplication: GBilinearMap<B, B, B, D, S, V, M>

    public fun multiply(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this.multiplication(a, b)
    }

    public fun multiply(a: GVectorOrZero<D, B, S, V>, b: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return when (a) {
            is ZeroGVector -> this.zeroGVector
            is GVector -> when (b) {
                is ZeroGVector -> this.zeroGVector
                is GVector -> this.multiply(a, b)
            }
        }
    }

    public fun isBasis(
        gVectorList: List<GVector<D, B, S, V>>,
        degree: D,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }

    public fun isBasis(
        gVectorList: List<GVector<D, B, S, V>>,
        degree: Int,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }

    public fun getIdentity(): GLinearMap<D, B, B, S, V, M> {
        return GLinearMap(this, this, this.degreeGroup.zero, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }

    /**
     * Returns a [GLinearMap] which multiplies [gVector] from left.
     */
    public fun leftMultiplication(gVector: GVector<D, B, S, V>): GLinearMap<D, B, B, S, V, M> {
        return GLinearMap.fromGVectors(
            this,
            this,
            gVector.degree,
            this.matrixSpace,
            "($gVector * (-))"
        ) { degree ->
            this.context.run {
                this@GMagma.getBasis(degree).map { gVector * it }
            }
        }
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            gVectorSpace: GVectorSpace<D, B, S, V>,
            multiplication: GBilinearMap<B, B, B, D, S, V, M>,
        ): GMagma<D, B, S, V, M> {
            return GMagmaImpl(matrixSpace, gVectorSpace, multiplication)
        }

        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getVectorSpace: (D) -> VectorSpace<B, S, V>,
            getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
            boundedness: Boundedness = Boundedness(),
        ): GMagma<D, B, S, V, M> {
            val gVectorSpace = GVectorSpace(
                matrixSpace.numVectorSpace,
                degreeGroup,
                name,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
                boundedness,
                getVectorSpace
            )
            val bilinearMapName = "Multiplication($name)"
            val multiplication = GBilinearMap(matrixSpace, gVectorSpace, gVectorSpace, gVectorSpace, 0, bilinearMapName) { p, q -> getMultiplication(p, q) }
            return GMagmaImpl(matrixSpace, gVectorSpace, multiplication)
        }
    }
}

private class GMagmaImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val multiplication: GBilinearMap<B, B, B, D, S, V, M>,
) : GMagma<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {
    override val context: GMagmaContext<D, B, S, V, M> = GMagmaContext(this)

    override val underlyingGVectorSpace: GVectorSpace<D, B, S, V> = gVectorSpace.underlyingGVectorSpace
}

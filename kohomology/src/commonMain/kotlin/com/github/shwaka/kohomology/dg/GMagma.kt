package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.VectorSpace

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

public open class GMagma<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val matrixSpace: MatrixSpace<S, V, M>,
    degreeGroup: DegreeGroup<D>,
    name: String,
    getVectorSpace: (D) -> VectorSpace<B, S, V>,
    public val getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
    listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
) : GVectorSpace<D, B, S, V>(matrixSpace.numVectorSpace, degreeGroup, name, getInternalPrintConfig, listDegreesForAugmentedDegree, getVectorSpace),
    GMagmaOperations<D, B, S, V, M> {
    public override val context: GMagmaContext<D, B, S, V, M> by lazy {
        // use 'lazy' to avoid the following warning:
        //   Leaking 'this' in constructor of non-final class GAlgebra
        GMagmaContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this)
    }

    private val multiplication: GBilinearMap<B, B, B, D, S, V, M> by lazy {
        val bilinearMapName = "Multiplication(${this.name})"
        GBilinearMap(this, this, this, 0, bilinearMapName) { p, q -> getMultiplication(p, q) }
    }
    override fun multiply(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this.multiplication(a, b)
    }

    override fun multiply(a: GVectorOrZero<D, B, S, V>, b: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
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

    public open fun getId(): GLinearMap<D, B, B, S, V, M> {
        return GLinearMap(this, this, this.degreeGroup.zero, this.matrixSpace, "id") { degree ->
            this[degree].getId(this.matrixSpace)
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
}

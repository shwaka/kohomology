package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.util.directProductOf
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.LazyBilinearMap
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public data class FreeModuleBasisName<BA : BasisName, BV : BasisName>(
    val algebraBasisName: BA,
    val generatingBasisName: BV,
) : BasisName {
    override fun toString(): String {
        return "${this.algebraBasisName}*${this.generatingBasisName}"
    }
}

public interface FreeModule<
    BA : BasisName,
    BV : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Module<BA, FreeModuleBasisName<BA, BV>, S, V, M> {

    public val generatingBasisNames: List<BV>
    public val tensorWithBaseField: VectorSpace<BV, S, V>
    public val projection: LinearMap<FreeModuleBasisName<BA, BV>, BV, S, V, M>
    public val inclusion: LinearMap<BV, FreeModuleBasisName<BA, BV>, S, V, M>
    public fun fromGeneratingBasisName(generatingBasisName: BV): Vector<FreeModuleBasisName<BA, BV>, S, V>
    public fun getGeneratingBasis(): List<Vector<FreeModuleBasisName<BA, BV>, S, V>>

    public val rank: Int
        get() = generatingBasisNames.size

    public companion object {
        public operator fun <BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            coeffAlgebra: Algebra<BA, S, V, M>,
            generatingBasisNames: List<BV>,
        ): FreeModule<BA, BV, S, V, M> {
            return FreeModuleImpl(coeffAlgebra, generatingBasisNames)
        }
    }
}

private class FreeModuleImpl<BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val coeffAlgebra: Algebra<BA, S, V, M>,
    override val generatingBasisNames: List<BV>,
) : FreeModule<BA, BV, S, V, M> {
    override val underlyingVectorSpace: VectorSpace<FreeModuleBasisName<BA, BV>, S, V> by lazy {
        val basisNames = directProductOf(
            this.coeffAlgebra.basisNames,
            generatingBasisNames
        ).map { (algebraBasisName, generatingBasisName) ->
            FreeModuleBasisName(algebraBasisName, generatingBasisName)
        }
        VectorSpace(
            this.coeffAlgebra.numVectorSpace,
            basisNames
        )
    }
    override val context: ModuleContext<BA, FreeModuleBasisName<BA, BV>, S, V, M> = ModuleContext(this)
    override val matrixSpace: MatrixSpace<S, V, M> = coeffAlgebra.matrixSpace
    override val action: BilinearMap<BA, FreeModuleBasisName<BA, BV>, FreeModuleBasisName<BA, BV>, S, V, M> by lazy {
        LazyBilinearMap(
            source1 = this.coeffAlgebra,
            source2 = this.underlyingVectorSpace,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
        ) { algebraBasisName, freeModuleBasisName ->
            val coeff: Vector<BA, S, V> = this.coeffAlgebra.multiplication(
                this.coeffAlgebra.fromBasisName(algebraBasisName),
                this.coeffAlgebra.fromBasisName(freeModuleBasisName.algebraBasisName),
            )
            this.fromGeneratingBasisNameWithCoeff(coeff, freeModuleBasisName.generatingBasisName)
        }
    }

    override val tensorWithBaseField: VectorSpace<BV, S, V> by lazy {
        require(this.coeffAlgebra is MonoidRing<*, *, *, *>) {
            "FreeModule.tensorWithBaseField can be applied only for FreeModule over MonoidRing"
        }
        VectorSpace(
            numVectorSpace = this.matrixSpace.numVectorSpace,
            basisNames = this.generatingBasisNames,
        )
    }

    override val projection: LinearMap<FreeModuleBasisName<BA, BV>, BV, S, V, M> by lazy {
        val vectors = this.underlyingVectorSpace.basisNames.map { freeModuleBasisName ->
            this.tensorWithBaseField.fromBasisName(freeModuleBasisName.generatingBasisName)
        }
        LinearMap.fromVectors(
            source = this.underlyingVectorSpace,
            target = this.tensorWithBaseField,
            matrixSpace = this.matrixSpace,
            vectors = vectors,
        )
    }

    override val inclusion: LinearMap<BV, FreeModuleBasisName<BA, BV>, S, V, M> by lazy {
        LinearMap.fromVectors(
            source = this.tensorWithBaseField,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
            vectors = this.getGeneratingBasis(),
        )
    }

    private fun fromGeneratingBasisNameWithCoeff(
        coeff: Vector<BA, S, V>,
        generatingBasisName: BV,
    ): Vector<FreeModuleBasisName<BA, BV>, S, V> {
        val basisMap = coeff.toBasisMap().mapKeys { (algebraBasisName, _) ->
            FreeModuleBasisName(algebraBasisName, generatingBasisName)
        }
        return this.underlyingVectorSpace.fromBasisMap(basisMap)
    }

    override fun fromGeneratingBasisName(generatingBasisName: BV): Vector<FreeModuleBasisName<BA, BV>, S, V> {
        return this.fromGeneratingBasisNameWithCoeff(
            coeff = this.coeffAlgebra.unit,
            generatingBasisName = generatingBasisName,
        )
    }

    override fun getGeneratingBasis(): List<Vector<FreeModuleBasisName<BA, BV>, S, V>> {
        return this.generatingBasisNames.map { generatingBasisName ->
            this.fromGeneratingBasisName(generatingBasisName)
        }
    }
}

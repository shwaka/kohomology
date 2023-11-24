package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.directProductOf
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.LazyBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public data class FreeModuleBasis<BA : BasisName, BV : BasisName>(
    val algebraBasisName: BA,
    val generatingBasisName: BV,
) : BasisName

public class FreeModule<BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val coeffAlgebra: Algebra<BA, S, V, M>,
    public val generatingBasisNames: List<BV>,
) : Module<BA, FreeModuleBasis<BA, BV>, S, V, M> {
    override val underlyingVectorSpace: VectorSpace<FreeModuleBasis<BA, BV>, S, V> by lazy {
        val basisNames = directProductOf(
            this.coeffAlgebra.basisNames,
            generatingBasisNames
        ).map { (algebraBasisName, generatingBasisName) ->
            FreeModuleBasis(algebraBasisName, generatingBasisName)
        }
        VectorSpace(
            this.coeffAlgebra.numVectorSpace,
            basisNames
        )
    }
    override val context: ModuleContext<BA, FreeModuleBasis<BA, BV>, S, V, M> = ModuleContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = coeffAlgebra.matrixSpace
    override val action: BilinearMap<BA, FreeModuleBasis<BA, BV>, FreeModuleBasis<BA, BV>, S, V, M> by lazy {
        LazyBilinearMap(
            source1 = this.coeffAlgebra,
            source2 = this.underlyingVectorSpace,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
        ) { algebraBasisName, freeModuleBasis ->
            val coefficient: Vector<BA, S, V> = this.coeffAlgebra.multiplication(
                this.coeffAlgebra.fromBasisName(algebraBasisName),
                this.coeffAlgebra.fromBasisName(freeModuleBasis.algebraBasisName),
            )
            this.fromGeneratingBasisNameWithCoeff(coefficient, freeModuleBasis.generatingBasisName)
        }
    }

    private fun fromGeneratingBasisNameWithCoeff(
        coefficient: Vector<BA, S, V>,
        generatingBasisName: BV,
    ): Vector<FreeModuleBasis<BA, BV>, S, V> {
        val basisMap = coefficient.toBasisMap().mapKeys { (algebraBasisName, _) ->
            FreeModuleBasis(algebraBasisName, generatingBasisName)
        }
        return this.underlyingVectorSpace.fromBasisMap(basisMap)
    }

    public fun fromGeneratingBasisName(generatingBasisName: BV): Vector<FreeModuleBasis<BA, BV>, S, V> {
        return this.fromGeneratingBasisNameWithCoeff(
            coefficient = this.coeffAlgebra.unit,
            generatingBasisName = generatingBasisName,
        )
    }

    public fun getGeneratingBasis(): List<Vector<FreeModuleBasis<BA, BV>, S, V>> {
        return this.generatingBasisNames.map { generatingBasisName ->
            this.fromGeneratingBasisName(generatingBasisName)
        }
    }
}

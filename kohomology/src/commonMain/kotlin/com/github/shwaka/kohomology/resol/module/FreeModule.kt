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
    override val coefficientAlgebra: Algebra<BA, S, V, M>,
    public val generatingBasisNames: List<BV>,
) : Module<BA, FreeModuleBasis<BA, BV>, S, V, M> {
    override val underlyingVectorSpace: VectorSpace<FreeModuleBasis<BA, BV>, S, V> by lazy {
        val basisNames = directProductOf(
            this.coefficientAlgebra.basisNames,
            generatingBasisNames
        ).map { (algebraBasisName, generatingBasisName) ->
            FreeModuleBasis(algebraBasisName, generatingBasisName)
        }
        VectorSpace(
            this.coefficientAlgebra.numVectorSpace,
            basisNames
        )
    }
    override val context: ModuleContext<BA, FreeModuleBasis<BA, BV>, S, V, M> = ModuleContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = coefficientAlgebra.matrixSpace
    override val action: BilinearMap<BA, FreeModuleBasis<BA, BV>, FreeModuleBasis<BA, BV>, S, V, M> by lazy {
        LazyBilinearMap(
            source1 = this.coefficientAlgebra,
            source2 = this.underlyingVectorSpace,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
        ) { algebraBasisName, freeModuleBasis ->
            val coefficient: Vector<BA, S, V> = this.coefficientAlgebra.multiplication(
                this.coefficientAlgebra.fromBasisName(algebraBasisName),
                this.coefficientAlgebra.fromBasisName(freeModuleBasis.algebraBasisName),
            )
            val basisMap = coefficient.toBasisMap().mapKeys { (multipliedAlgebraBasisName, _) ->
                FreeModuleBasis(multipliedAlgebraBasisName, freeModuleBasis.generatingBasisName)
            }
            this.underlyingVectorSpace.fromBasisMap(basisMap)
        }
    }
}
package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.QuotBasis
import com.github.shwaka.kohomology.vectsp.QuotVectorSpace
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap

public interface QuotModuleAlongAlgebraMap<
    BAS : BasisName,
    BAT : BasisName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Module<BAT, QuotBasis<B, S, V>, S, V, M> {

    override val underlyingVectorSpace: QuotVectorSpace<B, S, V, M>
    public val totalModule: Module<BAS, B, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>
    public val projection: ModuleMapAlongAlgebraMap<BAS, BAT, B, QuotBasis<B, S, V>, S, V, M>
    public val section: LinearMap<QuotBasis<B, S, V>, B, S, V, M>

    public companion object {
        public operator fun <
            BAS : BasisName,
            BAT : BasisName,
            B : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            totalModule: Module<BAS, B, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
        ): QuotModuleAlongAlgebraMap<BAS, BAT, B, S, V, M> {
            return QuotModuleAlongAlgebraMapImpl(totalModule, algebraMap)
        }
    }
}

private class QuotModuleAlongAlgebraMapImpl<
    BAS : BasisName,
    BAT : BasisName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val totalModule: Module<BAS, B, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
) : QuotModuleAlongAlgebraMap<BAS, BAT, B, S, V, M> {
    init {
        require(algebraMap.underlyingLinearMap.isSurjective()) {
            "algebraMap $algebraMap must be surjective to define QuotModuleAlongAlgebraMap"
        }
    }

    override val context: ModuleContext<BAT, QuotBasis<B, S, V>, S, V, M> = ModuleContext(this)
    override val matrixSpace: MatrixSpace<S, V, M> = totalModule.matrixSpace
    override val coeffAlgebra: Algebra<BAT, S, V, M> = algebraMap.target
    override val underlyingVectorSpace: QuotVectorSpace<B, S, V, M> by lazy {
        val ideal = this.algebraMap.kernel()
        val subModule = SubModule(this.totalModule, ideal)
        QuotVectorSpace(
            matrixSpace = this.matrixSpace,
            totalVectorSpace = this.totalModule.underlyingVectorSpace,
            quotientGenerator = subModule.underlyingVectorSpace,
        )
    }
    override val action: BilinearMap<BAT, QuotBasis<B, S, V>, QuotBasis<B, S, V>, S, V, M> by lazy {
        val section = this.algebraMap.section()
        ValueBilinearMap(
            source1 = this.coeffAlgebra,
            source2 = this.underlyingVectorSpace,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
        ) { algebraBasisName, quotBasisName ->
            val targetAlgebraElement = this.algebraMap.target.fromBasisName(algebraBasisName)
            val sourceAlgebraElement = section(targetAlgebraElement)
            val quotVector = this.underlyingVectorSpace.fromBasisName(quotBasisName)
            val totalVector = this.underlyingVectorSpace.section(quotVector)
            val multipliedVector = this.totalModule.context.run {
                sourceAlgebraElement * totalVector
            }
            this.underlyingVectorSpace.projection(multipliedVector)
        }
    }

    override val projection: ModuleMapAlongAlgebraMap<BAS, BAT, B, QuotBasis<B, S, V>, S, V, M> by lazy {
        ModuleMapAlongAlgebraMap(
            source = this.totalModule,
            target = this,
            algebraMap = this.algebraMap,
            underlyingLinearMap = this.underlyingVectorSpace.projection,
        )
    }
    override val section: LinearMap<QuotBasis<B, S, V>, B, S, V, M>
        get() = underlyingVectorSpace.section
}

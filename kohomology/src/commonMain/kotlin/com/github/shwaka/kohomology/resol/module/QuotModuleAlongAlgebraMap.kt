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
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface QuotModuleAlongAlgebraMap<
    BAS : BasisName,
    BAT : BasisName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Module<BAT, QuotBasis<B, S, V>, S, V, M> {

    public val totalModule: Module<BAS, B, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>
    public val projection: ModuleMapAlongAlgebraMap<BAS, BAT, B, QuotBasis<B, S, V>, S, V, M>
    public val section: LinearMap<QuotBasis<B, S, V>, B, S, V, M>
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
    override val underlyingVectorSpace: VectorSpace<QuotBasis<B, S, V>, S, V>
        get() = TODO("Not yet implemented")
    override val action: BilinearMap<BAT, QuotBasis<B, S, V>, QuotBasis<B, S, V>, S, V, M>
        get() = TODO("Not yet implemented")

    override val projection: ModuleMapAlongAlgebraMap<BAS, BAT, B, QuotBasis<B, S, V>, S, V, M>
        get() = TODO("Not yet implemented")
    override val section: LinearMap<QuotBasis<B, S, V>, B, S, V, M>
        get() = TODO("Not yet implemented")
}

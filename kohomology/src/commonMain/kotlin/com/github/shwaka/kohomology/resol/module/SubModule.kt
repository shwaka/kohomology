package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.SubBasis
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.asSubVectorSpace

public interface SubModule<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    Module<BA, SubBasis<B, S, V>, S, V, M> {
    public val totalModule: Module<BA, B, S, V, M>
    public val generatorOverCoeff: List<Vector<B, S, V>>
    public val inclusion: ModuleMap<BA, SubBasis<B, S, V>, B, S, V, M>
    public val retraction: ModuleMap<BA, B, SubBasis<B, S, V>, S, V, M>
    public fun subspaceContains(vector: Vector<B, S, V>): Boolean

    override val underlyingVectorSpace: SubVectorSpace<B, S, V, M>

    public companion object {
        public operator fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            totalModule: Module<BA, B, S, V, M>,
            generatorOverCoeff: List<Vector<B, S, V>>,
        ): SubModule<BA, B, S, V, M> {
            return SubModuleImpl(totalModule, generatorOverCoeff)
        }
    }
}

private class SubModuleImpl<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val totalModule: Module<BA, B, S, V, M>,
    override val generatorOverCoeff: List<Vector<B, S, V>>,
) : SubModule<BA, B, S, V, M> {
    override val underlyingVectorSpace: SubVectorSpace<B, S, V, M> by lazy {
        this.totalModule.action.image(
            source1Sub = this.totalModule.coeffAlgebra.asSubVectorSpace(this.matrixSpace),
            source2Sub = SubVectorSpace(
                matrixSpace = this.matrixSpace,
                totalVectorSpace = this.totalModule.underlyingVectorSpace,
                generator = this.generatorOverCoeff,
            ),
        )
    }

    override val coeffAlgebra: Algebra<BA, S, V, M>
        get() = totalModule.coeffAlgebra
    override val context: ModuleContext<BA, SubBasis<B, S, V>, S, V, M> = ModuleContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M>
        get() = totalModule.matrixSpace
    override val action: BilinearMap<BA, SubBasis<B, S, V>, SubBasis<B, S, V>, S, V, M> by lazy {
        this.totalModule.action.induce(
            source2Sub = this.underlyingVectorSpace,
            targetSub = this.underlyingVectorSpace,
        )
    }

    override val inclusion: ModuleMap<BA, SubBasis<B, S, V>, B, S, V, M> by lazy {
        val underlyingLinearMap = this.underlyingVectorSpace.inclusion
        ModuleMap(
            source = this,
            target = this.totalModule,
            underlyingLinearMap = underlyingLinearMap,
        )
    }

    override val retraction: ModuleMap<BA, B, SubBasis<B, S, V>, S, V, M> by lazy {
        val underlyingLinearMap = this.underlyingVectorSpace.retraction
        ModuleMap(
            source = this.totalModule,
            target = this,
            underlyingLinearMap = underlyingLinearMap,
        )
    }

    override fun subspaceContains(vector: Vector<B, S, V>): Boolean {
        return this.underlyingVectorSpace.subspaceContains(vector)
    }
}

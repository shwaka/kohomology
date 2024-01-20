package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.resol.module.RestrictedModule
import com.github.shwaka.kohomology.resol.module.RestrictedModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface RestrictedComplex<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Complex<D, BAS, B, S, V, M> {

    public val originalComplex: Complex<D, BAT, B, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>

    public companion object {
        public operator fun <
            D : Degree,
            BAS : BasisName,
            BAT : BasisName,
            B : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            originalComplex: Complex<D, BAT, B, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
        ): RestrictedComplex<D, BAS, BAT, B, S, V, M> {
            return RestrictedComplexImpl(originalComplex, algebraMap)
        }
    }
}

private class RestrictedComplexImpl<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val originalComplex: Complex<D, BAT, B, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
) : RestrictedComplex<D, BAS, BAT, B, S, V, M> {
    override val degreeGroup: DegreeGroup<D> = originalComplex.degreeGroup
    override val matrixSpace: MatrixSpace<S, V, M> = originalComplex.matrixSpace
    override val name: String = "Res(${originalComplex.name})"

    override fun getModule(degree: D): RestrictedModule<BAS, BAT, B, S, V, M> {
        val originalModule = this.originalComplex.getModule(degree)
        return RestrictedModule(originalModule, this.algebraMap)
    }

    override fun getDifferential(degree: D): ModuleMap<BAS, B, B, S, V, M> {
        val source = this.getModule(degree)
        val target = this.getModule(this.degreeGroup.context.run { degree + 1 })
        val originalDifferential = this.originalComplex.getDifferential(degree)
        return RestrictedModuleMap(
            source = source,
            target = target,
            originalModuleMap = originalDifferential,
            algebraMap = this.algebraMap,
        )
    }

    override val underlyingDGVectorSpace: DGVectorSpace<D, B, S, V, M>
        get() = originalComplex.underlyingDGVectorSpace
}

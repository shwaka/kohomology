package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface Complex<
    D : Degree,
    BA : BasisName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>> {

    public val matrixSpace: MatrixSpace<S, V, M>
    public val degreeGroup: DegreeGroup<D>
    public val name: String

    public fun getModule(degree: D): Module<BA, B, S, V, M>
    public fun getDifferential(degree: D): ModuleMap<BA, B, B, S, V, M>

    public fun getModule(degree: Int): Module<BA, B, S, V, M> {
        return this.getModule(this.degreeGroup.fromInt(degree))
    }
    public fun getDifferential(degree: Int): ModuleMap<BA, B, B, S, V, M> {
        return this.getDifferential(this.degreeGroup.fromInt(degree))
    }

    public val underlyingDGVectorSpace: DGVectorSpace<D, B, S, V, M>
}

package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMap<
    D : Degree,
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>> {

    public val source: Complex<D, BA, BS, S, V, M>
    public val target: Complex<D, BA, BT, S, V, M>

    public fun getModuleMap(degree: D): ModuleMap<BA, BS, BT, S, V, M>

    public val underlyingDGLinearMap: DGLinearMap<D, BS, BT, S, V, M>

    public fun invoke(gVector: GVector<D, BS, S, V>): GVector<D, BT, S, V> {
        return this.underlyingDGLinearMap(gVector)
    }
}

package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMapFromFreeComplex<
    D : Degree,
    BA : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ChainMap<D, BA, FreeModuleBasisName<BA, BVS>, BT, S, V, M> {

    override val source: FreeComplex<D, BA, BVS, S, V, M>
}

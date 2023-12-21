package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMapAlongAlgebraMapFromFreeComplex<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ChainMapAlongAlgebraMap<D, BAS, BAT, FreeModuleBasisName<BAS, BVS>, BT, S, V, M> {

    override val source: FreeComplex<D, BAS, BVS, S, V, M>
}

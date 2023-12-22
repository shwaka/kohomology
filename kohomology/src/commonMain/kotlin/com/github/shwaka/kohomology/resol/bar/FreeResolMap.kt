package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.complex.FreeChainMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName

private class FreeResolMapFactory<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    val source: FreeResol<BAS, BS, S, V, M>,
    val target: FreeResol<BAT, BT, S, V, M>,
    val moduleMap: ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>,
) {
    init {
        require(source.module == moduleMap.source) {
            "Invalid arguments for FreeResolMap: source.module and moduleMap.source are different"
        }
        require(target.module == moduleMap.target) {
            "Invalid arguments for FreeResolMap: target.module and moduleMap.target are different"
        }
    }

    val freeChainMap: FreeChainMapAlongAlgebraMap<
        IntDegree,
        BAS,
        BAT,
        FreeResolBasisName,
        FreeResolBasisName,
        S,
        V,
        M> = TODO()
}

public class FreeResolMap<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > private constructor(
    private val freeResolMapFactory: FreeResolMapFactory<BAS, BAT, BS, BT, S, V, M>
) : FreeChainMapAlongAlgebraMap<
    IntDegree,
    BAS,
    BAT,
    FreeResolBasisName,
    FreeResolBasisName,
    S,
    V,
    M> by freeResolMapFactory.freeChainMap {
    public constructor(
        source: FreeResol<BAS, BS, S, V, M>,
        target: FreeResol<BAT, BT, S, V, M>,
        moduleMap: ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>,
    ) : this(
        FreeResolMapFactory(source, target, moduleMap)
    )
}

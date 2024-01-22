package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface TensorProductBasisName<BR : BasisName, BL : BasisName> : BasisName

public interface TensorProductOverAlgebra<
    BA : BasisName,
    BR : BasisName,
    BL : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : VectorSpace<TensorProductBasisName<BR, BL>, S, V> {

    public val rightModule: Module<BA, BR, S, V, M>
    public val leftModule: Module<BA, BL, S, V, M>
}

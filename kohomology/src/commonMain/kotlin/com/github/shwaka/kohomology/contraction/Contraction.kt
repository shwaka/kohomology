package com.github.shwaka.kohomology.contraction

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public interface Contraction<D : Degree, BR : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val retractDGVectorSpace: DGVectorSpace<D, BR, S, V, M>
    public val totalDGVectorSpace: DGVectorSpace<D, BT, S, V, M>

    public val inclusion: DGLinearMap<D, BR, BT, S, V, M>
    public val retraction: DGLinearMap<D, BT, BR, S, V, M>
    public val homotopy: GLinearMap<D, BT, BT, S, V, M>
}

package com.github.shwaka.kohomology.contraction

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public class CohomologyContraction<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val totalDGVectorSpace: DGVectorSpace<D, B, S, V, M>
) : Contraction<D, SubQuotBasis<B, S, V>, B, S, V, M> {
    override val matrixSpace: MatrixSpace<S, V, M>
        get() = totalDGVectorSpace.matrixSpace

    override val retractDGVectorSpace: DGVectorSpace<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        DGVectorSpace.fromGVectorSpace(
            matrixSpace = this.matrixSpace,
            gVectorSpace = totalDGVectorSpace.cohomology,
        )
    }

    override val inclusion: DGLinearMap<D, SubQuotBasis<B, S, V>, B, S, V, M>
        get() = TODO("Not yet implemented")

    override val retraction: DGLinearMap<D, B, SubQuotBasis<B, S, V>, S, V, M>
        get() = TODO("Not yet implemented")

    override val homotopy: GLinearMap<D, B, B, S, V, M>
        get() = TODO("Not yet implemented")
}

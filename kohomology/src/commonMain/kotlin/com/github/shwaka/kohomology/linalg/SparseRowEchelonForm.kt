package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign

class SparseRowEchelonForm<S : Scalar>(
    private val matrixSpace: SparseMatrixSpace<S>,
    private val originalMatrix: SparseMatrix<S>
) : RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>> {
    override val matrix: SparseMatrix<S>
        get() = TODO("Not yet implemented")
    override val pivots: List<Int>
        get() = TODO("Not yet implemented")
    override val sign: Sign
        get() = TODO("Not yet implemented")
    override val reducedMatrix: SparseMatrix<S>
        get() = TODO("Not yet implemented")
}

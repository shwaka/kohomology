package com.github.shwaka.kohomology.linalg

data class SparseMatrix<S : Scalar>(
    override val numVectorSpace: SparseNumVectorSpace<S>,
    val values: Map<Int, Map<Int, S>>,
    override val rowCount: Int,
    override val colCount: Int,
) : Matrix<S, SparseNumVector<S>> {
    init {
        // TODO: check that each index is smaller than rowCount or colCount
    }

    override fun toPrettyString(): String {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun get(rowInd: Int, colInd: Int): S {
        TODO("Not yet implemented")
    }
}

class SparseMatrixSpace<S : Scalar>(
    override val numVectorSpace: SparseNumVectorSpace<S>
) : MatrixSpace<S, SparseNumVector<S>, SparseMatrix<S>> {
    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<SparseNumVectorSpace<*>, SparseMatrixSpace<*>> = mutableMapOf()
        fun <S : Scalar> from(numVectorSpace: SparseNumVectorSpace<S>): SparseMatrixSpace<S> {
            if (this.cache.containsKey(numVectorSpace)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[numVectorSpace] as SparseMatrixSpace<S>
            } else {
                val matrixSpace = SparseMatrixSpace(numVectorSpace)
                this.cache[numVectorSpace] = matrixSpace
                return matrixSpace
            }
        }
    }

    override val field: Field<S> = this.numVectorSpace.field

    override val context = MatrixContext(this.field, this.numVectorSpace, this)

    override fun contains(matrix: SparseMatrix<S>): Boolean {
        return matrix.numVectorSpace == this.numVectorSpace
    }

    override fun add(first: SparseMatrix<S>, second: SparseMatrix<S>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun subtract(first: SparseMatrix<S>, second: SparseMatrix<S>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(first: SparseMatrix<S>, second: SparseMatrix<S>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(matrix: SparseMatrix<S>, scalar: S): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(matrix: SparseMatrix<S>, numVector: SparseNumVector<S>): SparseNumVector<S> {
        TODO("Not yet implemented")
    }

    override fun computeRowEchelonForm(matrix: SparseMatrix<S>): RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>> {
        TODO("Not yet implemented")
    }

    override fun computeTranspose(matrix: SparseMatrix<S>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeInnerProduct(
        matrix: SparseMatrix<S>,
        numVector1: SparseNumVector<S>,
        numVector2: SparseNumVector<S>
    ): S {
        TODO("Not yet implemented")
    }

    override fun fromRows(rows: List<List<S>>, colCount: Int?): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun fromCols(cols: List<List<S>>, rowCount: Int?): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun joinMatrices(matrixList: List<SparseMatrix<S>>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeRowSlice(matrix: SparseMatrix<S>, rowRange: IntRange): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeColSlice(matrix: SparseMatrix<S>, colRange: IntRange): SparseMatrix<S> {
        TODO("Not yet implemented")
    }
}

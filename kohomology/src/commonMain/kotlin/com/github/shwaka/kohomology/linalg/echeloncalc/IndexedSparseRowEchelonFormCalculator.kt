package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.cancel.CancellationContext

internal class IndexedSparseRowEchelonFormCalculator<S : Scalar>(
    private val field: Field<S>,
    private val cancellationContext: CancellationContext?,
) : SparseRowEchelonFormCalculator<S> {
    override fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S> {
        return SparseEliminationEngine(
            field = this.field,
            matrix = matrix,
            cancellationContext = this.cancellationContext,
        ).computeRowEchelonForm(colCount)
    }

    override fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>> {
        return SparseEliminationEngine(
            field = this.field,
            matrix = rowEchelonRowMap,
            cancellationContext = this.cancellationContext,
        ).reduce(pivots)
    }
}

package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.cancel.CancellationContext

internal class MarkowitzSparseRowEchelonFormCalculator<S : Scalar>(
    private val field: Field<S>,
    private val cancellationContext: CancellationContext?,
) : TransformTrackingSparseRowEchelonFormCalculator<S> {
    override fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S> {
        return SparseEliminationEngine(
            field = this.field,
            matrix = matrix,
            cancellationContext = this.cancellationContext,
            pivotRowSelection = PivotRowSelection.Markowitz,
        ).computeRowEchelonForm(colCount)
    }

    override fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>> {
        return SparseEliminationEngine(
            field = this.field,
            matrix = rowEchelonRowMap,
            cancellationContext = this.cancellationContext,
        ).reduce(pivots)
    }

    override fun rowEchelonFormWithTransformation(
        matrix: Map<Int, Map<Int, S>>,
        rowCount: Int,
        colCount: Int,
    ): TransformTrackingSparseRowEchelonFormData<S> {
        return SparseEliminationEngine(
            field = this.field,
            matrix = matrix,
            cancellationContext = this.cancellationContext,
            pivotRowSelection = PivotRowSelection.Markowitz,
            transformationRowMap = this.identityRowMap(rowCount),
        ).computeRowEchelonFormWithTransformation(colCount)
    }

    override fun reduceWithTransformation(
        data: TransformTrackingSparseRowEchelonFormData<S>,
    ): TransformTrackingSparseRowEchelonFormData<S> {
        val reducedData = SparseEliminationEngine(
            field = this.field,
            matrix = data.rowMap,
            cancellationContext = this.cancellationContext,
            transformationRowMap = data.transformationRowMap,
        ).reduceWithTransformation(data.pivots)
        return reducedData.copy(exchangeCount = data.exchangeCount)
    }

    private fun identityRowMap(rowCount: Int): Map<Int, Map<Int, S>> {
        return this.field.context.run {
            (0 until rowCount).associateWith { rowIndex ->
                mapOf(rowIndex to one)
            }
        }
    }
}

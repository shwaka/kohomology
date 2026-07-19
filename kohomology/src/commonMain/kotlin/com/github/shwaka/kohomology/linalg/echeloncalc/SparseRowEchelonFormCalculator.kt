package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Scalar

internal data class SparseRowEchelonFormData<S : Scalar>(
    val rowMap: Map<Int, Map<Int, S>>,
    val pivots: List<Int>,
    val exchangeCount: Int,
)

internal data class TransformTrackingSparseRowEchelonFormData<S : Scalar>(
    val rowMap: Map<Int, Map<Int, S>>,
    val pivots: List<Int>,
    val exchangeCount: Int,
    val transformationRowMap: Map<Int, Map<Int, S>>,
)

internal interface SparseRowEchelonFormCalculator<S : Scalar> {
    fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S>
    fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>>
}

internal interface TransformTrackingSparseRowEchelonFormCalculator<S : Scalar> :
    SparseRowEchelonFormCalculator<S> {
    fun rowEchelonFormWithTransformation(
        matrix: Map<Int, Map<Int, S>>,
        rowCount: Int,
        colCount: Int,
    ): TransformTrackingSparseRowEchelonFormData<S>

    fun reduceWithTransformation(
        data: TransformTrackingSparseRowEchelonFormData<S>,
    ): TransformTrackingSparseRowEchelonFormData<S>
}

package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar

internal class AugmentedTransformTrackingSparseRowEchelonFormCalculator<S : Scalar>(
    private val baseCalculator: SparseRowEchelonFormCalculator<S>,
    private val field: Field<S>,
) : TransformTrackingSparseRowEchelonFormCalculator<S> {
    override fun rowEchelonForm(
        matrix: Map<Int, Map<Int, S>>,
        colCount: Int,
    ): SparseRowEchelonFormData<S> {
        return this.baseCalculator.rowEchelonForm(matrix, colCount)
    }

    override fun reduce(
        rowEchelonRowMap: Map<Int, Map<Int, S>>,
        pivots: List<Int>,
    ): Map<Int, Map<Int, S>> {
        return this.baseCalculator.reduce(rowEchelonRowMap, pivots)
    }

    override fun rowEchelonFormWithTransformation(
        matrix: Map<Int, Map<Int, S>>,
        rowCount: Int,
        colCount: Int,
    ): SparseRowEchelonFormData<S> {
        val augmentedRowMap = this.augmentWithIdentity(matrix, rowCount, colCount)
        val augmentedData = this.baseCalculator.rowEchelonForm(augmentedRowMap, colCount)
        return SparseRowEchelonFormData(
            rowMap = augmentedData.rowMap.leftPart(colCount),
            pivots = augmentedData.pivots,
            exchangeCount = augmentedData.exchangeCount,
            transformationRowMap = augmentedData.rowMap.rightPart(colCount),
        )
    }

    override fun reduceWithTransformation(
        data: SparseRowEchelonFormData<S>,
    ): SparseRowEchelonFormData<S> {
        val transformationRowMap = data.transformationRowMap
            ?: error("reduceWithTransformation requires transformationRowMap")
        val reducedRowMap = data.rowMap.toMutableMapDeeply()
        val reducedTransformationRowMap = transformationRowMap.toMutableMapDeeply()
        for ((rowIndex, pivot) in data.pivots.withIndex()) {
            val row = reducedRowMap[rowIndex] ?: throw Exception("This can't happen!")
            val elm = row[pivot] ?: throw Exception("This can't happen!")
            val scalar = this.field.context.run { elm.inv() }
            reducedRowMap.multiplyRow(rowIndex, scalar)
            reducedTransformationRowMap.multiplyRow(rowIndex, scalar)
        }
        for ((rowIndex, pivot) in data.pivots.withIndex()) {
            val mainRow = reducedRowMap[rowIndex] ?: throw Exception("This can't happen!")
            val mainTransformationRow = reducedTransformationRowMap[rowIndex] ?: emptyMap()
            for (targetRowIndex in 0 until rowIndex) {
                val coeff = reducedRowMap[targetRowIndex]?.get(pivot)
                if (coeff != null) {
                    reducedRowMap.subtractMultiple(targetRowIndex, mainRow, coeff)
                    reducedTransformationRowMap.subtractMultiple(targetRowIndex, mainTransformationRow, coeff)
                }
            }
        }
        return SparseRowEchelonFormData(
            rowMap = reducedRowMap,
            pivots = data.pivots,
            exchangeCount = data.exchangeCount,
            transformationRowMap = reducedTransformationRowMap,
        )
    }

    private fun augmentWithIdentity(
        matrix: Map<Int, Map<Int, S>>,
        rowCount: Int,
        colCount: Int,
    ): Map<Int, Map<Int, S>> {
        return this.field.context.run {
            (0 until rowCount).associateWith { rowIndex ->
                val row = matrix[rowIndex] ?: emptyMap()
                row + mapOf((colCount + rowIndex) to one)
            }
        }
    }

    private fun Map<Int, Map<Int, S>>.leftPart(colCount: Int): Map<Int, Map<Int, S>> {
        return this.mapValues { (_, row) ->
            row.filterKeys { colIndex -> colIndex < colCount }
        }.filterValues { row -> row.isNotEmpty() }
    }

    private fun Map<Int, Map<Int, S>>.rightPart(colCount: Int): Map<Int, Map<Int, S>> {
        return this.mapValues { (_, row) ->
            row.filterKeys { colIndex -> colIndex >= colCount }
                .mapKeys { (colIndex, _) -> colIndex - colCount }
        }.filterValues { row -> row.isNotEmpty() }
    }

    private fun Map<Int, Map<Int, S>>.toMutableMapDeeply(): MutableMap<Int, MutableMap<Int, S>> {
        return this.mapValues { (_, row) -> row.toMutableMap() }.toMutableMap()
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.multiplyRow(rowIndex: Int, scalar: S) {
        val row = this[rowIndex] ?: return
        if (scalar.isZero()) {
            this.remove(rowIndex)
            return
        }
        this@AugmentedTransformTrackingSparseRowEchelonFormCalculator.field.context.run {
            val newRow = row.mapValues { (_, value) -> value * scalar }
            this@multiplyRow[rowIndex] = newRow.toMutableMap()
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.subtractMultiple(
        rowIndex: Int,
        other: Map<Int, S>,
        scalar: S,
    ) {
        val row = this.getOrPut(rowIndex) { mutableMapOf() }
        this@AugmentedTransformTrackingSparseRowEchelonFormCalculator.field.context.run {
            for ((colIndex, value) in other) {
                val oldValue = row[colIndex]
                val newValue = when (oldValue) {
                    null ->
                        -value * scalar
                    else ->
                        this@AugmentedTransformTrackingSparseRowEchelonFormCalculator
                            .field
                            .subtractProduct(oldValue, value, scalar)
                }
                if (newValue.isZero()) {
                    row.remove(colIndex)
                } else {
                    row[colIndex] = newValue
                }
            }
        }
        if (row.isEmpty()) {
            this.remove(rowIndex)
        }
    }
}

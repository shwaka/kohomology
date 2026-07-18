package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.sparseMatrixTag
import com.github.shwaka.kohomology.myArbList
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.Rational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverRational
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll

class SparseRowEchelonFormCalculatorTest : FreeSpec({
    tags(sparseMatrixTag, rationalTag)

    val field = SparseNumVectorSpaceOverRational.field
    val referenceCalculator = SparseRowEchelonFormAlgorithm.NonInPlace.createCalculator(
        field = field,
        cancellationContext = null,
    )
    val calculators = listOf(
        "NonInPlace" to referenceCalculator,
        "InPlace" to SparseRowEchelonFormAlgorithm.InPlace.createCalculator(
            field = field,
            cancellationContext = null,
        ),
        "Indexed" to SparseRowEchelonFormAlgorithm.Indexed.createCalculator(
            field = field,
            cancellationContext = null,
        ),
        "Parallel" to SparseRowEchelonFormAlgorithm.Parallel(
            parallelMinSize = 0,
            parallelChunkSize = 1,
            parallelism = 2,
        ).createCalculator(
            field = field,
            cancellationContext = null,
        ),
    )
    val trackingCalculators = calculators.map { (name, calculator) ->
        name to AugmentedTransformTrackingSparseRowEchelonFormCalculator(
            baseCalculator = calculator,
            field = field,
        )
    }

    "rowEchelonForm should satisfy sparse row echelon data invariants for fixed examples" {
        val examples = field.context.run {
            listOf(
                sparseRowMap(),
                sparseRowMap(
                    listOf(0, 0, 1),
                    listOf(1, 0, 0),
                    listOf(0, 1, 1),
                ),
                sparseRowMap(
                    listOf(0, 2, 4, 0),
                    listOf(0, 1, 2, 3),
                    listOf(0, 0, 0, 5),
                    listOf(0, 3, 6, 9),
                ),
                sparseRowMap(
                    listOf(1, 2, 0, 0),
                    listOf(2, 4, 0, 0),
                    listOf(0, 0, 3, 6),
                    listOf(0, 0, 1, 2),
                ),
            )
        }
        for ((name, calculator) in calculators) {
            for (rowMap in examples) {
                withClue(name) {
                    val data = calculator.rowEchelonForm(rowMap, colCount = 4)
                    assertRowEchelonData(data, field.zero)
                }
            }
        }
    }

    "reduced row echelon form should agree with NonInPlace for fixed examples" {
        val examples = field.context.run {
            listOf(
                0 to sparseRowMap(),
                3 to sparseRowMap(
                    listOf(0, 0, 1),
                    listOf(1, 0, 0),
                    listOf(0, 1, 1),
                ),
                4 to sparseRowMap(
                    listOf(0, 2, 4, 0),
                    listOf(0, 1, 2, 3),
                    listOf(0, 0, 0, 5),
                    listOf(0, 3, 6, 9),
                ),
                4 to sparseRowMap(
                    listOf(1, 2, 0, 0),
                    listOf(2, 4, 0, 0),
                    listOf(0, 0, 3, 6),
                    listOf(0, 0, 1, 2),
                ),
            )
        }
        for ((name, calculator) in calculators) {
            for ((colCount, rowMap) in examples) {
                withClue(name) {
                    val reducedRowMap = calculator.computeReducedRowMap(rowMap, colCount)
                    val referenceReducedRowMap = referenceCalculator.computeReducedRowMap(rowMap, colCount)
                    val pivots = referenceCalculator.rowEchelonForm(rowMap, colCount).pivots
                    reducedRowMap shouldBe referenceReducedRowMap
                    assertReducedRowEchelonMap(reducedRowMap, pivots, field.zero, field.one)
                }
            }
        }
    }

    "reduced row echelon form should agree with NonInPlace for generated matrices" {
        val rowMapArb = sparseRowMapArb(
            valueArb = Arb.int(-5..5),
            rowCount = 4,
            colCount = 5,
        )
        checkAll(rowMapArb) { rowMap ->
            for ((name, calculator) in calculators) {
                withClue(name) {
                    val reducedRowMap = calculator.computeReducedRowMap(rowMap, colCount = 5)
                    val referenceReducedRowMap = referenceCalculator.computeReducedRowMap(rowMap, colCount = 5)
                    reducedRowMap shouldBe referenceReducedRowMap
                }
            }
        }
    }

    "rowEchelonForm should satisfy sparse row echelon data invariants for generated matrices" {
        val rowMapArb = sparseRowMapArb(
            valueArb = Arb.int(-5..5),
            rowCount = 4,
            colCount = 5,
        )
        checkAll(rowMapArb) { rowMap ->
            for ((name, calculator) in calculators) {
                withClue(name) {
                    val data = calculator.rowEchelonForm(rowMap, colCount = 5)
                    assertRowEchelonData(data, field.zero)
                }
            }
        }
    }

    "rowEchelonFormWithTransformation should agree with ordinary rowEchelonForm" {
        val rowMapArb = sparseRowMapArb(
            valueArb = Arb.int(-5..5),
            rowCount = 4,
            colCount = 5,
        )
        checkAll(rowMapArb) { rowMap ->
            for ((name, trackingCalculator) in trackingCalculators) {
                withClue(name) {
                    val data = trackingCalculator.rowEchelonFormWithTransformation(
                        matrix = rowMap,
                        rowCount = 4,
                        colCount = 5,
                    )
                    val ordinaryData = trackingCalculator.rowEchelonForm(rowMap, colCount = 5)
                    data.copy(transformationRowMap = null) shouldBe ordinaryData
                    assertTransformationMatches(
                        originalRowMap = rowMap,
                        rowCount = 4,
                        colCount = 5,
                        data = data,
                    )
                }
            }
        }
    }

    "reduceWithTransformation should produce reducedTransformation matching reduced row echelon form" {
        val rowMapArb = sparseRowMapArb(
            valueArb = Arb.int(-5..5),
            rowCount = 4,
            colCount = 5,
        )
        checkAll(rowMapArb) { rowMap ->
            for ((name, trackingCalculator) in trackingCalculators) {
                withClue(name) {
                    val data = trackingCalculator.rowEchelonFormWithTransformation(
                        matrix = rowMap,
                        rowCount = 4,
                        colCount = 5,
                    )
                    val reducedData = trackingCalculator.reduceWithTransformation(data)
                    reducedData.rowMap shouldBe trackingCalculator.reduce(data.rowMap, data.pivots)
                    assertTransformationMatches(
                        originalRowMap = rowMap,
                        rowCount = 4,
                        colCount = 5,
                        data = reducedData,
                    )
                }
            }
        }
    }
})

private fun <S : Scalar> SparseRowEchelonFormCalculator<S>.computeReducedRowMap(
    rowMap: Map<Int, Map<Int, S>>,
    colCount: Int,
): Map<Int, Map<Int, S>> {
    val data = this.rowEchelonForm(rowMap, colCount)
    return this.reduce(data.rowMap, data.pivots)
}

private fun <S : Scalar> assertRowEchelonData(
    data: SparseRowEchelonFormData<S>,
    zero: S,
) {
    val rank = data.pivots.size
    if (rank == 0) {
        data.rowMap.shouldBeEmpty()
    } else {
        data.rowMap.keys shouldBe (0 until rank).toSet()
    }
    data.pivots shouldBe data.pivots.sorted()
    for ((rowIndex, pivot) in data.pivots.withIndex()) {
        val row = data.rowMap[rowIndex] ?: error("Missing pivot row $rowIndex")
        row.values.all { value -> value.isNotZero() } shouldBe true
        row.firstNonZeroColumn() shouldBe pivot
        for (targetRowIndex in (rowIndex + 1) until rank) {
            (data.rowMap[targetRowIndex]?.get(pivot) ?: zero) shouldBe zero
        }
    }
}

private fun <S : Scalar> assertReducedRowEchelonMap(
    rowMap: Map<Int, Map<Int, S>>,
    pivots: List<Int>,
    zero: S,
    one: S,
) {
    val rank = pivots.size
    if (rank == 0) {
        rowMap.shouldBeEmpty()
    } else {
        rowMap.keys shouldBe (0 until rank).toSet()
    }
    for ((rowIndex, pivot) in pivots.withIndex()) {
        val row = rowMap[rowIndex] ?: error("Missing pivot row $rowIndex")
        row.values.all { value -> value.isNotZero() } shouldBe true
        row.firstNonZeroColumn() shouldBe pivot
        row[pivot] shouldBe one
        for (targetRowIndex in 0 until rank) {
            if (targetRowIndex != rowIndex) {
                (rowMap[targetRowIndex]?.get(pivot) ?: zero) shouldBe zero
            }
        }
    }
}

private fun assertTransformationMatches(
    originalRowMap: Map<Int, Map<Int, Rational>>,
    rowCount: Int,
    colCount: Int,
    data: SparseRowEchelonFormData<Rational>,
) {
    val transformationRowMap = data.transformationRowMap
        ?: error("Expected transformationRowMap")
    SparseMatrixSpaceOverRational.context.run {
        val originalMatrix = originalRowMap.toMatrix(rowCount, colCount)
        val rowEchelonMatrix = data.rowMap.toMatrix(rowCount, colCount)
        val transformation = transformationRowMap.toMatrix(rowCount, rowCount)
        (transformation * originalMatrix) shouldBe rowEchelonMatrix
    }
}

private fun <S : Scalar> Map<Int, S>.firstNonZeroColumn(): Int? {
    return this.entries
        .filter { (_, value) -> value.isNotZero() }
        .minOfOrNull { (colIndex, _) -> colIndex }
}

private fun List<Int>.toSparseRow(): Map<Int, Rational> {
    return SparseNumVectorSpaceOverRational.field.context.run {
        this@toSparseRow.mapIndexedNotNull { colIndex, value ->
            val scalar = value.toScalar()
            if (scalar.isZero()) {
                null
            } else {
                colIndex to scalar
            }
        }.toMap()
    }
}

private fun sparseRowMap(vararg rows: List<Int>): Map<Int, Map<Int, Rational>> {
    return rows.mapIndexedNotNull { rowIndex, row ->
        val sparseRow = row.toSparseRow()
        if (sparseRow.isEmpty()) {
            null
        } else {
            rowIndex to sparseRow
        }
    }.toMap()
}

private fun sparseRowMapArb(
    valueArb: Arb<Int>,
    rowCount: Int,
    colCount: Int,
): Arb<Map<Int, Map<Int, Rational>>> {
    return myArbList(valueArb, rowCount * colCount).map { values ->
        SparseNumVectorSpaceOverRational.field.context.run {
            values.chunked(colCount).map { row ->
                row.map { value -> value.toScalar() }
            }
        }.mapIndexedNotNull { rowIndex, row ->
            val sparseRow = row.mapIndexedNotNull { colIndex, value ->
                if (value.isZero()) {
                    null
                } else {
                    colIndex to value
                }
            }.toMap()
            if (sparseRow.isEmpty()) {
                null
            } else {
                rowIndex to sparseRow
            }
        }.toMap()
    }
}

package com.github.shwaka.kohomology.linalg.log

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

private data class MatrixLoggerTestCase(
    val operation: MatrixOperation,
    val measurements: List<OperationMeasurement<MatrixOperation, MatrixOperationInput>>,
    val expectedMetrics: MatrixOperationMetrics,
)

private fun measurement(
    durationMillis: Long,
    input: MatrixOperationInput,
): OperationMeasurement<MatrixOperation, MatrixOperationInput> {
    return OperationMeasurement(
        duration = durationMillis.milliseconds,
        input = input,
    )
}

class MatrixLoggerTest : FreeSpec({
    "MatrixOperationSummaryFactory should create metrics for each operation" {
        val testCases = listOf(
            MatrixLoggerTestCase(
                operation = MatrixOperation.ADD,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.Add(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.Add(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.Add(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.SUBTRACT,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.Subtract(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.Subtract(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.Subtract(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.MULTIPLY_MATRIX,
                measurements = listOf(
                    measurement(
                        1,
                        MatrixOperationInput.MultiplyMatrix(
                            firstRowCount = 2,
                            firstColCount = 3,
                            secondColCount = 4,
                        ),
                    ),
                    measurement(
                        2,
                        MatrixOperationInput.MultiplyMatrix(
                            firstRowCount = 5,
                            firstColCount = 2,
                            secondColCount = 6,
                        ),
                    ),
                ),
                expectedMetrics = MatrixOperationMetrics.MultiplyMatrix(
                    maxFirstRowCount = 5,
                    maxFirstColCount = 3,
                    maxSecondColCount = 6,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.MULTIPLY_NUM_VECTOR,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.MultiplyNumVector(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.MultiplyNumVector(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MultiplyNumVector(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.MULTIPLY_SCALAR,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.MultiplyScalar(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.MultiplyScalar(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MultiplyScalar(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.COMPUTE_ROW_ECHELON_FORM,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.ComputeRowEchelonForm(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.ComputeRowEchelonForm(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.ComputeRowEchelonForm(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.COMPUTE_TRANSPOSE,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.ComputeTranspose(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.ComputeTranspose(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.ComputeTranspose(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.JOIN_MATRICES,
                measurements = listOf(
                    measurement(
                        1,
                        MatrixOperationInput.JoinMatrices(
                            rowCount = 2,
                            firstColCount = 3,
                            secondColCount = 4,
                        ),
                    ),
                    measurement(
                        2,
                        MatrixOperationInput.JoinMatrices(
                            rowCount = 5,
                            firstColCount = 2,
                            secondColCount = 6,
                        ),
                    ),
                ),
                expectedMetrics = MatrixOperationMetrics.JoinMatrices(
                    maxRowCount = 5,
                    maxColCountSum = 8,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.COMPUTE_ROW_SLICE,
                measurements = listOf(
                    measurement(
                        1,
                        MatrixOperationInput.ComputeRowSlice(
                            rowCount = 2,
                            colCount = 3,
                            rangeSize = 1,
                        ),
                    ),
                    measurement(
                        2,
                        MatrixOperationInput.ComputeRowSlice(
                            rowCount = 4,
                            colCount = 1,
                            rangeSize = 3,
                        ),
                    ),
                ),
                expectedMetrics = MatrixOperationMetrics.ComputeRowSlice(
                    maxRowCount = 4,
                    maxColCount = 3,
                    maxRangeSize = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.COMPUTE_COL_SLICE,
                measurements = listOf(
                    measurement(
                        1,
                        MatrixOperationInput.ComputeColSlice(
                            rowCount = 2,
                            colCount = 3,
                            rangeSize = 1,
                        ),
                    ),
                    measurement(
                        2,
                        MatrixOperationInput.ComputeColSlice(
                            rowCount = 4,
                            colCount = 1,
                            rangeSize = 3,
                        ),
                    ),
                ),
                expectedMetrics = MatrixOperationMetrics.ComputeColSlice(
                    maxRowCount = 4,
                    maxColCount = 3,
                    maxRangeSize = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.FROM_ROW_LIST,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.FromRowList(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.FromRowList(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.FromRowList(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.FROM_ROW_MAP,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.FromRowMap(rowCount = 2, colCount = 3)),
                    measurement(2, MatrixOperationInput.FromRowMap(rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.FromRowMap(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
        )

        for (testCase in testCases) {
            MatrixOperationSummaryFactory.create(
                operation = testCase.operation,
                measurements = testCase.measurements,
            ).metrics shouldBe testCase.expectedMetrics
        }
    }

    "MatrixOperationSummaryFactory should create summary" {
        val summary = MatrixOperationSummaryFactory.create(
            operation = MatrixOperation.ADD,
            measurements = listOf(
                measurement(1, MatrixOperationInput.Add(rowCount = 2, colCount = 3)),
                measurement(5, MatrixOperationInput.Add(rowCount = 4, colCount = 1)),
            ),
        )

        summary shouldBe MatrixOperationSummary(
            operation = MatrixOperation.ADD,
            invocationCount = 2,
            maxDuration = 5.milliseconds,
            totalDuration = 6.milliseconds,
            metrics = MatrixOperationMetrics.Add(
                maxRowCount = 4,
                maxColCount = 3,
            ),
        )
    }

    "metricsText should be metrics.toPrettyString()" {
        val metrics = MatrixOperationMetrics.ComputeRowSlice(
            maxRowCount = 4,
            maxColCount = 3,
            maxRangeSize = 2,
        )
        val summary = MatrixOperationSummary(
            operation = MatrixOperation.COMPUTE_ROW_SLICE,
            invocationCount = 1,
            maxDuration = 1.milliseconds,
            totalDuration = 1.milliseconds,
            metrics = metrics,
        )

        summary.metricsText shouldBe metrics.toPrettyString()
    }

    "MatrixOperationSummaryFactory should throw for empty measurements" {
        shouldThrow<IllegalArgumentException> {
            MatrixOperationSummaryFactory.create(
                operation = MatrixOperation.ADD,
                measurements = emptyList(),
            )
        }
    }

    "MatrixOperationSummaryFactory should throw if operation does not match measurements" {
        shouldThrow<IllegalArgumentException> {
            MatrixOperationSummaryFactory.create(
                operation = MatrixOperation.ADD,
                measurements = listOf(
                    measurement(1, MatrixOperationInput.Subtract(rowCount = 2, colCount = 3)),
                ),
            )
        }
    }
})

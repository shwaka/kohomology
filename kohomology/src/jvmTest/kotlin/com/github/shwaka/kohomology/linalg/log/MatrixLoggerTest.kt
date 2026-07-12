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

private fun matrixSizeInput(
    operation: MatrixOperation,
    rowCount: Int,
    colCount: Int,
): MatrixOperationInput.MatrixSize {
    return MatrixOperationInput.MatrixSize(
        operation = operation,
        rowCount = rowCount,
        colCount = colCount,
    )
}

private fun sliceInput(
    operation: MatrixOperation,
    rowCount: Int,
    colCount: Int,
    rangeSize: Int,
): MatrixOperationInput.Slice {
    return MatrixOperationInput.Slice(
        operation = operation,
        rowCount = rowCount,
        colCount = colCount,
        rangeSize = rangeSize,
    )
}

private fun String.trimLineEnds(): String {
    return this.lines().joinToString("\n") { it.trimEnd() }
}

class MatrixLoggerTest : FreeSpec({
    "MatrixOperationSummaryFactory should create metrics for each operation" {
        val testCases = listOf(
            MatrixLoggerTestCase(
                operation = MatrixOperation.ADD,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.ADD, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.ADD, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.SUBTRACT,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.SUBTRACT, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.SUBTRACT, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
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
                    measurement(1, matrixSizeInput(MatrixOperation.MULTIPLY_NUM_VECTOR, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.MULTIPLY_NUM_VECTOR, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.MULTIPLY_SCALAR,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.MULTIPLY_SCALAR, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.MULTIPLY_SCALAR, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.COMPUTE_ROW_ECHELON_FORM,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.COMPUTE_ROW_ECHELON_FORM, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.COMPUTE_ROW_ECHELON_FORM, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.COMPUTE_TRANSPOSE,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.COMPUTE_TRANSPOSE, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.COMPUTE_TRANSPOSE, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
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
                        sliceInput(
                            operation = MatrixOperation.COMPUTE_ROW_SLICE,
                            rowCount = 2,
                            colCount = 3,
                            rangeSize = 1,
                        ),
                    ),
                    measurement(
                        2,
                        sliceInput(
                            operation = MatrixOperation.COMPUTE_ROW_SLICE,
                            rowCount = 4,
                            colCount = 1,
                            rangeSize = 3,
                        ),
                    ),
                ),
                expectedMetrics = MatrixOperationMetrics.Slice(
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
                        sliceInput(
                            operation = MatrixOperation.COMPUTE_COL_SLICE,
                            rowCount = 2,
                            colCount = 3,
                            rangeSize = 1,
                        ),
                    ),
                    measurement(
                        2,
                        sliceInput(
                            operation = MatrixOperation.COMPUTE_COL_SLICE,
                            rowCount = 4,
                            colCount = 1,
                            rangeSize = 3,
                        ),
                    ),
                ),
                expectedMetrics = MatrixOperationMetrics.Slice(
                    maxRowCount = 4,
                    maxColCount = 3,
                    maxRangeSize = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.FROM_ROW_LIST,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.FROM_ROW_LIST, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.FROM_ROW_LIST, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixLoggerTestCase(
                operation = MatrixOperation.FROM_ROW_MAP,
                measurements = listOf(
                    measurement(1, matrixSizeInput(MatrixOperation.FROM_ROW_MAP, rowCount = 2, colCount = 3)),
                    measurement(2, matrixSizeInput(MatrixOperation.FROM_ROW_MAP, rowCount = 4, colCount = 1)),
                ),
                expectedMetrics = MatrixOperationMetrics.MatrixSize(
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
                measurement(1, matrixSizeInput(MatrixOperation.ADD, rowCount = 2, colCount = 3)),
                measurement(5, matrixSizeInput(MatrixOperation.ADD, rowCount = 4, colCount = 1)),
            ),
        )

        summary shouldBe MatrixOperationSummary(
            operation = MatrixOperation.ADD,
            invocationCount = 2,
            maxDuration = 5.milliseconds,
            totalDuration = 6.milliseconds,
            metrics = MatrixOperationMetrics.MatrixSize(
                maxRowCount = 4,
                maxColCount = 3,
            ),
        )
    }

    "metricsText should be metrics.toPrettyString()" {
        val metrics = MatrixOperationMetrics.Slice(
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

    "MatrixOperationMetrics should be converted to pretty strings" {
        val metricsList = listOf(
            MatrixOperationMetrics.MatrixSize(
                maxRowCount = 4,
                maxColCount = 3,
            ) to "maxRow=4, maxCol=3",
            MatrixOperationMetrics.MultiplyMatrix(
                maxFirstRowCount = 2,
                maxFirstColCount = 3,
                maxSecondColCount = 4,
            ) to "maxRow1=2, maxCol1=3, maxCol2=4",
            MatrixOperationMetrics.JoinMatrices(
                maxRowCount = 5,
                maxColCountSum = 8,
            ) to "maxRow=5, maxColSum=8",
            MatrixOperationMetrics.Slice(
                maxRowCount = 4,
                maxColCount = 3,
                maxRangeSize = 2,
            ) to "maxRow=4, maxCol=3, maxRangeSize=2",
        )

        for ((metrics, expected) in metricsList) {
            metrics.toPrettyString() shouldBe expected
        }
    }

    "MatrixOperationInput should expose numeric values" {
        matrixSizeInput(
            MatrixOperation.ADD,
            rowCount = 2,
            colCount = 3,
        ).numericValues shouldBe mapOf(
            "col_count" to 3.0,
            "row_count" to 2.0,
            "size" to 6.0,
            "work_size" to 6.0,
        )
        MatrixOperationInput.MultiplyMatrix(
            firstRowCount = 2,
            firstColCount = 3,
            secondColCount = 4,
        ).numericValues shouldBe mapOf(
            "first_col_count" to 3.0,
            "first_row_count" to 2.0,
            "second_col_count" to 4.0,
            "second_row_count" to 3.0,
            "size" to 8.0,
            "work_size" to 24.0,
        )
        MatrixOperationInput.JoinMatrices(
            rowCount = 2,
            firstColCount = 3,
            secondColCount = 4,
        ).numericValues shouldBe mapOf(
            "col_count_sum" to 7.0,
            "first_col_count" to 3.0,
            "row_count" to 2.0,
            "second_col_count" to 4.0,
            "size" to 14.0,
            "work_size" to 14.0,
        )
        sliceInput(
            MatrixOperation.COMPUTE_COL_SLICE,
            rowCount = 2,
            colCount = 3,
            rangeSize = 1,
        ).numericValues shouldBe mapOf(
            "col_count" to 3.0,
            "range_size" to 1.0,
            "row_count" to 2.0,
            "size" to 2.0,
            "work_size" to 6.0,
        )
    }

    "formatSummaries should format matrix operation summaries" {
        val summaries: Map<MatrixOperation, MatrixOperationSummary> = listOf(
            MatrixOperationSummary(
                operation = MatrixOperation.ADD,
                invocationCount = 2,
                maxDuration = 5.milliseconds,
                totalDuration = 6.milliseconds,
                metrics = MatrixOperationMetrics.MatrixSize(
                    maxRowCount = 4,
                    maxColCount = 3,
                ),
            ),
            MatrixOperationSummary(
                operation = MatrixOperation.MULTIPLY_MATRIX,
                invocationCount = 1,
                maxDuration = 10.milliseconds,
                totalDuration = 10.milliseconds,
                metrics = MatrixOperationMetrics.MultiplyMatrix(
                    maxFirstRowCount = 2,
                    maxFirstColCount = 3,
                    maxSecondColCount = 4,
                ),
            ),
        ).associateBy { it.operation }
        val expected = """
            |          name total  max count metrics
            |MultiplyMatrix  10ms 10ms     1 maxRow1=2, maxCol1=3, maxCol2=4
            |           Add   6ms  5ms     2 maxRow=4, maxCol=3
        """.trimMargin()

        formatSummaries(summaries).trimLineEnds() shouldBe expected
    }

    "MatrixLogger should format summaries from recorded measurements" {
        val logger = MatrixLogger()
        logger.add(measurement(1, matrixSizeInput(MatrixOperation.ADD, rowCount = 2, colCount = 3)))
        logger.add(measurement(5, matrixSizeInput(MatrixOperation.ADD, rowCount = 4, colCount = 1)))
        logger.add(
            measurement(
                10,
                MatrixOperationInput.MultiplyMatrix(
                    firstRowCount = 2,
                    firstColCount = 3,
                    secondColCount = 4,
                ),
            ),
        )
        val expected = """
            |          name total  max count metrics
            |MultiplyMatrix  10ms 10ms     1 maxRow1=2, maxCol1=3, maxCol2=4
            |           Add   6ms  5ms     2 maxRow=4, maxCol=3
        """.trimMargin()

        logger.getFormattedSummaries().trimLineEnds() shouldBe expected
    }

    "MatrixLogger should format measurements as CSV" {
        val logger = MatrixLogger()
        logger.add(measurement(1, matrixSizeInput(MatrixOperation.ADD, rowCount = 2, colCount = 3)))
        logger.add(
            measurement(
                10,
                MatrixOperationInput.MultiplyMatrix(
                    firstRowCount = 2,
                    firstColCount = 3,
                    secondColCount = 4,
                ),
            ),
        )
        val expected = """
            |operation,duration_ms,col_count,first_col_count,first_row_count,row_count,second_col_count,second_row_count,size,work_size
            |Add,1.0,3.0,,,2.0,,,6.0,6.0
            |MultiplyMatrix,10.0,,3.0,2.0,,4.0,3.0,8.0,24.0
        """.trimMargin()

        logger.getMeasurementsCSV() shouldBe expected
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
                    measurement(1, matrixSizeInput(MatrixOperation.SUBTRACT, rowCount = 2, colCount = 3)),
                ),
            )
        }
    }
})

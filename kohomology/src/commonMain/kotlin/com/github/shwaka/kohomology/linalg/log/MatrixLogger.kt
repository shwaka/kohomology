package com.github.shwaka.kohomology.linalg.log

import kotlin.time.Duration

public enum class MatrixOperation(
    override val displayName: String
) : OperationKind {
    ADD("Add"),
    SUBTRACT("Subtract"),
    MULTIPLY_MATRIX("MultiplyMatrix"),
}

public sealed interface MatrixOperationInput : OperationInput<MatrixOperation> {
    public data class Add(
        public val rowCount: Int,
        public val colCount: Int,
    ) : MatrixOperationInput {
        override val operation: MatrixOperation = MatrixOperation.ADD
    }

    public data class Subtract(
        public val rowCount: Int,
        public val colCount: Int,
    ) : MatrixOperationInput {
        override val operation: MatrixOperation = MatrixOperation.SUBTRACT
    }

    public data class MultiplyMatrix(
        public val firstRowCount: Int,
        public val firstColCount: Int,
        public val secondColCount: Int,
    ) : MatrixOperationInput {
        override val operation: MatrixOperation =
            MatrixOperation.MULTIPLY_MATRIX

        public val secondRowCount: Int
            get() = this.firstColCount
    }
}

public sealed interface MatrixOperationMetrics {
    public data class Add(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : MatrixOperationMetrics

    public data class Subtract(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : MatrixOperationMetrics

    public data class MultiplyMatrix(
        public val maxFirstRowCount: Int,
        public val maxFirstColCount: Int,
        public val maxSecondColCount: Int,
    ) : MatrixOperationMetrics
}

public data class MatrixOperationSummary(
    override val operation: MatrixOperation,
    override val invocationCount: Int,
    override val maxDuration: Duration,
    override val totalDuration: Duration,
    public val metrics: MatrixOperationMetrics,
) : OperationSummary<MatrixOperation>

public object MatrixOperationSummaryFactory :
    OperationSummaryFactory<
        MatrixOperation,
        MatrixOperationInput,
        MatrixOperationSummary,
        > {
    override fun create(
        operation: MatrixOperation,
        measurements: List<OperationMeasurement<MatrixOperation, MatrixOperationInput>>,
    ): MatrixOperationSummary {
        require(measurements.isNotEmpty()) {
            "measurements must not be empty."
        }

        require(measurements.all { it.input.operation == operation }) {
            "All measurements must have the specified operation."
        }

        val metrics = when (operation) {
            MatrixOperation.ADD -> {
                val inputs = measurements.map { measurement ->
                    measurement.input as? MatrixOperationInput.Add
                        ?: error("Unexpected MatrixOperationInput type.")
                }
                MatrixOperationMetrics.Add(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }

            MatrixOperation.SUBTRACT -> {
                val inputs = measurements.map { measurement ->
                    measurement.input as? MatrixOperationInput.Subtract
                        ?: error("Unexpected MatrixOperationInput type.")
                }
                MatrixOperationMetrics.Subtract(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }

            MatrixOperation.MULTIPLY_MATRIX -> {
                val inputs = measurements.map { measurement ->
                    measurement.input as? MatrixOperationInput.MultiplyMatrix
                        ?: error("Unexpected MatrixOperationInput type.")
                }
                MatrixOperationMetrics.MultiplyMatrix(
                    maxFirstRowCount = inputs.maxOf { it.firstRowCount },
                    maxFirstColCount = inputs.maxOf { it.firstColCount },
                    maxSecondColCount = inputs.maxOf { it.secondColCount },
                )
            }
        }

        return MatrixOperationSummary(
            operation = operation,
            invocationCount = measurements.size,
            maxDuration = measurements.maxOfOrNull { it.duration } ?: Duration.ZERO,
            totalDuration = measurements.fold(Duration.ZERO) { acc, measurement ->
                acc + measurement.duration
            },
            metrics = metrics,
        )
    }
}

public class MatrixLogger : OperationLogger<MatrixOperation, MatrixOperationInput, MatrixOperationSummary>(
    MatrixOperationSummaryFactory
)

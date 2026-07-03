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
    public fun toPrettyString(): String

    public data class Add(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : MatrixOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<MatrixOperationInput.Add>,
            ): Add {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Add(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }
        }
    }

    public data class Subtract(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : MatrixOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<MatrixOperationInput.Subtract>,
            ): Subtract {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Subtract(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }
        }
    }

    public data class MultiplyMatrix(
        public val maxFirstRowCount: Int,
        public val maxFirstColCount: Int,
        public val maxSecondColCount: Int,
    ) : MatrixOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow1=$maxFirstRowCount, maxCol1=$maxFirstColCount, maxCol2=$maxSecondColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<MatrixOperationInput.MultiplyMatrix>,
            ): MultiplyMatrix {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return MultiplyMatrix(
                    maxFirstRowCount = inputs.maxOf { it.firstRowCount },
                    maxFirstColCount = inputs.maxOf { it.firstColCount },
                    maxSecondColCount = inputs.maxOf { it.secondColCount },
                )
            }
        }
    }
}

public data class MatrixOperationSummary(
    override val operation: MatrixOperation,
    override val invocationCount: Int,
    override val maxDuration: Duration,
    override val totalDuration: Duration,
    public val metrics: MatrixOperationMetrics,
) : OperationSummary<MatrixOperation> {
    override val metricsText: String
        get() = metrics.toPrettyString()
}

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

        return MatrixOperationSummary(
            operation = operation,
            invocationCount = measurements.size,
            maxDuration = measurements.maxOfOrNull { it.duration } ?: Duration.ZERO,
            totalDuration = measurements.fold(Duration.ZERO) { acc, measurement ->
                acc + measurement.duration
            },
            metrics = this.createMetrics(operation, measurements),
        )
    }

    private fun createMetrics(
        operation: MatrixOperation,
        measurements: List<OperationMeasurement<MatrixOperation, MatrixOperationInput>>,
    ): MatrixOperationMetrics {
        return when (operation) {
            MatrixOperation.ADD ->
                MatrixOperationMetrics.Add.fromInputs(measurements.castedInputs())
            MatrixOperation.SUBTRACT ->
                MatrixOperationMetrics.Subtract.fromInputs(measurements.castedInputs())
            MatrixOperation.MULTIPLY_MATRIX ->
                MatrixOperationMetrics.MultiplyMatrix.fromInputs(measurements.castedInputs())
        }
    }
}

public class MatrixLogger : OperationLogger<MatrixOperation, MatrixOperationInput, MatrixOperationSummary>(
    MatrixOperationSummaryFactory
)

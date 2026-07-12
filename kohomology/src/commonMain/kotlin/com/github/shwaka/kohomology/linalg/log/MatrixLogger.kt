package com.github.shwaka.kohomology.linalg.log

import kotlin.time.Duration

public enum class MatrixOperation(
    override val displayName: String
) : OperationKind {
    ADD("Add"),
    SUBTRACT("Subtract"),
    MULTIPLY_MATRIX("MultiplyMatrix"),
    MULTIPLY_NUM_VECTOR("MultiplyNumVector"),
    MULTIPLY_SCALAR("MultiplyScalar"),
    COMPUTE_ROW_ECHELON_FORM("ComputeRowEchelonForm"),
    COMPUTE_TRANSPOSE("ComputeTranspose"),
    JOIN_MATRICES("JoinMatrices"),
    COMPUTE_ROW_SLICE("ComputeRowSlice"),
    COMPUTE_COL_SLICE("ComputeColSlice"),
    FROM_ROW_LIST("FromRowList"),
    FROM_ROW_MAP("FromRowMap"),
}

public sealed interface MatrixOperationInput : OperationInput<MatrixOperation> {
    public data class MatrixSize(
        override val operation: MatrixOperation,
        public val rowCount: Int,
        public val colCount: Int,
    ) : MatrixOperationInput {
        init {
            require(
                operation in setOf(
                    MatrixOperation.ADD,
                    MatrixOperation.SUBTRACT,
                    MatrixOperation.MULTIPLY_NUM_VECTOR,
                    MatrixOperation.MULTIPLY_SCALAR,
                    MatrixOperation.COMPUTE_ROW_ECHELON_FORM,
                    MatrixOperation.COMPUTE_TRANSPOSE,
                    MatrixOperation.FROM_ROW_LIST,
                    MatrixOperation.FROM_ROW_MAP,
                )
            ) {
                "Unsupported operation for MatrixSize: $operation"
            }
        }
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

    public data class JoinMatrices(
        public val rowCount: Int,
        public val firstColCount: Int,
        public val secondColCount: Int,
    ) : MatrixOperationInput {
        override val operation: MatrixOperation = MatrixOperation.JOIN_MATRICES
    }

    public data class Slice(
        override val operation: MatrixOperation,
        public val rowCount: Int,
        public val colCount: Int,
        public val rangeSize: Int,
    ) : MatrixOperationInput {
        init {
            require(
                operation in setOf(
                    MatrixOperation.COMPUTE_ROW_SLICE,
                    MatrixOperation.COMPUTE_COL_SLICE,
                )
            ) {
                "Unsupported operation for Slice: $operation"
            }
        }
    }
}

public sealed interface MatrixOperationMetrics {
    public fun toPrettyString(): String

    public data class MatrixSize(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : MatrixOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<MatrixOperationInput.MatrixSize>,
            ): MatrixSize {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return MatrixSize(
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

    public data class JoinMatrices(
        public val maxRowCount: Int,
        public val maxColCountSum: Int,
    ) : MatrixOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxColSum=$maxColCountSum"

        public companion object {
            public fun fromInputs(
                inputs: List<MatrixOperationInput.JoinMatrices>,
            ): JoinMatrices {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return JoinMatrices(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCountSum = inputs.maxOf { it.firstColCount + it.secondColCount },
                )
            }
        }
    }

    public data class Slice(
        public val maxRowCount: Int,
        public val maxColCount: Int,
        public val maxRangeSize: Int,
    ) : MatrixOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount, maxRangeSize=$maxRangeSize"

        public companion object {
            public fun fromInputs(
                inputs: List<MatrixOperationInput.Slice>,
            ): Slice {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Slice(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                    maxRangeSize = inputs.maxOf { it.rangeSize },
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
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.SUBTRACT ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.MULTIPLY_MATRIX ->
                MatrixOperationMetrics.MultiplyMatrix.fromInputs(measurements.castedInputs())
            MatrixOperation.MULTIPLY_NUM_VECTOR ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.MULTIPLY_SCALAR ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.COMPUTE_ROW_ECHELON_FORM ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.COMPUTE_TRANSPOSE ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.JOIN_MATRICES ->
                MatrixOperationMetrics.JoinMatrices.fromInputs(measurements.castedInputs())
            MatrixOperation.COMPUTE_ROW_SLICE ->
                MatrixOperationMetrics.Slice.fromInputs(measurements.castedInputs())
            MatrixOperation.COMPUTE_COL_SLICE ->
                MatrixOperationMetrics.Slice.fromInputs(measurements.castedInputs())
            MatrixOperation.FROM_ROW_LIST ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
            MatrixOperation.FROM_ROW_MAP ->
                MatrixOperationMetrics.MatrixSize.fromInputs(measurements.castedInputs())
        }
    }
}

public class MatrixLogger : OperationLogger<MatrixOperation, MatrixOperationInput, MatrixOperationSummary>(
    MatrixOperationSummaryFactory
)

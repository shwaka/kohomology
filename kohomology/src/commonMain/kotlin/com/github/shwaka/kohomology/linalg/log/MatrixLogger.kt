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
        override val numericValues: Map<String, Double>
            get() {
                val size = this.rowCount.toDouble() * this.colCount.toDouble()
                val workSize = when (this.operation) {
                    MatrixOperation.COMPUTE_ROW_ECHELON_FORM ->
                        size * minOf(this.rowCount, this.colCount).toDouble()
                    else -> size
                }
                return mapOf(
                    "col_count" to this.colCount.toDouble(),
                    "row_count" to this.rowCount.toDouble(),
                    "size" to size,
                    "work_size" to workSize,
                )
            }

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

        override val numericValues: Map<String, Double>
            get() {
                val size = this.firstRowCount.toDouble() * this.secondColCount.toDouble()
                val workSize = size * this.firstColCount.toDouble()
                return mapOf(
                    "first_col_count" to this.firstColCount.toDouble(),
                    "first_row_count" to this.firstRowCount.toDouble(),
                    "second_col_count" to this.secondColCount.toDouble(),
                    "second_row_count" to this.secondRowCount.toDouble(),
                    "size" to size,
                    "work_size" to workSize,
                )
            }
    }

    public data class JoinMatrices(
        public val rowCount: Int,
        public val firstColCount: Int,
        public val secondColCount: Int,
    ) : MatrixOperationInput {
        override val operation: MatrixOperation = MatrixOperation.JOIN_MATRICES

        override val numericValues: Map<String, Double>
            get() {
                val colCountSum = this.firstColCount + this.secondColCount
                val size = this.rowCount.toDouble() * colCountSum.toDouble()
                return mapOf(
                    "col_count_sum" to colCountSum.toDouble(),
                    "first_col_count" to this.firstColCount.toDouble(),
                    "row_count" to this.rowCount.toDouble(),
                    "second_col_count" to this.secondColCount.toDouble(),
                    "size" to size,
                    "work_size" to size,
                )
            }
    }

    public data class Slice(
        override val operation: MatrixOperation,
        public val rowCount: Int,
        public val colCount: Int,
        public val rangeSize: Int,
    ) : MatrixOperationInput {
        override val numericValues: Map<String, Double>
            get() {
                val size = when (this.operation) {
                    MatrixOperation.COMPUTE_ROW_SLICE ->
                        this.rangeSize.toDouble() * this.colCount.toDouble()
                    MatrixOperation.COMPUTE_COL_SLICE ->
                        this.rowCount.toDouble() * this.rangeSize.toDouble()
                    else -> error("Unsupported operation for Slice: ${this.operation}")
                }
                val workSize = this.rowCount.toDouble() * this.colCount.toDouble()
                return mapOf(
                    "col_count" to this.colCount.toDouble(),
                    "range_size" to this.rangeSize.toDouble(),
                    "row_count" to this.rowCount.toDouble(),
                    "size" to size,
                    "work_size" to workSize,
                )
            }

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
    override val maxExclusiveDuration: Duration = maxDuration,
    override val totalExclusiveDuration: Duration = totalDuration,
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
            maxExclusiveDuration = measurements.maxOfOrNull { it.exclusiveDuration } ?: Duration.ZERO,
            totalExclusiveDuration = measurements.fold(Duration.ZERO) { acc, measurement ->
                acc + measurement.exclusiveDuration
            },
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

public class MatrixLogger(
    traceContext: OperationTraceContext = OperationTraceContext(),
) : OperationLogger<MatrixOperation, MatrixOperationInput, MatrixOperationSummary>(
    MatrixOperationSummaryFactory,
    traceContext,
)

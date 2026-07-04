package com.github.shwaka.kohomology.linalg.log

import kotlin.time.Duration

public enum class RefOperation(
    override val displayName: String
) : OperationKind {
    UNREDUCED("REF_Unreduced"),
    REDUCED("REF_Reduced"),
    PIVOTS("REF_Pivots"),
    SIGN("REF_Sign"),
}

public sealed interface RefOperationInput : OperationInput<RefOperation> {
    public data class Unreduced(
        public val rowCount: Int,
        public val colCount: Int,
    ) : RefOperationInput {
        override val operation: RefOperation = RefOperation.UNREDUCED
    }

    public data class Reduced(
        public val rowCount: Int,
        public val colCount: Int,
    ) : RefOperationInput {
        override val operation: RefOperation = RefOperation.REDUCED
    }

    public data class Pivots(
        public val rowCount: Int,
        public val colCount: Int,
    ) : RefOperationInput {
        override val operation: RefOperation = RefOperation.PIVOTS
    }

    public data class Sign(
        public val rowCount: Int,
        public val colCount: Int,
    ) : RefOperationInput {
        override val operation: RefOperation = RefOperation.SIGN
    }
}

public sealed interface RefOperationMetrics {
    public fun toPrettyString(): String

    public data class Unreduced(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : RefOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<RefOperationInput.Unreduced>,
            ): Unreduced {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Unreduced(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }
        }
    }

    public data class Reduced(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : RefOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<RefOperationInput.Reduced>,
            ): Reduced {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Reduced(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }
        }
    }

    public data class Pivots(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : RefOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<RefOperationInput.Pivots>,
            ): Pivots {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Pivots(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }
        }
    }

    public data class Sign(
        public val maxRowCount: Int,
        public val maxColCount: Int,
    ) : RefOperationMetrics {
        override fun toPrettyString(): String =
            "maxRow=$maxRowCount, maxCol=$maxColCount"

        public companion object {
            public fun fromInputs(
                inputs: List<RefOperationInput.Sign>,
            ): Sign {
                require(inputs.isNotEmpty()) { "inputs must not be empty." }
                return Sign(
                    maxRowCount = inputs.maxOf { it.rowCount },
                    maxColCount = inputs.maxOf { it.colCount },
                )
            }
        }
    }
}

public data class RefOperationSummary(
    override val operation: RefOperation,
    override val invocationCount: Int,
    override val maxDuration: Duration,
    override val totalDuration: Duration,
    public val metrics: RefOperationMetrics,
) : OperationSummary<RefOperation> {
    override val metricsText: String
        get() = metrics.toPrettyString()
}

public object RefOperationSummaryFactory :
    OperationSummaryFactory<RefOperation, RefOperationInput, RefOperationSummary> {
    override fun create(
        operation: RefOperation,
        measurements: List<OperationMeasurement<RefOperation, RefOperationInput>>
    ): RefOperationSummary {
        require(measurements.isNotEmpty()) {
            "measurements must not be empty."
        }

        require(measurements.all { it.input.operation == operation }) {
            "All measurements must have the specified operation."
        }

        return RefOperationSummary(
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
        operation: RefOperation,
        measurements: List<OperationMeasurement<RefOperation, RefOperationInput>>,
    ): RefOperationMetrics {
        return when (operation) {
            RefOperation.UNREDUCED ->
                RefOperationMetrics.Unreduced.fromInputs(measurements.castedInputs())
            RefOperation.REDUCED ->
                RefOperationMetrics.Reduced.fromInputs(measurements.castedInputs())
            RefOperation.PIVOTS ->
                RefOperationMetrics.Pivots.fromInputs(measurements.castedInputs())
            RefOperation.SIGN ->
                RefOperationMetrics.Sign.fromInputs(measurements.castedInputs())
        }
    }
}

public class RefLogger : OperationLogger<RefOperation, RefOperationInput, RefOperationSummary>(
    RefOperationSummaryFactory
)

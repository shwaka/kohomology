package com.github.shwaka.kohomology.linalg.log

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

public interface OperationKind {
    public val displayName: String
}

public interface OperationInput<K : OperationKind> {
    public val operation: K
}

public data class OperationMeasurement<K : OperationKind, I : OperationInput<K>>(
    val duration: Duration,
    val input: I,
)

public interface OperationSummary<K : OperationKind> {
    public val operation: K
    public val invocationCount: Int
    public val maxDuration: Duration
    public val totalDuration: Duration
    public val metricsText: String
}

public fun interface OperationSummaryFactory<K : OperationKind, I : OperationInput<K>, S : OperationSummary<K>> {
    public fun create(operation: K, measurements: List<OperationMeasurement<K, I>>): S
}

public open class OperationLogger<K : OperationKind, I : OperationInput<K>, S : OperationSummary<K>>(
    private val summaryFactory: OperationSummaryFactory<K, I, S>
) {
    private val _measurements: MutableList<OperationMeasurement<K, I>> = mutableListOf()

    public val measurement: List<OperationMeasurement<K, I>>
        get() = this._measurements.toList()

    public fun add(measurement: OperationMeasurement<K, I>) {
        this._measurements.add(measurement)
    }

    public fun <T> measureOperation(input: I, block: () -> T): T {
        val (value, duration) = measureTimedValue(block)
        this.add(
            OperationMeasurement(
                duration = duration,
                input = input,
            ),
        )
        return value
    }

    public fun clear() {
        this._measurements.clear()
    }

    public fun summaries(): Map<K, S> {
        return this._measurements
            .groupBy { it.input.operation }
            .mapValues { (operation, measurements) ->
                this.summaryFactory.create(operation, measurements)
            }
    }

    public fun getSummariesString(): String {
        val summaryList: List<OperationSummary<K>> = this.summaries()
            .map { (_, summary) -> summary }
            .sortedBy { summary -> summary.totalDuration }
        val header = listOf(
            "name",
            "total",
            "max",
            "count",
            "metrics",
        )
        val stringTable: List<List<String>> = summaryList.map { summary ->
            listOf(
                summary.operation.displayName,
                summary.totalDuration.toString(DurationUnit.MILLISECONDS, 0),
                summary.maxDuration.toString(DurationUnit.MILLISECONDS, 0),
                summary.invocationCount.toString(),
                summary.metricsText,
            )
        }
        return TextTable(
            data = listOf(header) + stringTable,
            sameWidth = false,
        ).toPrettyString()
    }
}

public inline fun <K : OperationKind, I : OperationInput<K>, reified J : I>
List<OperationMeasurement<K, I>>.castedInputs(): List<J> {
    return this.map { measurement ->
        measurement.input as? J
            ?: error(
                "Expected ${J::class.simpleName}, " +
                    "but got ${measurement.input::class.simpleName}.",
            )
    }
}

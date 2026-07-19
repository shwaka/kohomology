package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.util.Alignment
import com.github.shwaka.kohomology.util.TableFormatter
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

public interface OperationKind {
    public val displayName: String
}

public interface OperationInput<out K : OperationKind> {
    public val operation: K
    public val numericValues: Map<String, Double>
        get() = emptyMap()
}

public data class OperationMeasurement<out K : OperationKind, out I : OperationInput<K>>(
    val duration: Duration,
    val input: I,
    val exclusiveDuration: Duration = duration,
)

public class OperationTraceContext(
    private val timeSource: TimeSource = TimeSource.Monotonic,
) {
    private class ActiveMeasurement {
        var childDuration: Duration = Duration.ZERO
    }

    private val activeMeasurements: ArrayDeque<ActiveMeasurement> = ArrayDeque()

    internal fun <T> measure(
        record: (duration: Duration, exclusiveDuration: Duration) -> Unit,
        block: () -> T,
    ): T {
        val activeMeasurement = ActiveMeasurement()
        val start = this.timeSource.markNow()
        this.activeMeasurements.addLast(activeMeasurement)
        val value = try {
            block()
        } catch (throwable: Throwable) {
            this.popActiveMeasurement(activeMeasurement)
            throw throwable
        }
        val duration = start.elapsedNow()
        val exclusiveDuration = maxOf(Duration.ZERO, duration - activeMeasurement.childDuration)
        this.popActiveMeasurement(activeMeasurement)
        record(duration, exclusiveDuration)
        this.activeMeasurements.lastOrNull()?.let { parent ->
            parent.childDuration += duration
        }
        return value
    }

    private fun popActiveMeasurement(expected: ActiveMeasurement) {
        val actual = this.activeMeasurements.removeLastOrNull()
        check(actual === expected) {
            "OperationTraceContext stack is corrupted."
        }
    }
}

private fun String.escapeCSV(): String {
    val shouldQuote = this.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
    return if (shouldQuote) {
        "\"${this.replace("\"", "\"\"")}\""
    } else {
        this
    }
}

public fun <K : OperationKind, I : OperationInput<K>> List<OperationMeasurement<K, I>>.toCSV(): String {
    val numericValueKeys: List<String> = this.flatMap { measurement ->
        measurement.input.numericValues.keys
    }.distinct().sorted()
    val header = listOf("operation", "duration_ms", "exclusive_duration_ms") + numericValueKeys
    val rows = this.map { measurement ->
        val numericValues = measurement.input.numericValues
        listOf(
            measurement.input.operation.displayName,
            measurement.duration.toDouble(DurationUnit.MILLISECONDS).toString(),
            measurement.exclusiveDuration.toDouble(DurationUnit.MILLISECONDS).toString(),
        ) + numericValueKeys.map { key ->
            numericValues[key]?.toString() ?: ""
        }
    }
    return (listOf(header) + rows)
        .joinToString("\n") { row -> row.joinToString(",") { it.escapeCSV() } }
}

public interface OperationSummary<out K : OperationKind> {
    public val operation: K
    public val invocationCount: Int
    public val maxDuration: Duration
    public val totalDuration: Duration
    public val maxExclusiveDuration: Duration
        get() = this.maxDuration
    public val totalExclusiveDuration: Duration
        get() = this.totalDuration
    public val metricsText: String
}

public fun <K : OperationKind> formatSummaries(summaries: Map<K, OperationSummary<K>>): String {
    val summaryList: List<OperationSummary<K>> = summaries
        .map { (_, summary) -> summary }
        .sortedByDescending { summary -> summary.totalDuration }
    val header = listOf(
        "name",
        "total",
        "total_excl",
        "max",
        "max_excl",
        "count",
        "metrics",
    )
    val stringTable: List<List<String>> = summaryList.map { summary ->
        listOf(
            summary.operation.displayName,
            summary.totalDuration.toString(DurationUnit.MILLISECONDS, 0),
            summary.totalExclusiveDuration.toString(DurationUnit.MILLISECONDS, 0),
            summary.maxDuration.toString(DurationUnit.MILLISECONDS, 0),
            summary.maxExclusiveDuration.toString(DurationUnit.MILLISECONDS, 0),
            summary.invocationCount.toString(),
            summary.metricsText,
        )
    }
    val alignments = listOf(
        Alignment.RIGHT,
        Alignment.RIGHT,
        Alignment.RIGHT,
        Alignment.RIGHT,
        Alignment.RIGHT,
        Alignment.RIGHT,
        Alignment.LEFT,
    )
    return TableFormatter(
        data = listOf(header) + stringTable,
        separator = " ",
        alignments = alignments,
    ).formatPlain()
}

public fun interface OperationSummaryFactory<K : OperationKind, I : OperationInput<K>, S : OperationSummary<K>> {
    public fun create(operation: K, measurements: List<OperationMeasurement<K, I>>): S
}

public open class OperationLogger<K : OperationKind, I : OperationInput<K>, S : OperationSummary<K>>(
    private val summaryFactory: OperationSummaryFactory<K, I, S>,
    private val traceContext: OperationTraceContext = OperationTraceContext(),
) {
    private val _measurements: MutableList<OperationMeasurement<K, I>> = mutableListOf()

    public val measurement: List<OperationMeasurement<K, I>>
        get() = this._measurements.toList()

    public fun add(measurement: OperationMeasurement<K, I>) {
        this._measurements.add(measurement)
    }

    public fun <T> measureOperation(input: I, block: () -> T): T {
        return this.traceContext.measure(
            record = { duration, exclusiveDuration ->
                this.add(
                    OperationMeasurement(
                        duration = duration,
                        input = input,
                        exclusiveDuration = exclusiveDuration,
                    ),
                )
            },
            block = block,
        )
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

    public fun getFormattedSummaries(): String {
        return formatSummaries(this.summaries())
    }

    public fun getMeasurementsCSV(): String {
        return this.measurement.toCSV()
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

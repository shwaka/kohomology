package com.github.shwaka.kohomology.linalg.log

import kotlin.time.Duration
import kotlin.time.measureTimedValue

public class MatrixLogger {
    private val _entries: MutableList<MatrixLogEntry<*>> = mutableListOf()
    public val entries: List<MatrixLogEntry<*>>
        get() = _entries.toList()

    public fun addEntry(entry: MatrixLogEntry<*>) {
        this._entries.add(entry)
    }

    public fun <T> runLogging(data: MatrixLogData, block: () -> T): T {
        val (value, duration) = measureTimedValue(block)
        this.addEntry(MatrixLogEntry(duration, data))
        return value
    }

    public fun clearEntries() {
        this._entries.clear()
    }

    public fun getStats(): Map<String, MatrixLogStats> {
        val groups = this.entries.groupBy { it.data::class.simpleName ?: "<unknown>" }
        return groups.map { (className, groupEntries) ->
            val stats = MatrixLogStats.fromEntries(className, groupEntries)
            className to stats
        }.toMap()
    }
}

public data class MatrixLogStats(
    val name: String,
    val count: Int,
    val maxDuration: Duration,
    val totalDuration: Duration,
) {
    public companion object {
        public fun fromEntries(name: String, entries: List<MatrixLogEntry<*>>): MatrixLogStats {
            val count = entries.size
            val maxDuration = entries.maxOfOrNull { it.duration } ?: Duration.ZERO
            val totalDuration = entries
                .map { it.duration }
                .fold(Duration.ZERO) { acc, duration -> acc + duration }
            return MatrixLogStats(
                name = name,
                count = count,
                maxDuration = maxDuration,
                totalDuration = totalDuration,
            )
        }
    }
}

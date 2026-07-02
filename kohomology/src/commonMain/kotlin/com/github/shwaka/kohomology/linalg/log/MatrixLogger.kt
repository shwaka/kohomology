package com.github.shwaka.kohomology.linalg.log

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
}

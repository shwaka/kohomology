package com.github.shwaka.kohomology.linalg.log

public class MatrixLogger {
    private val _entries: MutableList<MatrixLogEntry<*>> = mutableListOf()
    public val entries: List<MatrixLogEntry<*>>
        get() = _entries.toList()

    public fun addEntry(entry: MatrixLogEntry<*>) {
        this._entries.add(entry)
    }

    public fun clearEntries() {
        this._entries.clear()
    }
}

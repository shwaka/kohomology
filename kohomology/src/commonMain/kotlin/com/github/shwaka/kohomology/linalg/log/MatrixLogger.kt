package com.github.shwaka.kohomology.linalg.log

public class MatrixLogger {
    private val entries: MutableList<MatrixLogEntry<*>> = mutableListOf()

    public fun addEntry(entry: MatrixLogEntry<*>) {
        this.entries.add(entry)
    }
}

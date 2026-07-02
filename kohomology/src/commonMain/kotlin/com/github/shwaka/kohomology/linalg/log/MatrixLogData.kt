package com.github.shwaka.kohomology.linalg.log

public sealed interface MatrixLogData {
    public data class MultiplyMatrix(
        val firstRowCount: Int,
        val firstColCount: Int,
        val secondColCount: Int,
    ) : MatrixLogData {
        public val secondRowCount: Int
            get() = firstColCount
    }
}

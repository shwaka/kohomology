package com.github.shwaka.kohomology.linalg.log

import kotlin.time.Duration

public data class MatrixLogEntry<D : MatrixLogData>(
    public val duration: Duration,
    public val data: D,
)

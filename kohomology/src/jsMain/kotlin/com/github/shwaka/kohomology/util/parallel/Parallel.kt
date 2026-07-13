package com.github.shwaka.kohomology.util.parallel

internal actual fun <T, R> parallelMap(
    values: List<T>,
    config: ParallelConfig,
    transform: (T) -> R,
): List<R> = values.map(transform)

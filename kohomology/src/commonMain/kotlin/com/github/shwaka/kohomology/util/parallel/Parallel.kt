package com.github.shwaka.kohomology.util.parallel

internal data class ParallelConfig(
    val minSize: Int = 128,
    val chunkSize: Int = 16,
    val parallelism: Int? = null,
) {
    init {
        require(minSize >= 0) { "minSize must be non-negative" }
        require(chunkSize > 0) { "chunkSize must be positive" }
        require(parallelism == null || parallelism > 0) { "parallelism must be positive" }
    }
}

internal expect fun <T, R> parallelMap(
    values: List<T>,
    config: ParallelConfig = ParallelConfig(),
    transform: (T) -> R,
): List<R>

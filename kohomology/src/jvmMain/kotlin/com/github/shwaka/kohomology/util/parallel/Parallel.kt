package com.github.shwaka.kohomology.util.parallel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
internal actual fun <T, R> parallelMap(
    values: List<T>,
    config: ParallelConfig,
    transform: (T) -> R,
): List<R> {
    if (values.size < config.minSize) {
        return values.map(transform)
    }
    return if (config.parallelism == null) {
        parallelMapWithDefaultDispatcher(values, config.chunkSize, transform)
    } else {
        val dispatcher = Dispatchers.Default.limitedParallelism(config.parallelism)
        runBlocking {
            values.chunked(config.chunkSize).map { chunk ->
                async(dispatcher) {
                    chunk.map(transform)
                }
            }.flatMap { deferred ->
                deferred.await()
            }
        }
    }
}

private fun <T, R> parallelMapWithDefaultDispatcher(
    values: List<T>,
    chunkSize: Int,
    transform: (T) -> R,
): List<R> = runBlocking {
    values.chunked(chunkSize).map { chunk ->
        async(Dispatchers.Default) {
            chunk.map(transform)
        }
    }.flatMap { deferred -> deferred.await() }
}

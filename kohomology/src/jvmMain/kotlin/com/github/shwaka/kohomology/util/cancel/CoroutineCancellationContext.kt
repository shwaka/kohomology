package com.github.shwaka.kohomology.util.cancel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

public class CoroutineCancellationContext : CancellationContext {
    private val storage: CancellationCheckerStorage =
        ThreadLocalCancellationCheckerStorage()

    override fun check() {
        storage.currentChecker()?.check()
    }

    public suspend fun <T> runWithTimeoutCoroutine(
        timeout: Duration,
        block: () -> T,
    ): CancellationResult<T> = try {
        withTimeout(timeout) {
            withContext(Dispatchers.Default) {
                val context = currentCoroutineContext()
                val checker = CancellationChecker {
                    context.ensureActive()
                }
                val value = storage.withChecker(checker) {
                    block()
                }
                CancellationResult.Success(value)
            }
        }
    } catch (_: TimeoutCancellationException) {
        CancellationResult.Cancelled
    }

    override fun <T> runWithTimeout(
        timeout: Duration,
        block: () -> T,
    ): CancellationResult<T> = runBlocking {
        runWithTimeoutCoroutine(timeout, block)
    }
}

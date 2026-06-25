package com.github.shwaka.kohomology.util.cancel

import kotlin.time.Duration

public sealed interface CancellationResult<out T> {
    public data object Cancelled : CancellationResult<Nothing>
    public data class Success<T>(val value: T) : CancellationResult<T>
}

public fun interface CancellationChecker {
    public fun check()
}

public interface CancellationContext {
    public fun check()

    public fun <T> runWithTimeout(
        timeout: Duration,
        block: () -> T,
    ): CancellationResult<T>
}

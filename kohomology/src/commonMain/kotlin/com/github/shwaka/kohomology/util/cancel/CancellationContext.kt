package com.github.shwaka.kohomology.util.cancel

import kotlin.time.Duration

public sealed interface CancellationResult<out T> {
    public data class Cancelled(val timeout: Duration) : CancellationResult<Nothing>
    public data class Success<T>(val value: T) : CancellationResult<T>
}

public fun interface CancellationChecker {
    public fun check()
}

public interface CancellationContext {
    public fun check()

    public fun <T> runWithTimeout(
        timeout: Duration?,
        block: () -> T,
    ): CancellationResult<T>

    public companion object {
        public fun getDefault(): CancellationContext {
            return getDefaultCancellationContext()
        }
    }
}

internal expect fun getDefaultCancellationContext(): CancellationContext

public abstract class CancellationContextBase(
    internal val storage: CancellationCheckerStorage,
) : CancellationContext {
    override fun check() {
        this.storage.currentChecker()?.check()
    }

    override fun <T> runWithTimeout(
        timeout: Duration?,
        block: () -> T,
    ): CancellationResult<T> {
        return if (timeout == null) {
            CancellationResult.Success(block())
        } else {
            this.runWithTimeoutImpl(timeout, block)
        }
    }

    internal abstract fun <T> runWithTimeoutImpl(
        timeout: Duration,
        block: () -> T,
    ): CancellationResult<T>
}

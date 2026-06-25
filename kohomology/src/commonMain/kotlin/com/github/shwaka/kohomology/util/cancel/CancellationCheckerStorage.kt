package com.github.shwaka.kohomology.util.cancel

public interface CancellationCheckerStorage {
    public fun currentChecker(): CancellationChecker?

    public fun <T> withChecker(
        checker: CancellationChecker,
        block: () -> T,
    ): T

    public companion object {
        public fun getDefault(): CancellationCheckerStorage {
            return getDefaultStorage()
        }
    }
}

internal expect fun getDefaultStorage(): CancellationCheckerStorage

public class SingleThreadCancellationCheckerStorage : CancellationCheckerStorage {
    private var checker: CancellationChecker? = null

    public override fun currentChecker(): CancellationChecker? =
        checker

    public override fun <T> withChecker(
        checker: CancellationChecker,
        block: () -> T,
    ): T {
        val previous = this.checker
        this.checker = checker

        return try {
            block()
        } finally {
            this.checker = previous
        }
    }
}

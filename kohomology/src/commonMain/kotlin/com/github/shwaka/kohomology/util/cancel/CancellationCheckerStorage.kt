package com.github.shwaka.kohomology.util.cancel

internal expect class CancellationCheckerStorage() {
    fun currentChecker(): CancellationChecker?

    fun <T> withChecker(
        checker: CancellationChecker,
        block: () -> T,
    ): T
}

internal class SingleThreadCancellationCheckerStorage {
    private var checker: CancellationChecker? = null

    fun currentChecker(): CancellationChecker? =
        checker

    fun <T> withChecker(
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

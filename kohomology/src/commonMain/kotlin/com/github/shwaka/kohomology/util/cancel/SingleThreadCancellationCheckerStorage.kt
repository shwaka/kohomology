package com.github.shwaka.kohomology.util.cancel

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

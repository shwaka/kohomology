package com.github.shwaka.kohomology.util.cancel

public class ThreadLocalCancellationCheckerStorage : CancellationCheckerStorage {
    private val threadLocal = ThreadLocal<CancellationChecker?>()

    public override fun currentChecker(): CancellationChecker? =
        threadLocal.get()

    public override fun <T> withChecker(
        checker: CancellationChecker,
        block: () -> T,
    ): T {
        val previous = threadLocal.get()
        threadLocal.set(checker)

        return try {
            block()
        } finally {
            if (previous == null) {
                threadLocal.remove()
            } else {
                threadLocal.set(previous)
            }
        }
    }
}

internal actual fun getDefaultStorage(): CancellationCheckerStorage {
    return ThreadLocalCancellationCheckerStorage()
}

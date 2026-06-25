package com.github.shwaka.kohomology.util.cancel

internal actual class CancellationCheckerStorage {
    private val threadLocal = ThreadLocal<CancellationChecker?>()

    actual fun currentChecker(): CancellationChecker? =
        threadLocal.get()

    actual fun <T> withChecker(
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

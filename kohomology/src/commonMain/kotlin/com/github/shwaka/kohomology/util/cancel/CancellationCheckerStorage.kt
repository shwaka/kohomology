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

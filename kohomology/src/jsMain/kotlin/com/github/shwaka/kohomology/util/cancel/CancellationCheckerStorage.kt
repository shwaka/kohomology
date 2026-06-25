package com.github.shwaka.kohomology.util.cancel

internal actual fun getDefaultStorage(): CancellationCheckerStorage {
    return SingleThreadCancellationCheckerStorage()
}

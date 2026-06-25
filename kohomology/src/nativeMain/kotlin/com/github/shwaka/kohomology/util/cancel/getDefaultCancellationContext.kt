package com.github.shwaka.kohomology.util.cancel

public actual fun getDefaultCancellationContext(): CancellationContext {
    return DeadlineCancellationContext()
}

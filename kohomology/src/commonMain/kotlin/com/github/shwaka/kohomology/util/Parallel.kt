package com.github.shwaka.kohomology.util

// runBlocking, async, await do not exist on multiplatform project
expect fun <T, R> Iterable<T>.pmap(transform: (T) -> R): List<R>

package com.github.shwaka.kohomology.parallel

// runBlocking, async, await do not exist on multiplatform project
public expect fun <T, R> Iterable<T>.pmap(transform: (T) -> R): List<R>

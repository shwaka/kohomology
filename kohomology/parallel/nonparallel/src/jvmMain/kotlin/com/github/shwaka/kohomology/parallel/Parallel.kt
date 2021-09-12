package com.github.shwaka.kohomology.parallel

public actual fun <T, R> Iterable<T>.pmap(transform: (T) -> R): List<R> =
    this.map(transform)

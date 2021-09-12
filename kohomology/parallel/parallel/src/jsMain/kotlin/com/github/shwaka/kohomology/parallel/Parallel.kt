package com.github.shwaka.kohomology.parallel

public actual fun <T, R> Iterable<T>.pmap(transform: (T) -> R): List<R> =
    this.map(transform)
public actual fun <T> Iterable<T>.pforEach(action: (T) -> Unit): Unit =
    this.forEach(action)

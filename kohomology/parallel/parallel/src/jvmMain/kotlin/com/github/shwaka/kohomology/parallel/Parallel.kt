package com.github.shwaka.kohomology.parallel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

public actual fun <T, R> Iterable<T>.pmap(transform: (T) -> R): List<R> = runBlocking {
    this@pmap.map {
        async(Dispatchers.Default) { transform(it) }
    }.map { it.await() }
}

public actual fun <T> Iterable<T>.pforEach(action: (T) -> Unit): Unit = runBlocking {
    this@pforEach.map {
        async(Dispatchers.Default) { action(it) }
    }.forEach { it.await() }
}

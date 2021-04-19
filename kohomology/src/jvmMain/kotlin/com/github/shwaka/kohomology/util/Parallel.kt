package com.github.shwaka.kohomology.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

actual fun <T, R> Iterable<T>.pmap(transform: (T) -> R): List<R> = runBlocking {
    this@pmap.map { async(Dispatchers.Default) { transform(it) } }.map { it.await() }
}

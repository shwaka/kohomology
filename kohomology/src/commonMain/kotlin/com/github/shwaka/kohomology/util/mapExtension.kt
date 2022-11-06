package com.github.shwaka.kohomology.util

public fun <K, V> MutableMap<K, V>.exchange(key1: K, key2: K) {
    require(key1 != key2) { "key1 and key2 must be distinct, but both are $key1" }
    when (val row1 = this[key1]) {
        null -> when (val row2 = this[key2]) {
            null -> return
            else -> {
                this[key1] = row2
                this.remove(key2)
            }
        }
        else -> when (val row2 = this[key2]) {
            null -> {
                this[key2] = row1
                this.remove(key1)
            }
            else -> {
                this[key1] = row2
                this[key2] = row1
            }
        }
    }
}

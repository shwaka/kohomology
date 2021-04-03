package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.util.Sign

interface MonoidElement {
    val degree: Degree
}

sealed class MaybeZero<T>
class Zero<T> : MaybeZero<T>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}
data class NonZero<T>(val value: T) : MaybeZero<T>()

interface Monoid<E : MonoidElement> {
    val unit: E
    fun multiply(monoidElement1: E, monoidElement2: E): MaybeZero<Pair<E, Sign>>
    fun listAll(degree: Degree): List<E>
}

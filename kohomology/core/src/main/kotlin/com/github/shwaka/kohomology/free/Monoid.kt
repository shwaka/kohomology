package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.util.Sign

interface MonoidElement {
    val degree: Degree
}

sealed class MaybeZero<T>
class Zero<T> : MaybeZero<T>()
data class NonZero<T>(val value: T) : MaybeZero<T>()

interface Monoid<E : MonoidElement> {
    val unit: E
    fun multiply(a: E, b: E): MaybeZero<Pair<E, Sign>>
    fun listAll(degree: Degree): List<E>
}

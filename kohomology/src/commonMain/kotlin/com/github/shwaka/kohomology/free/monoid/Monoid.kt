package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BasisName

interface MonoidElement<D : Degree> : BasisName {
    val degree: D
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

interface Monoid<D : Degree, E : MonoidElement<D>> {
    val unit: E
    val degreeGroup: DegreeGroup<D>
    fun multiply(monoidElement1: E, monoidElement2: E): MaybeZero<Pair<E, Sign>>
    fun listElements(degree: D): List<E>
    fun listElements(degree: Int): List<E> = this.listElements(this.degreeGroup.fromInt(degree))
    fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        throw NotImplementedError("Monoid.listDegreesForAugmentedDegree() is not implemented for a general monoid")
    }
}

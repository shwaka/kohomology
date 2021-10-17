package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.util.IntAsSign
import com.github.shwaka.kohomology.vectsp.BasisName

public interface MonoidElement<D : Degree> : BasisName {
    public val degree: D
}

public sealed class MaybeZero<T>
public class Zero<T> : MaybeZero<T>() {
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
public data class NonZero<T>(val value: T) : MaybeZero<T>()

public interface Monoid<D : Degree, E : MonoidElement<D>> {
    public val unit: E
    public val degreeGroup: DegreeGroup<D>
    public fun multiply(monoidElement1: E, monoidElement2: E): MaybeZero<Pair<E, IntAsSign>>
    public fun listElements(degree: D): List<E>
    public fun listElements(degree: Int): List<E> = this.listElements(this.degreeGroup.fromInt(degree))
    public fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        throw NotImplementedError("Monoid.listDegreesForAugmentedDegree() is not implemented for a general monoid")
    }
}

public interface MonoidMorphismWithDegreeChange<DS : Degree, ES : MonoidElement<DS>, DT : Degree, ET : MonoidElement<DT>> {
    public val source: Monoid<DS, ES>
    public val target: Monoid<DT, ET>
    public val degreeMorphism: DegreeMorphism<DS, DT>
    public operator fun invoke(monoidElement: ES): ET
}

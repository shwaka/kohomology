package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BasisName

public interface GMonoidElement<D : Degree> : BasisName {
    public val degree: D
}

public sealed class SignedOrZero<out T>
public data class Signed<T>(val value: T, val sign: Sign) : SignedOrZero<T>()
public object Zero : SignedOrZero<Nothing>()

/**
 * A monoid representing basis of [com.github.shwaka.kohomology.free.FreeGAlgebra]
 * (or something similar to it).
 *
 * To model the multiplication in [com.github.shwaka.kohomology.free.FreeGAlgebra],
 * the multiplication of two elements in [GMonoid] can be one of
 * - an element,
 * - the minus of an element or
 * - zero.
 */
public interface GMonoid<D : Degree, E : GMonoidElement<D>> {
    public val unit: E
    public val isCommutative: Boolean
    public val boundedness: Boundedness
    public val degreeGroup: DegreeGroup<D>
    public fun multiply(monoidElement1: E, monoidElement2: E): SignedOrZero<E>
    public fun listElements(degree: D): List<E>
    public fun listElements(degree: Int): List<E> = this.listElements(this.degreeGroup.fromInt(degree))
    public fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        throw NotImplementedError("Monoid.listDegreesForAugmentedDegree() is not implemented for a general monoid")
    }
}

public interface GMonoidMorphismWithDegreeChange<DS : Degree, ES : GMonoidElement<DS>, DT : Degree, ET : GMonoidElement<DT>> {
    public val source: GMonoid<DS, ES>
    public val target: GMonoid<DT, ET>
    public val degreeMorphism: DegreeMorphism<DS, DT>
    public operator fun invoke(monoidElement: ES): ET
}

package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.util.PartialIdentifier

/**
 * Wrapper of [Int] as an element of [IntDegreeGroup].
 */
public data class IntDegree(val value: Int) : Degree {
    override val identifier: PartialIdentifier
        get() = PartialIdentifier.fromInt(value)

    override fun isEven(): Boolean {
        return (this.value % 2 == 0)
    }

    override fun isZero(): Boolean {
        return this.value == 0
    }

    override fun isOne(): Boolean {
        return this.value == 1
    }

    override fun toString(): String {
        return this.value.toString()
    }
}

/**
 * An [AugmentedDegreeGroup] representing the additive group Z of integers.
 */
public object IntDegreeGroup : AugmentedDegreeGroup<IntDegree> {
    override val context: AugmentedDegreeContext<IntDegree> by lazy {
        AugmentedDegreeContext(this)
    }

    override fun fromInt(n: Int): IntDegree {
        return IntDegree(n)
    }

    override fun augmentation(degree: IntDegree): Int {
        return degree.value
    }

    override fun add(degree1: IntDegree, degree2: IntDegree): IntDegree {
        return IntDegree(degree1.value + degree2.value)
    }

    override fun subtract(degree1: IntDegree, degree2: IntDegree): IntDegree {
        return IntDegree(degree1.value - degree2.value)
    }

    override fun multiply(degree: IntDegree, n: Int): IntDegree {
        return IntDegree(degree.value * n)
    }

    override fun listAllDegrees(augmentedDegree: Int): List<IntDegree> {
        return listOf(IntDegree(augmentedDegree))
    }

    override fun contains(degree: IntDegree): Boolean {
        // type information is sufficient
        return true
    }
}

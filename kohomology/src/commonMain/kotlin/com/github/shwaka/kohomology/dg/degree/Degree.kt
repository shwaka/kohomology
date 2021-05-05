package com.github.shwaka.kohomology.dg.degree

/**
 * Represents degrees in graded objects.
 *
 * Operations are implemented in [DegreeGroup].
 * See [IntDegree] and [LinearDegree] for examples.
 */
interface Degree {
    fun isZero(): Boolean
    fun isNotZero(): Boolean = !this.isZero()
    fun isOne(): Boolean
    fun isEven(): Boolean
    fun isOdd(): Boolean = !this.isEven()
}

open class DegreeContext<D : Degree>(group: DegreeGroup<D>) : DegreeGroup<D> by group {
    fun Int.toDegree(): D = this@DegreeContext.fromInt(this)
    operator fun D.plus(other: D): D = this@DegreeContext.add(this, other)
    operator fun Int.plus(other: D): D = this@DegreeContext.add(this.toDegree(), other)
    operator fun D.plus(other: Int): D = this@DegreeContext.add(this, other.toDegree())
    operator fun D.minus(other: D): D = this@DegreeContext.subtract(this, other)
    operator fun Int.minus(other: D): D = this@DegreeContext.subtract(this.toDegree(), other)
    operator fun D.minus(other: Int): D = this@DegreeContext.subtract(this, other.toDegree())
    operator fun D.times(n: Int): D = this@DegreeContext.multiply(this, n)
    operator fun Int.times(degree: D): D = this@DegreeContext.multiply(degree, this)
}

class AugmentedDegreeContext<D : Degree>(group: AugmentedDegreeGroup<D>) :
    DegreeContext<D>(group),
    AugmentedDegreeGroupOperations<D> by group {
    fun D.toInt(): Int = this@AugmentedDegreeContext.augmentation(this)
}

/**
 * Represents a group used for degrees.
 */
interface DegreeGroup<D : Degree> {
    val context: DegreeContext<D>

    /**
     * A group homomorphism from Z to a [DegreeGroup].
     */
    fun fromInt(n: Int): D

    /** Adds two degrees. */
    fun add(degree1: D, degree2: D): D

    /**
     * Subtracts a degree from another degree.
     *
     * Compared with addition, this method is used in fewer places, but necessary in
     * [com.github.shwaka.kohomology.dg.GLinearMap.findPreimage] and
     * [com.github.shwaka.kohomology.free.FreeMonoid.listAll].
     */
    fun subtract(degree1: D, degree2: D): D

    /** Multiplies an integer to a degree. */
    fun multiply(degree: D, n: Int): D
    val zero: D
        get() = this.fromInt(0)
}

interface AugmentedDegreeGroupOperations<D : Degree> {
    fun augmentation(degree: D): Int
}

/**
 * Represents a group used for degrees in free objects (polynomial and exterior algebras).
 */
interface AugmentedDegreeGroup<D : Degree> : DegreeGroup<D>, AugmentedDegreeGroupOperations<D> {
    /** An augmentation homomorphism to Z. */
    override val context: AugmentedDegreeContext<D>
}

package com.github.shwaka.kohomology.dg.degree

/**
 * Represents degrees in graded objects.
 *
 * Operations are implemented in [DegreeGroup].
 * See [IntDegree] and [MultiDegree] for examples.
 */
public interface Degree {
    public fun isZero(): Boolean
    public fun isNotZero(): Boolean = !this.isZero()
    public fun isOne(): Boolean
    public fun isEven(): Boolean
    public fun isOdd(): Boolean = !this.isEven()
}

public open class DegreeContext<D : Degree>(group: DegreeGroup<D>) : DegreeGroup<D> by group {
    public fun Int.toDegree(): D = this@DegreeContext.fromInt(this)
    public operator fun D.plus(other: D): D = this@DegreeContext.add(this, other)
    public operator fun Int.plus(other: D): D = this@DegreeContext.add(this.toDegree(), other)
    public operator fun D.plus(other: Int): D = this@DegreeContext.add(this, other.toDegree())
    public operator fun D.minus(other: D): D = this@DegreeContext.subtract(this, other)
    public operator fun Int.minus(other: D): D = this@DegreeContext.subtract(this.toDegree(), other)
    public operator fun D.minus(other: Int): D = this@DegreeContext.subtract(this, other.toDegree())
    public operator fun D.times(n: Int): D = this@DegreeContext.multiply(this, n)
    public operator fun Int.times(degree: D): D = this@DegreeContext.multiply(degree, this)
    public operator fun D.unaryMinus(): D = this@DegreeContext.multiply(this, -1)
}

public class AugmentedDegreeContext<D : Degree>(group: AugmentedDegreeGroup<D>) :
    DegreeContext<D>(group),
    AugmentedDegreeGroupOperations<D> by group {
    public fun D.toInt(): Int = this@AugmentedDegreeContext.augmentation(this)
}

/**
 * Represents a group used for degrees.
 */
public interface DegreeGroup<D : Degree> {
    public val context: DegreeContext<D>

    /**
     * A group homomorphism from Z to a [DegreeGroup].
     */
    public fun fromInt(n: Int): D

    /** Adds two degrees. */
    public fun add(degree1: D, degree2: D): D

    /**
     * Subtracts a degree from another degree.
     *
     * Compared with addition, this method is used in fewer places, but necessary in
     * [com.github.shwaka.kohomology.dg.GLinearMap.findPreimage] and
     * [com.github.shwaka.kohomology.free.monoid.FreeMonoid.listElements].
     */
    public fun subtract(degree1: D, degree2: D): D

    /** Multiplies an integer to a degree. */
    public fun multiply(degree: D, n: Int): D
    public val zero: D
        get() = this.fromInt(0)
    public operator fun contains(degree: D): Boolean
}

public interface AugmentedDegreeGroupOperations<D : Degree> {
    public fun augmentation(degree: D): Int
    public fun listAllDegrees(augmentedDegree: Int): List<D>
}

/**
 * Represents a group used for degrees in free objects (polynomial and exterior algebras).
 */
public interface AugmentedDegreeGroup<D : Degree> : DegreeGroup<D>, AugmentedDegreeGroupOperations<D> {
    /** An augmentation homomorphism to Z. */
    override val context: AugmentedDegreeContext<D>
}

public interface DegreeMorphism<DS : Degree, DT : Degree> {
    public val source: DegreeGroup<DS>
    public val target: DegreeGroup<DT>
    public operator fun invoke(degree: DS): DT
}

public interface AugmentedDegreeMorphism<DS : Degree, DT : Degree> : DegreeMorphism<DS, DT> {
    override val source: AugmentedDegreeGroup<DS>
    override val target: AugmentedDegreeGroup<DT>
}

public class AugmentationDegreeMorphism<D : Degree>(override val source: AugmentedDegreeGroup<D>) : AugmentedDegreeMorphism<D, IntDegree> {
    override val target: AugmentedDegreeGroup<IntDegree> = IntDegreeGroup
    override fun invoke(degree: D): IntDegree {
        return IntDegree(this.source.augmentation(degree))
    }
}

package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.util.PartialIdentifier
import com.github.shwaka.kohomology.util.Sign

/**
 * Represents degrees in graded objects.
 *
 * Operations are implemented in [DegreeGroup].
 * See [IntDegree] and [MultiDegree] for examples.
 */
public interface Degree {
    /** The name of this [Degree] containing only alphanumeric characters and underscore. */
    public val identifier: PartialIdentifier

    /** Returns `true` if the degree is zero. */
    public fun isZero(): Boolean

    /** Returns `true` if the degree is nonzero. */
    public fun isNotZero(): Boolean = !this.isZero()

    /** Returns `true` if the degree is one. */
    public fun isOne(): Boolean

    /** Returns `true` if the degree is even. */
    public fun isEven(): Boolean

    /** Returns `true` if the degree is odd. */
    public fun isOdd(): Boolean = !this.isEven()

    /** (-1)^degree. */
    public val sign: Sign
        get() = Sign.fromParity(this.isEven())

    /** Returns (-1)^(this * other) */
    public fun koszulSign(other: Degree): Sign {
        return Sign.fromParity(this.isEven() || other.isEven())
    }
}

/**
 * Context for [DegreeGroup], stored in [DegreeGroup.context].
 */
public open class DegreeContext<D : Degree>(group: DegreeGroup<D>) : DegreeGroup<D> by group {
    /** The inclusion from [IntDegreeGroup] to the current [DegreeGroup]. */
    public fun Int.toDegree(): D = this@DegreeContext.fromInt(this)

    /** Adds two [Degree]s. */
    public operator fun D.plus(other: D): D = this@DegreeContext.add(this, other)
    public operator fun Int.plus(other: D): D = this@DegreeContext.add(this.toDegree(), other)
    public operator fun D.plus(other: Int): D = this@DegreeContext.add(this, other.toDegree())

    /** Subtract a [Degree] from another. */
    public operator fun D.minus(other: D): D = this@DegreeContext.subtract(this, other)
    public operator fun Int.minus(other: D): D = this@DegreeContext.subtract(this.toDegree(), other)
    public operator fun D.minus(other: Int): D = this@DegreeContext.subtract(this, other.toDegree())

    /** Multiplies two [Degree]s. */
    public operator fun D.times(n: Int): D = this@DegreeContext.multiply(this, n)
    public operator fun Int.times(degree: D): D = this@DegreeContext.multiply(degree, this)

    /** Returns the minus of a [Degree].  */
    public operator fun D.unaryMinus(): D = this@DegreeContext.multiply(this, -1)

    /** Adds all [Degree]s in an iterable. */
    public fun Iterable<D>.sum(): D = this.fold(this@DegreeContext.zero) { acc, d -> acc + d }
}

/**
 * Context for [AugmentedDegreeGroup] extending [DegreeContext].
 */
public class AugmentedDegreeContext<D : Degree>(group: AugmentedDegreeGroup<D>) :
    DegreeContext<D>(group),
    AugmentedDegreeGroupOperations<D> by group {
    /** Sends a [Degree] in [AugmentedDegreeGroup] by [AugmentedDegreeGroup.augmentation]. */
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

    /** The zero element of the [DegreeGroup] */
    public val zero: D
        get() = this.fromInt(0)

    /** Returns `true` if [degree] is contained in the [DegreeGroup]. */
    public operator fun contains(degree: D): Boolean
}

/**
 * Additional operations for [AugmentedDegreeGroup].
 */
public interface AugmentedDegreeGroupOperations<D : Degree> {
    public fun augmentation(degree: D): Int
    public fun listAllDegrees(augmentedDegree: Int): List<D>
}

/**
 * Represents a group used for degrees in free objects (polynomial and exterior algebras).
 *
 * Used in
 * [FreeGAlgebra][com.github.shwaka.kohomology.free.FreeGAlgebra]
 * and
 * [FreeDGAlgebra][com.github.shwaka.kohomology.free.FreeDGAlgebra].
 */
public interface AugmentedDegreeGroup<D : Degree> : DegreeGroup<D>, AugmentedDegreeGroupOperations<D> {
    /** An augmentation homomorphism to Z. */
    override val context: AugmentedDegreeContext<D>
}

/**
 * Homomorphism between two [DegreeGroup]s.
 */
public interface DegreeMorphism<DS : Degree, DT : Degree> {
    /** The source [DegreeGroup] */
    public val source: DegreeGroup<DS>

    /** The target [DegreeGroup] */
    public val target: DegreeGroup<DT>

    /** Sends [degree] from [source] to [target]. */
    public operator fun invoke(degree: DS): DT
}

/**
 * Homomorphism between two [AugmentedDegreeGroup]s.
 */
public interface AugmentedDegreeMorphism<DS : Degree, DT : Degree> : DegreeMorphism<DS, DT> {
    override val source: AugmentedDegreeGroup<DS>
    override val target: AugmentedDegreeGroup<DT>
}

/**
 * The homomorphism from a [AugmentedDegreeGroup] to [IntDegreeGroup] given by [AugmentedDegreeGroup.augmentation].
 */
public class AugmentationDegreeMorphism<D : Degree>(override val source: AugmentedDegreeGroup<D>) : AugmentedDegreeMorphism<D, IntDegree> {
    override val target: AugmentedDegreeGroup<IntDegree> = IntDegreeGroup
    override fun invoke(degree: D): IntDegree {
        return IntDegree(this.source.augmentation(degree))
    }
}

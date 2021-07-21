package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeMorphism
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.PrintType

internal sealed class IndeterminateList<D : Degree, I : IndeterminateName>(
    protected val degreeGroup: AugmentedDegreeGroup<D>,
    protected val rawList: List<Indeterminate<D, I>>
) {
    fun isEmpty(): Boolean = this.rawList.isEmpty()
    fun first(): Indeterminate<D, I> = this.rawList.first()
    abstract fun drop(): IndeterminateList<D, I>
    val size: Int
        get() = this.rawList.size
    fun <T> zip(list: List<T>): List<Pair<Indeterminate<D, I>, T>> = this.rawList.zip(list)
    fun mapIndexedIntArray(transform: (Int, Indeterminate<D, I>) -> Int): IntArray {
        return IntArray(this.size) { transform(it, this.rawList[it]) }
    }
    fun <T> map(transform: (Indeterminate<D, I>) -> T): List<T> {
        return this.rawList.map(transform)
    }
    fun <T> mapIndexed(transform: (Int, Indeterminate<D, I>) -> T): List<T> {
        return List(this.size) { transform(it, this.rawList[it]) }
    }
    operator fun get(index: Int): Indeterminate<D, I> = this.rawList[index]
    val indices: IntRange = this.rawList.indices
    fun joinToString(separator: CharSequence, transform: ((Indeterminate<D, I>) -> String)? = null): String {
        return this.rawList.joinToString(separator = separator, transform = transform)
    }

    abstract fun isAllowedDegree(degree: D): Boolean
    abstract fun <D_ : Degree> convertDegree(degreeMorphism: AugmentedDegreeMorphism<D, D_>): IndeterminateList<D_, I>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as IndeterminateList<*, *>

        if (rawList != other.rawList) return false

        return true
    }

    override fun hashCode(): Int {
        return rawList.hashCode()
    }

    companion object {
        fun <D : Degree, I : IndeterminateName> from(degreeGroup: AugmentedDegreeGroup<D>, indeterminateList: List<Indeterminate<D, I>>): IndeterminateList<D, I> {
            return degreeGroup.context.run {
                when {
                    indeterminateList.any { it.degree.toInt() == 0 } -> {
                        val degree0IndeterminateList = indeterminateList.filter { it.degree.toInt() == 0 }
                        throw IllegalArgumentException(
                            "Cannot consider indeterminate of degree zero: $degree0IndeterminateList"
                        )
                    }
                    indeterminateList.all { it.degree.toInt() > 0 } -> PositiveIndeterminateList(degreeGroup, indeterminateList)
                    indeterminateList.all { it.degree.toInt() < 0 } -> NegativeIndeterminateList(degreeGroup, indeterminateList)
                    else -> {
                        val positiveIndeterminateList = indeterminateList.filter { it.degree.toInt() > 0 }
                        val negativeIndeterminateList = indeterminateList.filter { it.degree.toInt() < 0 }
                        throw IllegalArgumentException(
                            "Cannot consider a list of indeterminate containing both positive and negative degrees; " +
                                "positive: $positiveIndeterminateList, negative: $negativeIndeterminateList"
                        )
                    }
                }
            }
        }
    }
}

internal class PositiveIndeterminateList<D : Degree, I : IndeterminateName>(
    degreeGroup: AugmentedDegreeGroup<D>,
    rawList: List<Indeterminate<D, I>>
) : IndeterminateList<D, I>(degreeGroup, rawList) {
    init {
        this.degreeGroup.context.run {
            for (indeterminate in rawList) {
                if (indeterminate.degree.toInt() <= 0)
                    throw IllegalArgumentException("The degree of an indeterminate in PositiveIndeterminateList must be positive")
            }
        }
    }

    override fun isAllowedDegree(degree: D): Boolean {
        return this.degreeGroup.context.run {
            degree.toInt() >= 0
        }
    }

    override fun <D_ : Degree> convertDegree(degreeMorphism: AugmentedDegreeMorphism<D, D_>): PositiveIndeterminateList<D_, I> {
        if (this.degreeGroup != degreeMorphism.source)
            throw IllegalArgumentException("The source of degreeMorphism is invalid")
        val rawList = this.rawList.map { indeterminate ->
            indeterminate.convertDegree(degreeMorphism)
        }
        return PositiveIndeterminateList(degreeMorphism.target, rawList)
    }

    override fun drop(): PositiveIndeterminateList<D, I> = PositiveIndeterminateList(this.degreeGroup, this.rawList.drop(1))
}

internal class NegativeIndeterminateList<D : Degree, I : IndeterminateName>(
    degreeGroup: AugmentedDegreeGroup<D>,
    rawList: List<Indeterminate<D, I>>
) : IndeterminateList<D, I>(degreeGroup, rawList) {
    init {
        this.degreeGroup.context.run {
            for (indeterminate in rawList) {
                if (indeterminate.degree.toInt() >= 0)
                    throw IllegalArgumentException("The degree of an indeterminate in NegativeIndeterminateList must be negative")
            }
        }
    }

    override fun isAllowedDegree(degree: D): Boolean {
        return this.degreeGroup.context.run {
            degree.toInt() <= 0
        }
    }

    override fun <D_ : Degree> convertDegree(degreeMorphism: AugmentedDegreeMorphism<D, D_>): NegativeIndeterminateList<D_, I> {
        if (this.degreeGroup != degreeMorphism.source)
            throw IllegalArgumentException("The source of degreeMorphism is invalid")
        val rawList = this.rawList.map { indeterminate ->
            indeterminate.convertDegree(degreeMorphism)
        }
        return NegativeIndeterminateList(degreeMorphism.target, rawList)
    }

    override fun drop(): NegativeIndeterminateList<D, I> = NegativeIndeterminateList(this.degreeGroup, this.rawList.drop(1))
}

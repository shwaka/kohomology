package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Degree
import com.github.shwaka.kohomology.util.IntAsDegree

data class GeneralizedIndeterminate<I : IndeterminateName, D : Degree>(val name: I, val degree: D) {
    companion object {
        operator fun <D : Degree> invoke(name: String, degree: D): Indeterminate<StringIndeterminateName> {
            return GeneralizedIndeterminate(StringIndeterminateName(name), degree)
        }
    }
    override fun toString(): String {
        return this.name.toString()
    }
    fun toTex(): String {
        return this.name.toTex()
    }
}

private sealed class GeneralizedIndeterminateList<I : IndeterminateName, D : Degree>(
    protected val rawList: List<GeneralizedIndeterminate<I, D>>
) {
    fun isEmpty(): Boolean = this.rawList.isEmpty()
    fun first(): GeneralizedIndeterminate<I, D> = this.rawList.first()
    abstract fun drop(): GeneralizedIndeterminateList<I, D>
    val size: Int
        get() = this.rawList.size
    fun <T> zip(list: List<T>): List<Pair<GeneralizedIndeterminate<I, D>, T>> = this.rawList.zip(list)
    fun mapIndexedIntArray(transform: (Int, GeneralizedIndeterminate<I, D>) -> Int): IntArray {
        return IntArray(this.size) { transform(it, this.rawList[it]) }
    }
    operator fun get(index: Int): GeneralizedIndeterminate<I, D> = this.rawList[index]

    abstract fun checkDegree(monomial: Monomial<I>, degreeLimit: IntAsDegree): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as GeneralizedIndeterminateList<*, *>

        if (rawList != other.rawList) return false

        return true
    }

    override fun hashCode(): Int {
        return rawList.hashCode()
    }

    companion object {
        fun <I : IndeterminateName, D : Degree> from(indeterminateList: List<GeneralizedIndeterminate<I, D>>): GeneralizedIndeterminateList<I, D> {
            return when {
                indeterminateList.any { it.degree.toInt() == 0 } -> throw IllegalArgumentException("Cannot consider an indeterminate of degree zero")
                indeterminateList.all { it.degree.toInt() > 0 } -> GeneralizedPositiveIndeterminateList(indeterminateList)
                indeterminateList.all { it.degree.toInt() < 0 } -> GeneralizedNegativeIndeterminateList(indeterminateList)
                else -> throw IllegalArgumentException("Cannot consider a list of indeterminate containing both positive and negative degrees")
            }
        }
    }
}

private class GeneralizedPositiveIndeterminateList<I : IndeterminateName>(
    rawList: List<Indeterminate<I>>
) : IndeterminateList<I>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree <= 0)
                throw IllegalArgumentException("The degree of an indeterminate in PositiveIndeterminateList must be positive")
        }
    }

    override fun checkDegree(monomial: Monomial<I>, degreeLimit: IntAsDegree): Boolean {
        // monomial.indeterminateList == this を確認しなくて良い？
        // private にしちゃったから困ってる
        return monomial.degree.toInt() <= degreeLimit
    }

    override fun drop(): PositiveIndeterminateList<I> = PositiveIndeterminateList(this.rawList.drop(1))
}

private class GeneralizedNegativeIndeterminateList<I : IndeterminateName>(
    rawList: List<Indeterminate<I>>
) : IndeterminateList<I>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree >= 0)
                throw IllegalArgumentException("The degree of an indeterminate in NegativeIndeterminateList must be negative")
        }
    }

    override fun checkDegree(monomial: Monomial<I>, degreeLimit: IntAsDegree): Boolean {
        // monomial.indeterminateList == this を確認しなくて良い？
        // private にしちゃったから困ってる
        return monomial.degree.toInt() >= degreeLimit
    }

    override fun drop(): NegativeIndeterminateList<I> = NegativeIndeterminateList(this.rawList.drop(1))
}

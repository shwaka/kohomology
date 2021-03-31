package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.vectsp.Degree

data class Indeterminate<I>(val name: I, val degree: Degree) {
    override fun toString(): String {
        return this.name.toString()
    }
}

private sealed class IndeterminateList<I>(
    protected val rawList: List<Indeterminate<I>>
) {
    fun isEmpty(): Boolean = this.rawList.isEmpty()
    fun first(): Indeterminate<I> = this.rawList.first()
    abstract fun drop(): IndeterminateList<I>
    val size: Int
        get() = this.rawList.size
    fun <T> zip(list: List<T>): List<Pair<Indeterminate<I>, T>> = this.rawList.zip(list)
    operator fun get(index: Int): Indeterminate<I> = this.rawList[index]

    abstract fun checkDegree(monomial: Monomial<I>, degreeLimit: Degree): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as IndeterminateList<*>

        if (rawList != other.rawList) return false

        return true
    }

    override fun hashCode(): Int {
        return rawList.hashCode()
    }

    companion object {
        fun <I> from(indeterminateList: List<Indeterminate<I>>): IndeterminateList<I> {
            return when {
                indeterminateList.any { it.degree == 0 } -> throw IllegalArgumentException("Cannot consider an indeterminate of degree zero")
                indeterminateList.all { it.degree > 0 } -> PositiveIndeterminateList(indeterminateList)
                indeterminateList.all { it.degree < 0 } -> NegativeIndeterminateList(indeterminateList)
                else -> throw IllegalArgumentException("Cannot consider a list of indeterminate containing both positive and negative degrees")
            }
        }
    }
}

private class PositiveIndeterminateList<I>(
    rawList: List<Indeterminate<I>>
) : IndeterminateList<I>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree <= 0)
                throw IllegalArgumentException("The degree of an indeterminate in PositiveIndeterminateList must be positive")
        }
    }

    override fun checkDegree(monomial: Monomial<I>, degreeLimit: Degree): Boolean {
        // monomial.indeterminateList == this を確認しなくて良い？
        // private にしちゃったから困ってる
        return monomial.totalDegree() <= degreeLimit
    }

    override fun drop(): PositiveIndeterminateList<I> = PositiveIndeterminateList(this.rawList.drop(1))
}

private class NegativeIndeterminateList<I>(
    rawList: List<Indeterminate<I>>
) : IndeterminateList<I>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree >= 0)
                throw IllegalArgumentException("The degree of an indeterminate in NegativeIndeterminateList must be negative")
        }
    }

    override fun checkDegree(monomial: Monomial<I>, degreeLimit: Degree): Boolean {
        // monomial.indeterminateList == this を確認しなくて良い？
        // private にしちゃったから困ってる
        return monomial.totalDegree() >= degreeLimit
    }

    override fun drop(): NegativeIndeterminateList<I> = NegativeIndeterminateList(this.rawList.drop(1))
}

class Monomial<I> private constructor(
    private val indeterminateList: IndeterminateList<I>,
    val exponentList: List<Int>,
) {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw Exception("Invalid size of the exponent list")
    }

    constructor(
        indeterminateList: List<Indeterminate<I>>,
        exponentList: List<Int>
    ) : this(IndeterminateList.from(indeterminateList), exponentList)

    fun totalDegree(): Int {
        return this.indeterminateList.zip(this.exponentList)
            .map { (generator, exponent) -> generator.degree * exponent }
            .reduce { a, b -> a + b }
    }

    private fun drop(): Monomial<I> {
        if (this.indeterminateList.isEmpty())
            throw Exception("This can't happen!")
        return Monomial(this.indeterminateList.drop(), this.exponentList.drop(1))
    }

    private fun getNextMonomial(maxDegree: Degree): Monomial<I>? {
        if (this.indeterminateList.isEmpty())
            return null
        this.increaseFirstExponent(maxDegree)?.let { return it }
        this.drop().getNextMonomial(maxDegree)?.let {
            val newExponents = listOf(0) + it.exponentList
            return Monomial(this.indeterminateList, newExponents)
        }
        return null
    }

    private fun increaseFirstExponent(maxDegree: Degree): Monomial<I>? {
        // 奇数次の場合
        if ((this.indeterminateList.first().degree % 2 == 1) and (this.exponentList.first() == 1))
            return null
        val newExponents = listOf(this.exponentList.first() + 1) + this.exponentList.drop(1)
        val firstIncreased = Monomial(this.indeterminateList, newExponents)
        return if (this.indeterminateList.checkDegree(firstIncreased, maxDegree)) firstIncreased else null
    }

    operator fun times(other: Monomial<I>): Pair<Monomial<I>, Int>? {
        if (this.indeterminateList != other.indeterminateList)
            throw IllegalArgumentException("Cannot multiply two monomials of different indeterminate")
        val size = this.indeterminateList.size
        val exponentList = this.exponentList.zip(other.exponentList).map { (p, q) -> p + q }
        for (i in 0 until size) {
            if ((this.indeterminateList[i].degree % 2 == 1) and (exponentList[i] >= 2))
                return null
        }
        var sign = 1
        for (i in 0 until size) {
            if ((this.indeterminateList[i].degree % 2 == 1) and (this.exponentList[i] == 1)) {
                for (j in 0 until i) {
                    if ((other.indeterminateList[j].degree % 2 == 1) and (other.exponentList[j] == 1)) {
                        sign = -sign
                    }
                }
            }
        }
        val monomial = Monomial(this.indeterminateList, exponentList)
        return Pair(monomial, sign)
    }

    override fun toString(): String {
        return this.indeterminateList.zip(this.exponentList).joinToString("") { (indeterminate, exponent) ->
            when (exponent) {
                0 -> ""
                1 -> indeterminate.toString()
                else -> "$indeterminate^$exponent"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Monomial<*>

        if (indeterminateList != other.indeterminateList) return false
        if (exponentList != other.exponentList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = indeterminateList.hashCode()
        result = 31 * result + exponentList.hashCode()
        return result
    }


    companion object {
        fun <I> listAll(indeterminateList: List<Indeterminate<I>>, degree: Degree): List<Monomial<I>> {
            val exponentList = List(indeterminateList.size) { 0 }
            var monomial: Monomial<I>? = Monomial(indeterminateList, exponentList)
            val monomialList: MutableList<Monomial<I>> = mutableListOf()
            while (monomial != null) {
                if (monomial.totalDegree() == degree)
                    monomialList.add(monomial)
                monomial = monomial.getNextMonomial(degree)
            }
            return monomialList
        }

        fun <I> fromIndeterminate(indeterminateList: List<Indeterminate<I>>, indeterminate: Indeterminate<I>): Monomial<I> {
            val index = indeterminateList.indexOf(indeterminate)
            if (index == -1)
                throw IllegalArgumentException("Indeterminate $indeterminate is not contained in the indeterminate list $indeterminateList")
            val exponentList = indeterminateList.map { if (it == indeterminate) 1 else 0 }
            return Monomial(indeterminateList, exponentList)
        }
    }
}

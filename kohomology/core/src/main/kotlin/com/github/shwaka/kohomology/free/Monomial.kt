package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.vectsp.Degree

data class Indeterminate<I>(val name: I, val degree: Degree) {
    override fun toString(): String {
        return this.name.toString()
    }
}

data class Monomial<I>(
    val indeterminateList: List<Indeterminate<I>>,
    val exponentList: List<Int>,
) {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw Exception("Invalid size of the exponent list")
    }

    fun totalDegree(): Int {
        return this.indeterminateList.zip(this.exponentList)
            .map { (generator, exponent) -> generator.degree * exponent }
            .reduce { a, b -> a + b }
    }

    private fun drop(): Monomial<I> {
        if (this.indeterminateList.isEmpty())
            throw Exception("This can't happen!")
        return Monomial(this.indeterminateList.drop(1), this.exponentList.drop(1))
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
        return if (firstIncreased.totalDegree() <= maxDegree) firstIncreased else null
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
    }
}

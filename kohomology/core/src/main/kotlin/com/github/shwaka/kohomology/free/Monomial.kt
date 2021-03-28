package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.vectsp.Degree

data class Indeterminate<B>(val name: B, val degree: Degree)

data class Monomial<B>(
    val indeterminateList: List<Indeterminate<B>>,
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

    private fun drop(): Monomial<B> {
        if (this.indeterminateList.isEmpty())
            throw Exception("This can't happen!")
        return Monomial(this.indeterminateList.drop(1), this.exponentList.drop(1))
    }

    private fun getNextMonomial(maxDegree: Degree): Monomial<B>? {
        if (this.indeterminateList.isEmpty())
            return null
        this.increaseFirstExponent(maxDegree)?.let { return it }
        this.drop().getNextMonomial(maxDegree)?.let {
            val newExponents = listOf(0) + it.exponentList
            return Monomial(this.indeterminateList, newExponents)
        }
        return null
    }

    private fun increaseFirstExponent(maxDegree: Degree): Monomial<B>? {
        // 奇数次の場合
        if ((this.indeterminateList.first().degree % 2 == 1) and (this.exponentList.first() == 1))
            return null
        val newExponents = listOf(this.exponentList.first() + 1) + this.exponentList.drop(1)
        val firstIncreased = Monomial(this.indeterminateList, newExponents)
        return if (firstIncreased.totalDegree() <= maxDegree) firstIncreased else null
    }

    companion object {
        fun <B> listAll(generators: List<Indeterminate<B>>, degree: Degree): List<Monomial<B>> {
            val exponents = List(generators.size) { 0 }
            var monomial: Monomial<B>? = Monomial(generators, exponents)
            val monomialList: MutableList<Monomial<B>> = mutableListOf()
            while (monomial != null) {
                if (monomial.totalDegree() == degree)
                    monomialList.add(monomial)
                monomial = monomial.getNextMonomial(degree)
            }
            return monomialList
        }
    }
}

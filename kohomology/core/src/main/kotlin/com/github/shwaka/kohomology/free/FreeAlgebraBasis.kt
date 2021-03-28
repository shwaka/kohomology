package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.vectsp.Degree

data class FreeAlgebraGenerator<B>(val name: B, val degree: Degree)

data class FreeAlgebraBasis<B>(
    val generators: List<FreeAlgebraGenerator<B>>,
    val exponents: List<Int>,
) {
    init {
        if (this.generators.size != this.exponents.size)
            throw Exception("Invalid size of the exponent list")
    }

    fun totalDegree(): Int {
        return this.generators.zip(this.exponents)
            .map { (generator, exponent) -> generator.degree * exponent }
            .reduce { a, b -> a + b }
    }

    private fun drop(): FreeAlgebraBasis<B> {
        if (this.generators.isEmpty())
            throw Exception("This can't happen!")
        return FreeAlgebraBasis(this.generators.drop(1), this.exponents.drop(1))
    }

    private fun getNextBasis(maxDegree: Degree): FreeAlgebraBasis<B>? {
        if (this.generators.isEmpty())
            return null
        this.increaseFirstExponent(maxDegree)?.let { return it }
        this.drop().getNextBasis(maxDegree)?.let {
            val newExponents = listOf(0) + it.exponents
            return FreeAlgebraBasis(this.generators, newExponents)
        }
        return null
    }

    private fun increaseFirstExponent(maxDegree: Degree): FreeAlgebraBasis<B>? {
        // 奇数次の場合
        if ((this.generators.first().degree % 2 == 1) and (this.exponents.first() == 1))
            return null
        val newExponents = listOf(this.exponents.first() + 1) + this.exponents.drop(1)
        val firstIncreased = FreeAlgebraBasis(this.generators, newExponents)
        return if (firstIncreased.totalDegree() <= maxDegree) firstIncreased else null
    }

    companion object {
        fun <B> computeBasis(generators: List<FreeAlgebraGenerator<B>>, degree: Degree): List<FreeAlgebraBasis<B>> {
            val exponents = List(generators.size) { 0 }
            var basis: FreeAlgebraBasis<B>? = FreeAlgebraBasis(generators, exponents)
            val basisList: MutableList<FreeAlgebraBasis<B>> = mutableListOf()
            while (basis != null) {
                if (basis.totalDegree() == degree)
                    basisList.add(basis)
                basis = basis.getNextBasis(degree)
            }
            return basisList
        }
    }
}

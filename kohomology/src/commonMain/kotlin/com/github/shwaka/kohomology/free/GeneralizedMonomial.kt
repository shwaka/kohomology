package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Degree
import com.github.shwaka.kohomology.dg.DegreeMonoid
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.IntAsDegree

data class GeneralizedIndeterminate<I : IndeterminateName, D : Degree>(val name: I, val degree: D) {
    companion object {
        operator fun <D : Degree> invoke(name: String, degree: D): GeneralizedIndeterminate<StringIndeterminateName, D> {
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
    fun <T> mapIndexed(transform: (Int, GeneralizedIndeterminate<I, D>) -> T): List<T> {
        return List(this.size) { transform(it, this.rawList[it]) }
    }
    operator fun get(index: Int): GeneralizedIndeterminate<I, D> = this.rawList[index]

    abstract fun checkDegree(monomial: GeneralizedMonomial<I, D>, degreeLimit: IntAsDegree): Boolean

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

private class GeneralizedPositiveIndeterminateList<I : IndeterminateName, D : Degree>(
    rawList: List<GeneralizedIndeterminate<I, D>>
) : GeneralizedIndeterminateList<I, D>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree.toInt() <= 0)
                throw IllegalArgumentException("The degree of an indeterminate in PositiveIndeterminateList must be positive")
        }
    }

    override fun checkDegree(monomial: GeneralizedMonomial<I, D>, degreeLimit: IntAsDegree): Boolean {
        // monomial.indeterminateList == this を確認しなくて良い？
        // private にしちゃったから困ってる
        return monomial.degree.toInt() <= degreeLimit
    }

    override fun drop(): GeneralizedPositiveIndeterminateList<I, D> = GeneralizedPositiveIndeterminateList(this.rawList.drop(1))
}

private class GeneralizedNegativeIndeterminateList<I : IndeterminateName, D : Degree>(
    rawList: List<GeneralizedIndeterminate<I, D>>
) : GeneralizedIndeterminateList<I, D>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree.toInt() >= 0)
                throw IllegalArgumentException("The degree of an indeterminate in NegativeIndeterminateList must be negative")
        }
    }

    override fun checkDegree(monomial: GeneralizedMonomial<I, D>, degreeLimit: IntAsDegree): Boolean {
        // monomial.indeterminateList == this を確認しなくて良い？
        // private にしちゃったから困ってる
        return monomial.degree.toInt() >= degreeLimit
    }

    override fun drop(): GeneralizedNegativeIndeterminateList<I, D> = GeneralizedNegativeIndeterminateList(this.rawList.drop(1))
}

class GeneralizedMonomial<I : IndeterminateName, D : Degree> private constructor(
    val degreeMonoid: DegreeMonoid<D>,
    private val indeterminateList: GeneralizedIndeterminateList<I, D>,
    val exponentList: IntArray,
) : MonoidElement<D> {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw InvalidSizeException("Invalid size of the exponent list")
    }

    constructor(
        degreeMonoid: DegreeMonoid<D>,
        indeterminateList: List<GeneralizedIndeterminate<I, D>>,
        exponentList: IntArray
    ) : this(degreeMonoid, GeneralizedIndeterminateList.from(indeterminateList), exponentList)

    constructor(
        degreeMonoid: DegreeMonoid<D>,
        indeterminateList: List<GeneralizedIndeterminate<I, D>>,
        exponentList: List<Int>
    ) : this(degreeMonoid, GeneralizedIndeterminateList.from(indeterminateList), exponentList.toIntArray())

    override val degree: D by lazy {
        // this.indeterminateList.zip(this.exponentList.toList())
        //     .map { (generator, exponent) -> generator.degree * exponent }
        //     .reduce { a, b -> a + b }
        this.degreeMonoid.context.run {
            this@GeneralizedMonomial.indeterminateList.mapIndexed { i, indeterminate ->
                indeterminate.degree * this@GeneralizedMonomial.exponentList[i]
            }.fold(zero) { acc, b -> acc + b }
        }
    }

    private fun drop(): GeneralizedMonomial<I, D> {
        if (this.indeterminateList.isEmpty())
            throw Exception("This can't happen!")
        return GeneralizedMonomial(
            this.degreeMonoid,
            this.indeterminateList.drop(),
            this.exponentList.sliceArray(1 until this.indeterminateList.size)
        )
    }

    fun getNextMonomial(maxDegree: IntAsDegree): GeneralizedMonomial<I, D>? {
        if (this.indeterminateList.isEmpty())
            return null
        this.increaseFirstExponent(maxDegree)?.let { return it }
        this.drop().getNextMonomial(maxDegree)?.let {
            val newExponents = intArrayOf(0) + it.exponentList
            return GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, newExponents)
        }
        return null
    }

    private fun increaseFirstExponent(maxDegree: IntAsDegree): GeneralizedMonomial<I, D>? {
        // 奇数次の場合
        if ((this.indeterminateList.first().degree.isOdd()) && (this.exponentList.first() == 1))
            return null
        val newExponents = intArrayOf(this.exponentList.first() + 1) + this.exponentList.sliceArray(1 until this.indeterminateList.size)
        val firstIncreased = GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, newExponents)
        return if (this.indeterminateList.checkDegree(firstIncreased, maxDegree)) firstIncreased else null
    }

    fun containsIndeterminate(indeterminateIndex: Int): Boolean {
        return this.exponentList[indeterminateIndex] > 0
    }

    override fun toString(): String {
        val indeterminateAndExponentList = this.indeterminateList.zip(this.exponentList.toList())
            .filter { (_, exponent) -> exponent != 0 }
        if (indeterminateAndExponentList.isEmpty())
            return "1"
        return indeterminateAndExponentList.joinToString("") { (indeterminate, exponent) ->
            when (exponent) {
                0 -> throw Exception("This can't happen!")
                1 -> indeterminate.toString()
                else -> "$indeterminate^$exponent"
            }
        }
    }

    override fun toTex(): String {
        val indeterminateAndExponentList = this.indeterminateList.zip(this.exponentList.toList())
            .filter { (_, exponent) -> exponent != 0 }
        if (indeterminateAndExponentList.isEmpty())
            return "1"
        return indeterminateAndExponentList.joinToString("") { (indeterminate, exponent) ->
            when (exponent) {
                0 -> throw Exception("This can't happen!")
                1 -> indeterminate.toTex()
                else -> "${indeterminate.toTex()}^{$exponent}"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as GeneralizedMonomial<*, *>

        if (indeterminateList != other.indeterminateList) return false
        if (!exponentList.contentEquals(other.exponentList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = indeterminateList.hashCode()
        result = 31 * result + exponentList.contentHashCode()
        return result
    }

    companion object {
        fun <I : IndeterminateName> fromIndeterminate(indeterminateList: List<Indeterminate<I>>, indeterminate: Indeterminate<I>): Monomial<I> {
            val index = indeterminateList.indexOf(indeterminate)
            if (index == -1)
                throw NoSuchElementException("Indeterminate $indeterminate is not contained in the indeterminate list $indeterminateList")
            val exponentList = indeterminateList.map { if (it == indeterminate) 1 else 0 }
            return Monomial(indeterminateList, exponentList)
        }
    }
}

package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.IntDegree
import com.github.shwaka.kohomology.dg.IntDegreeMonoid
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.isOdd

interface IndeterminateName {
    fun toTex(): String = this.toString()
}
class StringIndeterminateName(val name: String, tex: String? = null) : IndeterminateName {
    val tex: String = tex ?: name

    override fun toString(): String = this.name
    override fun toTex(): String = this.tex

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StringIndeterminateName

        if (name != other.name) return false
        if (tex != other.tex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + tex.hashCode()
        return result
    }
}

data class Indeterminate<I : IndeterminateName>(val name: I, val degree: IntAsDegree) {
    companion object {
        operator fun invoke(name: String, degree: IntAsDegree): Indeterminate<StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name), degree)
        }
    }
    override fun toString(): String {
        return this.name.toString()
    }
    fun toTex(): String {
        return this.name.toTex()
    }
}

private sealed class IndeterminateList<I : IndeterminateName>(
    protected val rawList: List<Indeterminate<I>>
) {
    fun isEmpty(): Boolean = this.rawList.isEmpty()
    fun first(): Indeterminate<I> = this.rawList.first()
    abstract fun drop(): IndeterminateList<I>
    val size: Int
        get() = this.rawList.size
    fun <T> zip(list: List<T>): List<Pair<Indeterminate<I>, T>> = this.rawList.zip(list)
    fun mapIndexedIntArray(transform: (Int, Indeterminate<I>) -> Int): IntArray {
        return IntArray(this.size) { transform(it, this.rawList[it]) }
    }
    operator fun get(index: Int): Indeterminate<I> = this.rawList[index]

    abstract fun checkDegree(monomial: Monomial<I>, degreeLimit: IntAsDegree): Boolean

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
        fun <I : IndeterminateName> from(indeterminateList: List<Indeterminate<I>>): IndeterminateList<I> {
            return when {
                indeterminateList.any { it.degree == 0 } -> throw IllegalArgumentException("Cannot consider an indeterminate of degree zero")
                indeterminateList.all { it.degree > 0 } -> PositiveIndeterminateList(indeterminateList)
                indeterminateList.all { it.degree < 0 } -> NegativeIndeterminateList(indeterminateList)
                else -> throw IllegalArgumentException("Cannot consider a list of indeterminate containing both positive and negative degrees")
            }
        }
    }
}

private class PositiveIndeterminateList<I : IndeterminateName>(
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

private class NegativeIndeterminateList<I : IndeterminateName>(
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

class Monomial<I : IndeterminateName> private constructor(
    private val indeterminateList: IndeterminateList<I>,
    val exponentList: IntArray,
) : MonoidElement<IntDegree> {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw InvalidSizeException("Invalid size of the exponent list")
    }

    constructor(
        indeterminateList: List<Indeterminate<I>>,
        exponentList: IntArray
    ) : this(IndeterminateList.from(indeterminateList), exponentList)

    constructor(
        indeterminateList: List<Indeterminate<I>>,
        exponentList: List<Int>
    ) : this(IndeterminateList.from(indeterminateList), exponentList.toIntArray())

    override val degree: IntDegree by lazy {
        // this.indeterminateList.zip(this.exponentList.toList())
        //     .map { (generator, exponent) -> generator.degree * exponent }
        //     .reduce { a, b -> a + b }
        val degree = this.indeterminateList.mapIndexedIntArray { i, indeterminate ->
            indeterminate.degree * this.exponentList[i]
        }.fold(0) { acc, b -> acc + b }
        IntDegree(degree)
    }

    private fun drop(): Monomial<I> {
        if (this.indeterminateList.isEmpty())
            throw Exception("This can't happen!")
        return Monomial(
            this.indeterminateList.drop(),
            this.exponentList.sliceArray(1 until this.indeterminateList.size)
        )
    }

    fun getNextMonomial(maxDegree: IntAsDegree): Monomial<I>? {
        if (this.indeterminateList.isEmpty())
            return null
        this.increaseFirstExponent(maxDegree)?.let { return it }
        this.drop().getNextMonomial(maxDegree)?.let {
            val newExponents = intArrayOf(0) + it.exponentList
            return Monomial(this.indeterminateList, newExponents)
        }
        return null
    }

    private fun increaseFirstExponent(maxDegree: IntAsDegree): Monomial<I>? {
        // 奇数次の場合
        if ((this.indeterminateList.first().degree.isOdd()) && (this.exponentList.first() == 1))
            return null
        val newExponents = intArrayOf(this.exponentList.first() + 1) + this.exponentList.sliceArray(1 until this.indeterminateList.size)
        val firstIncreased = Monomial(this.indeterminateList, newExponents)
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

        other as Monomial<*>

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

class FreeMonoid<I : IndeterminateName> (
    val indeterminateList: List<Indeterminate<I>>
) : Monoid<IntDegree, Monomial<I>> {
    // constructor(
    //     indeterminateList: List<Indeterminate<I>>,
    // ) : this(IndeterminateList.from(indeterminateList))

    override val unit: Monomial<I> = Monomial(this.indeterminateList, List(this.indeterminateList.size) { 0 })
    override val degreeMonoid = IntDegreeMonoid

    override fun multiply(
        monoidElement1: Monomial<I>,
        monoidElement2: Monomial<I>
    ): MaybeZero<Pair<Monomial<I>, Sign>> {
        // if (monoidElement1.indeterminateList != monoidElement2.indeterminateList)
        //     throw IllegalArgumentException("Cannot multiply two monomials of different indeterminate")
        val size = this.indeterminateList.size
        // val exponentList = monoidElement1.exponentList
        //     .zip(monoidElement2.exponentList)
        //     .map { (p, q) -> p + q }
        val exponentList = this.addExponentLists(monoidElement1.exponentList, monoidElement2.exponentList)
        for (i in 0 until size) {
            if ((this.indeterminateList[i].degree.isOdd()) && (exponentList[i] >= 2))
                return Zero()
        }
        var sign = 1
        for (i in 0 until size) {
            if ((this.indeterminateList[i].degree.isOdd()) && (monoidElement1.exponentList[i] == 1)) {
                for (j in 0 until i) {
                    if ((this.indeterminateList[j].degree.isOdd()) && (monoidElement2.exponentList[j] == 1)) {
                        sign = -sign
                    }
                }
            }
        }
        val monomial = Monomial(this.indeterminateList, exponentList)
        return NonZero(Pair(monomial, sign))
    }

    private fun addExponentLists(exponentList1: IntArray, exponentList2: IntArray): IntArray {
        // return exponentList1.zip(exponentList2).map { (p, q) -> p + q }
        // return exponentList1.indices.map { i -> exponentList1[i] + exponentList2[i] }
        // return exponentList1.mapIndexed { index, exponent -> exponent + exponentList2[index] }
        return IntArray(exponentList1.size) { exponentList1[it] + exponentList2[it] }
    }

    override fun listAll(degree: IntDegree): List<Monomial<I>> {
        val exponentList = List(this.indeterminateList.size) { 0 }
        var monomial: Monomial<I>? = Monomial(this.indeterminateList, exponentList)
        val monomialList: MutableList<Monomial<I>> = mutableListOf()
        while (monomial != null) {
            if (monomial.degree == degree)
                monomialList.add(monomial)
            monomial = monomial.getNextMonomial(degree.toInt())
        }
        return monomialList
    }

    private fun separate(monomial: Monomial<I>, index: Int): MonomialSeparation<I>? {
        val separatedExponent = monomial.exponentList[index]
        if (separatedExponent == 0)
            return null
        val remainingExponentList = monomial.exponentList.mapIndexed { i, exponent ->
            if (i == index) 0 else exponent
        }
        val remainingMonomial = Monomial(this.indeterminateList, remainingExponentList)
        val separatedIndeterminate = this.indeterminateList[index]
        val separatedExponentList = monomial.exponentList.mapIndexed { i, exponent ->
            if (i == index) exponent else 0
        }
        val multipliedMonomialOrZero = this.multiply(
            Monomial(this.indeterminateList, separatedExponentList),
            remainingMonomial
        )
        val (_, sign) = when (multipliedMonomialOrZero) {
            is NonZero -> multipliedMonomialOrZero.value
            is Zero -> throw Exception("This can't happen!")
        }
        return MonomialSeparation(remainingMonomial, separatedIndeterminate, separatedExponent, sign, index)
    }

    fun allSeparations(monomial: Monomial<I>): List<MonomialSeparation<I>> {
        // TODO: List じゃなくて Iterator の方が良い？
        return this.indeterminateList.indices.mapNotNull { i -> this.separate(monomial, i) }
    }

    override fun toString(): String {
        val indeterminateListString = this.indeterminateList.joinToString(", ") { it.toString() }
        return "FreeMonoid($indeterminateListString)"
    }
}

data class MonomialSeparation<I : IndeterminateName>(
    val remainingMonomial: Monomial<I>,
    val separatedIndeterminate: Indeterminate<I>,
    val separatedExponent: Int,
    val sign: Sign,
    val index: Int,
) {
    init {
        if (separatedExponent <= 0)
            throw Exception("separatedExponent must be positive")
    }
}

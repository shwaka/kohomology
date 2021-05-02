package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMonoid
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeMonoid
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.util.Sign

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

data class GeneralizedIndeterminate<I : IndeterminateName, D : Degree>(val name: I, val degree: D) {
    companion object {
        operator fun <D : Degree> invoke(name: String, degree: D): GeneralizedIndeterminate<StringIndeterminateName, D> {
            return GeneralizedIndeterminate(StringIndeterminateName(name), degree)
        }
        operator fun invoke(name: String, degree: Int): GeneralizedIndeterminate<StringIndeterminateName, IntDegree> {
            return GeneralizedIndeterminate(StringIndeterminateName(name), IntDegree(degree))
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

    companion object {
        operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<GeneralizedIndeterminate<I, IntDegree>>,
            exponentList: List<Int>
        ): GeneralizedMonomial<I, IntDegree> {
            return GeneralizedMonomial(IntDegreeMonoid, GeneralizedIndeterminateList.from(indeterminateList), exponentList.toIntArray())
        }

        fun <I : IndeterminateName, D : Degree> fromIndeterminate(
            degreeMonoid: DegreeMonoid<D>,
            indeterminateList: List<GeneralizedIndeterminate<I, D>>,
            indeterminate: GeneralizedIndeterminate<I, D>
        ): GeneralizedMonomial<I, D> {
            val index = indeterminateList.indexOf(indeterminate)
            if (index == -1)
                throw NoSuchElementException("Indeterminate $indeterminate is not contained in the indeterminate list $indeterminateList")
            val exponentList = indeterminateList.map { if (it == indeterminate) 1 else 0 }
            return GeneralizedMonomial(degreeMonoid, indeterminateList, exponentList)
        }
    }

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
}

class GeneralizedFreeMonoid<I : IndeterminateName, D : Degree> (
    override val degreeMonoid: DegreeMonoid<D>,
    val indeterminateList: List<GeneralizedIndeterminate<I, D>>
) : Monoid<D, GeneralizedMonomial<I, D>> {
    // constructor(
    //     indeterminateList: List<Indeterminate<I>>,
    // ) : this(IndeterminateList.from(indeterminateList))

    companion object {
        operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<GeneralizedIndeterminate<I, IntDegree>>
        ): Monoid<IntDegree, GeneralizedMonomial<I, IntDegree>> {
            return GeneralizedFreeMonoid(IntDegreeMonoid, indeterminateList)
        }
    }

    override val unit: GeneralizedMonomial<I, D> = GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, List(this.indeterminateList.size) { 0 })

    override fun multiply(
        monoidElement1: GeneralizedMonomial<I, D>,
        monoidElement2: GeneralizedMonomial<I, D>
    ): MaybeZero<Pair<GeneralizedMonomial<I, D>, Sign>> {
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
        val monomial = GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, exponentList)
        return NonZero(Pair(monomial, sign))
    }

    private fun addExponentLists(exponentList1: IntArray, exponentList2: IntArray): IntArray {
        // return exponentList1.zip(exponentList2).map { (p, q) -> p + q }
        // return exponentList1.indices.map { i -> exponentList1[i] + exponentList2[i] }
        // return exponentList1.mapIndexed { index, exponent -> exponent + exponentList2[index] }
        return IntArray(exponentList1.size) { exponentList1[it] + exponentList2[it] }
    }

    override fun listAll(degree: D): List<GeneralizedMonomial<I, D>> {
        val exponentList = List(this.indeterminateList.size) { 0 }
        var monomial: GeneralizedMonomial<I, D>? = GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, exponentList)
        val monomialList: MutableList<GeneralizedMonomial<I, D>> = mutableListOf()
        while (monomial != null) {
            if (monomial.degree == degree)
                monomialList.add(monomial)
            monomial = monomial.getNextMonomial(degree.toInt())
        }
        return monomialList
    }

    private fun separate(monomial: GeneralizedMonomial<I, D>, index: Int): GeneralizedMonomialSeparation<I, D>? {
        val separatedExponent = monomial.exponentList[index]
        if (separatedExponent == 0)
            return null
        val remainingExponentList = monomial.exponentList.mapIndexed { i, exponent ->
            if (i == index) 0 else exponent
        }
        val remainingMonomial = GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, remainingExponentList)
        val separatedIndeterminate = this.indeterminateList[index]
        val separatedExponentList = monomial.exponentList.mapIndexed { i, exponent ->
            if (i == index) exponent else 0
        }
        val multipliedMonomialOrZero = this.multiply(
            GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, separatedExponentList),
            remainingMonomial
        )
        val (_, sign) = when (multipliedMonomialOrZero) {
            is NonZero -> multipliedMonomialOrZero.value
            is Zero -> throw Exception("This can't happen!")
        }
        return GeneralizedMonomialSeparation(remainingMonomial, separatedIndeterminate, separatedExponent, sign, index)
    }

    fun allSeparations(monomial: GeneralizedMonomial<I, D>): List<GeneralizedMonomialSeparation<I, D>> {
        // TODO: List じゃなくて Iterator の方が良い？
        return this.indeterminateList.indices.mapNotNull { i -> this.separate(monomial, i) }
    }

    override fun toString(): String {
        val indeterminateListString = this.indeterminateList.joinToString(", ") { it.toString() }
        return "FreeMonoid($indeterminateListString)"
    }
}

data class GeneralizedMonomialSeparation<I : IndeterminateName, D : Degree>(
    val remainingMonomial: GeneralizedMonomial<I, D>,
    val separatedIndeterminate: GeneralizedIndeterminate<I, D>,
    val separatedExponent: Int,
    val sign: Sign,
    val index: Int,
) {
    init {
        if (separatedExponent <= 0)
            throw Exception("separatedExponent must be positive")
    }
}

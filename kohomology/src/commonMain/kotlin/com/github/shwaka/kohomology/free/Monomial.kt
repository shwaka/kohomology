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

data class Indeterminate<I : IndeterminateName, D : Degree>(val name: I, val degree: D) {
    companion object {
        operator fun <D : Degree> invoke(name: String, degree: D): Indeterminate<StringIndeterminateName, D> {
            return Indeterminate(StringIndeterminateName(name), degree)
        }
        operator fun invoke(name: String, degree: Int): Indeterminate<StringIndeterminateName, IntDegree> {
            return Indeterminate(StringIndeterminateName(name), IntDegree(degree))
        }
    }
    override fun toString(): String {
        return this.name.toString()
    }
    fun toTex(): String {
        return this.name.toTex()
    }
}

internal sealed class IndeterminateList<I : IndeterminateName, D : Degree>(
    protected val rawList: List<Indeterminate<I, D>>
) {
    fun isEmpty(): Boolean = this.rawList.isEmpty()
    fun first(): Indeterminate<I, D> = this.rawList.first()
    abstract fun drop(): IndeterminateList<I, D>
    val size: Int
        get() = this.rawList.size
    fun <T> zip(list: List<T>): List<Pair<Indeterminate<I, D>, T>> = this.rawList.zip(list)
    fun mapIndexedIntArray(transform: (Int, Indeterminate<I, D>) -> Int): IntArray {
        return IntArray(this.size) { transform(it, this.rawList[it]) }
    }
    fun <T> mapIndexed(transform: (Int, Indeterminate<I, D>) -> T): List<T> {
        return List(this.size) { transform(it, this.rawList[it]) }
    }
    operator fun get(index: Int): Indeterminate<I, D> = this.rawList[index]
    val indices: IntRange = this.rawList.indices
    fun joinToString(separator: CharSequence, transform: ((Indeterminate<I, D>) -> String)? = null): String {
        return this.rawList.joinToString(separator = separator, transform = transform)
    }

    abstract fun isAllowedDegree(degree: D): Boolean

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
        fun <I : IndeterminateName, D : Degree> from(indeterminateList: List<Indeterminate<I, D>>): IndeterminateList<I, D> {
            return when {
                indeterminateList.any { it.degree.toInt() == 0 } -> throw IllegalArgumentException("Cannot consider an indeterminate of degree zero")
                indeterminateList.all { it.degree.toInt() > 0 } -> PositiveIndeterminateList(indeterminateList)
                indeterminateList.all { it.degree.toInt() < 0 } -> NegativeIndeterminateList(indeterminateList)
                else -> throw IllegalArgumentException("Cannot consider a list of indeterminate containing both positive and negative degrees")
            }
        }
    }
}

internal class PositiveIndeterminateList<I : IndeterminateName, D : Degree>(
    rawList: List<Indeterminate<I, D>>
) : IndeterminateList<I, D>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree.toInt() <= 0)
                throw IllegalArgumentException("The degree of an indeterminate in PositiveIndeterminateList must be positive")
        }
    }

    override fun isAllowedDegree(degree: D): Boolean {
        return degree.toInt() >= 0
    }

    override fun drop(): PositiveIndeterminateList<I, D> = PositiveIndeterminateList(this.rawList.drop(1))
}

internal class NegativeIndeterminateList<I : IndeterminateName, D : Degree>(
    rawList: List<Indeterminate<I, D>>
) : IndeterminateList<I, D>(rawList) {
    init {
        for (indeterminate in rawList) {
            if (indeterminate.degree.toInt() >= 0)
                throw IllegalArgumentException("The degree of an indeterminate in NegativeIndeterminateList must be negative")
        }
    }

    override fun isAllowedDegree(degree: D): Boolean {
        return degree.toInt() <= 0
    }

    override fun drop(): NegativeIndeterminateList<I, D> = NegativeIndeterminateList(this.rawList.drop(1))
}

class Monomial<I : IndeterminateName, D : Degree> internal constructor(
    val degreeMonoid: DegreeMonoid<D>,
    private val indeterminateList: IndeterminateList<I, D>,
    val exponentList: IntArray,
) : MonoidElement<D> {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw InvalidSizeException("Invalid size of the exponent list")
    }

    constructor(
        degreeMonoid: DegreeMonoid<D>,
        indeterminateList: List<Indeterminate<I, D>>,
        exponentList: IntArray
    ) : this(degreeMonoid, IndeterminateList.from(indeterminateList), exponentList)

    constructor(
        degreeMonoid: DegreeMonoid<D>,
        indeterminateList: List<Indeterminate<I, D>>,
        exponentList: List<Int>
    ) : this(degreeMonoid, IndeterminateList.from(indeterminateList), exponentList.toIntArray())

    companion object {
        operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<I, IntDegree>>,
            exponentList: List<Int>
        ): Monomial<I, IntDegree> {
            return Monomial(IntDegreeMonoid, IndeterminateList.from(indeterminateList), exponentList.toIntArray())
        }

        fun <I : IndeterminateName, D : Degree> fromIndeterminate(
            degreeMonoid: DegreeMonoid<D>,
            indeterminateList: List<Indeterminate<I, D>>,
            indeterminate: Indeterminate<I, D>
        ): Monomial<I, D> {
            val index = indeterminateList.indexOf(indeterminate)
            if (index == -1)
                throw NoSuchElementException("Indeterminate $indeterminate is not contained in the indeterminate list $indeterminateList")
            val exponentList = indeterminateList.map { if (it == indeterminate) 1 else 0 }
            return Monomial(degreeMonoid, indeterminateList, exponentList)
        }
    }

    override val degree: D by lazy {
        // this.indeterminateList.zip(this.exponentList.toList())
        //     .map { (generator, exponent) -> generator.degree * exponent }
        //     .reduce { a, b -> a + b }
        this.degreeMonoid.context.run {
            this@Monomial.indeterminateList.mapIndexed { i, indeterminate ->
                indeterminate.degree * this@Monomial.exponentList[i]
            }.fold(zero) { acc, b -> acc + b }
        }
    }

    internal fun increaseExponentAtIndex(index: Int): Monomial<I, D>? {
        // 奇数次の場合
        if ((this.indeterminateList[index].degree.isOdd()) && (this.exponentList[index] == 1))
            return null
        // val newExponents = intArrayOf(this.exponentList.first() + 1) + this.exponentList.sliceArray(1 until this.indeterminateList.size)
        val newExponents = IntArray(this.indeterminateList.size) {
            if (it == index) this.exponentList[it] + 1 else this.exponentList[it]
        }
        return Monomial(this.degreeMonoid, this.indeterminateList, newExponents)
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

        other as Monomial<*, *>

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

class FreeMonoid<I : IndeterminateName, D : Degree> (
    override val degreeMonoid: DegreeMonoid<D>,
    indeterminateList: List<Indeterminate<I, D>>
) : Monoid<D, Monomial<I, D>> {
    // constructor(
    //     indeterminateList: List<Indeterminate<I>>,
    // ) : this(IndeterminateList.from(indeterminateList))
    private val indeterminateList = IndeterminateList.from(indeterminateList)

    companion object {
        operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<I, IntDegree>>
        ): Monoid<IntDegree, Monomial<I, IntDegree>> {
            return FreeMonoid(IntDegreeMonoid, indeterminateList)
        }
    }

    override val unit: Monomial<I, D> = Monomial(this.degreeMonoid, this.indeterminateList, IntArray(this.indeterminateList.size) { 0 })

    override fun multiply(
        monoidElement1: Monomial<I, D>,
        monoidElement2: Monomial<I, D>
    ): MaybeZero<Pair<Monomial<I, D>, Sign>> {
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
        val monomial = Monomial(this.degreeMonoid, this.indeterminateList, exponentList)
        return NonZero(Pair(monomial, sign))
    }

    private fun addExponentLists(exponentList1: IntArray, exponentList2: IntArray): IntArray {
        // return exponentList1.zip(exponentList2).map { (p, q) -> p + q }
        // return exponentList1.indices.map { i -> exponentList1[i] + exponentList2[i] }
        // return exponentList1.mapIndexed { index, exponent -> exponent + exponentList2[index] }
        return IntArray(exponentList1.size) { exponentList1[it] + exponentList2[it] }
    }

    override fun listAll(degree: D): List<Monomial<I, D>> {
        if (!this.indeterminateList.isAllowedDegree(degree))
            return emptyList()
        return this.listAllInternal(degree, 0)
    }

    private fun listAllInternal(degree: D, index: Int): List<Monomial<I, D>> {
        if (index < 0 || index > this.indeterminateList.size)
            throw Exception("This can't happen! (illegal index: $index)")
        if (index == this.indeterminateList.size) {
            return if (degree.isZero())
                listOf(this.unit)
            else
                emptyList()
        }
        // Since 0 <= index < this.indeterminateList.size,
        // we have 0 < this.indeterminateList.size
        val newDegree = this.degreeMonoid.context.run { degree - this@FreeMonoid.indeterminateList[index].degree }
        val listWithNonZeroAtIndex = if (this.indeterminateList.isAllowedDegree(newDegree)) {
            this.listAllInternal(newDegree, index)
                .mapNotNull { monomial -> monomial.increaseExponentAtIndex(index) }
        } else emptyList()
        val listWithZeroAtIndex = this.listAllInternal(degree, index + 1)
        return listWithNonZeroAtIndex + listWithZeroAtIndex
    }

    private fun separate(monomial: Monomial<I, D>, index: Int): MonomialSeparation<I, D>? {
        val separatedExponent = monomial.exponentList[index]
        if (separatedExponent == 0)
            return null
        // val remainingExponentList = monomial.exponentList.mapIndexed { i, exponent ->
        //     if (i == index) 0 else exponent
        // }
        val remainingExponentList = IntArray(monomial.exponentList.size) {
            if (it == index) 0 else monomial.exponentList[it]
        }
        val remainingMonomial = Monomial(this.degreeMonoid, this.indeterminateList, remainingExponentList)
        val separatedIndeterminate = this.indeterminateList[index]
        // val separatedExponentList = monomial.exponentList.mapIndexed { i, exponent ->
        //     if (i == index) exponent else 0
        // }
        val separatedExponentList = IntArray(monomial.exponentList.size) {
            if (it == index) monomial.exponentList[it] else 0
        }
        val multipliedMonomialOrZero = this.multiply(
            Monomial(this.degreeMonoid, this.indeterminateList, separatedExponentList),
            remainingMonomial
        )
        val (_, sign) = when (multipliedMonomialOrZero) {
            is NonZero -> multipliedMonomialOrZero.value
            is Zero -> throw Exception("This can't happen!")
        }
        return MonomialSeparation(remainingMonomial, separatedIndeterminate, separatedExponent, sign, index)
    }

    fun allSeparations(monomial: Monomial<I, D>): List<MonomialSeparation<I, D>> {
        // TODO: List じゃなくて Iterator の方が良い？
        return this.indeterminateList.indices.mapNotNull { i -> this.separate(monomial, i) }
    }

    override fun toString(): String {
        val indeterminateListString = this.indeterminateList.joinToString(", ")
        return "FreeMonoid($indeterminateListString)"
    }
}

data class MonomialSeparation<I : IndeterminateName, D : Degree>(
    val remainingMonomial: Monomial<I, D>,
    val separatedIndeterminate: Indeterminate<I, D>,
    val separatedExponent: Int,
    val sign: Sign,
    val index: Int,
) {
    init {
        if (separatedExponent <= 0)
            throw Exception("separatedExponent must be positive")
    }
}

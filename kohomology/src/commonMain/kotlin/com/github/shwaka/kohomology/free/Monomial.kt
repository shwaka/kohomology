package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.exception.InvalidSizeException
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

data class Indeterminate<D : Degree, I : IndeterminateName>(val name: I, val degree: D) {
    companion object {
        operator fun <D : Degree> invoke(name: String, degree: D): Indeterminate<D, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name), degree)
        }
        operator fun <D : Degree> invoke(name: String, tex: String, degree: D): Indeterminate<D, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name, tex), degree)
        }
        operator fun <I : IndeterminateName> invoke(name: I, degree: Int): Indeterminate<IntDegree, I> {
            return Indeterminate(name, IntDegree(degree))
        }
        operator fun invoke(name: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name), IntDegree(degree))
        }
        operator fun invoke(name: String, tex: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name, tex), IntDegree(degree))
        }
    }
    override fun toString(): String {
        return this.name.toString()
    }
}

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
                    indeterminateList.any { it.degree.toInt() == 0 } -> throw IllegalArgumentException("Cannot consider an indeterminate of degree zero")
                    indeterminateList.all { it.degree.toInt() > 0 } -> PositiveIndeterminateList(degreeGroup, indeterminateList)
                    indeterminateList.all { it.degree.toInt() < 0 } -> NegativeIndeterminateList(degreeGroup, indeterminateList)
                    else -> throw IllegalArgumentException("Cannot consider a list of indeterminate containing both positive and negative degrees")
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

    override fun drop(): NegativeIndeterminateList<D, I> = NegativeIndeterminateList(this.degreeGroup, this.rawList.drop(1))
}

class Monomial<D : Degree, I : IndeterminateName> internal constructor(
    val degreeGroup: AugmentedDegreeGroup<D>,
    private val indeterminateList: IndeterminateList<D, I>,
    val exponentList: IntArray,
) : MonoidElement<D> {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw InvalidSizeException("Invalid size of the exponent list")
    }

    constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
        exponentList: IntArray
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList), exponentList)

    constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
        exponentList: List<Int>
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList), exponentList.toIntArray())

    companion object {
        operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<IntDegree, I>>,
            exponentList: List<Int>
        ): Monomial<IntDegree, I> {
            return Monomial(IntDegreeGroup, IndeterminateList.from(IntDegreeGroup, indeterminateList), exponentList.toIntArray())
        }

        fun <D : Degree, I : IndeterminateName> fromIndeterminate(
            degreeGroup: AugmentedDegreeGroup<D>,
            indeterminateList: List<Indeterminate<D, I>>,
            indeterminate: Indeterminate<D, I>
        ): Monomial<D, I> {
            val index = indeterminateList.indexOf(indeterminate)
            if (index == -1)
                throw NoSuchElementException("Indeterminate $indeterminate is not contained in the indeterminate list $indeterminateList")
            val exponentList = indeterminateList.map { if (it == indeterminate) 1 else 0 }
            return Monomial(degreeGroup, indeterminateList, exponentList)
        }
    }

    override val degree: D by lazy {
        // this.indeterminateList.zip(this.exponentList.toList())
        //     .map { (generator, exponent) -> generator.degree * exponent }
        //     .reduce { a, b -> a + b }
        this.degreeGroup.context.run {
            this@Monomial.indeterminateList.mapIndexed { i, indeterminate ->
                indeterminate.degree * this@Monomial.exponentList[i]
            }.fold(zero) { acc, b -> acc + b }
        }
    }

    internal fun increaseExponentAtIndex(index: Int): Monomial<D, I>? {
        // 奇数次の場合
        if ((this.indeterminateList[index].degree.isOdd()) && (this.exponentList[index] == 1))
            return null
        // val newExponents = intArrayOf(this.exponentList.first() + 1) + this.exponentList.sliceArray(1 until this.indeterminateList.size)
        val newExponents = IntArray(this.indeterminateList.size) {
            if (it == index) this.exponentList[it] + 1 else this.exponentList[it]
        }
        return Monomial(this.degreeGroup, this.indeterminateList, newExponents)
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
        return this.toTex { indeterminateName -> indeterminateName.toTex() }
    }

    fun toTex(indeterminateNameToTex: (I) -> String): String {
        val indeterminateAndExponentList = this.indeterminateList.zip(this.exponentList.toList())
            .filter { (_, exponent) -> exponent != 0 }
        if (indeterminateAndExponentList.isEmpty())
            return "1"
        return indeterminateAndExponentList.joinToString("") { (indeterminate, exponent) ->
            when (exponent) {
                0 -> throw Exception("This can't happen!")
                1 -> indeterminateNameToTex(indeterminate.name)
                else -> "${indeterminateNameToTex(indeterminate.name)}^{$exponent}"
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

class FreeMonoid<D : Degree, I : IndeterminateName> (
    override val degreeGroup: AugmentedDegreeGroup<D>,
    indeterminateList: List<Indeterminate<D, I>>
) : Monoid<D, Monomial<D, I>> {
    // constructor(
    //     indeterminateList: List<Indeterminate<I>>,
    // ) : this(IndeterminateList.from(indeterminateList))
    private val indeterminateList = IndeterminateList.from(degreeGroup, indeterminateList)

    companion object {
        operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<IntDegree, I>>
        ): Monoid<IntDegree, Monomial<IntDegree, I>> {
            return FreeMonoid(IntDegreeGroup, indeterminateList)
        }
    }

    override val unit: Monomial<D, I> = Monomial(this.degreeGroup, this.indeterminateList, IntArray(this.indeterminateList.size) { 0 })

    override fun multiply(
        monoidElement1: Monomial<D, I>,
        monoidElement2: Monomial<D, I>
    ): MaybeZero<Pair<Monomial<D, I>, Sign>> {
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
        val monomial = Monomial(this.degreeGroup, this.indeterminateList, exponentList)
        return NonZero(Pair(monomial, sign))
    }

    private fun addExponentLists(exponentList1: IntArray, exponentList2: IntArray): IntArray {
        // return exponentList1.zip(exponentList2).map { (p, q) -> p + q }
        // return exponentList1.indices.map { i -> exponentList1[i] + exponentList2[i] }
        // return exponentList1.mapIndexed { index, exponent -> exponent + exponentList2[index] }
        return IntArray(exponentList1.size) { exponentList1[it] + exponentList2[it] }
    }

    private val monomialListGenerator by lazy {
        MonomialListGenerator(this.degreeGroup, this.indeterminateList, this.unit)
    }

    override fun listElements(degree: D): List<Monomial<D, I>> {
        return this.monomialListGenerator.listMonomials(degree)
    }

    private val monomialListGeneratorWithAugmentedDegree by lazy {
        val indeterminateRawList: List<Indeterminate<IntDegree, I>> = this.indeterminateList.map { indeterminate ->
            Indeterminate(indeterminate.name, this.degreeGroup.augmentation(indeterminate.degree))
        }
        val indeterminateListWithAugDeg = IndeterminateList.from(IntDegreeGroup, indeterminateRawList)
        val unit = Monomial(IntDegreeGroup, indeterminateListWithAugDeg, IntArray(indeterminateListWithAugDeg.size) { 0 })
        MonomialListGenerator(IntDegreeGroup, indeterminateListWithAugDeg, unit)
    }

    override fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        val elementListWithAugDeg: List<Monomial<IntDegree, I>> =
            this.monomialListGeneratorWithAugmentedDegree.listMonomials(IntDegree(augmentedDegree))
        val elementList: List<Monomial<D, I>> =
            elementListWithAugDeg.map { elementWithAugDeg ->
                Monomial(this.degreeGroup, this.indeterminateList, elementWithAugDeg.exponentList)
            }
        return elementList.map { it.degree }.distinct()
    }

    private fun separate(monomial: Monomial<D, I>, index: Int): MonomialSeparation<D, I>? {
        val separatedExponent = monomial.exponentList[index]
        if (separatedExponent == 0)
            return null
        // val remainingExponentList = monomial.exponentList.mapIndexed { i, exponent ->
        //     if (i == index) 0 else exponent
        // }
        val remainingExponentList = IntArray(monomial.exponentList.size) {
            if (it == index) 0 else monomial.exponentList[it]
        }
        val remainingMonomial = Monomial(this.degreeGroup, this.indeterminateList, remainingExponentList)
        val separatedIndeterminate = this.indeterminateList[index]
        // val separatedExponentList = monomial.exponentList.mapIndexed { i, exponent ->
        //     if (i == index) exponent else 0
        // }
        val separatedExponentList = IntArray(monomial.exponentList.size) {
            if (it == index) monomial.exponentList[it] else 0
        }
        val multipliedMonomialOrZero = this.multiply(
            Monomial(this.degreeGroup, this.indeterminateList, separatedExponentList),
            remainingMonomial
        )
        val (_, sign) = when (multipliedMonomialOrZero) {
            is NonZero -> multipliedMonomialOrZero.value
            is Zero -> throw Exception("This can't happen!")
        }
        return MonomialSeparation(remainingMonomial, separatedIndeterminate, separatedExponent, sign, index)
    }

    fun allSeparations(monomial: Monomial<D, I>): List<MonomialSeparation<D, I>> {
        // TODO: List じゃなくて Iterator の方が良い？
        return this.indeterminateList.indices.mapNotNull { i -> this.separate(monomial, i) }
    }

    override fun toString(): String {
        val indeterminateListString = this.indeterminateList.joinToString(", ")
        return "FreeMonoid($indeterminateListString)"
    }
}

data class MonomialSeparation<D : Degree, I : IndeterminateName>(
    val remainingMonomial: Monomial<D, I>,
    val separatedIndeterminate: Indeterminate<D, I>,
    val separatedExponent: Int,
    val sign: Sign,
    val index: Int,
) {
    init {
        if (separatedExponent <= 0)
            throw Exception("separatedExponent must be positive")
    }
}

private class MonomialListGenerator<D : Degree, I : IndeterminateName>(
    val degreeGroup: AugmentedDegreeGroup<D>,
    val indeterminateList: IndeterminateList<D, I>,
    val unit: Monomial<D, I>,
) {
    // (degree: D, index: Int) -> List<Monomial<D, I>>
    private val cache: MutableMap<Pair<D, Int>, List<Monomial<D, I>>> = mutableMapOf()

    fun listMonomials(degree: D): List<Monomial<D, I>> {
        if (!this.indeterminateList.isAllowedDegree(degree))
            return emptyList()
        return this.listMonomialsInternal(degree, 0)
    }

    private fun listMonomialsInternal(degree: D, index: Int): List<Monomial<D, I>> {
        if (index < 0 || index > this.indeterminateList.size)
            throw Exception("This can't happen! (illegal index: $index)")
        if (index == this.indeterminateList.size) {
            return if (degree.isZero())
                listOf(this.unit)
            else
                emptyList()
        }
        val cacheKey = Pair(degree, index)
        this.cache[cacheKey]?.let { return it }
        // Since 0 <= index < this.indeterminateList.size,
        // we have 0 < this.indeterminateList.size
        val newDegree = this.degreeGroup.context.run { degree - this@MonomialListGenerator.indeterminateList[index].degree }
        val listWithNonZeroAtIndex = if (this.indeterminateList.isAllowedDegree(newDegree)) {
            this.listMonomialsInternal(newDegree, index)
                .mapNotNull { monomial -> monomial.increaseExponentAtIndex(index) }
        } else emptyList()
        val listWithZeroAtIndex = this.listMonomialsInternal(degree, index + 1)
        val result = listWithNonZeroAtIndex + listWithZeroAtIndex
        this.cache[cacheKey] = result
        return result
    }
}

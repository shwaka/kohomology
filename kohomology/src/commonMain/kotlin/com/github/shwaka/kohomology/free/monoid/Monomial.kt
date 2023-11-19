package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public class Monomial<D : Degree, I : IndeterminateName> internal constructor(
    public val degreeGroup: AugmentedDegreeGroup<D>,
    private val indeterminateList: IndeterminateList<D, I>,
    public val exponentList: IntArray,
) : GMonoidElement<D> {
    init {
        if (this.indeterminateList.size != this.exponentList.size)
            throw InvalidSizeException("Invalid size of the exponent list")
    }

    public constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
        exponentList: IntArray
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList), exponentList)

    public constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
        exponentList: List<Int>
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList), exponentList.toIntArray())

    public companion object {
        public operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<IntDegree, I>>,
            exponentList: List<Int>
        ): Monomial<IntDegree, I> {
            return Monomial(IntDegreeGroup, IndeterminateList.from(IntDegreeGroup, indeterminateList), exponentList.toIntArray())
        }

        public fun <D : Degree, I : IndeterminateName> fromIndeterminate(
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
            }.sum()
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

    public fun containsIndeterminate(indeterminateIndex: Int): Boolean {
        return this.exponentList[indeterminateIndex] > 0
    }

    override fun toString(): String {
        return this.toString(PrintType.PLAIN) { it.toString() }
    }

    override fun toString(printConfig: PrintConfig): String {
        return this.toString(printConfig.printType) { it.toString(printConfig) }
    }

    private inline fun toString(
        printType: PrintType,
        crossinline indeterminateNameToString: (IndeterminateName) -> String,
    ): String {
        val indeterminateAndExponentList = this.indeterminateList.zip(this.exponentList.toList())
            .filter { (_, exponent) -> exponent != 0 }
        if (indeterminateAndExponentList.isEmpty())
            return "1"
        val separator = when (printType) {
            PrintType.PLAIN, PrintType.TEX -> ""
            PrintType.CODE -> " * "
        }
        return indeterminateAndExponentList.joinToString(separator) { (indeterminate, exponent) ->
            when (exponent) {
                0 -> throw Exception("This can't happen!")
                1 -> indeterminateNameToString(indeterminate.name)
                else -> {
                    val exponentStr = when (printType) {
                        PrintType.PLAIN, PrintType.CODE -> exponent.toString()
                        PrintType.TEX -> "{$exponent}"
                    }
                    "${indeterminateNameToString(indeterminate.name)}^$exponentStr"
                }
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

package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
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

        internal fun <D : Degree, I : IndeterminateName> unit(
            degreeGroup: AugmentedDegreeGroup<D>,
            indeterminateList: IndeterminateList<D, I>,
        ): Monomial<D, I> {
            return Monomial(degreeGroup, indeterminateList, IntArray(indeterminateList.size) { 0 })
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

    private var degreeCache: D? = null

    override val degree: D
        get() {
            val cachedDegree = this.degreeCache
            if (cachedDegree != null) {
                return cachedDegree
            }
            val degree = this.computeDegree()
            this.degreeCache = degree
            return degree
        }

    private fun computeDegree(): D {
        this.computeMultiDegree()?.let { degree ->
            @Suppress("UNCHECKED_CAST")
            return degree as D
        }
        var degree = this.degreeGroup.zero
        return this.degreeGroup.context.run {
            for (index in this@Monomial.indeterminateList.indices) {
                val exponent = this@Monomial.exponentList[index]
                if (exponent != 0) {
                    degree += this@Monomial.indeterminateList[index].degree * exponent
                }
            }
            degree
        }
    }

    private fun computeMultiDegree(): MultiDegree? {
        val multiDegreeGroup = this.degreeGroup as? MultiDegreeGroup ?: return null
        var constantTerm = 0
        val coeffList = IntArray(multiDegreeGroup.indeterminateList.size)
        var index = 0
        while (index < this.indeterminateList.size) {
            val exponent = this.exponentList[index]
            if (exponent == 0) {
                index++
                continue
            }
            val indeterminateDegree = this.indeterminateList[index].degree as? MultiDegree ?: return null
            if (indeterminateDegree.group != multiDegreeGroup) {
                return null
            }
            constantTerm += indeterminateDegree.constantTerm * exponent
            var coeffIndex = 0
            while (coeffIndex < indeterminateDegree.coeffList.size) {
                coeffList[coeffIndex] += indeterminateDegree.coeffList[coeffIndex] * exponent
                coeffIndex++
            }
            index++
        }
        return MultiDegree(multiDegreeGroup, constantTerm, coeffList)
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

    // This was an inline method previously.
    // See the comment before monomialToString() for detail.
    private fun toString(
        printType: PrintType,
        indeterminateNameToString: (IndeterminateName) -> String,
    ): String {
        val powerList = this.indeterminateList.zip(this.exponentList.toList())
            .filter { (_, exponent) -> exponent != 0 }
            .map { (indeterminate, exponent) -> Power(indeterminate.name, exponent) }
        return monomialToString(powerList, printType, indeterminateNameToString)
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

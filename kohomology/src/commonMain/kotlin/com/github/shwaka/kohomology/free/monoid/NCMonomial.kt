package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public class NCMonomial<D : Degree, I : IndeterminateName> internal constructor(
    public val degreeGroup: AugmentedDegreeGroup<D>,
    private val indeterminateList: IndeterminateList<D, I>,
    public val word: List<Indeterminate<D, I>>,
) : GMonoidElement<D> {
    public constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
        word: List<Indeterminate<D, I>>,
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList), word)

    public companion object {
        public operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<IntDegree, I>>,
            word: List<Indeterminate<IntDegree, I>>,
        ): NCMonomial<IntDegree, I> {
            return NCMonomial(IntDegreeGroup, IndeterminateList.from(IntDegreeGroup, indeterminateList), word)
        }

        public fun <D : Degree, I : IndeterminateName> fromIndeterminate(
            degreeGroup: AugmentedDegreeGroup<D>,
            indeterminateList: List<Indeterminate<D, I>>,
            indeterminate: Indeterminate<D, I>
        ): NCMonomial<D, I> {
            val index = indeterminateList.indexOf(indeterminate)
            if (index == -1)
                throw NoSuchElementException("Indeterminate $indeterminate is not contained in the indeterminate list $indeterminateList")
            return NCMonomial(degreeGroup, indeterminateList, listOf(indeterminate))
        }
    }

    override val degree: D by lazy {
        this.degreeGroup.context.run {
            this@NCMonomial.word.map { indeterminate -> indeterminate.degree }.sum()
        }
    }

    override fun toString(): String {
        return this.toString(PrintType.PLAIN) { it.toString() }
    }

    override fun toString(printConfig: PrintConfig): String {
        return this.toString(printConfig.printType) { it.toString(printConfig) }
    }

    private fun toString(
        printType: PrintType,
        indeterminateNameToString: (IndeterminateName) -> String,
    ): String {
        val powerList = toPowerList(this.word.map { indeterminate -> indeterminate.name })
        return monomialToString(powerList, printType, indeterminateNameToString)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NCMonomial<*, *>

        if (degreeGroup != other.degreeGroup) return false
        if (indeterminateList != other.indeterminateList) return false
        if (word != other.word) return false

        return true
    }

    override fun hashCode(): Int {
        var result = degreeGroup.hashCode()
        result = 31 * result + indeterminateList.hashCode()
        result = 31 * result + word.hashCode()
        return result
    }
}

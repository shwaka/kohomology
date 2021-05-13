package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.free.IndeterminateName
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.DefaultVectorPrinter

data class CopiedName<D : Degree, I : IndeterminateName>(val name: I, val shift: D, val index: Int? = null) : IndeterminateName {
    override fun toString(): String {
        val indexString: String = this.index?.toString() ?: ""
        val shiftString = when {
            this.shift.isZero() -> ""
            this.shift.isOne() -> "s"
            else -> "s^${this.shift}"
        }
        return "$shiftString${this.name}$indexString"
    }

    override fun toTex(): String {
        return this.toTex(false)
    }

    fun toTex(useBar: Boolean): String {
        val indexString: String = this.index?.toString()?.let { "_{($it)}" } ?: ""
        val shiftString = when {
            this.shift.isZero() -> ""
            this.shift.isOne() -> if (useBar) "\\bar" else "s"
            else -> "s^{${this.shift}}"
        }
        // The brace surrounding ${this.name} is necessary to avoid "double subscript"
        //   when this.name contains a subscript
        return "$shiftString{${this.name.toTex()}}$indexString"
    }
}

fun <D : Degree, I : IndeterminateName> Indeterminate<D, I>.copy(
    degreeGroup: DegreeGroup<D>,
    shift: D,
    index: Int? = null
): Indeterminate<D, CopiedName<D, I>> {
    val newDegree = degreeGroup.context.run { this@copy.degree - shift }
    return Indeterminate(CopiedName(this.name, shift, index), newDegree)
}

fun <I : IndeterminateName> Indeterminate<IntDegree, I>.copy(
    shift: Int,
    index: Int? = null
): Indeterminate<IntDegree, CopiedName<IntDegree, I>> {
    return this.copy(IntDegreeGroup, IntDegree(shift), index)
}

private typealias MonomialOnCopiedName<D, I> = Monomial<D, CopiedName<D, I>>

class TexVectorPrinterForCopiedName<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>>(
    useBar: Boolean = false,
    beforeSign: String = " ",
    afterSign: String = " ",
    afterCoeff: String = " ",
    basisComparator: Comparator<MonomialOnCopiedName<D, I>>? = null,
) : DefaultVectorPrinter<MonomialOnCopiedName<D, I>, S, V>(
    beforeSign = beforeSign,
    afterSign = afterSign,
    afterCoeff = afterCoeff,
    coeffToString = { it.toTex() },
    coeffToStringWithoutSign = { it.toTexWithoutSign() },
    basisToString = { monomial ->
        monomial.toTex { copiedName -> copiedName.toTex(useBar) }
    },
    basisComparator = basisComparator
)

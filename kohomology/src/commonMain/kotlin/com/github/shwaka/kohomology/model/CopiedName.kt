package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.UseBar

private typealias MonomialOnCopiedName<D, I> = Monomial<D, CopiedName<D, I>>

public data class CopiedName<D : Degree, I : IndeterminateName>(val name: I, val shift: D, val index: Int? = null) :
    IndeterminateName {
    override fun toString(): String {
        val indexString: String = this.index?.toString() ?: ""
        val shiftString = when {
            this.shift.isZero() -> ""
            this.shift.isOne() -> "s"
            else -> "s^${this.shift}"
        }
        return "$shiftString${this.name}$indexString"
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.PLAIN -> this.toString()
            PrintType.TEX -> this.toTex(printConfig.useBar)
        }
    }

    private fun toTex(useBar: UseBar): String {
        val indexString: String = this.index?.toString()?.let { "_{($it)}" } ?: ""
        val shiftString = when (useBar) {
            UseBar.ALWAYS -> when {
                this.shift.isZero() -> ""
                else -> "\\bar"
            }
            UseBar.ONE -> when {
                this.shift.isZero() -> ""
                this.shift.isOne() -> "\\bar"
                else -> "s^{${this.shift}}"
            }
            UseBar.NEVER -> when {
                this.shift.isZero() -> ""
                this.shift.isOne() -> "s"
                else -> "s^{${this.shift}}"
            }
        }
        // The brace surrounding ${this.name} is necessary to avoid "double subscript"
        //   when this.name contains a subscript
        return "$shiftString{${this.name.toString(PrintConfig(PrintType.TEX))}}$indexString"
    }

    public companion object {
        public fun <D : Degree, I : IndeterminateName, S : Scalar> getInternalPrintConfig(
            printConfig: PrintConfig,
        ): InternalPrintConfig<MonomialOnCopiedName<D, I>, S> {
            return when (printConfig.printType) {
                PrintType.PLAIN -> InternalPrintConfig()
                PrintType.TEX -> InternalPrintConfig(
                    coeffToString = { coeff, withSign -> coeff.toString(PrintType.TEX, withSign) },
                    basisToString = { monomial ->
                        monomial.toTex { copiedName -> copiedName.toTex(printConfig.useBar) }
                    },
                )
            }
        }
    }
}

public fun <D : Degree, I : IndeterminateName> Indeterminate<D, I>.copy(
    degreeGroup: DegreeGroup<D>,
    shift: D,
    index: Int? = null
): Indeterminate<D, CopiedName<D, I>> {
    val newDegree = degreeGroup.context.run { this@copy.degree - shift }
    return Indeterminate(CopiedName(this.name, shift, index), newDegree)
}

public fun <I : IndeterminateName> Indeterminate<IntDegree, I>.copy(
    shift: Int,
    index: Int? = null
): Indeterminate<IntDegree, CopiedName<IntDegree, I>> {
    return this.copy(IntDegreeGroup, IntDegree(shift), index)
}

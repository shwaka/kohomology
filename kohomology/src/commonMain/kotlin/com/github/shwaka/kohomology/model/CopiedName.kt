package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Identifier
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.ShowShift

private typealias MonomialOnCopiedName<D, I> = Monomial<D, CopiedName<D, I>>

public class CopiedName<D : Degree, I : IndeterminateName>(
    public val name: I,
    public val shift: D,
    public val index: Int? = null,
) : IndeterminateName {
    // CopiedName.identifier is computed during initialization to validate its name.
    // This has no performance effect since CopiedName is created very few times
    // (only in initialization of some DGAlgebras, not their elements).
    override val identifier: Identifier = CopiedName.getDefaultIdentifier(name, shift, index)

    override fun toString(): String {
        return this.toPlain(ShowShift.S_WITH_DEGREE)
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.PLAIN -> this.toPlain(printConfig.showShift)
            PrintType.TEX -> this.toTex(printConfig.showShift)
        }
    }

    private fun toPlain(showShift: ShowShift): String {
        val indexString: String = this.index?.toString() ?: ""
        val shiftString = when (showShift) {
            ShowShift.BAR -> when {
                this.shift.isZero() -> ""
                else -> "_"
            }
            ShowShift.S -> when {
                this.shift.isZero() -> ""
                else -> "s"
            }
            ShowShift.S_WITH_DEGREE -> when {
                this.shift.isZero() -> ""
                this.shift.isOne() -> "s"
                else -> {
                    val shiftStr = this.shift.toString()
                    if (shiftStr.length == 1) {
                        "s^$shiftStr"
                    } else {
                        "s^{$shiftStr}"
                    }
                }
            }
        }
        return "$shiftString${this.name.toString(PrintConfig(PrintType.PLAIN))}$indexString"
    }

    private fun toTex(showShift: ShowShift): String {
        val indexString: String = this.index?.toString()?.let { "_{($it)}" } ?: ""
        val shiftString = when (showShift) {
            ShowShift.BAR -> when {
                this.shift.isZero() -> ""
                else -> "\\bar"
            }
            ShowShift.S -> when {
                this.shift.isZero() -> ""
                else -> "s"
            }
            ShowShift.S_WITH_DEGREE -> when {
                this.shift.isZero() -> ""
                this.shift.isOne() -> "s"
                else -> "s^{${this.shift}}"
            }
        }
        // The brace surrounding ${this.name} is necessary to avoid "double subscript"
        //   when this.name contains a subscript
        return "$shiftString{${this.name.toString(PrintConfig(PrintType.TEX))}}$indexString"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CopiedName<*, *>

        if (name != other.name) return false
        if (shift != other.shift) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + shift.hashCode()
        result = 31 * result + (index ?: 0)
        return result
    }


    public companion object {
        public fun <D : Degree, I : IndeterminateName, S : Scalar> getInternalPrintConfig(
            printConfig: PrintConfig,
        ): InternalPrintConfig<MonomialOnCopiedName<D, I>, S> {
            return when (printConfig.printType) {
                PrintType.PLAIN -> InternalPrintConfig(
                    coeffToString = { coeff, withSign -> coeff.toString(PrintType.PLAIN, withSign) },
                    basisToString = { monomial -> monomial.toString(printConfig) }
                )
                PrintType.TEX -> InternalPrintConfig(
                    coeffToString = { coeff, withSign -> coeff.toString(PrintType.TEX, withSign) },
                    basisToString = { monomial -> monomial.toString(printConfig) },
                )
            }
        }

        private fun <D : Degree, I : IndeterminateName> getDefaultIdentifier(
            name: I,
            shift: D,
            index: Int?,
        ): Identifier {
            val indexString: String = index?.toString() ?: ""
            val shiftString: String = if (shift.isZero()) {
                ""
            } else {
                when (val shiftIdentifierName = shift.identifier.name) {
                    "1" -> "s"
                    else -> "s_$shiftIdentifierName"
                }
            }
            val originalName = name.identifier.name
            return Identifier("$shiftString$originalName$indexString")
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

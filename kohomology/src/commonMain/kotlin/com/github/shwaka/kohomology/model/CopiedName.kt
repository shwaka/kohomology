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

/**
 * An implementation of [IndeterminateName] representing a shift or duplicate of [original].
 *
 * The option showShiftExponentInIdentifier is provided to the constructor
 * since necessity to print exponent (i.e. uniqueness of exponents) should be decided
 * in the caller of the constructor (e.g. FreeLoopSpace).
 * If it was provided to [PrintConfig], it would be impossible to decide.
 */
public class CopiedName<D : Degree, I : IndeterminateName>(
    public val original: I,
    public val shift: D,
    public val index: Int? = null,
    showShiftExponentInIdentifier: Boolean = true,
) : IndeterminateName {
    // CopiedName.identifier is computed during initialization to validate its name.
    // This has no performance effect since CopiedName is created very few times
    // (only in initialization of some DGAlgebras, not their elements).
    override val identifier: Identifier = CopiedName.getIdentifier(original, shift, index, showShiftExponentInIdentifier)

    override fun toString(): String {
        return this.toPlain(ShowShift.S_WITH_DEGREE)
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.PLAIN, PrintType.CODE -> this.toPlain(printConfig.showShift)
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
        return "$shiftString${this.original.toString(PrintConfig(PrintType.PLAIN))}$indexString"
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
        // The brace surrounding ${this.original} is necessary to avoid "double subscript"
        //   when this.original contains a subscript
        return "$shiftString{${this.original.toString(PrintConfig(PrintType.TEX))}}$indexString"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CopiedName<*, *>

        if (original != other.original) return false
        if (shift != other.shift) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = original.hashCode()
        result = 31 * result + shift.hashCode()
        result = 31 * result + (index ?: 0)
        return result
    }

    public companion object {
        public fun <D : Degree, I : IndeterminateName, S : Scalar> getInternalPrintConfig(
            printConfig: PrintConfig,
        ): InternalPrintConfig<MonomialOnCopiedName<D, I>, S> {
            return InternalPrintConfig(
                coeffToString = { coeff, withSign -> coeff.toString(printConfig.printType, withSign) },
                basisToString = { monomial -> monomial.toString(printConfig) }
            )
        }

        private fun <D : Degree> getShiftString(shift: D, showShiftExponent: Boolean): String {
            if (shift.isZero()) {
                // This must be the first.
                // Even if showShiftExponent is true, "" should be returned when shift.isZero().
                return ""
            } else if (!showShiftExponent) {
                // Here shift is assumed to be non-zero
                return "s"
            }
            return when (val shiftIdentifierName = shift.identifier.value) {
                "1" -> "s"
                else -> "s_$shiftIdentifierName"
            }
        }

        private fun <D : Degree, I : IndeterminateName> getIdentifier(
            original: I,
            shift: D,
            index: Int?,
            showShiftExponent: Boolean,
        ): Identifier {
            val indexString: String = index?.toString() ?: ""
            val shiftString: String = this.getShiftString(shift, showShiftExponent)
            val originalName = original.identifier.value
            return Identifier("$shiftString$originalName$indexString")
        }
    }
}

public fun <D : Degree, I : IndeterminateName> Indeterminate<D, I>.copy(
    degreeGroup: DegreeGroup<D>,
    shift: D,
    index: Int? = null,
    showShiftExponentInIdentifier: Boolean = true,
): Indeterminate<D, CopiedName<D, I>> {
    val newDegree = degreeGroup.context.run { this@copy.degree - shift }
    return Indeterminate(CopiedName(this.name, shift, index, showShiftExponentInIdentifier), newDegree)
}

public fun <I : IndeterminateName> Indeterminate<IntDegree, I>.copy(
    shift: Int,
    index: Int? = null,
    showShiftExponentInIdentifier: Boolean = true,
): Indeterminate<IntDegree, CopiedName<IntDegree, I>> {
    return this.copy(IntDegreeGroup, IntDegree(shift), index, showShiftExponentInIdentifier)
}

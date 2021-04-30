package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMonoid
import com.github.shwaka.kohomology.free.GeneralizedIndeterminate
import com.github.shwaka.kohomology.free.IndeterminateName

data class CopiedName<I : IndeterminateName, D : Degree>(val name: I, val shift: D, val index: Int? = null) : IndeterminateName {
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
        val indexString: String = this.index?.toString()?.let { "_{($it)}" } ?: ""
        val shiftString = when {
            this.shift.isZero() -> ""
            this.shift.isOne() -> "s"
            else -> "s^{${this.shift}}"
        }
        // The brace surrounding ${this.name} is necessary to avoid "double subscript"
        //   when this.name contains a subscript
        return "$shiftString{${this.name.toTex()}}$indexString"
    }
}

fun <I : IndeterminateName, D : Degree> GeneralizedIndeterminate<I, D>.copy(
    degreeMonoid: DegreeMonoid<D>,
    shift: D,
    index: Int? = null
): GeneralizedIndeterminate<CopiedName<I, D>, D> {
    val newDegree = degreeMonoid.context.run { this@copy.degree - shift }
    return GeneralizedIndeterminate(CopiedName(this.name, shift, index), newDegree)
}

package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.free.IndeterminateName
import kotlin.native.concurrent.ThreadLocal

data class CopiedName<D : Degree, I : IndeterminateName>(val name: I, val shift: D, val index: Int? = null) : IndeterminateName {
    // Variable in singleton without @ThreadLocal can't be changed after initialization
    @ThreadLocal
    companion object {
        var useBar: Boolean = false
    }
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
            this.shift.isOne() -> if (CopiedName.useBar) "\\bar" else "s"
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

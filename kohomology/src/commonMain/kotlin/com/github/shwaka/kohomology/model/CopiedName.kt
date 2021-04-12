package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.free.IndeterminateName
import com.github.shwaka.kohomology.util.Degree

data class CopiedName<I : IndeterminateName>(val name: I, val shift: Degree, val index: Int? = null) : IndeterminateName {
    override fun toString(): String {
        val indexString: String = this.index?.toString() ?: ""
        val shiftString = when (this.shift) {
            0 -> ""
            1 -> "s"
            else -> "s^${this.shift}"
        }
        return "$shiftString${this.name}$indexString"
    }

    override fun toTex(): String {
        val indexString: String = this.index?.toString()?.let { "_{($it)}" } ?: ""
        val shiftString = when (this.shift) {
            0 -> ""
            1 -> "s"
            else -> "s^{${this.shift}}"
        }
        // The brace surrounding ${this.name} is necessary to avoid "double subscript"
        //   when this.name contains a subscript
        return "$shiftString{${this.name}}$indexString"
    }
}

fun <I : IndeterminateName> Indeterminate<I>.copy(
    shift: Degree,
    index: Int? = null
): Indeterminate<CopiedName<I>> {
    return Indeterminate(CopiedName(this.name, shift, index), this.degree - shift)
}

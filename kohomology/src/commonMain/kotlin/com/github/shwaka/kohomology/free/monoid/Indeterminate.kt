package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.PrintType

interface IndeterminateName {
    fun toString(printConfig: PrintConfig): String = this.toString()
}

class StringIndeterminateName(val name: String, tex: String? = null) : IndeterminateName {
    val tex: String = tex ?: name

    override fun toString(): String = this.name
    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.PLAIN -> this.name
            PrintType.TEX -> this.tex
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StringIndeterminateName

        if (name != other.name) return false
        if (tex != other.tex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + tex.hashCode()
        return result
    }
}

data class Indeterminate<D : Degree, I : IndeterminateName>(val name: I, val degree: D) {
    companion object {
        operator fun <D : Degree> invoke(name: String, degree: D): Indeterminate<D, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name), degree)
        }
        operator fun <D : Degree> invoke(name: String, tex: String, degree: D): Indeterminate<D, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name, tex), degree)
        }
        operator fun <I : IndeterminateName> invoke(name: I, degree: Int): Indeterminate<IntDegree, I> {
            return Indeterminate(name, IntDegree(degree))
        }
        operator fun invoke(name: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name), IntDegree(degree))
        }
        operator fun invoke(name: String, tex: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
            return Indeterminate(StringIndeterminateName(name, tex), IntDegree(degree))
        }
    }
    override fun toString(): String {
        return this.name.toString()
    }

    fun <D_ : Degree> convertDegree(degreeMorphism: DegreeMorphism<D, D_>): Indeterminate<D_, I> {
        return Indeterminate(this.name, degreeMorphism(this.degree))
    }
}

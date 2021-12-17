package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public interface IndeterminateName {
    public fun toString(printConfig: PrintConfig): String = this.toString()
}

public class StringIndeterminateName(public val name: String, tex: String? = null) : IndeterminateName {
    public val tex: String = tex ?: name

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

public fun <D : Degree> Indeterminate(name: String, degree: D): Indeterminate<D, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name), degree)
}
public fun <D : Degree> Indeterminate(name: String, tex: String, degree: D): Indeterminate<D, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name, tex), degree)
}
public fun <I : IndeterminateName> Indeterminate(name: I, degree: Int): Indeterminate<IntDegree, I> {
    return Indeterminate(name, IntDegree(degree))
}
public fun Indeterminate(name: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name), IntDegree(degree))
}
public fun Indeterminate(name: String, tex: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name, tex), IntDegree(degree))
}

public data class Indeterminate<D : Degree, I : IndeterminateName>(val name: I, val degree: D) {
    override fun toString(): String {
        return this.name.toString()
    }

    public fun toString(printConfig: PrintConfig): String {
        return this.name.toString(printConfig)
    }

    public fun <D_ : Degree> convertDegree(degreeMorphism: DegreeMorphism<D, D_>): Indeterminate<D_, I> {
        return Indeterminate(this.name, degreeMorphism(this.degree))
    }
}

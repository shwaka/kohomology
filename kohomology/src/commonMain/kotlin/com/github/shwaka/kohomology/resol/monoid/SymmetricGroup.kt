package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.getPermutation

public data class Permutation(val values: List<Int>) : FiniteMonoidElement {
    val order: Int = values.size

    internal fun compose(other: Permutation): Permutation {
        require(this.order == other.order)
        val values = (0 until this.order).map {
            other.values[this.values[it]]
        }
        return Permutation(values)
    }

    internal fun inverse(): Permutation {
        val values: List<Int> = (0 until this.order).map { i ->
            this.values.indexOf(i).also {
                if (it == -1) {
                    throw Exception("Invalid permutation: $i not found in ${this.values}")
                }
            }
        }
        return Permutation(values)
    }

    public companion object {
        public fun getIdentity(order: Int): Permutation {
            return Permutation(
                (0 until order).toList()
            )
        }
    }
}

public class SymmetricGroup(public val order: Int) : FiniteGroup<Permutation> {
    override val context: FiniteGroupContext<Permutation> = FiniteGroupContext(this)
    override val unit: Permutation = Permutation.getIdentity(order)
    override val elements: List<Permutation> = getPermutation(
        (0 until order).toList()
    ).asSequence().map {
        Permutation(it.first)
    }.toList()
    override val isCommutative: Boolean = false
    override fun multiply(monoidElement1: Permutation, monoidElement2: Permutation): Permutation {
        return monoidElement1.compose(monoidElement2)
    }
    override fun invert(monoidElement: Permutation): Permutation {
        return monoidElement.inverse()
    }

    override val multiplicationTable: List<List<Permutation>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.TEX -> "{\\mathfrak S}_{${this.order}}"
            else -> "S_${this.order}"
        }
    }
}

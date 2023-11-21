package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.util.directProductOfFamily
import com.github.shwaka.kohomology.vectsp.BasisName

public class BarBasisName<E : FiniteMonoidElement>(
    public val monoid: FiniteMonoid<E>,
    public val elementList: List<E>,
) : BasisName {
    public val degree: Int = -elementList.size // cohomological degree
    public val size: Int = elementList.size

    public fun boundary(i: Int): BarBasisName<E> {
        require(this.size > 0) { "boundary can be applied only when the size is positive" }
        require(i >= 0) { "i must be non-negative, but was $i" }
        require(i <= this.size) { "i must be less than or equal to the degree ${this.size}, but was $i" }
        return when {
            (i == 0) -> BarBasisName(
                monoid = this.monoid,
                elementList = this.elementList.drop(1),
            )

            (i == this.size) -> BarBasisName(
                monoid = this.monoid,
                elementList = this.elementList.dropLast(1),
            )

            else -> {
                val first = this.elementList.take(i - 1)
                val multiplied = this.monoid.multiply(
                    this.elementList[i - 1],
                    this.elementList[i],
                )
                val last = this.elementList.takeLast(this.size - i - 1)
                BarBasisName(
                    monoid = this.monoid,
                    elementList = first + listOf(multiplied) + last,
                )
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BarBasisName<*>

        if (monoid != other.monoid) return false
        if (elementList != other.elementList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = monoid.hashCode()
        result = 31 * result + elementList.hashCode()
        return result
    }

    override fun toString(): String {
        return "[" + this.elementList.joinToString(",") + "]"
    }
}

public fun <E : FiniteMonoidElement> FiniteMonoid<E>.getAllBarBasisName(size: Int): List<BarBasisName<E>> {
    require(size >= 0) { "size must be non-negative, but was $size" }
    val elementLists: List<List<E>> = directProductOfFamily(List(size) { this.elements })
    return elementLists.map { elementList ->
        BarBasisName(this, elementList)
    }
}

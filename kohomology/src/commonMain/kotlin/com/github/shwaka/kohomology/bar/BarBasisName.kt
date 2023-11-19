package com.github.shwaka.kohomology.bar

import com.github.shwaka.kohomology.vectsp.BasisName

public class BarBasisName<E : FiniteMonoidElement>(
    public val monoid: FiniteMonoid<E>,
    public val elementList: List<E>,
) : BasisName {
    public val degree: Int = elementList.size

    public fun boundary(i: Int): BarBasisName<E> {
        require(this.degree > 0) { "boundary can be applied only when the degree is positive" }
        require(i >= 0) { "i must be non-negative, but was $i" }
        require(i <= this.degree) { "i must be less than the degree ${this.degree}, but was $i" }
        return when {
            (i == 0) -> BarBasisName(
                monoid = this.monoid,
                elementList = this.elementList.drop(1),
            )
            (i == this.degree) -> BarBasisName(
                monoid = this.monoid,
                elementList = this.elementList.dropLast(1),
            )
            else -> {
                val first = this.elementList.take(i - 1)
                val multiplied = this.monoid.multiply(
                    this.elementList[i - 1],
                    this.elementList[i],
                )
                val last = this.elementList.takeLast(this.degree - i - 1)
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

package com.github.shwaka.kohomology.dg

interface Degree {
    fun toInt(): Int
}

class DegreeContext<D : Degree>(monoid: DegreeMonoid<D>) : DegreeMonoid<D> by monoid {
    fun Int.toDegree(): D = this@DegreeContext.fromInt(this)
    operator fun D.plus(other: D): D = this@DegreeContext.add(this, other)
    operator fun Int.plus(other: D): D = this@DegreeContext.add(this.toDegree(), other)
    operator fun D.plus(other: Int): D = this@DegreeContext.add(this, other.toDegree())
    operator fun D.minus(other: D): D = this@DegreeContext.subtract(this, other)
    operator fun Int.minus(other: D): D = this@DegreeContext.subtract(this.toDegree(), other)
    operator fun D.minus(other: Int): D = this@DegreeContext.subtract(this, other.toDegree())
}

interface DegreeMonoid<D : Degree> {
    val context: DegreeContext<D>
    fun fromInt(n: Int): D
    fun add(degree1: D, degree2: D): D
    fun subtract(degree1: D, degree2: D): D
    val zero: D
        get() = this.fromInt(0)
}

data class IntDegree(val value: Int) : Degree {
    override fun toInt(): Int = this.value
}

object IntDegreeMonoid : DegreeMonoid<IntDegree> {
    override val context: DegreeContext<IntDegree> by lazy {
        DegreeContext(this)
    }

    override fun fromInt(n: Int): IntDegree {
        return IntDegree(n)
    }

    override fun add(degree1: IntDegree, degree2: IntDegree): IntDegree {
        return IntDegree(degree1.value + degree2.value)
    }

    override fun subtract(degree1: IntDegree, degree2: IntDegree): IntDegree {
        return IntDegree(degree1.value - degree2.value)
    }
}

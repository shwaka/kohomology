package com.github.shwaka.kohomology.dg.degree

interface Degree {
    fun toInt(): Int
    fun isZero(): Boolean
    fun isOne(): Boolean
    fun isNotZero(): Boolean = this.toInt() != 0
    fun isEven(): Boolean
    fun isOdd(): Boolean = !this.isEven()
}

class DegreeContext<D : Degree>(group: DegreeGroup<D>) : DegreeGroup<D> by group {
    fun Int.toDegree(): D = this@DegreeContext.fromInt(this)
    operator fun D.plus(other: D): D = this@DegreeContext.add(this, other)
    operator fun Int.plus(other: D): D = this@DegreeContext.add(this.toDegree(), other)
    operator fun D.plus(other: Int): D = this@DegreeContext.add(this, other.toDegree())
    operator fun D.minus(other: D): D = this@DegreeContext.subtract(this, other)
    operator fun Int.minus(other: D): D = this@DegreeContext.subtract(this.toDegree(), other)
    operator fun D.minus(other: Int): D = this@DegreeContext.subtract(this, other.toDegree())
    operator fun D.times(n: Int): D = this@DegreeContext.multiply(this, n)
    operator fun Int.times(degree: D): D = this@DegreeContext.multiply(degree, this)
}

interface DegreeGroup<D : Degree> {
    val context: DegreeContext<D>
    fun fromInt(n: Int): D
    fun add(degree1: D, degree2: D): D
    fun subtract(degree1: D, degree2: D): D
    fun multiply(degree: D, n: Int): D
    val zero: D
        get() = this.fromInt(0)
}

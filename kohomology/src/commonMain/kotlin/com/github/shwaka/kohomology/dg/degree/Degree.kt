package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.util.isEven
import com.github.shwaka.kohomology.util.isOdd

interface Degree {
    fun toInt(): Int
    fun isZero(): Boolean = this.toInt() == 0
    fun isNotZero(): Boolean = this.toInt() != 0
    fun isEven(): Boolean
    fun isOdd(): Boolean = !this.isEven()
}

class DegreeContext<D : Degree>(monoid: DegreeMonoid<D>) : DegreeMonoid<D> by monoid {
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

interface DegreeMonoid<D : Degree> {
    val context: DegreeContext<D>
    fun fromInt(n: Int): D
    fun add(degree1: D, degree2: D): D
    fun subtract(degree1: D, degree2: D): D
    fun multiply(degree: D, n: Int): D
    val zero: D
        get() = this.fromInt(0)
}

data class IntDegree(val value: Int) : Degree {
    override fun toInt(): Int = this.value
    override fun isEven(): Boolean {
        return (this.value % 2 == 0)
    }
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

    override fun multiply(degree: IntDegree, n: Int): IntDegree {
        return IntDegree(degree.value * n)
    }
}

data class DegreeIndeterminate(val name: String, val defaultValue: Int)

data class LinearDegree(val monoid: LinearDegreeMonoid, val constantTerm: Int, val coeffList: IntArray) : Degree {
    override fun toInt(): Int {
        return this.constantTerm + this.coeffList.indices.map {
            this.coeffList[it] * this.monoid.indeterminateList[it].defaultValue
        }.sum()
    }

    override fun isEven(): Boolean {
        this.coeffList.indices.filter { this.coeffList[it].isOdd() }.let { oddIndices ->
            if (oddIndices.isNotEmpty()) {
                val oddIndeterminateString = oddIndices.joinToString(", ") {
                    this.monoid.indeterminateList[it].toString()
                }
                throw ArithmeticException(
                    "Cannot determine the parity of $this, since the coefficients of $oddIndeterminateString are odd"
                )
            }
        }
        return this.constantTerm.isEven()
    }
}

data class LinearDegreeMonoid(val indeterminateList: List<DegreeIndeterminate>) : DegreeMonoid<LinearDegree> {
    override val context: DegreeContext<LinearDegree> by lazy {
        DegreeContext(this)
    }

    override fun fromInt(n: Int): LinearDegree {
        return LinearDegree(this, n, intArrayOf())
    }

    override fun add(degree1: LinearDegree, degree2: LinearDegree): LinearDegree {
        if (degree1.monoid != this)
            throw IllegalContextException("$degree1 is not an element of $this")
        if (degree2.monoid != this)
            throw IllegalContextException("$degree2 is not an element of $this")
        val coeffList = degree1.coeffList.indices.map { degree1.coeffList[it] + degree2.coeffList[it] }
        return this.fromCoefficients(degree1.constantTerm + degree2.constantTerm, coeffList)
    }

    override fun subtract(degree1: LinearDegree, degree2: LinearDegree): LinearDegree {
        if (degree1.monoid != this)
            throw IllegalContextException("$degree1 is not an element of $this")
        if (degree2.monoid != this)
            throw IllegalContextException("$degree2 is not an element of $this")
        val coeffList = degree1.coeffList.indices.map { degree1.coeffList[it] - degree2.coeffList[it] }
        return this.fromCoefficients(degree1.constantTerm + degree2.constantTerm, coeffList)
    }

    override fun multiply(degree: LinearDegree, n: Int): LinearDegree {
        if (degree.monoid != this)
            throw IllegalContextException("$degree is not an element of $this")
        val coeffList = degree.coeffList.indices.map { degree.coeffList[it] * n }
        return this.fromCoefficients(degree.constantTerm * n, coeffList)
    }

    fun fromCoefficients(constantTerm: Int, coeffList: List<Int>): LinearDegree {
        if (coeffList.size != this.indeterminateList.size)
            throw IllegalArgumentException("The length of $coeffList should be ${this.indeterminateList.size}, but ${coeffList.size} was given")
        return LinearDegree(this, constantTerm, coeffList.toIntArray())
    }
}

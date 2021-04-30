package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.util.isEven
import com.github.shwaka.kohomology.util.isOdd

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

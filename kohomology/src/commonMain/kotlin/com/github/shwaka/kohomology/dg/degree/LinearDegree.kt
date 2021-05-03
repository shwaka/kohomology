package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.util.isEven
import com.github.shwaka.kohomology.util.isOdd

data class DegreeIndeterminate(val name: String, val defaultValue: Int)

class LinearDegree(val monoid: LinearDegreeGroup, val constantTerm: Int, val coeffList: IntArray) : Degree {
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

    override fun isZero(): Boolean {
        return (this.constantTerm == 0) && this.coeffList.all { it == 0 }
    }

    override fun isOne(): Boolean {
        return (this.constantTerm == 1) && this.coeffList.all { it == 0 }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LinearDegree

        if (monoid != other.monoid) return false
        if (constantTerm != other.constantTerm) return false
        if (!coeffList.contentEquals(other.coeffList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = monoid.hashCode()
        result = 31 * result + constantTerm
        result = 31 * result + coeffList.contentHashCode()
        return result
    }

    override fun toString(): String {
        return if (this.coeffList.isEmpty()) {
            this.constantTerm.toString()
        } else {
            "${this.constantTerm} + " + this.coeffList.indices.joinToString(" + ") {
                "${this.coeffList[it]}${this.monoid.indeterminateList[it].name}"
            }
        }
    }
}

data class LinearDegreeGroup(val indeterminateList: List<DegreeIndeterminate>) : DegreeGroup<LinearDegree> {
    override val context: DegreeContext<LinearDegree> by lazy {
        DegreeContext(this)
    }

    val generatorList: List<LinearDegree>
        get() = this.indeterminateList.indices.map { i ->
            val coeffList = List(this.indeterminateList.size) { j -> if (i == j) 1 else 0 }
            this.fromCoefficients(0, coeffList)
        }

    override fun fromInt(n: Int): LinearDegree {
        return LinearDegree(this, n, IntArray(this.indeterminateList.size) { 0 })
    }

    override fun augmentation(degree: LinearDegree): Int {
        return degree.constantTerm + degree.coeffList.indices.map {
            degree.coeffList[it] * this.indeterminateList[it].defaultValue
        }.sum()
    }

    override fun add(degree1: LinearDegree, degree2: LinearDegree): LinearDegree {
        if (degree1.monoid != this)
            throw IllegalContextException("$degree1 is not an element of $this")
        if (degree2.monoid != this)
            throw IllegalContextException("$degree2 is not an element of $this")
        val coeffList = IntArray(degree1.coeffList.size) { degree1.coeffList[it] + degree2.coeffList[it] }
        return LinearDegree(this, degree1.constantTerm + degree2.constantTerm, coeffList)
    }

    override fun subtract(degree1: LinearDegree, degree2: LinearDegree): LinearDegree {
        if (degree1.monoid != this)
            throw IllegalContextException("$degree1 is not an element of $this")
        if (degree2.monoid != this)
            throw IllegalContextException("$degree2 is not an element of $this")
        val coeffList = IntArray(degree1.coeffList.size) { degree1.coeffList[it] - degree2.coeffList[it] }
        return LinearDegree(this, degree1.constantTerm - degree2.constantTerm, coeffList)
    }

    override fun multiply(degree: LinearDegree, n: Int): LinearDegree {
        if (degree.monoid != this)
            throw IllegalContextException("$degree is not an element of $this")
        val coeffList = IntArray(degree.coeffList.size) { degree.coeffList[it] * n }
        return LinearDegree(this, degree.constantTerm * n, coeffList)
    }

    fun fromCoefficients(constantTerm: Int, coeffList: List<Int>): LinearDegree {
        if (coeffList.size != this.indeterminateList.size)
            throw IllegalArgumentException("The length of $coeffList should be ${this.indeterminateList.size}, but ${coeffList.size} was given")
        return LinearDegree(this, constantTerm, coeffList.toIntArray())
    }

    fun fromList(coeffList: List<Int>): LinearDegree {
        if (coeffList.size != this.indeterminateList.size + 1)
            throw IllegalArgumentException("The length of $coeffList should be ${this.indeterminateList.size + 1}, but ${coeffList.size} was given")
        return LinearDegree(this, coeffList[0], coeffList.drop(1).toIntArray())
    }
}

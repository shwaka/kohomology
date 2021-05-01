package com.github.shwaka.kohomology.dg.degree

data class IntDegree(val value: Int) : Degree {
    override fun toInt(): Int = this.value
    override fun isEven(): Boolean {
        return (this.value % 2 == 0)
    }

    override fun isZero(): Boolean {
        return this.value == 0
    }

    override fun isOne(): Boolean {
        return this.value == 1
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

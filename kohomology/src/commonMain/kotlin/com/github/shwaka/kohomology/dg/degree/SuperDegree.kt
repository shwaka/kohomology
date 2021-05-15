package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.util.isEven

sealed class SuperDegree : Degree {
    internal abstract val theOther: SuperDegree
}
object EvenSuperDegree : SuperDegree() {
    override fun isEven() = true
    override fun isZero() = true
    override fun isOne() = false
    override val theOther = OddSuperDegree
}
object OddSuperDegree : SuperDegree() {
    override fun isEven() = false
    override fun isZero() = false
    override fun isOne() = true
    override val theOther = EvenSuperDegree
}

object SuperDegreeGroup : DegreeGroup<SuperDegree> {
    override val context: DegreeContext<SuperDegree> by lazy {
        DegreeContext(this)
    }

    override fun fromInt(n: Int): SuperDegree {
        return if (n.isEven()) EvenSuperDegree else OddSuperDegree
    }

    override fun add(degree1: SuperDegree, degree2: SuperDegree): SuperDegree {
        return when (degree1) {
            is EvenSuperDegree -> degree2
            is OddSuperDegree -> degree2.theOther
        }
    }

    override fun subtract(degree1: SuperDegree, degree2: SuperDegree) = this.add(degree1, degree2)

    override fun multiply(degree: SuperDegree, n: Int): SuperDegree {
        return if (n.isEven()) EvenSuperDegree else degree
    }

    override fun contains(degree: SuperDegree): Boolean {
        // type information is sufficient
        return true
    }
}

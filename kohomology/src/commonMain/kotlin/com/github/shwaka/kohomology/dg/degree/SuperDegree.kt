package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.util.PartialIdentifier
import com.github.shwaka.kohomology.util.isEven

/** An element of [SuperDegreeGroup]. */
public sealed class SuperDegree : Degree {
    internal abstract val theOther: SuperDegree
}
/** The even element of [SuperDegreeGroup]. */
public object EvenSuperDegree : SuperDegree() {
    override val identifier: PartialIdentifier
        get() = PartialIdentifier("0")
    override fun isEven(): Boolean = true
    override fun isZero(): Boolean = true
    override fun isOne(): Boolean = false
    override val theOther = OddSuperDegree
}
/** The odd element of [SuperDegreeGroup]. */
public object OddSuperDegree : SuperDegree() {
    override val identifier: PartialIdentifier
        get() = PartialIdentifier("1")
    override fun isEven(): Boolean = false
    override fun isZero(): Boolean = false
    override fun isOne(): Boolean = true
    override val theOther = EvenSuperDegree
}

/**
 * A [DegreeGroup] representing Z/2.
 *
 * This [DegreeGroup] can be used to treat
 * [super vector space](https://en.wikipedia.org/wiki/Super_vector_space).
 */
public object SuperDegreeGroup : DegreeGroup<SuperDegree> {
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

    override fun subtract(degree1: SuperDegree, degree2: SuperDegree): SuperDegree {
        return this.add(degree1, degree2)
    }

    override fun multiply(degree: SuperDegree, n: Int): SuperDegree {
        return if (n.isEven()) EvenSuperDegree else degree
    }

    override fun contains(degree: SuperDegree): Boolean {
        // type information is sufficient
        return true
    }
}

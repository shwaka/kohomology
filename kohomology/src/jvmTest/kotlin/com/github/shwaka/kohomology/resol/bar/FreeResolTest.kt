package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.module.finder.EarlyReturnSelector
import com.github.shwaka.kohomology.resol.module.finder.FilteredSelector
import com.github.shwaka.kohomology.resol.module.finder.MonoidRingFinder
import com.github.shwaka.kohomology.resol.module.finder.SimpleSelector
import com.github.shwaka.kohomology.resol.module.finder.SmallGeneratorFinder
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.resol.monoid.SimpleFiniteMonoidElement
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF5
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF7
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

val freeResolTag = NamedTag("FreeResol")

private typealias GetFinder<E, S, V, M> =
    (coeffAlgebra: MonoidRing<E, S, V, M>) -> SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>>

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testFreeResolOfCyclicGroup(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
    getFinder: GetFinder<CyclicGroupElement, S, V, M> = { SmallGeneratorFinder.getDefaultFor(it) },
) = freeSpec {
    require(order > 1)
    val coeffAlgebra = MonoidRing(CyclicGroup(order), matrixSpace)
    val finder = getFinder(coeffAlgebra)
    val complex = FreeResol(coeffAlgebra, finder)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Z/$order] (with $finder)" - {
        val maxDegree = 10

        "underlyingDGVectorSpace[degree].dim should be 0 or $order" {
            (-maxDegree..maxDegree).forAll { degree ->
                complex.underlyingDGVectorSpace[degree].dim shouldBeIn listOf(0, order)
            }
        }

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when {
                    (order == matrixSpace.field.characteristic) -> when {
                        (degree > 0) -> 0
                        else -> 1
                    }
                    else -> when (degree) {
                        0 -> 1
                        else -> 0
                    }
                }
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe expected
            }
        }

        "test sequential access" {
            val newComplex = FreeResol(coeffAlgebra, finder)
            val maxDeg = 10

            (0..maxDeg).forAll { n ->
                val degree = -n
                shouldNotThrow<IllegalStateException> {
                    newComplex.underlyingDGVectorSpace[degree]
                }
            }
        }
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testFreeResolOfFiedorowiczMonoid(
    matrixSpace: MatrixSpace<S, V, M>,
    checkResolRank: Boolean,
    getFinder: GetFinder<SimpleFiniteMonoidElement<String>, S, V, M> =
        { SmallGeneratorFinder.getDefaultFor(it) },
) = freeSpec {
    // Z. Fiedorowicz,
    // A counterexample to a group completion conjecture of J. C. Moore,
    // Algebr. Geom. Topol., 2002
    val elements = listOf("1", "x1", "x2", "y1", "y2")
    val multiplicationTable = listOf(
        listOf("1", "x1", "x2", "y1", "y2"),
        listOf("x1", "x1", "x1", "y1", "y1"),
        listOf("x2", "x2", "x2", "y2", "y2"),
        listOf("y1", "x1", "x1", "y1", "y1"),
        listOf("y2", "x2", "x2", "y2", "y2"),
    )
    val monoid = FiniteMonoidFromList(elements, multiplicationTable, "M")
    val coeffAlgebra = MonoidRing(monoid, matrixSpace)
    val finder = getFinder(coeffAlgebra)
    val complex = FreeResol(coeffAlgebra, finder)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Fiedorowicz monoid] (with $finder)" - {
        val maxDegree = 10

        if (checkResolRank) {
            "underlyingDGVectorSpace[degree].dim should be not greater than 15" {
                // Expected values have no mathematical meaning.
                // They are the values from SmallGeneratorFinder.SimpleFinder
                // and chosen to assert that other finders are "not bad".
                (-maxDegree..maxDegree).forAll { degree ->
                    val expected = when {
                        (degree == 0) -> 5
                        (degree == -1) -> 10
                        (degree <= -2) -> 15
                        else -> 0
                    }
                    complex.underlyingDGVectorSpace[degree].dim shouldBeLessThanOrEqual expected
                }
            }
        }

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0, -2 -> 1
                    else -> 0
                }
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testFreeResolOfMonoidOfOrder6(
    matrixSpace: MatrixSpace<S, V, M>,
    checkResolRank: Boolean,
    getFinder: GetFinder<SimpleFiniteMonoidElement<String>, S, V, M> =
        { SmallGeneratorFinder.getDefaultFor(it) },
) = freeSpec {
    // variant of Fiedorowicz monoid
    val elements = listOf("1", "x1", "x0", "x2", "y1", "y2")
    val multiplicationTable = listOf(
        listOf("1", "x1", "x0", "x2", "y1", "y2"),
        listOf("x1", "x1", "x1", "x1", "y1", "y1"),
        listOf("x0", "x1", "x1", "x1", "y1", "y1"),
        listOf("x2", "x2", "x2", "x2", "y2", "y2"),
        listOf("y1", "x1", "x1", "x1", "y1", "y1"),
        listOf("y2", "x2", "x2", "x2", "y2", "y2"),
    )
    val monoid = FiniteMonoidFromList(elements, multiplicationTable, "M")
    val coeffAlgebra = MonoidRing(monoid, matrixSpace)
    val finder = getFinder(coeffAlgebra)
    val complex = FreeResol(coeffAlgebra, finder)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[monoid of order 6] (with $finder)" - {
        val maxDegree = 10

        if (checkResolRank) {
            "test underlyingDGVectorSpace[degree].dim" {
                // Expected values have no mathematical meaning.
                // They are the values from SmallGeneratorFinder.SimpleFinder
                // and chosen to assert that other finders are "not bad".
                (-maxDegree..maxDegree).forAll { degree ->
                    val expected = when {
                        (degree <= 0) -> (-degree + 1) * 6
                        else -> 0
                    }
                    complex.underlyingDGVectorSpace[degree].dim shouldBeLessThanOrEqual expected
                }
            }
        }

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when {
                    (degree <= -2) -> 1
                    (degree == 0) -> 1
                    else -> 0
                }
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class FreeResolTest : FreeSpec({
    tags(moduleTag, freeResolTag)

    include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(3, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(3, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(3, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverF5))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF5))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF7))
    include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverRational, true))
    include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverF2, true))
    include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverF3, true))
    include(testFreeResolOfMonoidOfOrder6(SparseMatrixSpaceOverRational, true))
    include(testFreeResolOfMonoidOfOrder6(SparseMatrixSpaceOverF2, true))
    include(testFreeResolOfMonoidOfOrder6(SparseMatrixSpaceOverF3, true))

    "test minDegreeComputedAlready" - {
        val order = 5
        val matrixSpace = SparseMatrixSpaceOverRational
        val coeffAlgebra = MonoidRing(CyclicGroup(order), matrixSpace)

        "minDegreeComputedAlready should be 1 if nothing is computed" {
            val complex = FreeResol(coeffAlgebra)
            complex.minDegreeComputedAlready shouldBe 1
        }

        "minDegreeComputedAlready should be 1 if only positive degree is accessed" {
            (1..10).forAll { degree ->
                val complex = FreeResol(coeffAlgebra)
                // The following line is a trivial test,
                // which is added to access the module at the degree
                complex.underlyingDGVectorSpace[degree].dim shouldBeGreaterThanOrEqual 0
                complex.minDegreeComputedAlready shouldBe 1
            }
        }

        "minDegreeComputedAlready should be -n if only degree -n is computed" {
            (0..10).forAll { n ->
                val degree = -n
                val complex = FreeResol(coeffAlgebra)
                // The following line is a trivial test,
                // which is added to compute the module at the degree
                complex.underlyingDGVectorSpace[degree].dim shouldBeGreaterThanOrEqual 0
                complex.minDegreeComputedAlready shouldBe degree
            }
        }
    }
})

private interface FinderCreator {
    val checkResolRank: Boolean
    // Note: functional interface cannot contain generic function
    fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getFinder(
        coeffAlgebra: MonoidRing<E, S, V, M>
    ): SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>>
}

private object SimpleFinderCreator : FinderCreator {
    override val checkResolRank: Boolean = true
    override fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getFinder(coeffAlgebra: MonoidRing<E, S, V, M>): SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> {
        return SimpleSelector(coeffAlgebra)
    }
}

private object FilteredFinderCreator : FinderCreator {
    override val checkResolRank: Boolean = true
    override fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getFinder(coeffAlgebra: MonoidRing<E, S, V, M>): SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> {
        return FilteredSelector(coeffAlgebra)
    }
}

private object EarlyReturnFinderCreator : FinderCreator {
    override val checkResolRank: Boolean = true
    override fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getFinder(coeffAlgebra: MonoidRing<E, S, V, M>): SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> {
        return EarlyReturnSelector(coeffAlgebra)
    }
}

private object MonoidRingFinderCreator : FinderCreator {
    override val checkResolRank: Boolean = false
    override fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getFinder(coeffAlgebra: MonoidRing<E, S, V, M>): SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> {
        return MonoidRingFinder(coeffAlgebra)
    }
}

class FreeResolWithFinderTest : FreeSpec({
    tags(moduleTag, freeResolTag)

    val finderCreatorList = listOf(
        SimpleFinderCreator,
        FilteredFinderCreator,
        EarlyReturnFinderCreator,
        MonoidRingFinderCreator,
    )

    for (creator in finderCreatorList) {
        include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverRational, creator::getFinder))
        include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverF2, creator::getFinder))
        include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverRational, creator.checkResolRank, creator::getFinder))
        include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverF3, creator.checkResolRank, creator::getFinder))
        include(testFreeResolOfMonoidOfOrder6(SparseMatrixSpaceOverRational, creator.checkResolRank, creator::getFinder))
    }
})

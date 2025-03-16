package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.forAll
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.math.min

private typealias GetMonomialListGenerator<D, I> =
    (AugmentedDegreeGroup<D>, IndeterminateList<D, I>) -> MonomialListGenerator<D, I>

private fun monomialListGeneratorTestOverIntDegree(
    getMonomialListGenerator: GetMonomialListGenerator<IntDegree, StringIndeterminateName>,
) = freeSpec {
    "test monomialListGenerator over IntDegree" - {
        "free monoid with two generators of even degree" {
            val indeterminateList = IndeterminateList.from(
                IntDegreeGroup,
                listOf(
                    Indeterminate("x", 2),
                    Indeterminate("y", 2),
                )
            )
            val monomialListGenerator = getMonomialListGenerator(IntDegreeGroup, indeterminateList)
            (0..10).forAll { n ->
                monomialListGenerator.listMonomials(IntDegree(n)) shouldHaveSize when {
                    n.mod(2) == 1 -> 0
                    else -> n / 2 + 1
                }
            }
            monomialListGenerator.listMonomials(IntDegree(2)).map { it.toString() } shouldBe
                listOf("x", "y")
            monomialListGenerator.listMonomials(IntDegree(4)).map { it.toString() } shouldBe
                listOf("x^2", "xy", "y^2")
            monomialListGenerator.listMonomials(IntDegree(6)).map { it.toString() } shouldBe
                listOf("x^3", "x^2y", "xy^2", "y^3")
        }

        "free monoid with two generators of odd degree" {
            val indeterminateList = IndeterminateList.from(
                IntDegreeGroup,
                listOf(
                    Indeterminate("x", 3),
                    Indeterminate("y", 3),
                )
            )
            val monomialListGenerator = getMonomialListGenerator(IntDegreeGroup, indeterminateList)
            (0..10).forAll { n ->
                monomialListGenerator.listMonomials(IntDegree(n)) shouldHaveSize when (n) {
                    0, 6 -> 1
                    3 -> 2
                    else -> 0
                }
            }
            monomialListGenerator.listMonomials(IntDegree(3)).map { it.toString() } shouldBe
                listOf("x", "y")
            monomialListGenerator.listMonomials(IntDegree(6)).map { it.toString() } shouldBe
                listOf("xy")
        }
    }
}

private fun monomialListGeneratorTestOverMultiDegree(
    getMonomialListGenerator: GetMonomialListGenerator<MultiDegree, StringIndeterminateName>,
) = freeSpec {
    "test monomialListGenerator over MultiDegree" - {
        "free monoid with two generators of even degree" {
            val degreeGroup = MultiDegreeGroup(listOf(DegreeIndeterminate("S", 1)))
            fun getDegree(a: Int, b: Int): MultiDegree = degreeGroup.fromList(listOf(a, b))
            val indeterminateList = IndeterminateList.from(
                degreeGroup,
                listOf(
                    Indeterminate("x", getDegree(2, 0)),
                    Indeterminate("y", getDegree(0, 2)),
                )
            )
            val monomialListGenerator = getMonomialListGenerator(degreeGroup, indeterminateList)
            (0..10).forAll { n ->
                (0..10).forAll { m ->
                    monomialListGenerator.listMonomials(
                        getDegree(2 * n, 2 * m)
                    ) shouldHaveSize 1
                }
            }
            monomialListGenerator.listMonomials(getDegree(2, 0)).map { it.toString() } shouldBe
                listOf("x")
            monomialListGenerator.listMonomials(getDegree(0, 2)).map { it.toString() } shouldBe
                listOf("y")
            monomialListGenerator.listMonomials(getDegree(2, 2)).map { it.toString() } shouldBe
                listOf("xy")
        }

        "free monoid with three generators of even degree" {
            val degreeGroup = MultiDegreeGroup(listOf(DegreeIndeterminate("S", 1)))
            fun getDegree(a: Int, b: Int): MultiDegree = degreeGroup.fromList(listOf(a, b))
            val indeterminateList = IndeterminateList.from(
                degreeGroup,
                listOf(
                    Indeterminate("x", getDegree(2, 0)),
                    Indeterminate("y", getDegree(2, 2)),
                    Indeterminate("z", getDegree(0, 2)),
                )
            )
            val monomialListGenerator = getMonomialListGenerator(degreeGroup, indeterminateList)
            (0..10).forAll { n ->
                (0..10).forAll { m ->
                    monomialListGenerator.listMonomials(
                        getDegree(2 * n, 2 * m)
                    ) shouldHaveSize (min(n, m) + 1)
                }
            }
            monomialListGenerator.listMonomials(getDegree(2, 0)).map { it.toString() } shouldBe
                listOf("x")
            monomialListGenerator.listMonomials(getDegree(2, 2)).map { it.toString() }.toSet() shouldBe
                setOf("xz", "y")
            monomialListGenerator.listMonomials(getDegree(0, 2)).map { it.toString() } shouldBe
                listOf("z")
            monomialListGenerator.listMonomials(getDegree(4, 2)).map { it.toString() }.toSet() shouldBe
                setOf("xy", "x^2z")
        }
    }
}

class MonomialListGeneratorBasicTest : FreeSpec({
    include(monomialListGeneratorTestOverIntDegree(::MonomialListGeneratorBasic))
    include(monomialListGeneratorTestOverMultiDegree(::MonomialListGeneratorBasic))
})

class MonomialListGeneratorAugmentedTest : FreeSpec({
    include(monomialListGeneratorTestOverIntDegree(::MonomialListGeneratorAugmented))
    include(monomialListGeneratorTestOverMultiDegree(::MonomialListGeneratorAugmented))
})

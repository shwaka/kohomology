package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

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

class MonomialListGeneratorTest : FreeSpec({
    include(
        monomialListGeneratorTestOverIntDegree { degreeGroup, indeterminateList ->
            MonomialListGeneratorBasic(degreeGroup, indeterminateList)
        }
    )
})

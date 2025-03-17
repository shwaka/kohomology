package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.util.pow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

private typealias GetNCMonomialListGenerator<D, I> =
    (AugmentedDegreeGroup<D>, IndeterminateList<D, I>) -> NCMonomialListGenerator<D, I>

private fun ncMonomialListGeneratorTestOverIntDegree(
    getNCMonomialListGenerator: GetNCMonomialListGenerator<IntDegree, StringIndeterminateName>,
) = freeSpec {
    "test ncMonomialListGenerator over IntDegree" - {
        "nc free monoid with two generators of even degree" {
            val indeterminateList = IndeterminateList.from(
                IntDegreeGroup,
                listOf(
                    Indeterminate("x", 2),
                    Indeterminate("y", 2),
                )
            )
            val ncMonomialListGenerator = getNCMonomialListGenerator(IntDegreeGroup, indeterminateList)
            (0..10).forAll { n ->
                ncMonomialListGenerator.listNCMonomials(IntDegree(n)) shouldHaveSize when {
                    n.mod(2) == 1 -> 0
                    else -> 2.pow(n / 2)
                }
            }
            ncMonomialListGenerator.listNCMonomials(IntDegree(0)).map { it.toString() } shouldBe
                listOf("1")
            ncMonomialListGenerator.listNCMonomials(IntDegree(2)).map { it.toString() } shouldBe
                listOf("x", "y")
            ncMonomialListGenerator.listNCMonomials(IntDegree(4)).map { it.toString() }.toSet() shouldBe
                setOf("x^2", "xy", "yx", "y^2")
        }
    }
}

class NCMonomialListGeneratorBasicTest : FreeSpec({
    include(ncMonomialListGeneratorTestOverIntDegree(::NCMonomialListGeneratorBasic))
})

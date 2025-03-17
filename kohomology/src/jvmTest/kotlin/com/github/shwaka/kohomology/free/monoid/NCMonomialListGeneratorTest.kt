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
        suspend fun testWithDegree(degree: Int) {
            "nc free monoid with two generators of degree $degree" {
                val indeterminateList = IndeterminateList.from(
                    IntDegreeGroup,
                    listOf(
                        Indeterminate("x", degree),
                        Indeterminate("y", degree),
                    )
                )
                val ncMonomialListGenerator = getNCMonomialListGenerator(IntDegreeGroup, indeterminateList)
                (0..10).forAll { n ->
                    ncMonomialListGenerator.listNCMonomials(IntDegree(n)) shouldHaveSize when {
                        n.mod(degree) != 0 -> 0
                        else -> 2.pow(n / degree)
                    }
                }
                ncMonomialListGenerator.listNCMonomials(IntDegree(0)).map { it.toString() } shouldBe
                    listOf("1")
                ncMonomialListGenerator.listNCMonomials(IntDegree(degree)).map { it.toString() } shouldBe
                    listOf("x", "y")
                ncMonomialListGenerator.listNCMonomials(IntDegree(2 * degree)).map { it.toString() }.toSet() shouldBe
                    setOf("x^2", "xy", "yx", "y^2")
            }
        }

        testWithDegree(2)
        testWithDegree(4)
    }
}

class NCMonomialListGeneratorBasicTest : FreeSpec({
    include(ncMonomialListGeneratorTestOverIntDegree(::NCMonomialListGeneratorBasic))
})

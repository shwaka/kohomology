package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.forAll
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

val boundednessTag = NamedTag("Boundedness")

class BoundednessTest : FreeSpec({
    tags(boundednessTag)

    "test Boundedness.listDegreePairsOfSum for IntDegree" - {
        "p >= 0 and q >= 0" {
            val boundedness = Boundedness(upperBound = null, lowerBound = 0)
            (0..10).forAll { n ->
                Boundedness.listDegreePairsOfSum(
                    IntDegreeGroup,
                    IntDegree(n),
                    boundedness, boundedness,
                ) shouldBe (0..n).map { p ->
                    Pair(IntDegree(p), IntDegree(n - p))
                }
            }
            (-10 until 0).forAll { n ->
                Boundedness.listDegreePairsOfSum(
                    IntDegreeGroup,
                    IntDegree(n),
                    boundedness, boundedness,
                ).shouldBeEmpty()
            }
        }
        "p >= -1 and q >= 2" {
            val boundedness1 = Boundedness(upperBound = null, lowerBound = -1)
            val boundedness2 = Boundedness(upperBound = null, lowerBound = 2)
            (1..10).forAll { n ->
                Boundedness.listDegreePairsOfSum(
                    IntDegreeGroup,
                    IntDegree(n),
                    boundedness1, boundedness2,
                ) shouldBe (-1..(n - 2)).map { p ->
                    Pair(IntDegree(p), IntDegree(n - p))
                }
            }
            (-10 until 1).forAll { n ->
                Boundedness.listDegreePairsOfSum(
                    IntDegreeGroup,
                    IntDegree(n),
                    boundedness1, boundedness2,
                ).shouldBeEmpty()
            }
        }
    }

    "test Boundedness.listDegreePairsOfSum for MultiDegree" - {
        val indeterminateList = listOf(
            DegreeIndeterminate("N", 1)
        )
        val degreeGroup = MultiDegreeGroup(indeterminateList)
        "p >= 0 and q >= 0" {
            val boundedness = Boundedness(upperBound = null, lowerBound = 0)
            Boundedness.listDegreePairsOfSum(
                degreeGroup,
                degreeGroup.fromList(listOf(1, 2)),
                boundedness, boundedness,
            ).toSet() shouldBe listOf(
                Pair(Pair(0, 0), Pair(1, 2)),
                Pair(Pair(1, 0), Pair(0, 2)),
                Pair(Pair(0, 1), Pair(1, 1)),
                Pair(Pair(1, 1), Pair(0, 1)),
                Pair(Pair(0, 2), Pair(1, 0)),
                Pair(Pair(1, 2), Pair(0, 0)),
            ).map { (pair1, pair2) ->
                Pair(
                    degreeGroup.fromList(pair1.toList()),
                    degreeGroup.fromList(pair2.toList()),
                )
            }.toSet()
        }
    }

    "test Boundedness.fromDegreeList" - {
        "fromDegreeList should return Boundedness(0, 0) for empty list" {
            Boundedness.fromDegreeList(IntDegreeGroup, emptyList()) shouldBe Boundedness(0, 0)
        }
        "fromDegreeList for non-empty list" {
            val degreeList = listOf(0, -3, -2, 1, 4).map { IntDegreeGroup.fromInt(it) }
            Boundedness.fromDegreeList(IntDegreeGroup, degreeList) shouldBe
                Boundedness(upperBound = 4, lowerBound = -3)
        }
    }
})

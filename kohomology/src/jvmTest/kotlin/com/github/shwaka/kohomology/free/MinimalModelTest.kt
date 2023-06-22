package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.withTrivialDifferential
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.pow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

val minimalModelTag = NamedTag("MinimalModel")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> minimalModelTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    for (n in listOf(1, 2, 10)) {
        "minimal model of ${2 * n}-sphere" - {
            val cohomologyAsDGA = sphere(matrixSpace, 2 * n).cohomology.withTrivialDifferential()
            val isomorphismUpTo = 5 * n
            val minimalModel = MinimalModel.of(
                targetDGAlgebra = cohomologyAsDGA,
                isomorphismUpTo = isomorphismUpTo,
            )
            "the number of generators should be 2" {
                minimalModel.freeDGAlgebra.generatorList.size shouldBe 2
            }
            "the degrees of generators should be 2n and 4n-1" {
                minimalModel.freeDGAlgebra.generatorList[0].degree shouldBe IntDegree(2 * n)
                minimalModel.freeDGAlgebra.generatorList[1].degree shouldBe IntDegree(4 * n - 1)
            }
            "should be quasi-isomorphism up to degree $isomorphismUpTo" {
                (0..isomorphismUpTo).forAll { degree ->
                    minimalModel.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
                }
            }
        }
    }

    "minimal model of CP^2#CP2" - {
        val polynomialAlgebra = FreeGAlgebra(
            matrixSpace,
            listOf(
                Indeterminate("a", 2),
                Indeterminate("b", 2),
            )
        )
        val idealGenerator = polynomialAlgebra.context.run {
            val (a, b) = polynomialAlgebra.generatorList
            listOf(a * b, a.pow(2) - b.pow(2))
        }
        val ideal = polynomialAlgebra.getIdeal(idealGenerator)
        val cohomologyAsDGA = polynomialAlgebra.getQuotientByIdeal(ideal).withTrivialDifferential()
        val isomorphismUpTo = 10
        val minimalModel = MinimalModel.of(
            targetDGAlgebra = cohomologyAsDGA,
            isomorphismUpTo = isomorphismUpTo,
        )
        "the number of generators should be 4" {
            minimalModel.freeDGAlgebra.generatorList.size shouldBe 4
        }
        "the degrees of generators should be 2, 2, 3, 3" {
            minimalModel.freeDGAlgebra.generatorList[0].degree shouldBe IntDegree(2)
            minimalModel.freeDGAlgebra.generatorList[1].degree shouldBe IntDegree(2)
            minimalModel.freeDGAlgebra.generatorList[2].degree shouldBe IntDegree(3)
            minimalModel.freeDGAlgebra.generatorList[3].degree shouldBe IntDegree(3)
        }
        "should be quasi-isomorphism up to degree $isomorphismUpTo" {
            (0..isomorphismUpTo).forAll { degree ->
                minimalModel.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
            }
        }
    }

    "minimal model of non-formal DGA" - {
        // dgAlgebra = (Λ(x, y, z)/(yz), dz=xy) with |x|, |y|, |z| odd
        // This is non-formal since the triple Massey product
        //   <[x], [x], [y]> = [xz]
        // is non-trivial.
        val indeterminateList = listOf(
            Indeterminate("x", 3),
            Indeterminate("y", 5),
            Indeterminate("z", 7),
        )
        val freeDGAlgebra = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (x, y, z) ->
            mapOf(
                z to x * y,
            )
        }
        val dgIdeal = freeDGAlgebra.context.run {
            val (_, y, z) = freeDGAlgebra.generatorList
            freeDGAlgebra.getDGIdeal(listOf(y * z))
        }
        val dgAlgebra = freeDGAlgebra.getQuotientByIdeal(dgIdeal)
        val isomorphismUpTo = 20
        val minimalModel = MinimalModel.of(dgAlgebra, isomorphismUpTo)
        val minimalModelOfCohomology = MinimalModel.of(
            dgAlgebra.cohomology.withTrivialDifferential(),
            isomorphismUpTo,
        )
        "minimalModel.dgAlgebraMap should be quasi-isomorphism up to $isomorphismUpTo" {
            (0..isomorphismUpTo).forAll { degree ->
                minimalModel.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
            }
        }
        "minimalModelOfCohomology.dgAlgebraMap should be quasi-isomorphism up to $isomorphismUpTo" {
            (0..isomorphismUpTo).forAll { degree ->
                minimalModelOfCohomology.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
            }
        }
        "two minimal models should be different since dgAlgebra is not formal" {
            minimalModel.freeDGAlgebra.generatorList.size shouldNotBe
                minimalModelOfCohomology.freeDGAlgebra.generatorList.size
        }
    }

    "minimal model of S^{2n+1}∨S^{2n+1}" - {
        val n = 3
        val exteriorAlgebra = FreeGAlgebra(
            matrixSpace,
            listOf(
                Indeterminate("x", 2 * n + 1),
                Indeterminate("y", 2 * n + 1),
            )
        )
        val idealGenerator = exteriorAlgebra.context.run {
            val (x, y) = exteriorAlgebra.generatorList
            listOf(x * y)
        }
        val ideal = exteriorAlgebra.getIdeal(idealGenerator)
        val cohomologyAsDGA = exteriorAlgebra.getQuotientByIdeal(ideal).withTrivialDifferential()
        val isomorphismUpTo = 10 * n
        val minimalModel = MinimalModel.of(
            targetDGAlgebra = cohomologyAsDGA,
            isomorphismUpTo = isomorphismUpTo,
        )
        "check the numbers of generators in lower degrees" {
            minimalModel.freeDGAlgebra.generatorList.filter {
                it.degree == IntDegree(2 * n + 1)
            }.size shouldBe 2
            minimalModel.freeDGAlgebra.generatorList.filter {
                it.degree == IntDegree(4 * n + 1)
            }.size shouldBe 1
            minimalModel.freeDGAlgebra.generatorList.filter {
                it.degree == IntDegree(6 * n + 1)
            }.size shouldBe 2
        }
        "should be quasi-isomorphism up to degree $isomorphismUpTo" {
            (0..isomorphismUpTo).forAll { degree ->
                minimalModel.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
            }
        }
        "check dimension of minimalModel.freeDGAlgebra[degree]" {
            // Mathematically, this can be easily computed as follows:
            // Write N = 2n + 1.
            // By considering the Serre spectral sequence for the fibration
            //   Ω(S^N∨S^N) → P(S^N∨S^N) → S^N∨S^N,
            // it is easy to show that H^*(Ω(S^N∨S^N)) is the polynomial algebra with
            //   dim H^{2kn}(Ω(S^N∨S^N)) = 2^k.
            // Hence the number of generators in each degree can be computed
            // as in WedgeGeneratorCalculator.
            (0..isomorphismUpTo).forAll { degree ->
                val expected = when {
                    (degree == 1) -> 0 // This must be excluded from the case degree = 2 * k * n + 1
                    ((degree - 1) % (2 * n) == 0) -> {
                        // when degree = 2 * k * n + 1
                        val k = (degree - 1) / (2 * n)
                        WedgeGeneratorCalculator.get(k)
                    }
                    else -> 0
                }
                minimalModel.freeDGAlgebra.generatorList.filter {
                    it.degree == IntDegree(degree)
                }.size shouldBe expected
            }
        }
    }

    "minimal model of non-positively graded dga" - {
        // minimal model of S^{2n+1}∨S^{2n+1}, but with n < 0
        val n = -3
        val exteriorAlgebra = FreeGAlgebra(
            matrixSpace,
            listOf(
                Indeterminate("x", 2 * n + 1),
                Indeterminate("y", 2 * n + 1),
            )
        )
        val idealGenerator = exteriorAlgebra.context.run {
            val (x, y) = exteriorAlgebra.generatorList
            listOf(x * y)
        }
        val ideal = exteriorAlgebra.getIdeal(idealGenerator)
        val cohomologyAsDGA = exteriorAlgebra.getQuotientByIdeal(ideal).withTrivialDifferential()
        val isomorphismUpTo = 10 * n
        val minimalModel = MinimalModel.of(
            targetDGAlgebra = cohomologyAsDGA,
            isomorphismUpTo = isomorphismUpTo,
        )
        "check the numbers of generators in lower degrees" {
            minimalModel.freeDGAlgebra.generatorList.filter {
                it.degree == IntDegree(2 * n + 1)
            }.size shouldBe 2
            minimalModel.freeDGAlgebra.generatorList.filter {
                it.degree == IntDegree(4 * n + 1)
            }.size shouldBe 1
            minimalModel.freeDGAlgebra.generatorList.filter {
                it.degree == IntDegree(6 * n + 1)
            }.size shouldBe 2
        }
        "should be quasi-isomorphism up to degree $isomorphismUpTo" {
            (isomorphismUpTo..0).forAll { degree ->
                minimalModel.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
            }
        }
    }

    "test progress reporting" - {
        val n = 3
        "progress reporting for minimal model of S^${2 * n}" - {
            val cohomologyAsDGA = sphere(matrixSpace, 2 * n).cohomology.withTrivialDifferential()
            val isomorphismUpTo = 5 * n
            val progressList = mutableListOf<MinimalModelProgress>()
            MinimalModel.of(
                targetDGAlgebra = cohomologyAsDGA,
                isomorphismUpTo = isomorphismUpTo,
            ) { progress ->
                progressList.add(progress)
            }

            "progressList.size should be $isomorphismUpTo" {
                progressList shouldHaveSize isomorphismUpTo
            }

            "check elements of progressList" {
                progressList shouldBe (1..isomorphismUpTo).map { k ->
                    MinimalModelProgress(
                        currentIsomorphismUpTo = k,
                        targetIsomorphismUpTo = isomorphismUpTo,
                        currentNumberOfGenerators = when {
                            (k < 2 * n) -> 0
                            (k < 4 * n - 1) -> 1
                            else -> 2
                        }
                    )
                }
            }
        }
    }
}

private object WedgeGeneratorCalculator {
    // Computes the number sequence {a_k} defined by
    //   a_k = (1/k)Σ_{d|k} μ(k/d) 2^d
    // where μ is the Möbius function (https://en.wikipedia.org/wiki/M%C3%B6bius_function).
    // This is equivalent to
    //   Σ_{d|k} d a_d = 2^k
    private val values: MutableMap<Int, Int> = mutableMapOf()

    fun get(k: Int): Int {
        this.values[k]?.let { return it }
        var temp: Int = 2.pow(k)
        for (d in 1 until k) {
            if (k % d == 0) {
                temp -= d * this.get(d)
            }
        }
        val result = temp / k
        this.values[k] = result
        return result
    }
}

class MinimalModelTest : FreeSpec({
    tags(minimalModelTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(minimalModelTest(matrixSpace))
})

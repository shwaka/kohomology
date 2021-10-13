package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.checkAll

class GVectorCollection<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    private val gVectorSpace: GVectorSpace<D, B, S, V>,
    gVectorList: List<GVector<D, B, S, V>>,
) {
    val list: List<GVector<D, B, S, V>> = gVectorList + gVectorSpace.context.run {
        listOf(gVectorSpace.getZero(0)) +
            gVectorList.map { it.degree }.distinct().map { gVectorSpace.getZero(it) }
    }.distinct()

    val map: Map<D, List<GVector<D, B, S, V>>> = list.groupBy { it.degree }
    val degreeWiseArb: Map<D, Arb<GVector<D, B, S, V>>> =
        map.mapValues { (_, elementListForDegree) -> Arb.element(elementListForDegree) }
    val arb: Arb<GVector<D, B, S, V>> = Arb.element(list)
}

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGAlgebraAxioms(
    gAlgebra: GAlgebra<D, B, S, V, M>,
    gVectorList: List<GVector<D, B, S, V>>,
    commutative: Boolean = true,
) {
    val gVectorCollection = GVectorCollection(gAlgebra, gVectorList)
    val degreeWiseGVectorArb: Map<D, Arb<GVector<D, B, S, V>>> = gVectorCollection.degreeWiseArb
    val gVectorArb: Arb<GVector<D, B, S, V>> = gVectorCollection.arb
    "check GAlgebra axioms" - {
        gAlgebra.context.run {
            "addition should be associative" {
                // TODO: This is automatically satisfied?
                for ((_, arb) in degreeWiseGVectorArb) {
                    checkAll(arb, arb, arb) { a, b, c ->
                        ((a + b) + c) shouldBe (a + (b + c))
                    }
                }
            }
            "multiplication should be associative" {
                checkAll(gVectorArb, gVectorArb, gVectorArb) { a, b, c ->
                    ((a * b) * c) shouldBe (a * (b * c))
                }
            }
            "unit should be the unit of multiplication" {
                checkAll(gVectorArb) { a ->
                    (unit * a) shouldBe a
                    (a * unit) shouldBe a
                }
            }
            "multiplication should be distributive w.r.t. addition" {
                for ((_, arb) in degreeWiseGVectorArb) {
                    checkAll(arb, arb, gVectorArb) { a, b, c ->
                        ((a + b) * c) shouldBe (a * c + b * c)
                        (c * (a + b)) shouldBe (c * a + c * b)
                    }
                }
            }
            "multiplication with zero should return zero" {
                for (degree in degreeWiseGVectorArb.keys) {
                    val zeroAtTheDegree = gAlgebra.getZero(degree)
                    checkAll(gVectorArb) { a ->
                        (a * zeroAtTheDegree).isZero().shouldBeTrue()
                        (zeroAtTheDegree * a).isZero().shouldBeTrue()
                    }
                }
            }
            if (commutative) {
                "multiplication should be commutative" {
                    checkAll(gVectorArb, gVectorArb) { a, b ->
                        val sign: Int = a.degree.koszulSign(b.degree)
                        (a * b) shouldBe (sign * b * a)
                    }
                }
            }
        }
    }
}

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGAlgebraAxioms(
    gAlgebra: GAlgebra<D, B, S, V, M>,
    degreeRange: IntRange,
    commutative: Boolean = true,
) {
    val gVectorList = degreeRange.map { intDegree ->
        gAlgebra.getBasis(intDegree)
    }.flatten()
    checkGAlgebraAxioms(gAlgebra, gVectorList, commutative)
}

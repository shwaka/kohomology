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

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGAlgebraAxioms(
    gAlgebra: GAlgebra<D, B, S, V, M>,
    elementList: List<GVector<D, B, S, V>>,
) {
    val newElementList: List<GVector<D, B, S, V>> = elementList + gAlgebra.context.run {
        listOf(unit, gAlgebra.getZero(0)) +
            elementList.map { it.degree }.distinct().map { gAlgebra.getZero(it) }
    }.distinct()
    val elementMap: Map<D, List<GVector<D, B, S, V>>> = newElementList.groupBy { it.degree }
    val degreeWiseElementArb: Map<D, Arb<GVector<D, B, S, V>>> =
        elementMap.mapValues { (_, elementListForDegree) -> Arb.element(elementListForDegree) }
    val elementArb: Arb<GVector<D, B, S, V>> = Arb.element(elementList)
    "check axioms" - {
        gAlgebra.context.run {
            "addition should be associative" {
                for ((_, arb) in degreeWiseElementArb) {
                    checkAll(arb, arb, arb) { a, b, c ->
                        ((a + b) + c) shouldBe (a + (b + c))
                    }
                }
            }
            "multiplication should be associative" {
                checkAll(elementArb, elementArb, elementArb) { a, b, c ->
                    ((a * b) * c) shouldBe (a * (b * c))
                }
            }
            "unit should be the unit of multiplication" {
                checkAll(elementArb) { a ->
                    (unit * a) shouldBe a
                    (a * unit) shouldBe a
                }
            }
            "multiplication should be distributive w.r.t. addition" {
                for ((_, arb) in degreeWiseElementArb) {
                    checkAll(arb, arb, elementArb) { a, b, c ->
                        ((a + b) * c) shouldBe (a * c + b * c)
                        (c * (a + b)) shouldBe (c * a + c * b)
                    }
                }
            }
            "multiplication with zero should return zero" {
                for (degree in degreeWiseElementArb.keys) {
                    val zeroAtTheDegree = gAlgebra.getZero(degree)
                    checkAll(elementArb) { a ->
                        (a * zeroAtTheDegree).isZero().shouldBeTrue()
                        (zeroAtTheDegree * a).isZero().shouldBeTrue()
                    }
                }
            }
        }
    }
}

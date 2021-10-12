package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.checkAll

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGAlgebraAxiomsWithArb(
    gAlgebra: GAlgebra<D, B, S, V, M>,
    elements: Map<D, Arb<GVector<D, B, S, V>>>,
) {
    "check axioms" - {
        gAlgebra.context.run {
            "addition should be associative" {
                for ((_, arb) in elements) {
                    checkAll(arb, arb, arb) { a, b, c ->
                        ((a + b) + c) shouldBe (a + (b + c))
                    }
                }
            }
        }
    }
}

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGAlgebraAxioms(
    gAlgebra: GAlgebra<D, B, S, V, M>,
    elements: Map<D, List<GVector<D, B, S, V>>>,
) {
    val elementsArb = elements.mapValues { (_, elementList) -> Arb.element(elementList) }
    checkGAlgebraAxiomsWithArb(gAlgebra, elementsArb)
}

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGAlgebraAxioms(
    gAlgebra: GAlgebra<D, B, S, V, M>,
    elements: List<GVector<D, B, S, V>>,
) {
    val elementsMap = elements.groupBy { it.degree }
    checkGAlgebraAxioms(gAlgebra, elementsMap)
}

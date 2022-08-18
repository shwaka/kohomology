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
import io.kotest.property.checkAll

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkDGAlgebraAxioms(
    dgAlgebra: DGAlgebra<D, B, S, V, M>,
    gVectorList: List<GVector<D, B, S, V>>,
    commutative: Boolean = true,
) {
    "check DGAlgebra axioms" - {
        checkGAlgebraAxioms(dgAlgebra, gVectorList, commutative)
        val gVectorCollection = GVectorCollection(dgAlgebra, gVectorList)
        val gVectorArb: Arb<GVector<D, B, S, V>> = gVectorCollection.arb
        dgAlgebra.context.run {
            "d should satisfy Leibniz rule" {
                checkAll(gVectorArb, gVectorArb) { a, b ->
                    d(a * b) shouldBe (d(a) * b + a.degree.sign * a * d(b))
                }
            }
            "d(d(a)) should be zero for any a" {
                checkAll(gVectorArb) { a ->
                    d(d(a)).isZero().shouldBeTrue()
                }
            }
            "d(unit) should be zero" {
                // mathematically this follows from Leibniz rule
                d(dgAlgebra.unit).isZero().shouldBeTrue()
            }
        }
    }
}

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkDGAlgebraAxioms(
    dgAlgebra: DGAlgebra<D, B, S, V, M>,
    degreeRange: IntRange,
    commutative: Boolean = true,
) {
    val gVectorList = degreeRange.map { intDegree ->
        dgAlgebra.getBasis(intDegree)
    }.flatten()
    checkDGAlgebraAxioms(dgAlgebra, gVectorList, commutative)
}

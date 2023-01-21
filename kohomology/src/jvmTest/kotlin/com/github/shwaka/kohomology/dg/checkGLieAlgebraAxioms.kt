package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BasisName
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGLieAlgebraAxioms(
    gLieAlgebra: GLieAlgebra<D, B, S, V, M>,
    gVectorList: List<GVector<D, B, S, V>>,
) {
    val gVectorCollection = GVectorCollection(gLieAlgebra, gVectorList)
    // val degreeWiseGVectorArb: Map<D, Arb<GVector<D, B, S, V>>> = gVectorCollection.degreeWiseArb
    val gVectorArb: Arb<GVector<D, B, S, V>> = gVectorCollection.arb
    "check GLieAlgebra axioms" - {
        gLieAlgebra.context.run {
            "bracket should be anti-commutative" {
                checkAll(gVectorArb, gVectorArb) { a, b ->
                    val sign: Sign = a.degree.koszulSign(b.degree)
                    (a * b) shouldBe (-sign * b * a)
                }
            }
            "Jacobi identity should be satisfied" {
                checkAll(gVectorArb, gVectorArb, gVectorArb) { a, b, c ->
                    val sign: Sign = a.degree.koszulSign(b.degree)
                    (a * (b * c)) shouldBe ((a * b) * c + sign * b * (a * c))
                }
            }
        }
    }
}

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkGLieAlgebraAxioms(
    gLieAlgebra: GLieAlgebra<D, B, S, V, M>,
    degreeRange: IntRange
) {
    val gVectorList = degreeRange.map { intDegree ->
        gLieAlgebra.getBasis(intDegree)
    }.flatten()
    checkGLieAlgebraAxioms(gLieAlgebra, gVectorList)
}

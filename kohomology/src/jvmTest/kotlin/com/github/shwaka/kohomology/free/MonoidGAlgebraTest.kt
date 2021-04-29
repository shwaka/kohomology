package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.IntDegree
import com.github.shwaka.kohomology.dg.IntDegreeMonoid
import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.Sign
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val monoidGAlgebraTag = NamedTag("MonoidGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> complexProjectiveSpaceTest(
    matrixSpace: MatrixSpace<S, V, M>,
    n: Int
) = freeSpec {
    if (n < 0)
        throw IllegalArgumentException("Invalid test parameter: n must be non-negative")
    "complex projective space of complex dimension $n" {
        val elements = (0..n).map { i -> SimpleMonoidElement("c$i", 2 * i) }
        val multiplicationTable: List<List<MaybeZero<Pair<SimpleMonoidElement<String, IntDegree>, Sign>>>> =
            (0..n).map { i ->
                (0..n).map { j ->
                    if (i + j <= n) {
                        NonZero(Pair(elements[i + j], 1))
                    } else {
                        Zero()
                    }
                }
            }
        val monoid = MonoidFromList(elements, IntDegreeMonoid, multiplicationTable)
        val gAlgebra = MonoidGAlgebra(matrixSpace, IntDegreeMonoid, monoid, "M")
        for (degree in 0..(3 * n)) {
            val expectedDim = if ((degree <= 2 * n) && (degree % 2 == 0)) 1 else 0
            gAlgebra[degree].dim shouldBe expectedDim
        }
        val basis: List<GVector<SimpleMonoidElement<String, IntDegree>, IntDegree, S, V>> =
            (0..n).map { i -> gAlgebra.getBasis(2 * i)[0] }
        gAlgebra.context.run {
            for (i in 0..n) {
                for (j in 0..n) {
                    if (i + j <= n) {
                        (basis[i] * basis[j]) shouldBe basis[i + j]
                    } else {
                        (basis[i] * basis[j]).isZero().shouldBeTrue()
                    }
                }
            }
        }
    }
}

class MonoidGAlgebraTest : FreeSpec({
    tags(monoidGAlgebraTag, bigRationalTag)

    include(complexProjectiveSpaceTest(DenseMatrixSpaceOverBigRational, 5))
})

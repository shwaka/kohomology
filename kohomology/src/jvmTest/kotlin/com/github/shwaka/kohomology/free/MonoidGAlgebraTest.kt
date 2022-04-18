package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.checkGAlgebraAxioms
import com.github.shwaka.kohomology.dg.degree.EvenSuperDegree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.OddSuperDegree
import com.github.shwaka.kohomology.dg.degree.SuperDegree
import com.github.shwaka.kohomology.dg.degree.SuperDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.MonoidFromList
import com.github.shwaka.kohomology.free.monoid.Signed
import com.github.shwaka.kohomology.free.monoid.SignedOrZero
import com.github.shwaka.kohomology.free.monoid.SimpleMonoidElement
import com.github.shwaka.kohomology.free.monoid.Zero
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.Sign
import io.kotest.assertions.throwables.shouldNotThrowAny
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
    "complex projective space of complex dimension $n" - {
        val elements = (0..n).map { i -> SimpleMonoidElement("c$i", 2 * i) }
        val multiplicationTable: List<List<SignedOrZero<SimpleMonoidElement<String, IntDegree>>>> =
            (0..n).map { i ->
                (0..n).map { j ->
                    if (i + j <= n) {
                        Signed(elements[i + j], Sign.PLUS)
                    } else {
                        Zero
                    }
                }
            }
        val monoid = MonoidFromList(elements, IntDegreeGroup, multiplicationTable)
        val gAlgebra = MonoidGAlgebra(matrixSpace, IntDegreeGroup, monoid, "M")

        "check dimension" {
            (0..(3 * n)).forAll { degree ->
                val expectedDim = if ((degree <= 2 * n) && (degree % 2 == 0)) 1 else 0
                gAlgebra[degree].dim shouldBe expectedDim
            }
        }
        val basis: List<GVector<IntDegree, SimpleMonoidElement<String, IntDegree>, S, V>> =
            (0..n).map { i -> gAlgebra.getBasis(2 * i)[0] }

        checkGAlgebraAxioms(gAlgebra, basis)

        gAlgebra.context.run {
            "check multiplication" {
                (0..n).forAll { i ->
                    (0..n).forAll { j ->
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
}

class MonoidGAlgebraTest : FreeSpec({
    tags(monoidGAlgebraTag, bigRationalTag)

    include(complexProjectiveSpaceTest(DenseMatrixSpaceOverRational, 5))

    "exterior algebra as a super algebra" - {
        val matrixSpace = DenseMatrixSpaceOverRational
        // Without explicit type parameter, this will be
        //   List<SimpleMonoidElement<String, out SuperDegree>>
        val elements = listOf<SimpleMonoidElement<String, SuperDegree>>(
            SimpleMonoidElement("1", EvenSuperDegree),
            SimpleMonoidElement("x", OddSuperDegree),
            SimpleMonoidElement("y", OddSuperDegree),
            SimpleMonoidElement("xy", EvenSuperDegree),
        )
        val multiplicationTable: List<List<SignedOrZero<SimpleMonoidElement<String, SuperDegree>>>> = run {
            val (e, x, y, xy) = elements.map { Signed(it, Sign.PLUS) }
            val minusXY = Signed(elements[3], Sign.MINUS)
            listOf(
                listOf(e, x, y, xy),
                listOf(x, Zero, xy, Zero),
                listOf(y, minusXY, Zero, Zero),
                listOf(xy, Zero, Zero, Zero),
            )
        }
        val monoid = MonoidFromList(elements, SuperDegreeGroup, multiplicationTable)
        val gAlgebra = MonoidGAlgebra(matrixSpace, SuperDegreeGroup, monoid, "A")
        val (e, xy) = gAlgebra.getBasis(EvenSuperDegree)
        val (x, y) = gAlgebra.getBasis(OddSuperDegree)
        "even part should have dimension 2" {
            gAlgebra[EvenSuperDegree].dim shouldBe 2
        }
        "odd part should have dimension 2" {
            gAlgebra[OddSuperDegree].dim shouldBe 2
        }
        gAlgebra.context.run {
            "x * y should be xy" {
                (x * y).degree shouldBe EvenSuperDegree
                (x * y) shouldBe xy
            }
            "y * x should be -xy" {
                (y * x).degree shouldBe EvenSuperDegree
                (y * x) shouldBe (-xy)
            }
            "(x + y).pow(2) should be zero" {
                (x + y).pow(2).isZero().shouldBeTrue()
            }
            "(e + xy) should not throw any" {
                shouldNotThrowAny { e + xy }
            }
            "(e + xy).pow(2) should be e + 2xy" {
                (e + xy).pow(2) shouldBe (e + 2 * xy)
            }
        }
    }
})

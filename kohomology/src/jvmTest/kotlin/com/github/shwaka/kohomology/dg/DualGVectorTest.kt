package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverRational
import com.github.shwaka.kohomology.vectsp.DualVectorSpace
import com.github.shwaka.kohomology.vectsp.dualVectorTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

fun <S : Scalar, V : NumVector<S>> dualGVectorTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "dual graded vector test" - {
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            (0 until degree).map { "v$it" }
        }
        val dualGVectorSpace = DualGVectorSpace(gVectorSpace)
        gVectorSpace.context.run {
            dualGVectorSpace.context.run {
                "check dualGVectorSpace.originalGVectorSpace" {
                    dualGVectorSpace.originalGVectorSpace shouldBeSameInstanceAs gVectorSpace
                }
                "dualGVectorSpace[degree].dim should be the same as gVectorSpace[-degree]" {
                    (-10..10).forAll { degree ->
                        dualGVectorSpace[degree].dim shouldBe gVectorSpace[-degree].dim
                    }
                }
                "dualGVectorSpace[degree].originalVectorSpace should be the same instance as gVectorSpace[-degree]" {
                    (-10..10).forAll { degree ->
                        val dualVectorSpace = dualGVectorSpace[degree]
                        if (dualVectorSpace is DualVectorSpace) {
                            dualVectorSpace.originalVectorSpace shouldBeSameInstanceAs gVectorSpace[-degree]
                        } else {
                            throw Exception("This can't happen")
                        }
                    }
                }
                "test evaluation at degree 2" {
                    val (v0, v1) = gVectorSpace.getBasis(2)
                    val (f0, f1) = dualGVectorSpace.getBasis(-2)
                    f0(v0) shouldBe one
                    f0(v1) shouldBe zero
                    f1(v1) shouldBe one
                    f1(v0) shouldBe zero
                    (f0 + f1)(v0 + v1) shouldBe two
                    (f0 + f1)(v0 - v1) shouldBe zero
                }
                "test evaluation at incompatible degrees" {
                    val (v0, v1) = gVectorSpace.getBasis(2)
                    val (g0, g1, g2) = dualGVectorSpace.getBasis(-3)
                    g0(v0) shouldBe zero
                    g1(v1) shouldBe zero
                    g2(v0 + v1) shouldBe zero
                }
            }
        }
    }
}

class DualGVectorTest : FreeSpec({
    tags(dualVectorTag, gVectorTag)
    val numVectorSpace = DenseNumVectorSpaceOverRational

    include(dualGVectorTest(numVectorSpace))
})

package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val dualVectorTag = NamedTag("DualVector")

fun <S : Scalar, V : NumVector<S>> dualVectorTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "dual vector test" - {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
        val (a, b, c) = vectorSpace.getBasis()
        val dualVectorSpace = DualVectorSpace(vectorSpace)
        val (f, g, h) = dualVectorSpace.getBasis()
        dualVectorSpace.context.run {
            "dualVectorSpace.dim should be the same as the original one" {
                dualVectorSpace.dim shouldBe vectorSpace.dim
            }
            "test toString for dual basis" {
                f.toString() shouldBe "$a*"
            }
            "test toString for dual basis with PrintType.TEX" {
                val p = Printer(PrintType.TEX)
                p(g) shouldBe "$b^*"
            }
        }
    }
}

class DualVectorTest : FreeSpec({
    tags(vectorTag, dualVectorTag)
    val numVectorSpace = DenseNumVectorSpaceOverRational

    include(dualVectorTest(numVectorSpace))
})

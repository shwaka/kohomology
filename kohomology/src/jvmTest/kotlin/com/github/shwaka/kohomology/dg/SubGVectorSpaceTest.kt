package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val subGVectorSpaceTag = NamedTag("SubGVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subGVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "sub graded vector space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val totalVectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val subVectorSpace = run {
            val (u, v, w) = totalVectorSpace.getBasis()
            totalVectorSpace.context.run {
                val generator = listOf(u + v, v + w, u - w)
                SubVectorSpace(matrixSpace, totalVectorSpace, generator)
            }
        }
        val totalGVectorSpace = GVectorSpace(
            numVectorSpace,
            IntDegreeGroup,
            "V",
        ) { _ -> totalVectorSpace }
        val subGVectorSpace = SubGVectorSpace(
            matrixSpace,
            totalGVectorSpace,
            "V",
        ) { _ -> subVectorSpace }

        "check dimension" {
            (-5..5).forAll { degree ->
                subGVectorSpace[degree].dim shouldBe 2
            }
        }

        "check subGVectorSpace.totalGVectorSpace" {
            subGVectorSpace.totalGVectorSpace shouldBe totalGVectorSpace
        }

        "test totalGVectorSpace.asSubGVectorSpace" {
            val wholeSubGVectorSpace = totalGVectorSpace.asSubGVectorSpace(matrixSpace)
            wholeSubGVectorSpace.totalGVectorSpace shouldBe totalGVectorSpace
            (-5..5).forAll { degree ->
                wholeSubGVectorSpace[degree].dim shouldBe 3
            }
        }

        "test inclusion" {
            (-5..5).forAll { degree ->
                val (u, v, w) = totalGVectorSpace.getBasis(degree)
                val (x, y) = subGVectorSpace.getBasis(degree)
                val incl = subGVectorSpace.inclusion
                totalGVectorSpace.context.run {
                    incl(x) shouldBe u + v
                    incl(y) shouldBe v + w
                }
            }
        }
    }

    "test SubGVectorSpace.fromList" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val totalVectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val totalGVectorSpace = GVectorSpace(
            numVectorSpace,
            IntDegreeGroup,
            "V",
        ) { _ -> totalVectorSpace }

        "empty list of generators" {
            val subGVectorSpace = SubGVectorSpace.fromList(
                matrixSpace,
                totalGVectorSpace,
                "W",
                emptyList()
            )
            (-5..5).forAll { degree ->
                subGVectorSpace[degree].dim shouldBe 0
            }
            subGVectorSpace.boundedness shouldBe Boundedness(upperBound = 0, lowerBound = 0)
        }

        "non-empty list of generators" {
            val subGVectorSpace = SubGVectorSpace.fromList(
                matrixSpace,
                totalGVectorSpace,
                "W",
                listOf(
                    totalGVectorSpace.getBasis(-1)[0],
                    totalGVectorSpace.getBasis(0)[2],
                    totalGVectorSpace.getBasis(3)[0],
                    totalGVectorSpace.getBasis(3)[1],
                )
            )
            (-5..5).forAll { degree ->
                val expected = when (degree) {
                    -1, 0 -> 1
                    3 -> 2
                    else -> 0
                }
                subGVectorSpace[degree].dim shouldBe expected
            }
            subGVectorSpace.boundedness shouldBe Boundedness(upperBound = 3, lowerBound = -1)

        }
    }

    "getInternalPrintConfig should be inherited from subGVectorSpace.totalGVectorSpace" {
        val numVectorSpace = matrixSpace.numVectorSpace
        val totalVectorSpace = VectorSpace(numVectorSpace, listOf("y", "x", "z")) {
            InternalPrintConfig(
                basisComparator = compareBy { it.name },
                basisToString = { basisName -> basisName.name.replaceFirstChar { it.uppercase() } },
            )
        }
        val (y, x, _) = totalVectorSpace.getBasis()
        val subVectorSpace = totalVectorSpace.context.run {
            val generator = listOf(y + x, x)
            SubVectorSpace(matrixSpace, totalVectorSpace, generator)
        }
        val totalGVectorSpace = GVectorSpace(
            numVectorSpace,
            IntDegreeGroup,
            "V",
        ) { _ -> totalVectorSpace }
        val subGVectorSpace = SubGVectorSpace(
            matrixSpace,
            totalGVectorSpace,
            "V",
        ) { _ -> subVectorSpace }
        val (v, w) = subGVectorSpace.getBasis(0)

        v.toString() shouldBe "(X + Y)"
        w.toString() shouldBe "(X)"
    }
}

class SubGVectorSpaceTest : FreeSpec({
    tags(subGVectorSpaceTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(subGVectorSpaceTest(matrixSpace))
})

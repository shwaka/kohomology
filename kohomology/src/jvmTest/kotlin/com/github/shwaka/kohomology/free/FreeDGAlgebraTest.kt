package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val freeDGAlgebraTag = NamedTag("FreeDGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invalidModelTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    "FreeDGAlgebra should throw IllegalArgumentException when d^2 != 0" {
        val indeterminateList = listOf(
            Indeterminate("x", 3),
            Indeterminate("y", 2),
            Indeterminate("z", 1),
        )
        shouldThrow<IllegalArgumentException> {
            FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y, _) ->
                listOf(zeroGVector, x, y)
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pointModelTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    "FreeDGAlgebra should work well even when the list of generator is empty" {
        val indeterminateList = listOf<Indeterminate<StringIndeterminateName>>()
        val freeDGAlgebra = shouldNotThrowAny {
            FreeDGAlgebra(matrixSpace, indeterminateList) { emptyList() }
        }
        val algebraMap = freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, emptyList())
        freeDGAlgebra.context.run {
            d(unit).isZero().shouldBeTrue()
            algebraMap(unit) shouldBe unit
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> evenSphereModelTest(matrixSpace: MatrixSpace<S, V, M>, sphereDim: Int) = stringSpec {
    if (sphereDim <= 0)
        throw IllegalArgumentException("The dimension of a sphere must be positive")
    if (sphereDim % 2 == 1)
        throw IllegalArgumentException("The dimension of a sphere must be even in this test")
    val freeDGAlgebra = sphere(matrixSpace, sphereDim)
    val (x, y) = freeDGAlgebra.gAlgebra.generatorList
    freeDGAlgebra.context.run {
        "[sphere of dim $sphereDim] check differential" {
            d(unit).isZero().shouldBeTrue()
            d(x).isZero().shouldBeTrue()
            d(y) shouldBe x.pow(2)
        }
        "[sphere of dim $sphereDim] check cohomology" {
            for (n in 0 until (sphereDim * 3)) {
                val expectedDim = when (n) {
                    0, sphereDim -> 1
                    else -> 0
                }
                freeDGAlgebra.cohomology[n].dim shouldBe expectedDim
            }
        }
        "[sphere of dim $sphereDim] check cocycle" {
            shouldNotThrowAny { cohomologyClassOf(x) }
            shouldNotThrowAny { cohomologyClassOf(x.pow(2)) }
            shouldThrow<IllegalArgumentException> { cohomologyClassOf(y) }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> modelTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    "model in FHT Section 12 (a) Example 7 (p.147)" {
        val indeterminateList = listOf(
            Indeterminate("a", 2),
            Indeterminate("b", 2),
            Indeterminate("x", 3),
            Indeterminate("y", 3),
            Indeterminate("z", 3),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, _, _, _) ->
            listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))
        }
        val (a, b, x, y, z) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            d(x * y) shouldBe (a.pow(2) * y - a * b * x)
            d(x * y * z) shouldBe (a.pow(2) * y * z - a * b * x * z + b.pow(2) * x * y)
        }
        for (n in 0 until 12) {
            val expectedDim = when (n) {
                0, 7 -> 1
                2, 5 -> 2
                else -> 0
            }
            freeDGAlgebra.cohomology[n].dim shouldBe expectedDim
        }
        freeDGAlgebra.context.run {
            val bClass = b.cohomologyClass()
            val someClass = (a * y - b * x).cohomologyClass()
            val topClass = (a * b * y - b.pow(2) * x).cohomologyClass()
            freeDGAlgebra.cohomology.context.run {
                (bClass * someClass) shouldBe topClass
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> errorTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    "generator must be sorted along a Sullivan filtration" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        shouldThrow<IllegalArgumentException> {
            FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y, _, _, _) ->
                listOf(y, x.pow(2))
            }
        }
    }
}

class FreeDGAlgebraTest : StringSpec({
    tags(freeDGAlgebraTag, bigRationalTag)

    include(invalidModelTest(DenseMatrixSpaceOverBigRational))
    include(pointModelTest(DenseMatrixSpaceOverBigRational))
    include(evenSphereModelTest(DenseMatrixSpaceOverBigRational, 2))
    include(modelTest(DenseMatrixSpaceOverBigRational))
    include(errorTest(DenseMatrixSpaceOverBigRational))
})

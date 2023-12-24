package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val linearMapTag = NamedTag("LinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> linearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "linear map test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
        val vectorSpace3 = VectorSpace(numVectorSpace, listOf("s", "t"))
        matrixSpace.context.run {
            "check value" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(two, zero),
                        listOf(one, one)
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val v = vectorSpace1.fromCoeffList(listOf(one, -one))
                val w = vectorSpace2.fromCoeffList(listOf(two, zero))
                f(v) shouldBe w
            }
            "getZero should return the zero map" {
                val f = LinearMap.getZero(vectorSpace1, vectorSpace2, matrixSpace)
                val v = vectorSpace1.fromCoeffList(listOf(one, two))
                f(v) shouldBe vectorSpace2.zeroVector
            }
            "getZero should return the zero map for vector spaces of different dim" {
                val threeDimVectorSpace = VectorSpace(numVectorSpace, listOf("x1", "x2", "x3"))
                val f = LinearMap.getZero(vectorSpace1, threeDimVectorSpace, matrixSpace)
                val v = vectorSpace1.fromCoeffList(listOf(one, two))
                f(v) shouldBe threeDimVectorSpace.zeroVector
            }
            "getIdentity should return the identity map" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
                val v = vectorSpace1.fromCoeffList(listOf(one, two))
                f(v) shouldBe v
            }
            "fromVectors test" {
                val v = vectorSpace2.fromCoeffList(listOf(one, -one))
                val w = vectorSpace2.fromCoeffList(listOf(two, zero))
                val matrix = matrixSpace.fromNumVectorList(listOf(v, w).map { it.toNumVector() })
                val f = LinearMap.fromVectors(vectorSpace1, vectorSpace2, matrixSpace, listOf(v, w))
                val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                f shouldBe expected
            }
            "kernel test" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, one),
                        listOf(zero, zero),
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val (a, b) = vectorSpace1.getBasis()
                vectorSpace1.context.run {
                    val kernelSubVectorSpace = f.kernel()
                    kernelSubVectorSpace.totalVectorSpace shouldBe vectorSpace1
                    val incl = kernelSubVectorSpace.inclusion
                    val kernelBasis = kernelSubVectorSpace.getBasis()
                    kernelBasis.map { incl(it) } shouldBe listOf(-a + b)
                    kernelSubVectorSpace.dim shouldBe 1
                }
            }
            "image test" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, one),
                        listOf(zero, zero),
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val (x, _) = vectorSpace2.getBasis()
                vectorSpace1.context.run {
                    val imageSubVectorSpace = f.image()
                    imageSubVectorSpace.totalVectorSpace shouldBe vectorSpace2
                    val incl = imageSubVectorSpace.inclusion
                    val imageBasis = imageSubVectorSpace.getBasis()
                    imageBasis.map { incl(it) } shouldBe listOf(x)
                    imageSubVectorSpace.dim shouldBe 1
                }
            }
            "imageContains test" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(two, two),
                        listOf(one, one)
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val v = vectorSpace2.fromCoeffList(listOf(-four, -two))
                val w = vectorSpace2.fromCoeffList(listOf(one, -one))
                f.imageContains(vectorSpace2.zeroVector).shouldBeTrue()
                f.imageContains(v).shouldBeTrue()
                f.imageContains(w).shouldBeFalse()
            }
            "cokernel test" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, two),
                        listOf(-one, -two),
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val (a, b) = vectorSpace1.getBasis()
                val (x, y) = vectorSpace2.getBasis()
                val cokernelQuotVectorSpace = f.cokernel()
                cokernelQuotVectorSpace.totalVectorSpace shouldBe vectorSpace2
                cokernelQuotVectorSpace.dim shouldBe 1
                val proj = cokernelQuotVectorSpace.projection
                proj(f(a)).isZero().shouldBeTrue()
                proj(f(b)).isZero().shouldBeTrue()
                vectorSpace2.context.run {
                    proj(x - y).isZero().shouldBeTrue()
                    proj(x).isZero().shouldBeFalse()
                }
            }
            "(zero map).isZero() should be true" {
                val f = LinearMap.getZero(vectorSpace1, vectorSpace2, matrixSpace)
                f.isZero().shouldBeTrue()
                f.isNotZero().shouldBeFalse()
            }
            "(non-zero map).isZero() should be false" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(two, zero),
                        listOf(one, one)
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                f.isZero().shouldBeFalse()
                f.isNotZero().shouldBeTrue()
            }
            "(identity map).isIdentity() should be true" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
                f.isIdentity().shouldBeTrue()
                f.isNotIdentity().shouldBeFalse()
            }
            "(non-identity map).isNotIdentity() should be false" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, one),
                        listOf(zero, one),
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                f.isIdentity().shouldBeFalse()
                f.isNotIdentity().shouldBeTrue()
            }
            "(identity map).isIsomorphism() should be true" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
                f.isIsomorphism().shouldBeTrue()
            }
            "(identity map).isSurjective() should be true" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
                f.isSurjective().shouldBeTrue()
            }
            "(identity map).isInjective() should be true" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
                f.isInjective().shouldBeTrue()
            }
            "f.isIsomorphism() should be false if different dimension" {
                val vectorSpaceOfDim3 = VectorSpace(numVectorSpace, listOf("p", "q", "r"))
                val (p, q, _) = vectorSpaceOfDim3.getBasis()
                val f = LinearMap.fromVectors(
                    source = vectorSpace1,
                    target = vectorSpaceOfDim3,
                    matrixSpace = matrixSpace,
                    vectors = listOf(p, q),
                )
                (f.source.dim != f.target.dim).shouldBeTrue()
                f.isIsomorphism().shouldBeFalse()
            }
            "f.isSurjective() should be false if the target has larger dim" {
                val vectorSpaceOfDim3 = VectorSpace(numVectorSpace, listOf("p", "q", "r"))
                val (p, q, _) = vectorSpaceOfDim3.getBasis()
                val f = LinearMap.fromVectors(
                    source = vectorSpace1,
                    target = vectorSpaceOfDim3,
                    matrixSpace = matrixSpace,
                    vectors = listOf(p, q),
                )
                (f.source.dim < f.target.dim).shouldBeTrue()
                f.isSurjective().shouldBeFalse()
            }
            "f.isInjective() should be false if the target has smaller dim" {
                val vectorSpaceOfDim3 = VectorSpace(numVectorSpace, listOf("p", "q", "r"))
                val (a, b) = vectorSpace1.getBasis()
                val f = LinearMap.fromVectors(
                    source = vectorSpaceOfDim3,
                    target = vectorSpace1,
                    matrixSpace = matrixSpace,
                    vectors = listOf(a, a, b),
                )
                (f.source.dim > f.target.dim).shouldBeTrue()
                f.isInjective().shouldBeFalse()
            }
            "f.isInjective() should be true for an inclusion map" {
                val vectorSpaceOfDim3 = VectorSpace(numVectorSpace, listOf("p", "q", "r"))
                val (p, q, _) = vectorSpaceOfDim3.getBasis()
                val f = LinearMap.fromVectors(
                    source = vectorSpace1,
                    target = vectorSpaceOfDim3,
                    matrixSpace = matrixSpace,
                    vectors = listOf(p, q),
                )
                f.isInjective().shouldBeTrue()
            }
            "f.isSurjective() should be true for a projection map" {
                val vectorSpaceOfDim3 = VectorSpace(numVectorSpace, listOf("p", "q", "r"))
                val (a, b) = vectorSpace1.getBasis()
                val f = LinearMap.fromVectors(
                    source = vectorSpaceOfDim3,
                    target = vectorSpace1,
                    matrixSpace = matrixSpace,
                    vectors = listOf(a, a, b),
                )
                f.isSurjective().shouldBeTrue()
            }
            "test addition" {
                val matrix1 = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, zero),
                        listOf(-one, two),
                    )
                )
                val matrix2 = matrixSpace.fromRowList(
                    listOf(
                        listOf(-two, one),
                        listOf(one, zero),
                    )
                )
                val expectedMatrix = matrixSpace.context.run {
                    matrix1 + matrix2
                }
                val f1 = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix1)
                val f2 = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix2)
                val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, expectedMatrix)
                (f1 + f2) shouldBe expected
            }
            "test composition" {
                val matrix1 = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, zero),
                        listOf(-one, two),
                    )
                )
                val matrix2 = matrixSpace.fromRowList(
                    listOf(
                        listOf(-two, one),
                        listOf(one, zero),
                    )
                )
                val expectedMatrix = matrixSpace.context.run {
                    matrix2 * matrix1
                }
                val f1 = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix1)
                val f2 = LinearMap.fromMatrix(vectorSpace2, vectorSpace3, matrixSpace, matrix2)
                val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace3, matrixSpace, expectedMatrix)
                (f2 * f1) shouldBe expected
            }
            "test linearMap.induce for SubVectorSpace" {
                val (a, b) = vectorSpace1.getBasis()
                val (x, y) = vectorSpace2.getBasis()
                val vectors = vectorSpace2.context.run {
                    listOf(x + y, x - y)
                }
                val f = LinearMap.fromVectors(vectorSpace1, vectorSpace2, matrixSpace, vectors)
                val subVectorSpace1 =
                    vectorSpace1.context.run {
                        SubVectorSpace(
                            matrixSpace,
                            totalVectorSpace = vectorSpace1,
                            generator = listOf(a - b),
                        )
                    }
                val subVectorSpace2 =
                    vectorSpace2.context.run {
                        SubVectorSpace(
                            matrixSpace,
                            totalVectorSpace = vectorSpace2,
                            generator = listOf(2 * y),
                        )
                    }
                val retract1 = subVectorSpace1.retraction
                val retract2 = subVectorSpace2.retraction
                val g = f.induce(subVectorSpace1, subVectorSpace2)
                g(
                    retract1(
                        vectorSpace1.context.run { a - b }
                    )
                ) shouldBe retract2(
                    vectorSpace2.context.run { 2 * y }
                )
            }
            "test linearMap.induce for QuotVectorSpace" {
                val (a, b) = vectorSpace1.getBasis()
                val (x, y) = vectorSpace2.getBasis()
                val vectors = vectorSpace2.context.run {
                    listOf(x + y, x - y)
                }
                val f = LinearMap.fromVectors(vectorSpace1, vectorSpace2, matrixSpace, vectors)
                val quotVectorSpace1 =
                    vectorSpace1.context.run {
                        QuotVectorSpace(
                            matrixSpace,
                            totalVectorSpace = vectorSpace1,
                            quotientGenerator = listOf(a - b),
                        )
                    }
                val quotVectorSpace2 =
                    vectorSpace2.context.run {
                        QuotVectorSpace(
                            matrixSpace,
                            totalVectorSpace = vectorSpace2,
                            quotientGenerator = listOf(2 * y),
                        )
                    }
                val proj1 = quotVectorSpace1.projection
                val proj2 = quotVectorSpace2.projection
                val g = f.induce(quotVectorSpace1, quotVectorSpace2)
                vectorSpace2.context.run {
                    g(proj1(a)) shouldBe proj2(x - y) // [x + y] = [x - y]
                    g(proj1(b)) shouldBe proj2(x - y)
                }
            }
            "test linearMap.induce for SubQuotVectorSpace" {
                val (a, b) = vectorSpace1.getBasis()
                val (x, y) = vectorSpace2.getBasis()
                val vectors = vectorSpace2.context.run {
                    listOf(x + y, x - y)
                }
                val f = LinearMap.fromVectors(vectorSpace1, vectorSpace2, matrixSpace, vectors)
                val subQuotVectorSpace1 =
                    vectorSpace1.context.run {
                        SubQuotVectorSpace(
                            matrixSpace,
                            totalVectorSpace = vectorSpace1,
                            subspaceGenerator = listOf(a, b),
                            quotientGenerator = listOf(a - b),
                        )
                    }
                val subQuotVectorSpace2 =
                    vectorSpace2.context.run {
                        SubQuotVectorSpace(
                            matrixSpace,
                            totalVectorSpace = vectorSpace2,
                            subspaceGenerator = listOf(x, y),
                            quotientGenerator = listOf(2 * y),
                        )
                    }
                val proj1 = subQuotVectorSpace1.projection
                val proj2 = subQuotVectorSpace2.projection
                val g = f.induce(subQuotVectorSpace1, subQuotVectorSpace2)
                vectorSpace2.context.run {
                    g(proj1(a)) shouldBe proj2(x - y) // [x + y] = [x - y]
                    g(proj1(b)) shouldBe proj2(x - y)
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> linearMapEdgeCaseTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "linear map test concerning 0-dim vector space" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b"))
        val (a, b) = vectorSpace.getBasis()
        // val zeroVectorSpace = VectorSpace<StringBasisName, S, V>(numVectorSpace, listOf())
        // Explicit type parameter for emptyList() is necessary to avoid overload resolution ambiguity
        val zeroVectorSpace = VectorSpace(numVectorSpace, emptyList<StringBasisName>())
        val zeroVector = zeroVectorSpace.zeroVector
        matrixSpace.context.run {
            "linear map to zero" {
                val f = LinearMap.fromVectors(vectorSpace, zeroVectorSpace, matrixSpace, listOf(zeroVector, zeroVector))
                f(a).isZero().shouldBeTrue()
                f(b).isZero().shouldBeTrue()
                f.isZero().shouldBeTrue()
            }
            "linear map from zero" {
                val g = LinearMap.fromVectors(zeroVectorSpace, vectorSpace, matrixSpace, listOf())
                g(zeroVector).isZero().shouldBeTrue()
                g.isZero().shouldBeTrue()
            }
        }
    }
}

class RationalLinearMapTest : FreeSpec({
    tags(linearMapTag, rationalTag)
    val matrixSpace = DenseMatrixSpaceOverRational
    include(linearMapTest(matrixSpace))
    include(linearMapEdgeCaseTest(matrixSpace))
})

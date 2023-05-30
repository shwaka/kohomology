package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.PrivateMemberAccessor
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val subVectorSpaceTag = NamedTag("SubVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "subspace test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val (u, v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            val generator = listOf(u + v, u, v)
            val subVectorSpace = SubVectorSpace(matrixSpace, vectorSpace, generator)
            "check inclusion" {
                subVectorSpace.dim shouldBe 2
                val (x, y) = subVectorSpace.getBasis()
                val incl = subVectorSpace.inclusion
                incl(x) shouldBe (u + v)
                incl(y) shouldBe u
            }
            "subspaceContains should work" {
                subVectorSpace.subspaceContains(u + v).shouldBeTrue()
                subVectorSpace.subspaceContains(u).shouldBeTrue()
                subVectorSpace.subspaceContains(v).shouldBeTrue()
                subVectorSpace.subspaceContains(2 * u - v).shouldBeTrue()
                subVectorSpace.subspaceContains(w).shouldBeFalse()
                subVectorSpace.subspaceContains(u + w).shouldBeFalse()
                subVectorSpace.subspaceContains(v - w).shouldBeFalse()
            }
        }

        "test vectorSpace.asSubVectorSpace" {
            val subVectorSpace = vectorSpace.asSubVectorSpace(matrixSpace)
            subVectorSpace.totalVectorSpace shouldBe vectorSpace
            val incl = subVectorSpace.inclusion
            val (x, y, z) = subVectorSpace.getBasis()
            incl(x) shouldBe u
            incl(y) shouldBe v
            incl(z) shouldBe w
        }
    }

    "check initialization of lazy properties" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val (u, v, _) = vectorSpace.getBasis()
        "accessing to subVectorSpace.dim should initialize subVectorSpace.basisNames" {
            vectorSpace.context.run {
                val generator = listOf(u + v, u, v)
                val subVectorSpace = SubVectorSpace(matrixSpace, vectorSpace, generator)
                PrivateMemberAccessor.isLazyInitialized(subVectorSpace, "basisNames").shouldBeFalse()
                subVectorSpace.dim shouldBe 2
                PrivateMemberAccessor.isLazyInitialized(subVectorSpace, "basisNames").shouldBeTrue()
                // subVectorSpace::basisNames.isLazyInitialized does not work
                // since SubVectorSpace is an interface and basisNames is implemented in SubVectorSpaceImpl
            }
        }
        "accessing to subVectorSpace.dim should initialize factory.rowEchelonForm" {
            vectorSpace.context.run {
                val generator = listOf(u + v, u, v)
                val subVectorSpace = SubVectorSpace(matrixSpace, vectorSpace, generator)
                val factory = PrivateMemberAccessor.getPropertyValue(subVectorSpace, "factory")
                    ?: throw Exception("factory is null")
                PrivateMemberAccessor.isLazyInitialized(factory, "rowEchelonForm").shouldBeFalse()
                subVectorSpace.dim shouldBe 2
                PrivateMemberAccessor.isLazyInitialized(factory, "rowEchelonForm").shouldBeTrue()
            }
        }
    }
}

class SubVectorSpaceTest : FreeSpec({
    tags(subVectorSpaceTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(subVectorSpaceTest(matrixSpace))
})

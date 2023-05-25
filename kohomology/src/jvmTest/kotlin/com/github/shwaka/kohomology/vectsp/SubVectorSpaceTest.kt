package com.github.shwaka.kohomology.vectsp

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
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

val subVectorSpaceTag = NamedTag("SubVectorSpace")

// https://stackoverflow.com/questions/42522739/kotlin-check-if-lazy-val-has-been-initialised
val KProperty0<*>.isLazyInitialized: Boolean
    get() {
        // Prevent IllegalAccessException from JVM access check on private properties.
        val originalAccessLevel = this.isAccessible
        this.isAccessible = true
        val delegate: Any = this.getDelegate() ?: throw Exception("Not delegate!")
        if (delegate !is Lazy<*>) {
            throw Exception("Not Lazy!")
        }
        val isLazyInitialized = delegate.isInitialized()
        // Reset access level.
        this.isAccessible = originalAccessLevel
        return isLazyInitialized
    }

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
    }

    "check initialization of lazy properties" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val (u, v, _) = vectorSpace.getBasis()
        "accessing to subVectorSpace.dim should initialize subVectorSpace.basisNames" {
            vectorSpace.context.run {
                val generator = listOf(u + v, u, v)
                val subVectorSpace = SubVectorSpace(matrixSpace, vectorSpace, generator)
                subVectorSpace::basisNames.isLazyInitialized.shouldBeFalse()
                subVectorSpace.dim shouldBe 2
                subVectorSpace::basisNames.isLazyInitialized.shouldBeTrue()
            }
        }
        "accessing to subVectorSpace.dim should initialize factory.rowEchelonForm" {
            vectorSpace.context.run {
                val generator = listOf(u + v, u, v)
                val subVectorSpace = SubVectorSpace(matrixSpace, vectorSpace, generator)
                // val factory = subVectorSpace.javaClass.getDeclaredField("factory")
                fun getProperty(target: Any, name: String): KProperty1<out Any, *> {
                    for (property in target::class.declaredMemberProperties) {
                        if (property.name == name) {
                            return property
                        }
                    }
                    throw Exception("Property not found: $name")
                }
                fun getPropertyValue(target: Any, name: String): Any? {
                    val property = getProperty(target, name)
                    property.isAccessible = true
                    return property.call(target)
                }
                fun isLazyInitialized(target: Any, name: String): Boolean {
                    val property = getProperty(target, name) as KProperty1<Any, *>
                    property.isAccessible = true
                    val delegate: Any = property.getDelegate(target) ?: throw Exception("Not delegate!")
                    if (delegate !is Lazy<*>) {
                        throw Exception("Not Lazy!")
                    }
                    return delegate.isInitialized()
                }
                val factory = getPropertyValue(subVectorSpace, "factory")
                    ?: throw Exception("factory is null")
                isLazyInitialized(factory, "rowEchelonForm").shouldBeFalse()
                subVectorSpace.dim shouldBe 2
                isLazyInitialized(factory, "rowEchelonForm").shouldBeTrue()
            }
        }
    }
}

class SubVectorSpaceTest : FreeSpec({
    tags(subVectorSpaceTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(subVectorSpaceTest(matrixSpace))
})

/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Fp
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.RationalField
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
import io.kotest.matchers.shouldBe

fun <S> fieldTest(field: Field<S>) = stringSpec {
    "1 + 2 should be 3" {
        (field.fromInt(1) + field.fromInt(2)) shouldBe field.fromInt(3)
    }
    "1 - 1 should be 0" {
        (field.fromInt(1) - field.fromInt(1)) shouldBe field.fromInt(0)
    }
    "0^0 should be 1" {
        field.fromInt(0).pow(0) shouldBe field.fromInt(1)
    }
    "unaryMinus of 2 should be -2" {
        (-field.fromInt(2)) shouldBe field.fromInt(-2)
    }
    "unaryMinus of -5 should be 5" {
        (-field.fromInt(-5)) shouldBe field.fromInt(5)
    }
    "2 / 3 should be equal to 2 * 3^{-1}" {
        val two = field.fromInt(2)
        val three = field.fromInt(3)
        (two / three) shouldBe (two * (three.inv()))
    }
}

fun <S> rationalTest(field: RationalField<S>) = stringSpec {
    "1/2 + 1/3 should be 5/6" {
        val a = field.fromIntPair(1, 2)
        val b = field.fromIntPair(1, 3)
        (a + b) shouldBe field.fromIntPair(5, 6)
    }
    "2/6 should be 1/3" {
        field.fromIntPair(2, 6) shouldBe field.fromIntPair(1, 3)
    }
    "1/(-2) should be (-1)/2" {
        field.fromIntPair(1, -2) shouldBe field.fromIntPair(-1, 2)
    }
    "0/2 should be 0/1" {
        field.fromIntPair(0, 2) shouldBe field.fromIntPair(0, 1)
    }
    "5/6 * 2/3 should be 5/9" {
        (field.fromIntPair(5, 6) * field.fromIntPair(2, 3)) shouldBe field.fromIntPair(5, 9)
    }
    "(2/1) / (3/1) should be 2/3" {
        (field.fromInt(2) / field.fromInt(3)) shouldBe field.fromIntPair(2, 3)
    }
    "(1/2)^3 should be 1/8" {
        field.fromIntPair(1, 2).pow(3) shouldBe field.fromIntPair(1, 8)
    }
}

class IntRationalTest : StringSpec({
    include(fieldTest(IntRationalField))
    include(rationalTest(IntRationalField))
})

class BigRationalTest : StringSpec({
    include(fieldTest(BigRationalField))
    include(rationalTest(BigRationalField))
})

class IntModpTest : StringSpec({
    val f5 = Fp.get(5)
    include(fieldTest(f5))
    "(8 mod 5) should be equal to (3 mod 5)" {
        f5.fromInt(8) shouldBe f5.fromInt(3)
    }
    "2^{-1} should be 3 in F_5" {
        f5.fromInt(2).inv() shouldBe f5.fromInt(3)
    }
})

class FpTest : StringSpec({
    "Fp.get should create only one instance for each p" {
        (Fp.get(3) === Fp.get(3)).shouldBeTrue()
    }
    "Fp.get(3) should be equal to another Fp.get(3)" {
        (Fp.get(3) == Fp.get(3)).shouldBeTrue()
    }
    "Fp.get(3) should be different from Fp.get(5)" {
        (Fp.get(3) != Fp.get(5)).shouldBeTrue()
    }
    "Fp.get(6) should throw ArithmeticException" {
        shouldThrow<ArithmeticException> { Fp.get(6) }
    }
})

class CompileTest : StringSpec({
    "IntRational + IntRational should compile" {
        val codeSnippet =
            """
            import com.github.shwaka.kohomology.field.IntRational
            val foo = IntRational(0, 1) + IntRational(1, 0)
            """ // compiles, but runtime error
        codeSnippet.shouldCompile()
    }
    "Rational + IntModp should not compile" {
        val codeSnippet =
            """
            import com.github.shwaka.kohomology.field.Rational
            import com.github.shwaka.kohomology.field.IntModp
            val foo = IntRational(0, 1) + IntModp(0, 7)
            """
        codeSnippet.shouldNotCompile()
    }
})

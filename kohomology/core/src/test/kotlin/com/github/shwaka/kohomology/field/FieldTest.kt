package com.github.shwaka.kohomology.field

import com.github.shwaka.kohomology.compileTag
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

val fieldTag = NamedTag("Field")

fun <S : Scalar<S>> fromIntTest(field: Field<S>) = stringSpec {
    val intMin = -100
    val intMax = 100
    val intArb = Arb.int(intMin..intMax)
    "fromInt should be additive" {
        checkAll(intArb, intArb) { a, b ->
            (field.fromInt(a) + field.fromInt(b)) shouldBe field.fromInt(a + b)
        }
    }
    "field.fromInt(0) should be field.zero" {
        field.fromInt(0) shouldBe field.zero
    }
    "fromInt should be multiplicative" {
        checkAll(intArb, intArb) { a, b ->
            (field.fromInt(a) * field.fromInt(b)) shouldBe field.fromInt(a * b)
        }
    }
    "field.fromInt(1) should be field.one" {
        field.fromInt(1) shouldBe field.one
    }
}

fun <S : Scalar<S>> fieldTest(field: Field<S>, intMax: Int = Int.MAX_VALUE) = stringSpec {
    if (intMax <= 0) throw IllegalArgumentException("intMax should be positive")
    val arb = field.arb(Arb.int(-intMax..intMax))
    "field.zero should be the unit of addition" {
        checkAll(arb) { a ->
            val zero = field.zero
            (a + zero) shouldBe a
            (zero + a) shouldBe a
        }
    }
    "field.one should be the unit of multiplication" {
        checkAll(arb) { a ->
            val one = field.one
            (a * one) shouldBe a
            (one * a) shouldBe a
        }
    }
    "addition should be associative" {
        checkAll(arb, arb, arb) { a, b, c ->
            ((a + b) + c) shouldBe (a + (b + c))
        }
    }
    "multiplication should be associative" {
        checkAll(arb, arb, arb) { a, b, c ->
            ((a * b) * c) shouldBe (a * (b * c))
        }
    }
    "multiplication should be distributive w.r.t. addition" {
        checkAll(arb, arb, arb) { a, b, c ->
            ((a + b) * c) shouldBe ((a * c) + (b * c))
        }
    }
    "unaryMinus() should give the additive inverse" {
        checkAll(arb) { a ->
            (a + (-a)) shouldBe field.zero
            ((-a) + a) shouldBe field.zero
        }
    }
    "inv() should give the multiplicative inverse" {
        checkAll(arb) { a ->
            if (a != field.zero) {
                (a * (a.inv())) shouldBe field.one
                ((a.inv()) * a) shouldBe field.one
            }
        }
    }
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
    "Int multiplication should work correctly" {
        val two = field.fromInt(2)
        val six = field.fromInt(6)
        (two * 3) shouldBe six
        (3 * two) shouldBe six
    }
    "2 / 3 should be equal to 2 * 3^{-1}" {
        val two = field.fromInt(2)
        val three = field.fromInt(3)
        (two / three) shouldBe (two * (three.inv()))
    }
}

fun <S : RationalScalar<S>> rationalTest(field: RationalField<S>) = stringSpec {
    tags(fieldTag)

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

val kococoDebug = (System.getProperty("kococo.debug") != null)

class IntRationalTest : StringSpec({
    tags(fieldTag)

    include(fromIntTest(IntRationalField))
    include(fieldTest(IntRationalField, 100))
    include(rationalTest(IntRationalField))

    "overflow test for IntRational".config(enabled = kococoDebug) {
        val a = IntRationalField.fromIntPair(Int.MAX_VALUE, 1)
        val b = IntRationalField.one
        shouldThrow<ArithmeticException> { a + b }
    }
})

class LongRationalTest : StringSpec({
    tags(fieldTag)

    include(fromIntTest(LongRationalField))
    include(fieldTest(LongRationalField, 100))
    include(rationalTest(LongRationalField))
})

class BigRationalTest : StringSpec({
    tags(fieldTag)

    include(fromIntTest(BigRationalField))
    include(fieldTest(BigRationalField))
    include(rationalTest(BigRationalField))
})

class IntModpTest : StringSpec({
    tags(fieldTag)

    include(fromIntTest(F5))
    include(fieldTest(F5))
    "(8 mod 5) should be equal to (3 mod 5)" {
        F5.fromInt(8) shouldBe F5.fromInt(3)
    }
    "(-1 mod 5) should be equal to (4 mod 5)" {
        println((-1) % 5)
        println((-6) % 5)
        F5.fromInt(-1) shouldBe F5.fromInt(4)
    }
    "2^{-1} should be 3 in F_5" {
        F5.fromInt(2).inv() shouldBe F5.fromInt(3)
    }
})

class FpTest : StringSpec({
    tags(fieldTag)

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
    tags(fieldTag, compileTag)

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

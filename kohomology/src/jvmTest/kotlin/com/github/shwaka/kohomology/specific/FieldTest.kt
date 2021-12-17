package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.compileTag
import com.github.shwaka.kohomology.intModpTag
import com.github.shwaka.kohomology.intRationalTag
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.longRationalTag
import com.github.shwaka.kohomology.overflowTag
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.isPrime
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

val fieldTag = NamedTag("Field")

val kococoDebug = (System.getProperty("kococo.debug") != null)

fun <S : Scalar> fromIntTest(field: Field<S>) = freeSpec {
    val intMin = -100
    val intMax = 100
    val intArb = Arb.int(intMin..intMax)
    field.context.run {
        "fromInt should be additive" {
            checkAll(intArb, intArb) { a, b ->
                (field.fromInt(a) + field.fromInt(b)) shouldBe field.fromInt(a + b)
            }
        }
        "field.fromInt(0) should be field.zero" {
            field.fromInt(0) shouldBe zero
        }
        "fromInt should be multiplicative" {
            checkAll(intArb, intArb) { a, b ->
                (field.fromInt(a) * field.fromInt(b)) shouldBe field.fromInt(a * b)
            }
        }
        "field.fromInt(1) should be field.one" {
            field.fromInt(1) shouldBe one
        }
    }
}

fun <S : Scalar> fieldTest(field: Field<S>, intMax: Int = Int.MAX_VALUE) = freeSpec {
    if (intMax <= 0) throw IllegalArgumentException("intMax should be positive")
    val arb = field.arb(Arb.int(-intMax..intMax))
    field.context.run {
        "field.characteristic should be zero or a prime" {
            val p: Int = field.characteristic
            ((p == 0) || p.isPrime()).shouldBeTrue()
        }
        "field.characteristic should be zero in the field" {
            val p: S = field.characteristic.toScalar()
            p shouldBe zero
        }
        "field.zero should be the unit of addition" {
            checkAll(arb) { a ->
                (a + zero) shouldBe a
                (zero + a) shouldBe a
            }
        }
        "field.one should be the unit of multiplication" {
            checkAll(arb) { a ->
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
                (a + (-a)) shouldBe zero
                ((-a) + a) shouldBe zero
            }
        }
        "multiplication with Sign" {
            checkAll(arb) { a ->
                (a * Sign.PLUS) shouldBe a
                (Sign.PLUS * a) shouldBe a
                (a * Sign.MINUS) shouldBe -a
                (Sign.MINUS * a) shouldBe -a
            }
        }
        "inv() should give the multiplicative inverse" {
            checkAll(arb) { a ->
                if (a.isNotZero()) {
                    (a * (a.inv())) shouldBe one
                    ((a.inv()) * a) shouldBe one
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
        "listOf(1, -1/2, 1/3).sum() should be 5/6" {
            val six = 6.toScalar()
            listOf(one, -one / two, one / three).sum() shouldBe five / six
        }
        "emptyList().sum() should be 0" {
            emptyList<S>().sum() shouldBe zero
        }
        "listOf(1, -2, 1/3).product() should be -2/3" {
            listOf(one, -two, one / three).product() shouldBe (-two / three)
        }
        "emptyList().product() should be 1" {
            emptyList<S>().product() shouldBe one
        }
    }
}

fun <S : Scalar> rationalTest(field: Field<S>) = freeSpec {
    tags(fieldTag)

    field.context.run {
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
}

class IntRationalTest : FreeSpec({
    tags(fieldTag, intRationalTag)

    include(fromIntTest(IntRationalField))
    include(fieldTest(IntRationalField, 100))
    include(rationalTest(IntRationalField))

    "overflow test for IntRational".config(enabled = kococoDebug, tags = setOf(overflowTag)) {
        IntRationalField.context.run {
            val a = Int.MAX_VALUE.toScalar()
            val b = one
            shouldThrow<ArithmeticException> { a + b }
            shouldThrow<ArithmeticException> { a * 2 }
        }
    }
})

class LongRationalTest : FreeSpec({
    tags(fieldTag, longRationalTag)

    include(fromIntTest(LongRationalField))
    include(fieldTest(LongRationalField, 100))
    include(rationalTest(LongRationalField))

    "overflow test for LongRational".config(enabled = kococoDebug, tags = setOf(overflowTag)) {
        val a = LongRationalField.fromInt(Int.MAX_VALUE)
        LongRationalField.context.run {
            shouldThrow<ArithmeticException> { a * a * 3 }
        }
    }
})

class BigRationalTest : FreeSpec({
    tags(fieldTag, bigRationalTag)

    include(fromIntTest(BigRationalField))
    include(fieldTest(BigRationalField))
    include(rationalTest(BigRationalField))

    "assertReduced should throw IllegalArgumentException".config(enabled = kococoDebug) {
        shouldThrow<IllegalArgumentException> {
            BigRational.fromReduced(1, -1)
        }
        shouldThrow<IllegalArgumentException> {
            BigRational.fromReduced(6, 2)
        }
        shouldThrow<IllegalArgumentException> {
            BigRational.fromReduced(0, 2)
        }
    }

    "(-1/2).toString(PrintType.TEX) should be -\\frac{1}{2}" {
        BigRational(-1, 2).toString(PrintType.TEX) shouldBe "-\\frac{1}{2}"
    }
})

class IntModpTest : FreeSpec({
    tags(fieldTag, intModpTag)

    include(fromIntTest(F5))
    include(fieldTest(F5))
    "(8 mod 5) should be equal to (3 mod 5)" {
        F5.fromInt(8) shouldBe F5.fromInt(3)
    }
    "(-1 mod 5) should be equal to (4 mod 5)" {
        F5.fromInt(-1) shouldBe F5.fromInt(4)
    }
    "2^{-1} should be 3 in F_5" {
        F5.context.run {
            two.inv() shouldBe three
        }
    }
    "1 + 2 should be 3 (mod 5)" {
        F5.context.run {
            (one + two) shouldBe three
        }
    }
    "3 + 4 should be 2 (mod 5)" {
        F5.context.run {
            (three + four) shouldBe two
        }
    }
    "addition of different characteristic should throw ArithmeticException" {
        val twoMod7 = F7.context.run { two }
        val threeMod7 = F7.context.run { three }
        shouldThrow<ArithmeticException> {
            F5.context.run {
                twoMod7 + threeMod7
            }
        }
    }
})

class FpTest : FreeSpec({
    tags(fieldTag, intModpTag)

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

class CompileTest : FreeSpec({
    tags(fieldTag, compileTag)

    "IntRational + IntRational should compile" {
        val codeSnippet =
            """
            import com.github.shwaka.kohomology.specific.IntRational
            import com.github.shwaka.kohomology.specific.IntRationalField
            val foo = IntRationalField.context.run { IntRational(0, 1) + IntRational(1, 0) }
            """ // compiles, but runtime error
        codeSnippet.shouldCompile()
    }
    "Rational + IntModp should not compile" {
        val codeSnippet =
            """
            import com.github.shwaka.kohomology.specific.IntRational
            import com.github.shwaka.kohomology.specific.IntRationalField
            import com.github.shwaka.kohomology.specific.IntModp
            val foo = IntRationalField.context.run { IntRational(0, 1) + IntModp(0, 7) }
            """
        codeSnippet.shouldNotCompile()
    }
})

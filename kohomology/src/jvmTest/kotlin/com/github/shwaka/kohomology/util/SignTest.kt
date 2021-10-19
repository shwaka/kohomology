package com.github.shwaka.kohomology.util

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val signTag = NamedTag("Sign")

class SignTest : FreeSpec({
    tags(signTag)

    "test multiplication" {
        (Sign.PLUS * Sign.PLUS) shouldBe Sign.PLUS
        (Sign.PLUS * Sign.MINUS) shouldBe Sign.MINUS
        (Sign.MINUS * Sign.PLUS) shouldBe Sign.MINUS
        (Sign.MINUS * Sign.MINUS) shouldBe Sign.PLUS
    }

    "test multiplication with Int" {
        (Sign.PLUS * 2) shouldBe 2
        ((-1) * Sign.MINUS) shouldBe 1
    }

    "test pow()" {
        Sign.PLUS.pow(3) shouldBe Sign.PLUS
        Sign.PLUS.pow(0) shouldBe Sign.PLUS
        Sign.MINUS.pow(5) shouldBe Sign.MINUS
        Sign.MINUS.pow(-2) shouldBe Sign.PLUS
    }

    "test unaryMinus()" {
        (-Sign.PLUS) shouldBe Sign.MINUS
        (-Sign.MINUS) shouldBe Sign.PLUS
    }

    "test toInt()" {
        Sign.PLUS.toInt() shouldBe 1
        Sign.MINUS.toInt() shouldBe -1
    }

    "test fromParity(Int)" {
        Sign.fromParity(0) shouldBe Sign.PLUS
        Sign.fromParity(1) shouldBe Sign.MINUS
        Sign.fromParity(-2) shouldBe Sign.PLUS
        Sign.fromParity(-3) shouldBe Sign.MINUS
    }

    "test fromParity(Boolean)" {
        Sign.fromParity(even = true) shouldBe Sign.PLUS
        Sign.fromParity(even = false) shouldBe Sign.MINUS
    }
})

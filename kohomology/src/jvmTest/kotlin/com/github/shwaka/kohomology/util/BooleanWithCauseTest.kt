package com.github.shwaka.kohomology.util

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

val booleanWithCauseTag = NamedTag("BooleanWithCause")

class BooleanWithCauseTest : FreeSpec({
    tags(booleanWithCauseTag)

    "True.toBoolean() should be true" {
        BooleanWithCause.True.toBoolean().shouldBeTrue()
    }

    "False.toBoolean() should be false" {
        BooleanWithCause.False(emptyList()).toBoolean().shouldBeFalse()
    }

    "True * True should be True" {
        (BooleanWithCause.True * BooleanWithCause.True) shouldBeSameInstanceAs BooleanWithCause.True
    }

    "True * False should be False" {
        val cause = listOf("some message")
        (BooleanWithCause.True * BooleanWithCause.False(cause)) shouldBe
            BooleanWithCause.False(cause)
    }

    "False * True should be False" {
        val cause = listOf("some message")
        (BooleanWithCause.False(cause) * BooleanWithCause.True) shouldBe
            BooleanWithCause.False(cause)
    }

    "False * False should be False" {
        val cause1 = listOf("some message")
        val cause2 = listOf("another message")
        (BooleanWithCause.False(cause1) * BooleanWithCause.False(cause2)) shouldBe
            BooleanWithCause.False(cause1 + cause2)
    }
})

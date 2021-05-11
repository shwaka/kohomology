package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.free.Indeterminate
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val copiedNameTag = NamedTag("CopiedName")

class CopiedNameTest : FreeSpec({
    tags(copiedNameTag)
    "CopiedName test" - {
        val n = 3
        val x = Indeterminate("x", n)
        val sx = x.copy(1, null)
        "sx.toTex() should be \"sx\" when CopiedName.useBar is false" {
            CopiedName.useBar = false
            sx.toTex() shouldBe "s{x}"
        }
        "sx.toTex() should be \"sx\" when CopiedName.useBar is true" {
            CopiedName.useBar = true
            sx.toTex() shouldBe "\\bar{x}"
        }
    }
})

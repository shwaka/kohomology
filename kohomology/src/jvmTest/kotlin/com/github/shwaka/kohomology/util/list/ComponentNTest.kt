package com.github.shwaka.kohomology.util.list

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val componentNTag = NamedTag("ComponentN")

class ComponentNTest : FreeSpec({
    tags(componentNTag)

    val stringList = (0..30).map { "foo$it" }

    "test component6" {
        stringList.component6() shouldBe "foo5"
    }

    "test component7" {
        stringList.component7() shouldBe "foo6"
    }

    "test component8" {
        stringList.component8() shouldBe "foo7"
    }

    "test component9" {
        stringList.component9() shouldBe "foo8"
    }

    "test component10" {
        stringList.component10() shouldBe "foo9"
    }

    "test component11" {
        stringList.component11() shouldBe "foo10"
    }

    "test component12" {
        stringList.component12() shouldBe "foo11"
    }

    "test component13" {
        stringList.component13() shouldBe "foo12"
    }

    "test component14" {
        stringList.component14() shouldBe "foo13"
    }

    "test component15" {
        stringList.component15() shouldBe "foo14"
    }

    "test component16" {
        stringList.component16() shouldBe "foo15"
    }

    "test component17" {
        stringList.component17() shouldBe "foo16"
    }

    "test component18" {
        stringList.component18() shouldBe "foo17"
    }

    "test component19" {
        stringList.component19() shouldBe "foo18"
    }

    "test component20" {
        stringList.component20() shouldBe "foo19"
    }

    "test component21" {
        stringList.component21() shouldBe "foo20"
    }

    "test component22" {
        stringList.component22() shouldBe "foo21"
    }

    "test component23" {
        stringList.component23() shouldBe "foo22"
    }

    "test component24" {
        stringList.component24() shouldBe "foo23"
    }

    "test component25" {
        stringList.component25() shouldBe "foo24"
    }

    "test component26" {
        stringList.component26() shouldBe "foo25"
    }

    "test component27" {
        stringList.component27() shouldBe "foo26"
    }

    "test component28" {
        stringList.component28() shouldBe "foo27"
    }

    "test component29" {
        stringList.component29() shouldBe "foo28"
    }

    "test component30" {
        stringList.component30() shouldBe "foo29"
    }
})

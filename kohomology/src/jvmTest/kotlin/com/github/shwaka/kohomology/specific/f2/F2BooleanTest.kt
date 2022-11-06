package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.intMod2BooleanTag
import com.github.shwaka.kohomology.specific.fieldTag
import com.github.shwaka.kohomology.specific.fieldTest
import com.github.shwaka.kohomology.specific.finiteFieldTest
import com.github.shwaka.kohomology.specific.fromIntTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class F2BooleanTest : FreeSpec({
    tags(fieldTag, intMod2BooleanTag)

    include(fromIntTest(F2Boolean))
    include(fieldTest(F2Boolean))
    include(finiteFieldTest(F2Boolean))

    F2Boolean.context.run {
        "fromInt(n).isZero() should be true iff n is even" {
            fromInt(-2).isZero().shouldBeTrue()
            fromInt(-1).isZero().shouldBeFalse()
            fromInt(0).isZero().shouldBeTrue()
            fromInt(1).isZero().shouldBeFalse()
            fromInt(2).isZero().shouldBeTrue()
            fromInt(3).isZero().shouldBeFalse()
        }
    }
})

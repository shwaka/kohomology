package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.intMod2BooleanTag
import com.github.shwaka.kohomology.linalg.numVectorTag
import com.github.shwaka.kohomology.linalg.numVectorTest
import com.github.shwaka.kohomology.specific.F3
import com.github.shwaka.kohomology.specific.F5
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec

val setNumVectorTag = NamedTag("SetNumVector")

class SetNumVectorTest : FreeSpec({
    tags(numVectorTag, setNumVectorTag, intMod2BooleanTag)

    include(numVectorTest(SetNumVectorSpaceOverF2Boolean))

    "should throw IllegalArgumentException for fields with 3 elements or more" {
        shouldThrow<IllegalArgumentException> {
            SetNumVectorSpace.from(F3)
        }
        shouldThrow<IllegalArgumentException> {
            SetNumVectorSpace.from(F5)
        }
    }
})

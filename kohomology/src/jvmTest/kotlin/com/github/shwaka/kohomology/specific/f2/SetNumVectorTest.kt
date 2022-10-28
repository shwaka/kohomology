package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.intMod2BooleanTag
import com.github.shwaka.kohomology.linalg.numVectorTag
import com.github.shwaka.kohomology.linalg.numVectorTest
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec

val setNumVectorTag = NamedTag("SetNumVector")

class SetNumVectorTest : FreeSpec({
    tags(numVectorTag, setNumVectorTag, intMod2BooleanTag)

    include(numVectorTest(SetNumVectorSpaceOverF2Boolean))
})

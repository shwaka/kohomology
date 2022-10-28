package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.intMod2BooleanTag
import com.github.shwaka.kohomology.specific.fieldTag
import com.github.shwaka.kohomology.specific.fieldTest
import com.github.shwaka.kohomology.specific.fromIntTest
import io.kotest.core.spec.style.FreeSpec

class F2BooleanTest : FreeSpec({
    tags(fieldTag, intMod2BooleanTag)

    include(fromIntTest(F2Boolean))
    include(fieldTest(F2Boolean))
})

package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.intMod2BooleanTag
import com.github.shwaka.kohomology.linalg.matrixOfRank2Test
import com.github.shwaka.kohomology.linalg.matrixTag
import com.github.shwaka.kohomology.linalg.matrixTest
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec

val setMatrixTag = NamedTag("SetMatrix")

class SetMatrixTest : FreeSpec({
    tags(matrixTag, setMatrixTag, intMod2BooleanTag)

    val matrixSpace = SetMatrixSpaceOverF2Boolean
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    // include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    // include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    // include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    // include(findPreimageGenTest(matrixSpace, 3, 3))
    // include(findPreimageGenTest(matrixSpace, 4, 3))
})

package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.intMod2BooleanTag
import com.github.shwaka.kohomology.linalg.determinantTest
import com.github.shwaka.kohomology.linalg.findPreimageGenTest
import com.github.shwaka.kohomology.linalg.matrixOfRank2Test
import com.github.shwaka.kohomology.linalg.matrixSizeForDet
import com.github.shwaka.kohomology.linalg.matrixTag
import com.github.shwaka.kohomology.linalg.matrixTest
import com.github.shwaka.kohomology.linalg.maxValueForDet
import com.github.shwaka.kohomology.linalg.rowEchelonFormGenTest
import com.github.shwaka.kohomology.specific.F3
import com.github.shwaka.kohomology.specific.F5
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val setMatrixTag = NamedTag("SetMatrix")

class SetMatrixTest : FreeSpec({
    tags(matrixTag, setMatrixTag, intMod2BooleanTag)

    val matrixSpace = SetMatrixSpaceOverF2Boolean
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))

    matrixSpace.context.run {
        "fromRowSetMap should return the same matrix as fromRowList" {
            val rowList = listOf(
                listOf(zero, one),
                listOf(zero, zero)
            )
            val rowSetMap = mapOf(
                0 to setOf(1),
            )
            matrixSpace.fromRowSetMap(rowSetMap, 2, 2) shouldBe matrixSpace.fromRowList(rowList)
        }
    }

    "should throw IllegalArgumentException for fields with 3 elements or more" {
        shouldThrow<IllegalArgumentException> {
            SetNumVectorSpace.from(F3)
        }
        shouldThrow<IllegalArgumentException> {
            SetNumVectorSpace.from(F5)
        }
    }
})

package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.linalg.determinantTest
import com.github.shwaka.kohomology.linalg.findPreimageGenTest
import com.github.shwaka.kohomology.linalg.matrixOfRank2Test
import com.github.shwaka.kohomology.linalg.matrixSizeForDet
import com.github.shwaka.kohomology.linalg.matrixTag
import com.github.shwaka.kohomology.linalg.matrixTest
import com.github.shwaka.kohomology.linalg.maxValueForDet
import com.github.shwaka.kohomology.linalg.rowEchelonFormGenTest
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class MatrixSpaceWithLogTest : FreeSpec({
    tags(matrixTag)

    val matrixSpace = SparseMatrixSpaceOverRational.withLog()

    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))

    "test toString()" {
        matrixSpace.toString() shouldBe "SparseMatrixSpace(RationalField)"
    }
})

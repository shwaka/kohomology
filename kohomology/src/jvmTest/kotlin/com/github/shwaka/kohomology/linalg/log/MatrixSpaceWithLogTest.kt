package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
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
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> matrixLogTest(
    matrixSpace: MatrixSpaceWithLog<S, V, M>
) = freeSpec {
    val logger = matrixSpace.logger
    beforeEach {
        logger.clearEntries()
    }
    "test logger" - {
        matrixSpace.context.run {
            "add entry" {
                logger.entries.shouldHaveSize(0)
                val matrix1 = matrixSpace.getZero(2, 3)
                val matrix2 = matrixSpace.getZero(3, 4)
                matrix1 * matrix2
                logger.entries.shouldHaveSize(1)
                logger.entries[0].data shouldBe MultiplyMatrixLog(
                    firstRowCount = 2,
                    firstColCount = 3,
                    secondColCount = 4,
                )
            }
        }
    }
}

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
        matrixSpace.toString() shouldBe "MatrixSpaceWithLog(SparseMatrixSpace(RationalField))"
    }

    include(matrixLogTest(matrixSpace))
})

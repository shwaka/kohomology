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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> matrixLogTest(
    matrixSpace: MatrixSpaceWithLog<S, V, M>
) = freeSpec {
    val logger = matrixSpace.logger
    beforeEach {
        logger.clear()
    }
    "test logger" - {
        matrixSpace.context.run {
            "add entry" {
                logger.measurement.shouldHaveSize(0)
                val matrix1 = matrixSpace.getZero(2, 3)
                val matrix2 = matrixSpace.getZero(3, 4)
                matrix1 * matrix2
                logger.measurement.shouldHaveSize(1)
                val input = logger.measurement[0].input
                input.shouldBeInstanceOf<MatrixOperationInput.MultiplyMatrix>()
                input.operation shouldBe MatrixOperation.MULTIPLY_MATRIX
                input shouldBe MatrixOperationInput.MultiplyMatrix(
                    firstRowCount = 2,
                    firstColCount = 3,
                    secondColCount = 4,
                )
            }

            "measurement" {
                logger.measurement.shouldHaveSize(0)
                val matrix = matrixSpace.getZero(2)
                matrix + matrix
                matrix + matrix
                matrix * matrix
                logger.measurement.shouldHaveSize(3)
                val summaries = logger.summaries()
                summaries[MatrixOperation.ADD].let {
                    it.shouldNotBeNull()
                    it.invocationCount shouldBe 2
                    val metrics = it.metrics
                    metrics.shouldBeInstanceOf<MatrixOperationMetrics.Add>()
                    metrics.maxRowCount shouldBe 2
                }
                summaries[MatrixOperation.MULTIPLY_MATRIX].let {
                    it.shouldNotBeNull()
                    it.invocationCount shouldBe 1
                }
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

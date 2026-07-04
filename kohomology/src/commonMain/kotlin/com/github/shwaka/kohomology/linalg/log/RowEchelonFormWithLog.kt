package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign

public class RowEchelonFormWithLog<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val originalRowEchelonForm: RowEchelonForm<S, V, M>,
    public val logger: RefLogger,
) : RowEchelonForm<S, V, M>(originalRowEchelonForm.matrixSpace, originalRowEchelonForm.originalMatrix) {
    override fun computeRowEchelonForm(): M {
        val input = RefOperationInput.Unreduced(
            rowCount = this.originalMatrix.rowCount,
            colCount = this.originalMatrix.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalRowEchelonForm.matrix
        }
    }

    override fun computeReducedRowEchelonForm(): M {
        TODO("Not yet implemented")
    }

    override fun computePivots(): List<Int> {
        TODO("Not yet implemented")
    }

    override fun computeSign(): Sign {
        TODO("Not yet implemented")
    }
}

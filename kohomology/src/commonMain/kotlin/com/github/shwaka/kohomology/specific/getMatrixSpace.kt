package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.github.shwaka.kohomology.util.cancel.CancellationContext
import com.github.shwaka.kohomology.util.isPrime

public fun getNumVectorSpace(characteristic: Int): SparseNumVectorSpace<*> {
    require((characteristic == 0) || characteristic.isPrime())
    return when (characteristic) {
        0 -> SparseNumVectorSpaceOverRational
        2 -> SparseNumVectorSpaceOverF2
        3 -> SparseNumVectorSpaceOverF3
        5 -> SparseNumVectorSpaceOverF5
        7 -> SparseNumVectorSpaceOverF7
        else -> {
            val field = Fp.get(characteristic)
            SparseNumVectorSpace.from(field)
        }
    }
}

public fun getMatrixSpace(characteristic: Int): SparseMatrixSpace<*> {
    require((characteristic == 0) || characteristic.isPrime())
    return when (characteristic) {
        0 -> SparseMatrixSpaceOverRational
        2 -> SparseMatrixSpaceOverF2
        3 -> SparseMatrixSpaceOverF3
        5 -> SparseMatrixSpaceOverF5
        7 -> SparseMatrixSpaceOverF7
        else -> {
            val numVectorSpace = getNumVectorSpace(characteristic)
            SparseMatrixSpace.from(numVectorSpace)
        }
    }
}

public fun getCancellableMatrixSpace(
    characteristic: Int,
): Pair<SparseMatrixSpace<*>, CancellationContext> {
    require((characteristic == 0) || characteristic.isPrime())
    val numVectorSpace = getNumVectorSpace(characteristic)
    val cancellationContext = CancellationContext.getDefault()
    return Pair(
        SparseMatrixSpace.from(numVectorSpace, cancellationContext),
        cancellationContext,
    )
}

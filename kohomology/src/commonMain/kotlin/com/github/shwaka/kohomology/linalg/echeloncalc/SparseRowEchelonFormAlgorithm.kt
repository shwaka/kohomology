package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.cancel.CancellationContext
import com.github.shwaka.kohomology.util.parallel.ParallelConfig

public sealed interface SparseRowEchelonFormAlgorithm {
    public data object InPlace : SparseRowEchelonFormAlgorithm

    public data class Parallel(
        val parallelMinSize: Int = 128,
        val parallelChunkSize: Int = 16,
        val parallelism: Int? = null,
    ) : SparseRowEchelonFormAlgorithm {
        init {
            require(parallelMinSize >= 0) { "parallelMinSize must be non-negative" }
            require(parallelChunkSize > 0) { "parallelChunkSize must be positive" }
            require(parallelism == null || parallelism > 0) { "parallelism must be positive" }
        }
    }

    public data object Indexed : SparseRowEchelonFormAlgorithm

    public companion object {
        public val default: SparseRowEchelonFormAlgorithm = Indexed
    }
}

internal fun <S : Scalar> SparseRowEchelonFormAlgorithm.createCalculator(
    field: Field<S>,
    cancellationContext: CancellationContext?,
): SparseRowEchelonFormCalculator<S> {
    return when (this) {
        SparseRowEchelonFormAlgorithm.InPlace -> InPlaceSparseRowEchelonFormCalculator(
            field,
            cancellationContext = cancellationContext,
        )
        is SparseRowEchelonFormAlgorithm.Parallel -> ParallelInPlaceSparseRowEchelonFormCalculator(
            field,
            cancellationContext = cancellationContext,
            parallelConfig = ParallelConfig(
                minSize = this.parallelMinSize,
                chunkSize = this.parallelChunkSize,
                parallelism = this.parallelism,
            ),
        )
        SparseRowEchelonFormAlgorithm.Indexed -> IndexedSparseRowEchelonFormCalculator(
            field,
            cancellationContext = cancellationContext,
        )
    }
}

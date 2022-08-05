package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.PrintableWithSign

public data class DualBasisName<B : BasisName>(public val originalBasisName: B) : BasisName {
    override fun toString(printConfig: PrintConfig): String {
        val originalString = this.originalBasisName.toString(printConfig)
        return when (printConfig.printType) {
            PrintType.PLAIN -> "$originalString*"
            PrintType.TEX -> "$originalString^*"
        }
    }
}

public class DualVectorContext<B : BasisName, S : Scalar, V : NumVector<S>>(
    numVectorSpace: NumVectorSpace<S, V>,
    vectorOperations: VectorOperations<DualBasisName<B>, S, V>,
) : VectorContext<DualBasisName<B>, S, V>(numVectorSpace, vectorOperations) {
    public operator fun Vector<DualBasisName<B>, S, V>.invoke(vector: Vector<B, S, V>): S {
        return this.numVector.dot(vector.numVector)
    }
}

internal fun <B : BasisName, S : PrintableWithSign> InternalPrintConfig<B, S>.dual(
    printConfig: PrintConfig
): InternalPrintConfig<DualBasisName<B>, S> {
    return InternalPrintConfig(
        coeffToString = this.coeffToString,
        basisToString = { dualBasisName ->
            val originalString = this.basisToString(dualBasisName.originalBasisName)
            when (printConfig.printType) {
                PrintType.PLAIN -> "$originalString*"
                PrintType.TEX -> "$originalString^*"
            }
        },
        basisComparator = this.basisComparator?.let { basisComparatorNonNull ->
            compareBy(basisComparatorNonNull) { dualBasisName ->
                dualBasisName.originalBasisName
            }
        },
    )
}

public class DualVectorSpace<B : BasisName, S : Scalar, V : NumVector<S>>(
    public val originalVectorSpace: VectorSpace<B, S, V>
) : VectorSpace<DualBasisName<B>, S, V>(
    numVectorSpace = originalVectorSpace.numVectorSpace,
    basisNames = originalVectorSpace.basisNames.map { DualBasisName(it) },
    getInternalPrintConfig = { printConfig ->
        originalVectorSpace.getInternalPrintConfig(printConfig).dual(printConfig)
    },
) {
    override val context: DualVectorContext<B, S, V> by lazy {
        DualVectorContext(this.numVectorSpace, this)
    }
}

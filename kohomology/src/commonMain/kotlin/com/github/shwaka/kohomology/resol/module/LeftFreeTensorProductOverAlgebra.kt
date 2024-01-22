package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.VectorSpace

// BA does not appear in implementation of this class,
// but it is necessary to extend TensorProductBasisName
public data class LeftFreeTensorProductBasisName<BA : BasisName, BR : BasisName, BVL : BasisName>(
    val rightBasisName: BR,
    val leftGeneratingBasisName: BVL,
) : TensorProductBasisName<BR, FreeModuleBasisName<BA, BVL>> {
    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.TEX))
    }

    override fun toString(printConfig: PrintConfig): String {
        val p = Printer(printConfig)
        val tensorString = when (printConfig.printType) {
            PrintType.TEX -> "\\tensor"
            else -> "*"
        }
        return "${p(this.rightBasisName)} $tensorString ${p(this.leftGeneratingBasisName)}"
    }
}

public interface LeftFreeTensorProductOverAlgebra<
    BA : BasisName,
    BR : BasisName,
    BVL : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : TensorProductOverAlgebra<BA, BR, FreeModuleBasisName<BA, BVL>, S, V, M> {

    public companion object {
        public operator fun <
            BA : BasisName,
            BR : BasisName,
            BVL : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            rightModule: RightModule<BA, BR, S, V, M>,
            leftModule: FreeModule<BA, BVL, S, V, M>,
        ): LeftFreeTensorProductOverAlgebra<BA, BR, BVL, S, V, M> {
            return LeftFreeTensorProductOverAlgebraImpl(rightModule, leftModule)
        }
    }
}

private class LeftFreeTensorProductOverAlgebraImpl<
    BA : BasisName,
    BR : BasisName,
    BVL : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val rightModule: RightModule<BA, BR, S, V, M>,
    override val leftModule: FreeModule<BA, BVL, S, V, M>,
) : LeftFreeTensorProductOverAlgebra<BA, BR, BVL, S, V, M>,
    VectorSpace<TensorProductBasisName<BR, FreeModuleBasisName<BA, BVL>>, S, V> by getVectorSpace(rightModule, leftModule) {

    init {
        require(rightModule.coeffAlgebra == leftModule.coeffAlgebra) {
            "Tensor product is not defined on different coeffAlgebra: " +
                "rightModule.coeffAlgebra=${rightModule.coeffAlgebra} and " +
                "leftModule.coeffAlgebra=${leftModule.coeffAlgebra}"
        }
    }

    companion object {
        private fun <
            BA : BasisName,
            BR : BasisName,
            BVL : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > getVectorSpace(
            rightModule: RightModule<BA, BR, S, V, M>,
            leftModule: FreeModule<BA, BVL, S, V, M>,
        ): VectorSpace<TensorProductBasisName<BR, FreeModuleBasisName<BA, BVL>>, S, V> {
            val basisNames = rightModule.underlyingVectorSpace.basisNames.flatMap { rightBasisName ->
                leftModule.generatingBasisNames.map { leftGeneratingBasisName ->
                    LeftFreeTensorProductBasisName<BA, BR, BVL>(rightBasisName, leftGeneratingBasisName)
                }
            }
            return VectorSpace(rightModule.matrixSpace.numVectorSpace, basisNames)
        }
    }
}

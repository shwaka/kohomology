package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.OpAlgebra
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.LazyBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
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
    > : TensorProductOverAlgebra<BA, BR, FreeModuleBasisName<BA, BVL>, LeftFreeTensorProductBasisName<BA, BR, BVL>, S, V, M> {

    override val leftModule: FreeModule<BA, BVL, S, V, M>

    override fun asPairList(
        vector: Vector<LeftFreeTensorProductBasisName<BA, BR, BVL>, S, V>
    ): List<Pair<Vector<BR, S, V>, Vector<FreeModuleBasisName<BA, BVL>, S, V>>> {
        return vector.toBasisMap().map { (basisName, scalar) ->
            val rightElement = this.rightModule.underlyingVectorSpace.fromBasisName(basisName.rightBasisName)
            val leftElement = this.leftModule.fromGeneratingBasisName(basisName.leftGeneratingBasisName)
            val leftElementMultiplied = this.leftModule.context.run {
                scalar * leftElement
            }
            Pair(rightElement, leftElementMultiplied)
        }
    }

    public companion object {
        public operator fun <
            BA : BasisName,
            BR : BasisName,
            BVL : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            rightModule: Module<BA, BR, S, V, M>,
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
    override val rightModule: Module<BA, BR, S, V, M>,
    override val leftModule: FreeModule<BA, BVL, S, V, M>,
) : LeftFreeTensorProductOverAlgebra<BA, BR, BVL, S, V, M>,
    VectorSpace<LeftFreeTensorProductBasisName<BA, BR, BVL>, S, V> by getVectorSpace(rightModule, leftModule) {

    init {
        val rightCoeffAlgebra = rightModule.coeffAlgebra
        require(rightCoeffAlgebra is OpAlgebra<*, *, *, *>) {
            "rightModule must be a module over an instance of OpAlgebra"
        }
        rightCoeffAlgebra as OpAlgebra<BA, S, V, M>
        require(rightCoeffAlgebra.isOppositeOf(leftModule.coeffAlgebra)) {
            "Tensor product is not defined since " +
                "rightModule.coeffAlgebra=${rightModule.coeffAlgebra} is not the opposite of " +
                "leftModule.coeffAlgebra=${leftModule.coeffAlgebra}"
        }
    }

    override val tensorProductMap: BilinearMap<BR, FreeModuleBasisName<BA, BVL>, LeftFreeTensorProductBasisName<BA, BR, BVL>, S, V, M> by lazy {
        LazyBilinearMap(
            source1 = this.rightModule.underlyingVectorSpace,
            source2 = this.leftModule.underlyingVectorSpace,
            target = this,
            matrixSpace = this.rightModule.matrixSpace,
        ) { rightBasisName, leftBasisName ->
            val algebraBasisName = leftBasisName.algebraBasisName
            val leftGeneratingBasisName = leftBasisName.generatingBasisName
            val rightElement = this.rightModule.underlyingVectorSpace.fromBasisName(rightBasisName)
            val algebraElement = this.leftModule.coeffAlgebra.fromBasisName(algebraBasisName)
            val rightElementMultiplied = this.rightModule.context.run {
                // left multiplication of an element of A^op
                // = right multiplication of an element of A
                algebraElement * rightElement
            }
            this.context.run {
                rightElementMultiplied.toBasisMap().map { (rightBasisNameOfSummand, scalar) ->
                    val basisName = LeftFreeTensorProductBasisName<BA, BR, BVL>(
                        rightBasisNameOfSummand,
                        leftGeneratingBasisName,
                    )
                    this@LeftFreeTensorProductOverAlgebraImpl.fromBasisName(basisName) * scalar
                }.sum()
            }
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
            rightModule: Module<BA, BR, S, V, M>,
            leftModule: FreeModule<BA, BVL, S, V, M>,
        ): VectorSpace<LeftFreeTensorProductBasisName<BA, BR, BVL>, S, V> {
            val basisNames = rightModule.underlyingVectorSpace.basisNames.flatMap { rightBasisName ->
                leftModule.generatingBasisNames.map { leftGeneratingBasisName ->
                    LeftFreeTensorProductBasisName<BA, BR, BVL>(rightBasisName, leftGeneratingBasisName)
                }
            }
            return VectorSpace(rightModule.matrixSpace.numVectorSpace, basisNames)
        }
    }
}

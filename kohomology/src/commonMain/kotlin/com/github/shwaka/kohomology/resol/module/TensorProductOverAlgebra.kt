package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface TensorProductBasisName<BR : BasisName, BL : BasisName> : BasisName

public interface TensorProductOverAlgebra<
    BA : BasisName,
    BR : BasisName,
    BL : BasisName,
    BT : TensorProductBasisName<BR, BL>,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : VectorSpace<BT, S, V> {

    public val rightModule: Module<BA, BR, S, V, M>
    public val leftModule: Module<BA, BL, S, V, M>
    public val tensorProductMap: BilinearMap<BR, BL, BT, S, V, M>
    public fun asPairList(vector: Vector<BT, S, V>): List<Pair<Vector<BR, S, V>, Vector<BL, S, V>>>
    public fun <BRT : BasisName, BLT : BasisName, BTT : TensorProductBasisName<BRT, BLT>> inducedMapOf(
        target: TensorProductOverAlgebra<BA, BRT, BLT, BTT, S, V, M>,
        rightModuleMap: ModuleMap<BA, BR, BRT, S, V, M>,
        leftModuleMap: ModuleMap<BA, BL, BLT, S, V, M>,
    ): LinearMap<BT, BTT, S, V, M> {
        val vectors = this.basisNames.map { basisName ->
            val vector = this.fromBasisName(basisName)
            val productList = this.asPairList(vector).map { (rightVector, leftVector) ->
                target.tensorProductMap(
                    rightModuleMap(rightVector),
                    leftModuleMap(leftVector),
                )
            }
            target.context.run { productList.sum() }
        }
        return LinearMap.fromVectors(
            source = this,
            target = target,
            matrixSpace = this.rightModule.matrixSpace,
            vectors = vectors,
        )
    }
}

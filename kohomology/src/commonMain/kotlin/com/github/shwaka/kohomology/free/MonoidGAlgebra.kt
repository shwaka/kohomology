package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

private class MonoidGAlgebraFactory<E : MonoidElement, Mon : Monoid<E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val monoid: Mon,
    val name: String,
) {
    private fun getBasisNames(degree: Degree): List<E> {
        return this.monoid.listAll(degree)
    }

    fun getVectorSpace(degree: Degree): VectorSpace<E, S, V> {
        return VectorSpace(this.matrixSpace.numVectorSpace, this.getBasisNames(degree))
    }

    fun getMultiplication(p: Degree, q: Degree): BilinearMap<E, E, E, S, V, M> {
        val source1 = this.getVectorSpace(p)
        val source2 = this.getVectorSpace(q)
        val target = this.getVectorSpace(p + q)
        val values = source1.basisNames.map { monoidElement1 ->
            source2.basisNames.map { monoidElement2 ->
                this.monoid.multiply(monoidElement1, monoidElement2).let { maybeZero ->
                    when (maybeZero) {
                        is Zero -> target.zeroVector
                        is NonZero -> {
                            val (monoidElement: E, sign: Sign) = maybeZero.value
                            target.fromBasisName(monoidElement, sign)
                        }
                    }
                }
            }
        }
        return BilinearMap.fromVectors(source1, source2, target, this.matrixSpace, values)
    }

    val unitVector: Vector<E, S, V> = this.getVectorSpace(0).fromBasisName(this.monoid.unit)
}

open class MonoidGAlgebra<E : MonoidElement, Mon : Monoid<E>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: MonoidGAlgebraFactory<E, Mon, S, V, M>,
) : GAlgebra<E, S, V, M>(
    factory.matrixSpace,
    factory.name,
    factory::getVectorSpace,
    factory::getMultiplication,
    factory.unitVector
) {
    val monoid: Mon = factory.monoid

    constructor(matrixSpace: MatrixSpace<S, V, M>, monoid: Mon, name: String) : this(
        MonoidGAlgebraFactory(matrixSpace, monoid, name),
    )
}

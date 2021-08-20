package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public open class GLinearMapWithDegreeChange<DS : Degree, BS : BasisName, DT : Degree, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val source: GVectorSpace<DS, BS, S, V>,
    public val target: GVectorSpace<DT, BT, S, V>,
    public val degreeMorphism: DegreeMorphism<DS, DT>,
    public val matrixSpace: MatrixSpace<S, V, M>,
    public val name: String,
    private val getValueOnBasis: (BS, DS) -> GVector<DT, BT, S, V>,
) {
    public companion object {
        public operator fun <DS : Degree, BS : BasisName, DT : Degree, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: GVectorSpace<DS, BS, S, V>,
            target: GVectorSpace<DT, BT, S, V>,
            degreeMorphism: DegreeMorphism<DS, DT>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            basisMap: (BS) -> BT,
        ): GLinearMapWithDegreeChange<DS, BS, DT, BT, S, V, M> {
            return GLinearMapWithDegreeChange(source, target, degreeMorphism, matrixSpace, name) { basisName, degree ->
                val targetBasisName = basisMap(basisName)
                val targetDegree = degreeMorphism(degree)
                target.fromBasisName(targetBasisName, targetDegree)
            }
        }
    }

    public operator fun invoke(gVector: GVector<DS, BS, S, V>): GVector<DT, BT, S, V> {
        if (gVector !in this.source)
            throw IllegalContextException("Invalid graded vector is given as an argument for a graded linear map with degree change")
        val targetDegree = this.degreeMorphism(gVector.degree)
        return this.target.context.run {
            gVector.vector.toBasisMap().toList().map { (basisName, coeff) ->
                this@GLinearMapWithDegreeChange.getValueOnBasis(basisName, gVector.degree) * coeff
            }.fold(this@GLinearMapWithDegreeChange.target.getZero(targetDegree)) { acc, gVector ->
                acc + gVector
            }
        }
    }

    override fun toString(): String {
        return this.name
    }
}

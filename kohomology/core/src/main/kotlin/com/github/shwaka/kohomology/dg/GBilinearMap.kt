package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Degree
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorSpace

class GBilinearMap<BS1, BS2, BT, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    val source1: GVectorSpace<BS1, S, V>,
    val source2: GVectorSpace<BS2, S, V>,
    val target: GVectorSpace<BT, S, V>,
    val degree: Degree,
    private val getBilinearMap: (Degree, Degree) -> BilinearMap<BS1, BS2, BT, S, V, M>,
) {
    operator fun invoke(gVector1: GVector<BS1, S, V>, gVector2: GVector<BS2, S, V>): GVector<BT, S, V> {
        if (gVector1.gVectorSpace != this.source1)
            throw IllegalArgumentException("Invalid graded vector is given as an argument for a graded bilinear map")
        if (gVector2.gVectorSpace != this.source2)
            throw IllegalArgumentException("Invalid graded vector is given as an argument for a graded bilinear map")
        val bilinearMap = this.getBilinearMap(gVector1.degree, gVector2.degree)
        if (gVector1.vector.vectorSpace != bilinearMap.source1)
            throw Exception("Graded bilinear map contains a bug: getBilinearMap returns incorrect linear map")
        if (gVector2.vector.vectorSpace != bilinearMap.source2)
            throw Exception("Graded bilinear map contains a bug: getBilinearMap returns incorrect linear map")
        val newVector = bilinearMap(gVector1.vector, gVector2.vector)
        val newDegree = gVector1.degree + gVector2.degree + this.degree
        return this.target.fromVector(newVector, newDegree)
    }
}

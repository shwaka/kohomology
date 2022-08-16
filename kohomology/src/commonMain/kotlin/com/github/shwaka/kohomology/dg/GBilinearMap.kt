package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap

public class GBilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val source1: GVectorSpace<D, BS1, S, V>,
    public val source2: GVectorSpace<D, BS2, S, V>,
    public val target: GVectorSpace<D, BT, S, V>,
    public val degree: D,
    public val name: String,
    private val getBilinearMap: (D, D) -> BilinearMap<BS1, BS2, BT, S, V, M>,
) {
    private val cache: MutableMap<Pair<D, D>, BilinearMap<BS1, BS2, BT, S, V, M>> = mutableMapOf()
    public val degreeGroup: DegreeGroup<D> = source1.degreeGroup

    public constructor(
        source1: GVectorSpace<D, BS1, S, V>,
        source2: GVectorSpace<D, BS2, S, V>,
        target: GVectorSpace<D, BT, S, V>,
        degree: Int,
        name: String,
        getBilinearMap: (D, D) -> BilinearMap<BS1, BS2, BT, S, V, M>,
    ) : this(source1, source2, target, source1.degreeGroup.fromInt(degree), name, getBilinearMap)

    public companion object {
        public fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withIntDegree(
            source1: GVectorSpace<IntDegree, BS1, S, V>,
            source2: GVectorSpace<IntDegree, BS2, S, V>,
            target: GVectorSpace<IntDegree, BT, S, V>,
            degree: Int,
            name: String,
            getBilinearMap: (Int, Int) -> BilinearMap<BS1, BS2, BT, S, V, M>,
        ): GBilinearMap<BS1, BS2, BT, IntDegree, S, V, M> {
            return GBilinearMap(source1, source2, target, IntDegree(degree), name) { p, q ->
                getBilinearMap(p.value, q.value)
            }
        }

        public fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            matrixSpace: MatrixSpace<S, V, M>,
            source1: GVectorSpace<D, BS1, S, V>,
            source2: GVectorSpace<D, BS2, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: D,
        ): GBilinearMap<BS1, BS2, BT, D, S, V, M> {
            return GBilinearMap(source1, source2, target, degree, "0") { p: D, q: D ->
                val targetDegree = target.degreeGroup.context.run {
                    p + q + degree
                }
                BilinearMap.getZero(source1[p], source2[q], target[targetDegree], matrixSpace)
            }
        }
    }

    public operator fun get(p: D, q: D): BilinearMap<BS1, BS2, BT, S, V, M> {
        this.cache[Pair(p, q)]?.let {
            // if cache exists
            return it
        }
        // if cache does not exists
        val bilinearMap = this.getBilinearMap(p, q)
        this.cache[Pair(p, q)] = bilinearMap
        return bilinearMap
    }

    public operator fun invoke(gVector1: GVector<D, BS1, S, V>, gVector2: GVector<D, BS2, S, V>): GVector<D, BT, S, V> {
        require(gVector1.gVectorSpace.underlyingGVectorSpace == this.source1.underlyingGVectorSpace) {
            "Cannot compute the value of the bilinear map $this; " +
                "the first argument $gVector1 should be an element of ${this.source1}"
        }
        require(gVector2.gVectorSpace.underlyingGVectorSpace == this.source2.underlyingGVectorSpace) {
            "Cannot compute the value of the bilinear map $this; " +
                "the second argument $gVector2 should be an element of ${this.source2}"
        }
        val bilinearMap = this[gVector1.degree, gVector2.degree]
        require(gVector1.vector.vectorSpace == bilinearMap.source1) {
            "Graded bilinear map contains a bug: getBilinearMap returns incorrect linear map"
        }
        require(gVector2.vector.vectorSpace == bilinearMap.source2) {
            "Graded bilinear map contains a bug: getBilinearMap returns incorrect linear map"
        }
        val newVector = bilinearMap(gVector1.vector, gVector2.vector)
        val newDegree = this.degreeGroup.context.run {
            gVector1.degree + gVector2.degree + this@GBilinearMap.degree
        }
        return this.target.fromVector(newVector, newDegree)
    }

    override fun toString(): String {
        return this.name
    }
}

package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import mu.KotlinLogging

class GBilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val source1: GVectorSpace<D, BS1, S, V>,
    val source2: GVectorSpace<D, BS2, S, V>,
    val target: GVectorSpace<D, BT, S, V>,
    val degree: D,
    val name: String,
    private val getBilinearMap: (D, D) -> BilinearMap<BS1, BS2, BT, S, V, M>,
) {
    private val cache: MutableMap<Pair<D, D>, BilinearMap<BS1, BS2, BT, S, V, M>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}
    val degreeMonoid = source1.degreeGroup

    constructor(
        source1: GVectorSpace<D, BS1, S, V>,
        source2: GVectorSpace<D, BS2, S, V>,
        target: GVectorSpace<D, BT, S, V>,
        degree: Int,
        name: String,
        getBilinearMap: (D, D) -> BilinearMap<BS1, BS2, BT, S, V, M>,
    ) : this(source1, source2, target, source1.degreeGroup.fromInt(degree), name, getBilinearMap)

    companion object {
        fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withIntDegree(
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
    }

    operator fun get(p: D, q: D): BilinearMap<BS1, BS2, BT, S, V, M> {
        this.cache[Pair(p, q)]?.let {
            // if cache exists
            this.logger.debug { "cache found for $this[$p, $q]" }
            return it
        }
        // if cache does not exists
        this.logger.debug { "cache not found for $this[$p, $q], create new instance" }
        val bilinearMap = this.getBilinearMap(p, q)
        this.cache[Pair(p, q)] = bilinearMap
        return bilinearMap
    }

    operator fun invoke(gVector1: GVector<D, BS1, S, V>, gVector2: GVector<D, BS2, S, V>): GVector<D, BT, S, V> {
        if (gVector1.gVectorSpace != this.source1)
            throw IllegalContextException("Invalid graded vector is given as an argument for a graded bilinear map")
        if (gVector2.gVectorSpace != this.source2)
            throw IllegalContextException("Invalid graded vector is given as an argument for a graded bilinear map")
        val bilinearMap = this[gVector1.degree, gVector2.degree]
        if (gVector1.vector.vectorSpace != bilinearMap.source1)
            throw Exception("Graded bilinear map contains a bug: getBilinearMap returns incorrect linear map")
        if (gVector2.vector.vectorSpace != bilinearMap.source2)
            throw Exception("Graded bilinear map contains a bug: getBilinearMap returns incorrect linear map")
        val newVector = bilinearMap(gVector1.vector, gVector2.vector)
        val newDegree = this.degreeMonoid.context.run {
            gVector1.degree + gVector2.degree + this@GBilinearMap.degree
        }
        return this.target.fromVector(newVector, newDegree)
    }

    override fun toString(): String {
        return this.name
    }
}

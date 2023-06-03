package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.QuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public class GBilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val matrixSpace: MatrixSpace<S, V, M>,
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
        matrixSpace: MatrixSpace<S, V, M>,
        source1: GVectorSpace<D, BS1, S, V>,
        source2: GVectorSpace<D, BS2, S, V>,
        target: GVectorSpace<D, BT, S, V>,
        degree: Int,
        name: String,
        getBilinearMap: (D, D) -> BilinearMap<BS1, BS2, BT, S, V, M>,
    ) : this(matrixSpace, source1, source2, target, source1.degreeGroup.fromInt(degree), name, getBilinearMap)

    public companion object {
        public fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withIntDegree(
            matrixSpace: MatrixSpace<S, V, M>,
            source1: GVectorSpace<IntDegree, BS1, S, V>,
            source2: GVectorSpace<IntDegree, BS2, S, V>,
            target: GVectorSpace<IntDegree, BT, S, V>,
            degree: Int,
            name: String,
            getBilinearMap: (Int, Int) -> BilinearMap<BS1, BS2, BT, S, V, M>,
        ): GBilinearMap<BS1, BS2, BT, IntDegree, S, V, M> {
            return GBilinearMap(matrixSpace, source1, source2, target, IntDegree(degree), name) { p, q ->
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
            return GBilinearMap(matrixSpace, source1, source2, target, degree, "0") { p: D, q: D ->
                val targetDegree = target.degreeGroup.context.run {
                    p + q + degree
                }
                BilinearMap.getZero(source1[p], source2[q], target[targetDegree], matrixSpace)
            }
        }
    }

    public operator fun get(p: D, q: D): BilinearMap<BS1, BS2, BT, S, V, M> {
        return this.cache.getOrPut(Pair(p, q)) {
            this.getBilinearMap(p, q)
        }
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
            "Graded bilinear map contains a bug: " +
                "gVector1.vector is an element of ${gVector1.vector.vectorSpace}, " +
                "but bilinearMap.source1 is ${bilinearMap.source1}"
        }
        require(gVector2.vector.vectorSpace == bilinearMap.source2) {
            "Graded bilinear map contains a bug: " +
                "gVector2.vector is an element of ${gVector2.vector.vectorSpace}, " +
                "but bilinearMap.source2 is ${bilinearMap.source2}"
        }
        val newVector = bilinearMap(gVector1.vector, gVector2.vector)
        val newDegree = this.degreeGroup.context.run {
            gVector1.degree + gVector2.degree + this@GBilinearMap.degree
        }
        return this.target.fromVector(newVector, newDegree)
    }

    public fun induce(
        source1Quot: QuotGVectorSpace<D, BS1, S, V, M>,
        source2Quot: QuotGVectorSpace<D, BS2, S, V, M>,
        targetQuot: QuotGVectorSpace<D, BT, S, V, M>,
    ): GBilinearMap<
        QuotBasis<BS1, S, V>,
        QuotBasis<BS2, S, V>,
        QuotBasis<BT, S, V>,
        D, S, V, M
        > {
        return GBilinearMap(
            this.matrixSpace,
            source1Quot,
            source2Quot,
            targetQuot,
            this.degree,
            this.name,
        ) { p, q ->
            val pPlusQ = this.degreeGroup.context.run { p + q }
            this[p, q].induce(
                source1Quot[p],
                source2Quot[q],
                targetQuot[pPlusQ],
            )
        }
    }

    public fun induce(
        source1SubQuot: SubQuotGVectorSpace<D, BS1, S, V, M>,
        source2SubQuot: SubQuotGVectorSpace<D, BS2, S, V, M>,
        targetSubQuot: SubQuotGVectorSpace<D, BT, S, V, M>,
    ): GBilinearMap<
        SubQuotBasis<BS1, S, V>,
        SubQuotBasis<BS2, S, V>,
        SubQuotBasis<BT, S, V>,
        D, S, V, M
        > {
        return GBilinearMap(
            this.matrixSpace,
            source1SubQuot,
            source2SubQuot,
            targetSubQuot,
            this.degree,
            this.name,
        ) { p, q ->
            val pPlusQ = this.degreeGroup.context.run { p + q }
            this[p, q].induce(
                source1SubQuot[p],
                source2SubQuot[q],
                targetSubQuot[pPlusQ],
            )
        }
    }

    public fun image(
        source1Sub: SubGVectorSpace<D, BS1, S, V, M> = this.source1.asSubGVectorSpace(this.matrixSpace),
        source2Sub: SubGVectorSpace<D, BS2, S, V, M> = this.source2.asSubGVectorSpace(this.matrixSpace),
    ): SubGVectorSpace<D, BT, S, V, M> {
        if (this.degreeGroup !is AugmentedDegreeGroup) {
            throw UnsupportedOperationException(
                "GBilinearMap.image can be computed " +
                    "only when its degreeGroup is an instance of AugmentedDegreeGroup"
            )
        }

        return SubGVectorSpace(
            this.matrixSpace,
            this.target,
            "Im(${this.name}",
        ) { targetDegree ->
            val sourceDegree = this.degreeGroup.context.run {
                targetDegree - this@GBilinearMap.degree
            }
            val degreePairList: List<Pair<D, D>> = Boundedness.listDegreePairsOfSum(
                this.degreeGroup,
                sourceDegree,
                source1Sub.boundedness,
                source2Sub.boundedness,
            )
            val generator: List<Vector<BT, S, V>> = degreePairList.map { (p, q) ->
                this[p, q].image(source1Sub[p], source2Sub[q]).generator
            }.flatten()
            SubVectorSpace(
                this.matrixSpace,
                this.target[targetDegree],
                generator,
            )
        }
    }

    override fun toString(): String {
        return this.name
    }
}

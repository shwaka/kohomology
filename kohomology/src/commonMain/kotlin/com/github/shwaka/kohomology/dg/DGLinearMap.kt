package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface DGLinearMap<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GLinearMap<D, BS, BT, S, V, M> {
    // We cannot move these declarations to the primary constructor
    // If we move them, get the warning "accessing non-final property in constructor"
    override val source: DGVectorSpace<D, BS, S, V, M>
    override val target: DGVectorSpace<D, BT, S, V, M>
    public val inducedMapOnCohomology: GLinearMap<D, SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M>

    public operator fun plus(other: DGLinearMap<D, BS, BT, S, V, M>): DGLinearMap<D, BS, BT, S, V, M> {
        require(this.source == other.source) { "DGLinear maps with different sources cannot be added" }
        require(this.target == other.target) { "DGLinear maps with different targets cannot be added" }
        require(this.degree == other.degree) { "DGLinear maps with different degrees cannot be added" }
        return DGLinearMap(
            source = this.source,
            target = this.target,
            gLinearMap = super.plus(other), // calls GLinearMap.plus(other: GLinearMap)
        )
    }

    public operator fun <BR : BasisName> times(other: DGLinearMap<D, BR, BS, S, V, M>): DGLinearMap<D, BR, BT, S, V, M> {
        require(other.target == this.source) {
            "Cannot composite dg linear maps since the source of $this and the target of $other are different"
        }
        return DGLinearMap(
            source = other.source,
            target = this.target,
            gLinearMap = super.times(other), // calls GLinearMap.plus(other: GLinearMap)
        )
    }

    /**
     * Find a lift of [targetCocycle] along a *surjective* quasi-isomorphism.
     *
     * Given f: A→B and b=[targetCocycle]∈B satisfying d(b)=0.
     * Then this method returns a∈A such that d(a)=0 and f(a)=b.
     */
    public fun findCocycleLift(targetCocycle: GVector<D, BT, S, V>): GVector<D, BS, S, V> {
        val degree = targetCocycle.degree
        require(this.target.differential(targetCocycle).isZero()) { "$targetCocycle is not a cocycle" }
        val targetClass = this.target.cohomologyClassOf(targetCocycle)
        val sourceClass = this.inducedMapOnCohomology.findPreimage(targetClass)
            ?: throw UnsupportedOperationException("H^$degree($this) is not surjective")
        val sourceCocycle = this.source.cocycleRepresentativeOf(sourceClass)
        val coboundary = this.target.context.run {
            this@DGLinearMap(sourceCocycle) - targetCocycle
        }
        val targetDifference = this.target.differential.findPreimage(coboundary)
            ?: throw Exception(
                "This can't happen since the element is the difference of two cocycles with the same cohomology class"
            )
        val sourceDifference = this.findPreimage(targetDifference)
            ?: throw UnsupportedOperationException(
                "$this[${this.degreeGroup.context.run { degree - 1 }}] is not surjective"
            )
        return this.source.context.run {
            sourceCocycle - d(sourceDifference)
        }
    }

    /**
     * Find a lift of [targetCochain] along a *surjective* quasi-isomorphism which bounds [sourceCocycle].
     *
     * Given f: A→B, a=[sourceCocycle]∈A and b=[targetCochain]∈B satisfying f(a)=d(b).
     * Then this method returns a'∈A such that d(a')=a and f(a')=b.
     */
    public fun findLift(targetCochain: GVector<D, BT, S, V>, sourceCocycle: GVector<D, BS, S, V>): GVector<D, BS, S, V> {
        require(sourceCocycle.degree == this.degreeGroup.context.run { targetCochain.degree + 1 }) {
            "deg($sourceCocycle) should be equal to deg($targetCochain) + 1"
        }
        require(this.source.differential(sourceCocycle).isZero()) {
            "$sourceCocycle is not a cocycle"
        }
        require(this(sourceCocycle) == this.target.differential(targetCochain)) {
            "$sourceCocycle and $targetCochain are not compatible: the image of $sourceCocycle must be equal to d($targetCochain)"
        }
        val sourceCochain = this.source.differential.findPreimage(sourceCocycle)
            ?: throw UnsupportedOperationException(
                "H^${sourceCocycle.degree}($this) is not injective"
            )
        val targetCocycle = this.target.context.run {
            targetCochain - this@DGLinearMap(sourceCochain)
        }
        val cocycleLift = this.findCocycleLift(targetCocycle)
        return this.source.context.run {
            sourceCochain + cocycleLift
        }
    }

    public data class LiftWithBoundingCochain<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>>(
        val lift: GVector<D, BS, S, V>,
        val boundingCochain: GVector<D, BT, S, V>,
    )

    /**
     * Find a homotopy lift of [targetCocycle] along a (non-surjective) quasi-isomorphism.
     *
     * Given f: A→B and b=[targetCocycle]∈B satisfying d(b)=0.
     * Then this method returns a∈A and b'∈B such that d(a)=0 and f(a)-b=d(b').
     */
    public fun findCocycleLiftUpToHomotopy(targetCocycle: GVector<D, BT, S, V>): LiftWithBoundingCochain<D, BS, BT, S, V> {
        val degree = targetCocycle.degree
        require(this.target.differential(targetCocycle).isZero()) { "$targetCocycle is not a cocycle" }
        val targetClass = this.target.cohomologyClassOf(targetCocycle)
        val sourceClass = this.inducedMapOnCohomology.findPreimage(targetClass)
            ?: throw UnsupportedOperationException("H^$degree($this) is not surjective")
        val sourceCocycle = this.source.cocycleRepresentativeOf(sourceClass)
        val coboundary = this.target.context.run {
            this@DGLinearMap(sourceCocycle) - targetCocycle
        }
        val targetDifference = this.target.differential.findPreimage(coboundary)
            ?: throw Exception(
                "This can't happen since the element is the difference of two cocycles with the same cohomology class."
            )
        return LiftWithBoundingCochain(sourceCocycle, targetDifference)
    }

    /**
     * Find a homotopy lift of [targetCochain] along a (non-surjective) quasi-isomorphism which bounds [sourceCocycle].
     *
     * Given f: A→B, a=[sourceCocycle]∈A and b=[targetCochain]∈B satisfying f(a)=d(b).
     * Then this method returns a'∈A and b'∈B such that d(a')=a and f(a')-b=d(b').
     */
    public fun findLiftUpToHomotopy(targetCochain: GVector<D, BT, S, V>, sourceCocycle: GVector<D, BS, S, V>): LiftWithBoundingCochain<D, BS, BT, S, V> {
        require(sourceCocycle.degree == this.degreeGroup.context.run { targetCochain.degree + 1 }) {
            "deg($sourceCocycle) should be equal to deg($targetCochain) + 1"
        }
        require(this.source.differential(sourceCocycle).isZero()) {
            "$sourceCocycle is not a cocycle"
        }
        require(this(sourceCocycle) == this.target.differential(targetCochain)) {
            "$sourceCocycle and $targetCochain are not compatible: the image of $sourceCocycle must be equal to d($targetCochain)"
        }
        val sourceCochain = this.source.differential.findPreimage(sourceCocycle)
            ?: throw UnsupportedOperationException(
                "H^${sourceCocycle.degree}($this) is not injective"
            )
        val targetCocycle = this.target.context.run {
            targetCochain - this@DGLinearMap(sourceCochain)
        }
        val (lift, boundingCochain) = this.findCocycleLiftUpToHomotopy(targetCocycle)
        return LiftWithBoundingCochain(
            this.source.context.run { sourceCochain + lift },
            boundingCochain,
        )
    }

    public companion object {
        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGVectorSpace<D, BS, S, V, M>,
            target: DGVectorSpace<D, BT, S, V, M>,
            gLinearMap: GLinearMap<D, BS, BT, S, V, M>,
        ): DGLinearMap<D, BS, BT, S, V, M> {
            return DGLinearMapImpl(source, target, gLinearMap)
        }

        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGVectorSpace<D, BS, S, V, M>,
            target: DGVectorSpace<D, BT, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
        ): DGLinearMap<D, BS, BT, S, V, M> {
            val gLinearMap = GLinearMap(source, target, degree, matrixSpace, name, getLinearMap)
            return DGLinearMap(source, target, gLinearMap)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGVectorSpace<D, BS, S, V, M>,
            target: DGVectorSpace<D, BT, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): DGLinearMap<D, BS, BT, S, V, M> {
            val gLinearMap = GLinearMap.fromGVectors(source, target, degree, matrixSpace, name, getGVectors)
            return DGLinearMap(source, target, gLinearMap)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            source: DGVectorSpace<D, BS, S, V, M>,
            target: DGVectorSpace<D, BT, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
        ): DGLinearMap<D, BS, BT, S, V, M> {
            val gLinearMap = GLinearMap.getZero(matrixSpace, source, target, degree)
            return DGLinearMap(source, target, gLinearMap)
        }
    }
}

private class DGLinearMapImpl<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: DGVectorSpace<D, BS, S, V, M>,
    override val target: DGVectorSpace<D, BT, S, V, M>,
    gLinearMap: GLinearMap<D, BS, BT, S, V, M>,
) : DGLinearMap<D, BS, BT, S, V, M>,
    GLinearMap<D, BS, BT, S, V, M> by gLinearMap {
    init {
        require(source.underlyingGVectorSpace == gLinearMap.source.underlyingGVectorSpace) {
            "The source DGVectorSpace does not match to the source GVectorSpace of GLinearMap"
        }
        require(target.underlyingGVectorSpace == gLinearMap.target.underlyingGVectorSpace) {
            "The target DGVectorSpace does not match to the target GVectorSpace of GLinearMap"
        }
    }

    override val inducedMapOnCohomology: GLinearMap<D, SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M> by lazy {
        this.induce(this.source.cohomology, this.target.cohomology)
    }
}

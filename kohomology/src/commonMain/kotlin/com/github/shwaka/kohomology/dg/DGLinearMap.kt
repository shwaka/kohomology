package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.IntDeg
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

open class DGLinearMap<BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: DGVectorSpace<BS, S, V, M>,
    target: DGVectorSpace<BT, S, V, M>,
    gLinearMap: GLinearMap<BS, BT, S, V, M>,
) {
    init {
        if (source.gVectorSpace != gLinearMap.source)
            throw IllegalArgumentException("The source DGVectorSpace does not match to the source GVectorSpace of GLinearMap")
        if (target.gVectorSpace != gLinearMap.target)
            throw IllegalArgumentException("The target DGVectorSpace does not match to the target GVectorSpace of GLinearMap")
    }

    // We cannot move these declarations to the primary constructor
    // If we move them, get the warning "accessing non-final property in constructor"
    open val source: DGVectorSpace<BS, S, V, M> = source
    open val target: DGVectorSpace<BT, S, V, M> = target
    open val gLinearMap: GLinearMap<BS, BT, S, V, M> = gLinearMap
    val degree = gLinearMap.degree
    val matrixSpace = gLinearMap.matrixSpace

    operator fun invoke(gVector: GVector<BS, S, V>): GVector<BT, S, V> {
        return this.gLinearMap(gVector)
    }

    fun inducedMapOnCohomology(): GLinearMap<SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        val getGVectors: (IntDeg) -> List<GVector<SubQuotBasis<BT, S, V>, S, V>> = { k ->
            this.source.cohomology.getBasis(k).map { cohomologyClass ->
                val cocycle = this.source.cocycleRepresentativeOf(cohomologyClass)
                this.target.cohomologyClassOf(this.gLinearMap(cocycle))
            }
        }
        val newName = "H^*(${this.gLinearMap.name})"
        return GLinearMap.fromGVectors(
            this.source.cohomology,
            this.target.cohomology,
            this.degree,
            this.matrixSpace,
            newName,
            getGVectors
        )
    }

    fun findCocycleLift(targetCocycle: GVector<BT, S, V>): GVector<BS, S, V> {
        val degree = targetCocycle.degree
        if (this.target.differential(targetCocycle).isNotZero())
            throw IllegalArgumentException("$targetCocycle is not a cocycle")
        val targetClass = this.target.cohomologyClassOf(targetCocycle)
        val sourceClass = this.inducedMapOnCohomology().findPreimage(targetClass)
            ?: throw UnsupportedOperationException("H^$degree($this) is not surjective")
        val sourceCocycle = this.source.cocycleRepresentativeOf(sourceClass)
        val coboundary = this.target.context.run {
            this@DGLinearMap.gLinearMap(sourceCocycle) - targetCocycle
        }
        val targetDifference = this.target.differential.findPreimage(coboundary)
            ?: throw Exception("This can't happen!")
        val sourceDifference = this.gLinearMap.findPreimage(targetDifference)
            ?: throw UnsupportedOperationException("$this[${degree - 1}] is not surjective")
        return this.source.context.run {
            sourceCocycle - d(sourceDifference)
        }
    }

    fun findLift(targetCochain: GVector<BT, S, V>, sourceCoboundary: GVector<BS, S, V>): GVector<BS, S, V> {
        if (sourceCoboundary.degree != targetCochain.degree + 1)
            throw IllegalArgumentException("deg($sourceCoboundary) should be equal to deg($targetCochain) + 1")
        if (this.source.differential(sourceCoboundary).isNotZero())
            throw IllegalArgumentException("$sourceCoboundary is not a cocycle")
        if (this.gLinearMap(sourceCoboundary) != this.target.differential(targetCochain))
            throw IllegalArgumentException(
                "$sourceCoboundary and $targetCochain are not compatible: the image of $sourceCoboundary must be equal to d($targetCochain)"
            )
        val sourceCochain = this.source.differential.findPreimage(sourceCoboundary)
            ?: throw Exception("This can't happen!")
        val targetCocycle = this.target.context.run {
            targetCochain - this@DGLinearMap.gLinearMap(sourceCochain)
        }
        val sourceCocycle = this.findCocycleLift(targetCocycle)
        return this.source.context.run {
            sourceCochain + sourceCocycle
        }
    }

    companion object {
        operator fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGVectorSpace<BS, S, V, M>,
            target: DGVectorSpace<BT, S, V, M>,
            degree: IntDeg,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (IntDeg) -> LinearMap<BS, BT, S, V, M>
        ): DGLinearMap<BS, BT, S, V, M> {
            val gLinearMap = GLinearMap(source.gVectorSpace, target.gVectorSpace, degree, matrixSpace, name, getLinearMap)
            return DGLinearMap(source, target, gLinearMap)
        }

        fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGVectorSpace<BS, S, V, M>,
            target: DGVectorSpace<BT, S, V, M>,
            degree: IntDeg,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (IntDeg) -> List<GVector<BT, S, V>>
        ): DGLinearMap<BS, BT, S, V, M> {
            val gLinearMap = GLinearMap.fromGVectors(source.gVectorSpace, target.gVectorSpace, degree, matrixSpace, name, getGVectors)
            return DGLinearMap(source, target, gLinearMap)
        }
    }
}

class DGAlgebraMap<BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: DGAlgebra<BS, S, V, M>,
    override val target: DGAlgebra<BT, S, V, M>,
    override val gLinearMap: GAlgebraMap<BS, BT, S, V, M>,
) : DGLinearMap<BS, BT, S, V, M>(source, target, gLinearMap) {
    companion object {
        operator fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<BS, S, V, M>,
            target: DGAlgebra<BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (IntDeg) -> LinearMap<BS, BT, S, V, M>
        ): DGAlgebraMap<BS, BT, S, V, M> {
            val gLinearMap = GAlgebraMap(source.gAlgebra, target.gAlgebra, matrixSpace, name, getLinearMap)
            return DGAlgebraMap(source, target, gLinearMap)
        }

        fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGAlgebra<BS, S, V, M>,
            target: DGAlgebra<BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (IntDeg) -> List<GVector<BT, S, V>>
        ): DGAlgebraMap<BS, BT, S, V, M> {
            val gLinearMap = GAlgebraMap.fromGVectors(source.gAlgebra, target.gAlgebra, matrixSpace, name, getGVectors)
            return DGAlgebraMap(source, target, gLinearMap)
        }
    }
}

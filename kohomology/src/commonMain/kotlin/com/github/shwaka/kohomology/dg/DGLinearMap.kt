package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public open class DGLinearMap<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: DGVectorSpace<D, BS, S, V, M>,
    target: DGVectorSpace<D, BT, S, V, M>,
    gLinearMap: GLinearMap<D, BS, BT, S, V, M>,
) {
    init {
        if (source.gVectorSpace != gLinearMap.source)
            throw IllegalArgumentException("The source DGVectorSpace does not match to the source GVectorSpace of GLinearMap")
        if (target.gVectorSpace != gLinearMap.target)
            throw IllegalArgumentException("The target DGVectorSpace does not match to the target GVectorSpace of GLinearMap")
    }

    // We cannot move these declarations to the primary constructor
    // If we move them, get the warning "accessing non-final property in constructor"
    public open val source: DGVectorSpace<D, BS, S, V, M> = source
    public open val target: DGVectorSpace<D, BT, S, V, M> = target
    public open val gLinearMap: GLinearMap<D, BS, BT, S, V, M> = gLinearMap
    public val degree: D = gLinearMap.degree
    public val matrixSpace: MatrixSpace<S, V, M> = gLinearMap.matrixSpace

    public operator fun invoke(gVector: GVector<D, BS, S, V>): GVector<D, BT, S, V> {
        return this.gLinearMap(gVector)
    }

    public fun inducedMapOnCohomology(): GLinearMap<D, SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        val getGVectors: (D) -> List<GVector<D, SubQuotBasis<BT, S, V>, S, V>> = { k ->
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

    public fun findCocycleLift(targetCocycle: GVector<D, BT, S, V>): GVector<D, BS, S, V> {
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
            ?: throw UnsupportedOperationException("$this[${this.gLinearMap.degreeGroup.context.run { degree - 1 }}] is not surjective")
        return this.source.context.run {
            sourceCocycle - d(sourceDifference)
        }
    }

    public fun findLift(targetCochain: GVector<D, BT, S, V>, sourceCoboundary: GVector<D, BS, S, V>): GVector<D, BS, S, V> {
        if (sourceCoboundary.degree != this.gLinearMap.degreeGroup.context.run { targetCochain.degree + 1 })
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

    public companion object {
        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGVectorSpace<D, BS, S, V, M>,
            target: DGVectorSpace<D, BT, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
        ): DGLinearMap<D, BS, BT, S, V, M> {
            val gLinearMap = GLinearMap(source.gVectorSpace, target.gVectorSpace, degree, matrixSpace, name, getLinearMap)
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
            val gLinearMap = GLinearMap.fromGVectors(source.gVectorSpace, target.gVectorSpace, degree, matrixSpace, name, getGVectors)
            return DGLinearMap(source, target, gLinearMap)
        }
    }
}

public class DGAlgebraMap<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: DGAlgebra<D, BS, S, V, M>,
    override val target: DGAlgebra<D, BT, S, V, M>,
    override val gLinearMap: GAlgebraMap<D, BS, BT, S, V, M>,
) : DGLinearMap<D, BS, BT, S, V, M>(source, target, gLinearMap) {
    public companion object {
        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<D, BS, S, V, M>,
            target: DGAlgebra<D, BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
        ): DGAlgebraMap<D, BS, BT, S, V, M> {
            val gLinearMap = GAlgebraMap(source.gAlgebra, target.gAlgebra, matrixSpace, name, getLinearMap)
            return DGAlgebraMap(source, target, gLinearMap)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGAlgebra<D, BS, S, V, M>,
            target: DGAlgebra<D, BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): DGAlgebraMap<D, BS, BT, S, V, M> {
            val gLinearMap = GAlgebraMap.fromGVectors(source.gAlgebra, target.gAlgebra, matrixSpace, name, getGVectors)
            return DGAlgebraMap(source, target, gLinearMap)
        }
    }
}

public class DGDerivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: DGAlgebra<D, B, S, V, M>,
    override val gLinearMap: Derivation<D, B, S, V, M>,
) : DGLinearMap<D, B, B, S, V, M>(source, source, gLinearMap) {
    override val target: DGAlgebra<D, B, S, V, M> = source
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<B, B, S, V, M>
        ): DGDerivation<D, B, S, V, M> {
            val gLinearMap = Derivation(source.gAlgebra, degree, matrixSpace, name, getLinearMap)
            return DGDerivation(source, gLinearMap)
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): DGDerivation<D, B, S, V, M> {
            val gLinearMap = Derivation.fromGVectors(source.gAlgebra, degree, matrixSpace, name, getGVectors)
            return DGDerivation(source, gLinearMap)
        }
    }
}

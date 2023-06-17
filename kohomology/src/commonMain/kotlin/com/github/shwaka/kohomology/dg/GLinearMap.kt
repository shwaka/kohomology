package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.QuotBasis
import com.github.shwaka.kohomology.vectsp.SubBasis
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface GLinearMap<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val source: GVectorSpace<D, BS, S, V>
    public val target: GVectorSpace<D, BT, S, V>
    public val degree: D
    public val matrixSpace: MatrixSpace<S, V, M>
    public val name: String
    public val degreeGroup: DegreeGroup<D>
        get() = source.degreeGroup

    public operator fun get(degree: D): LinearMap<BS, BT, S, V, M>
    public operator fun get(degree: Int): LinearMap<BS, BT, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    public operator fun invoke(gVector: GVector<D, BS, S, V>): GVector<D, BT, S, V> {
        if (gVector !in this.source)
            throw IllegalContextException("Invalid graded vector is given as an argument for a graded linear map")
        val linearMap = this[gVector.degree]
        if (gVector.vector.vectorSpace != linearMap.source)
            throw Exception("Graded linear map contains a bug: getLinearMap returns incorrect linear map")
        val newVector = linearMap(gVector.vector)
        val newDegree = this.degreeGroup.context.run { gVector.degree + this@GLinearMap.degree }
        return this.target.fromVector(newVector, newDegree)
    }

    public operator fun plus(other: GLinearMap<D, BS, BT, S, V, M>): GLinearMap<D, BS, BT, S, V, M> {
        require(this.source == other.source) { "GLinear maps with different sources cannot be added" }
        require(this.target == other.target) { "GLinear maps with different targets cannot be added" }
        require(this.degree == other.degree) { "GLinear maps with different degrees cannot be added" }
        return GLinearMap(
            source = this.source,
            target = this.target,
            degree = this.degree,
            matrixSpace = this.matrixSpace,
            name = "${this.name} + ${other.name}",
        ) { degree -> this[degree] + other[degree] }
    }

    public operator fun <BR : BasisName> times(other: GLinearMap<D, BR, BS, S, V, M>): GLinearMap<D, BR, BT, S, V, M> {
        require(other.target == this.source) {
            "Cannot composite graded linear maps since the source of $this and the target of $other are different"
        }
        val compositionDegree = this.degreeGroup.context.run {
            this@GLinearMap.degree + other.degree
        }
        return GLinearMap(
            source = other.source,
            target = this.target,
            degree = compositionDegree,
            matrixSpace = this.matrixSpace,
            name = "${this.name} + ${other.name}",
        ) { degree ->
            val intermediateDegree = this.degreeGroup.context.run {
                degree + other.degree
            }
            this[intermediateDegree] * other[degree]
        }
    }

    public fun imageContains(gVector: GVector<D, BT, S, V>): Boolean {
        if (gVector !in this.target)
            throw IllegalArgumentException("Invalid gVector is given: $gVector is not an element of ${this.target}")
        return (this.findPreimage(gVector) != null)
    }

    public fun findPreimage(gVector: GVector<D, BT, S, V>): GVector<D, BS, S, V>? {
        if (gVector !in this.target)
            throw IllegalArgumentException("Invalid gVector is given: $gVector is not an element of ${this.target}")
        val sourceDegree = this.degreeGroup.context.run { gVector.degree - this@GLinearMap.degree }
        return this[sourceDegree].findPreimage(gVector.vector)?.let { vector ->
            this.source.fromVector(vector, sourceDegree)
        }
    }

    public fun kernelBasis(degree: D): List<GVector<D, BS, S, V>> {
        return this[degree].kernelBasis().map { vector ->
            this.source.fromVector(vector, degree)
        }
    }

    public fun kernelBasis(degree: Int): List<GVector<D, BS, S, V>> {
        return this.kernelBasis(this.degreeGroup.fromInt(degree))
    }

    public fun kernel(): SubGVectorSpace<D, BS, S, V, M> {
        return SubGVectorSpace(
            this.matrixSpace,
            this.source,
            "Ker(${this.name})",
        ) { degree -> this[degree].kernel() }
    }

    public fun imageBasis(degree: D): List<GVector<D, BT, S, V>> {
        val sourceDegree = this.degreeGroup.context.run { degree - this@GLinearMap.degree }
        return this[sourceDegree].imageBasis().map { vector ->
            this.target.fromVector(vector, degree)
        }
    }

    public fun imageBasis(degree: Int): List<GVector<D, BT, S, V>> {
        return this.imageBasis(this.degreeGroup.fromInt(degree))
    }

    public fun image(): SubGVectorSpace<D, BT, S, V, M> {
        return SubGVectorSpace(
            this.matrixSpace,
            this.target,
            "Im(${this.name})",
        ) { degree ->
            val sourceDegree = this.degreeGroup.context.run { degree - this@GLinearMap.degree }
            this[sourceDegree].image()
        }
    }

    public fun cokernel(): QuotGVectorSpace<D, BT, S, V, M> {
        return QuotGVectorSpace(
            this.matrixSpace,
            "Coker(${this.name})",
            this.target,
            this.image(),
        )
    }

    public fun induce(
        sourceSub: SubGVectorSpace<D, BS, S, V, M>,
        targetSub: SubGVectorSpace<D, BT, S, V, M>,
    ): GLinearMap<D, SubBasis<BS, S, V>, SubBasis<BT, S, V>, S, V, M> {
        val gLinearMapDegree = this.degree
        return GLinearMap(
            sourceSub, targetSub,
            this.degree,
            this.matrixSpace,
            this.name,
        ) { degree ->
            val targetDegree = this.degreeGroup.context.run {
                // If this@GLinearMap.degree is written here, the compiler warns as follows:
                // > This label is now resolved to 'class GLinearMap'
                // > but soon it will be resolved to the closest 'anonymous function'.
                // > Please consider introducing or changing explicit label name
                // This may be because there are two candidates of 'GLinearMap' here:
                // - class GLinearMap in which fun induce is defined
                // - the (fake) constructor GLinearMap whose result is used as the return value of induce
                degree + gLinearMapDegree
            }
            this[degree].induce(sourceSub[degree], targetSub[targetDegree])
        }
    }

    public fun induce(
        sourceQuot: QuotGVectorSpace<D, BS, S, V, M>,
        targetQuot: QuotGVectorSpace<D, BT, S, V, M>,
    ): GLinearMap<D, QuotBasis<BS, S, V>, QuotBasis<BT, S, V>, S, V, M> {
        val gLinearMapDegree = this.degree
        return GLinearMap(
            sourceQuot, targetQuot,
            this.degree,
            this.matrixSpace,
            this.name,
        ) { degree ->
            val targetDegree = this.degreeGroup.context.run {
                // See the above definition of an overload concerning gLinearMapDegree
                degree + gLinearMapDegree
            }
            this[degree].induce(sourceQuot[degree], targetQuot[targetDegree])
        }
    }

    public fun induce(
        sourceSubQuot: SubQuotGVectorSpace<D, BS, S, V, M>,
        targetSubQuot: SubQuotGVectorSpace<D, BT, S, V, M>,
    ): GLinearMap<D, SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        val gLinearMapDegree = this.degree
        return GLinearMap(
            sourceSubQuot, targetSubQuot,
            this.degree,
            this.matrixSpace,
            this.name,
        ) { degree ->
            val targetDegree = this.degreeGroup.context.run {
                // See the above definition of an overload concerning gLinearMapDegree
                degree + gLinearMapDegree
            }
            this[degree].induce(sourceSubQuot[degree], targetSubQuot[targetDegree])
        }
    }

    public companion object {
        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
        ): GLinearMap<D, BS, BT, S, V, M> {
            return GLinearMapImpl(source, target, degree, matrixSpace, name, getLinearMap)
        }

        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: Int,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>,
        ): GLinearMap<D, BS, BT, S, V, M> {
            return GLinearMapImpl(source, target, source.degreeGroup.fromInt(degree), matrixSpace, name, getLinearMap)
        }

        private fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> createGetLinearMap(
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): (D) -> LinearMap<BS, BT, S, V, M> {
            if (source.degreeGroup != target.degreeGroup)
                throw IllegalArgumentException("Cannot consider a linear map between graded vector spaces with different degree groups")
            return { k ->
                val l = source.degreeGroup.context.run { k + degree }
                val sourceVectorSpace = source[k]
                val targetVectorSpace = target[l]
                val gVectorValueList = getGVectors(k)
                if (gVectorValueList.any { it !in target })
                    throw IllegalContextException("The value list contains an element not contained in $target")
                if (gVectorValueList.any { it.degree != l })
                    throw IllegalArgumentException("The value list contains an element with wrong degree")
                if (sourceVectorSpace.basisNames.size != gVectorValueList.size)
                    throw InvalidSizeException("The value list has incompatible size")
                val valueList = gVectorValueList.map { it.vector }
                LinearMap.fromVectors(sourceVectorSpace, targetVectorSpace, matrixSpace, valueList)
            }
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> createGetLinearMap(
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: Int,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): (D) -> LinearMap<BS, BT, S, V, M> {
            return this.createGetLinearMap(source, target, source.degreeGroup.fromInt(degree), matrixSpace, getGVectors)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): GLinearMap<D, BS, BT, S, V, M> {
            val getLinearMap = this.createGetLinearMap(source, target, degree, matrixSpace, getGVectors)
            return GLinearMap(source, target, degree, matrixSpace, name, getLinearMap)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: Int,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): GLinearMap<D, BS, BT, S, V, M> {
            return this.fromGVectors(source, target, source.degreeGroup.fromInt(degree), matrixSpace, name, getGVectors)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            matrixSpace: MatrixSpace<S, V, M>,
            source: GVectorSpace<D, BS, S, V>,
            target: GVectorSpace<D, BT, S, V>,
            degree: D,
        ): GLinearMap<D, BS, BT, S, V, M> {
            return GLinearMap(
                source,
                target,
                degree,
                matrixSpace,
                "0",
            ) { n ->
                source.degreeGroup.context.run {
                    LinearMap.getZero(source[n], target[n + degree], matrixSpace)
                }
            }
        }

        public fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            matrixSpace: MatrixSpace<S, V, M>,
            source: GVectorSpace<IntDegree, BS, S, V>,
            target: GVectorSpace<IntDegree, BT, S, V>,
            degree: Int,
        ): GLinearMap<IntDegree, BS, BT, S, V, M> {
            return GLinearMap.getZero(matrixSpace, source, target, source.degreeGroup.fromInt(degree))
        }
    }
}

private class GLinearMapImpl<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: GVectorSpace<D, BS, S, V>,
    override val target: GVectorSpace<D, BT, S, V>,
    override val degree: D,
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val name: String,
    private val getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
) : GLinearMap<D, BS, BT, S, V, M> {
    init {
        if (source.degreeGroup != target.degreeGroup)
            throw IllegalArgumentException("Cannot consider a linear map between graded vector spaces with different degree groups")
    }

    private val cache: MutableMap<D, LinearMap<BS, BT, S, V, M>> = mutableMapOf()

    override operator fun get(degree: D): LinearMap<BS, BT, S, V, M> {
        return this.cache.getOrPut(degree) {
            this.getLinearMap(degree)
        }
    }

    override fun toString(): String {
        return this.name
    }
}

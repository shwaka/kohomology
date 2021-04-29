package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import mu.KotlinLogging

open class GLinearMap<BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val source: GVectorSpace<BS, D, S, V>,
    val target: GVectorSpace<BT, D, S, V>,
    val degree: D,
    val matrixSpace: MatrixSpace<S, V, M>,
    val name: String,
    private val getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
) {
    private val cache: MutableMap<D, LinearMap<BS, BT, S, V, M>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}
    val degreeMonoid = source.degreeMonoid

    constructor(
        source: GVectorSpace<BS, D, S, V>,
        target: GVectorSpace<BT, D, S, V>,
        degree: Int,
        matrixSpace: MatrixSpace<S, V, M>,
        name: String,
        getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
    ) : this(source, target, source.degreeMonoid.fromInt(degree), matrixSpace, name, getLinearMap)

    operator fun invoke(gVector: GVector<BS, D, S, V>): GVector<BT, D, S, V> {
        if (gVector !in this.source)
            throw IllegalContextException("Invalid graded vector is given as an argument for a graded linear map")
        val linearMap = this.getLinearMap(gVector.degree)
        if (gVector.vector.vectorSpace != linearMap.source)
            throw Exception("Graded linear map contains a bug: getLinearMap returns incorrect linear map")
        val newVector = linearMap(gVector.vector)
        val newDegree = this.degreeMonoid.context.run { gVector.degree + this@GLinearMap.degree }
        return this.target.fromVector(newVector, newDegree)
    }

    operator fun get(degree: D): LinearMap<BS, BT, S, V, M> {
        this.cache[degree]?.let {
            // if cache exists
            this.logger.debug { "cache found for $this[$degree]" }
            return it
        }
        // if cache does not exist
        this.logger.debug { "cache not found for $this[$degree], create new instance" }
        val linearMap = this.getLinearMap(degree)
        this.cache[degree] = linearMap
        return linearMap
    }

    operator fun get(degree: Int): LinearMap<BS, BT, S, V, M> {
        return this.get(this.degreeMonoid.fromInt(degree))
    }

    fun findPreimage(gVector: GVector<BT, D, S, V>): GVector<BS, D, S, V>? {
        if (gVector !in this.target)
            throw IllegalArgumentException("Invalid gVector is given: $gVector is not an element of ${this.target}")
        val sourceDegree = this.degreeMonoid.context.run { gVector.degree - this@GLinearMap.degree }
        return this[sourceDegree].findPreimage(gVector.vector)?.let { vector ->
            this.source.fromVector(vector, sourceDegree)
        }
    }

    override fun toString(): String {
        return this.name
    }

    companion object {
        fun <BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> createGetLinearMap(
            source: GVectorSpace<BS, D, S, V>,
            target: GVectorSpace<BT, D, S, V>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (D) -> List<GVector<BT, D, S, V>>
        ): (D) -> LinearMap<BS, BT, S, V, M> {
            return { k ->
                val l = source.degreeMonoid.context.run { k + degree }
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

        fun <BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> createGetLinearMap(
            source: GVectorSpace<BS, D, S, V>,
            target: GVectorSpace<BT, D, S, V>,
            degree: Int,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (D) -> List<GVector<BT, D, S, V>>
        ): (D) -> LinearMap<BS, BT, S, V, M> {
            return this.createGetLinearMap(source, target, source.degreeMonoid.fromInt(degree), matrixSpace, getGVectors)
        }

        fun <BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GVectorSpace<BS, D, S, V>,
            target: GVectorSpace<BT, D, S, V>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<BT, D, S, V>>
        ): GLinearMap<BS, BT, D, S, V, M> {
            val getLinearMap = this.createGetLinearMap(source, target, degree, matrixSpace, getGVectors)
            return GLinearMap(source, target, degree, matrixSpace, name, getLinearMap)
        }

        fun <BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GVectorSpace<BS, D, S, V>,
            target: GVectorSpace<BT, D, S, V>,
            degree: Int,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<BT, D, S, V>>
        ): GLinearMap<BS, BT, D, S, V, M> {
            return this.fromGVectors(source, target, source.degreeMonoid.fromInt(degree), matrixSpace, name, getGVectors)
        }
    }
}

class GAlgebraMap<BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: GAlgebra<BS, D, S, V, M>,
    target: GAlgebra<BT, D, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
) : GLinearMap<BS, BT, D, S, V, M>(source, target, 0, matrixSpace, name, getLinearMap) {
    companion object {
        fun <BS : BasisName, BT : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GAlgebra<BS, D, S, V, M>,
            target: GAlgebra<BT, D, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<BT, D, S, V>>
        ): GAlgebraMap<BS, BT, D, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, target, 0, matrixSpace, getGVectors)
            return GAlgebraMap(source, target, matrixSpace, name, getLinearMap)
        }
    }
}

class Derivation<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: GAlgebra<B, D, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : GLinearMap<B, B, D, S, V, M>(source, source, degree, matrixSpace, name, getLinearMap) {
    companion object {
        fun <B : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GAlgebra<B, D, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<B, D, S, V>>
        ): Derivation<B, D, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return Derivation(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import mu.KotlinLogging

open class GLinearMap<BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val source: GVectorSpace<BS, S, V>,
    val target: GVectorSpace<BT, S, V>,
    val degree: Degree,
    val matrixSpace: MatrixSpace<S, V, M>,
    val name: String,
    private val getLinearMap: (Degree) -> LinearMap<BS, BT, S, V, M>
) {
    private val cache: MutableMap<Degree, LinearMap<BS, BT, S, V, M>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    operator fun invoke(gVector: GVector<BS, S, V>): GVector<BT, S, V> {
        if (gVector !in this.source)
            throw IllegalContextException("Invalid graded vector is given as an argument for a graded linear map")
        val linearMap = this.getLinearMap(gVector.degree)
        if (gVector.vector.vectorSpace != linearMap.source)
            throw Exception("Graded linear map contains a bug: getLinearMap returns incorrect linear map")
        val newVector = linearMap(gVector.vector)
        val newDegree = gVector.degree + this.degree
        return this.target.fromVector(newVector, newDegree)
    }

    operator fun get(degree: Degree): LinearMap<BS, BT, S, V, M> {
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

    fun findPreimage(gVector: GVector<BT, S, V>): GVector<BS, S, V>? {
        if (gVector !in this.target)
            throw IllegalArgumentException("Invalid gVector is given: $gVector is not an element of ${this.target}")
        val sourceDegree = gVector.degree - this.degree
        return this[sourceDegree].findPreimage(gVector.vector)?.let { vector ->
            this.source.fromVector(vector, sourceDegree)
        }
    }

    override fun toString(): String {
        return this.name
    }

    companion object {
        fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> createGetLinearMap(
            source: GVectorSpace<BS, S, V>,
            target: GVectorSpace<BT, S, V>,
            degree: Degree,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (Degree) -> List<GVector<BT, S, V>>
        ): (Degree) -> LinearMap<BS, BT, S, V, M> {
            return { k ->
                val sourceVectorSpace = source[k]
                val targetVectorSpace = target[k + degree]
                val gVectorValueList = getGVectors(k)
                if (gVectorValueList.any { it !in target })
                    throw IllegalContextException("The value list contains an element not contained in $target")
                if (gVectorValueList.any { it.degree != k + degree })
                    throw IllegalArgumentException("The value list contains an element with wrong degree")
                if (sourceVectorSpace.basisNames.size != gVectorValueList.size)
                    throw InvalidSizeException("The value list has incompatible size")
                val valueList = gVectorValueList.map { it.vector }
                LinearMap.fromVectors(sourceVectorSpace, targetVectorSpace, matrixSpace, valueList)
            }
        }

        fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GVectorSpace<BS, S, V>,
            target: GVectorSpace<BT, S, V>,
            degree: Degree,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (Degree) -> List<GVector<BT, S, V>>
        ): GLinearMap<BS, BT, S, V, M> {
            val getLinearMap = this.createGetLinearMap(source, target, degree, matrixSpace, getGVectors)
            return GLinearMap(source, target, degree, matrixSpace, name, getLinearMap)
        }
    }
}


class GAlgebraMap<BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: GAlgebra<BS, S, V, M>,
    target: GAlgebra<BT, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (Degree) -> LinearMap<BS, BT, S, V, M>
) : GLinearMap<BS, BT, S, V, M>(source, target, 0, matrixSpace, name, getLinearMap) {
    companion object {
        fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GAlgebra<BS, S, V, M>,
            target: GAlgebra<BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (Degree) -> List<GVector<BT, S, V>>
        ): GAlgebraMap<BS, BT, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, target, 0, matrixSpace, getGVectors)
            return GAlgebraMap(source, target, matrixSpace, name, getLinearMap)
        }
    }
}

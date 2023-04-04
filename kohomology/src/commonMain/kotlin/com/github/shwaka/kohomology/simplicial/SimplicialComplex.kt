package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorSpace
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.pow

public class SimplicialComplex<Vertex : Comparable<Vertex>>(
    public val getSimplices: (dim: Int) -> List<Simplex<Vertex>>,
) {
    public val vertices: List<Vertex> by lazy {
        this.getSimplices(0).map { zeroSimplex ->
            zeroSimplex.vertices[0]
        }
    }

    private val maximalFaces: MutableMap<Int, List<Simplex<Vertex>>> = mutableMapOf()
    public val allMaximalFaces: Map<Int, List<Simplex<Vertex>>> by lazy {
        (0..(this.vertices.size)).associateWith { dim ->
            this.getMaximalFaces(dim)
        }.filterValues { simplices -> simplices.isNotEmpty() }
    }

    public fun getMaximalFaces(dim: Int): List<Simplex<Vertex>> {
        this.maximalFaces[dim]?.let { return it }
        val result = this.getSimplices(dim).toMutableList()
        for (simplex in this.getSimplices(dim + 1)) {
            for (face in simplex.faceList) {
                result.remove(face)
            }
        }
        this.maximalFaces[dim] = result
        return result
    }

    private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getGVectorSpace(
        matrixSpace: MatrixSpace<S, V, M>,
    ): GVectorSpace<IntDegree, Simplex<Vertex>, S, V> {
        return GVectorSpace.fromBasisNames(
            matrixSpace.numVectorSpace,
            name = "ChainComplexFromSimplicialComplex",
        ) { degree -> this.getSimplices(-degree) }
    }

    private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDifferential(
        matrixSpace: MatrixSpace<S, V, M>,
        gVectorSpace: GVectorSpace<IntDegree, Simplex<Vertex>, S, V>,
    ): GLinearMap<IntDegree, Simplex<Vertex>, Simplex<Vertex>, S, V, M> {
        return GLinearMap.fromGVectors(gVectorSpace, gVectorSpace, 1, matrixSpace, "differential") { degree ->
            if (degree.isZero()) {
                // This is necessary since <x>.face(0) = <> (i.e. augmented)
                List(this.getSimplices(0).size) {
                    gVectorSpace.getZero(1)
                }
            } else {
                val dim = -degree.value
                this.getSimplices(dim).map { simplex ->
                    gVectorSpace.context.run {
                        (0..dim).map {
                            val face = simplex.face(it)
                            val sign = Sign.fromParity(it)
                            val gVector = gVectorSpace.fromBasisName(face, degree.value + 1)
                            gVectorSpace.context.run {
                                gVector * sign
                            }
                        }.sum(degree.value + 1)
                    }
                }
            }
        }
    }

    public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dgVectorSpace(
        matrixSpace: MatrixSpace<S, V, M>,
    ): DGVectorSpace<IntDegree, Simplex<Vertex>, S, V, M> {
        val gVectorSpace = this.getGVectorSpace(matrixSpace)
        val differential = this.getDifferential(matrixSpace, gVectorSpace)
        return DGVectorSpace(gVectorSpace, differential)
    }

    public fun eulerCharacteristic(): Int {
        val maxDim = this.getSimplices(0).size - 1
        return (0..maxDim).sumOf { dim -> this.getSimplices(dim).size * (-1).pow(dim) }
    }

    public fun isSameAs(other: SimplicialComplex<Vertex>): Boolean {
        // This method compares mathematically,
        // so could not be implemented as equals()
        if (this.vertices.size != other.vertices.size) {
            return false
        }
        return (0..(this.vertices.size)).all { dim ->
            this.getSimplices(dim).toSet() == other.getSimplices(dim).toSet()
        }
    }

    public companion object {
        public fun <Vertex : Comparable<Vertex>> generatedBy(
            generatingSimplices: Map<Int, List<Simplex<Vertex>>>
        ): SimplicialComplex<Vertex> {
            val maxDim = generatingSimplices.keys.max()
            val simplices: MutableMap<Int, List<Simplex<Vertex>>> = mutableMapOf()
            fun getSimplices(dim: Int): List<Simplex<Vertex>> {
                if (dim > maxDim || dim < 0) {
                    // We don't want to contain (-1)-simplex (the empty simplex)
                    return emptyList()
                }
                simplices[dim]?.let { return it }

                val resultAsSet: MutableSet<Simplex<Vertex>> =
                    generatingSimplices.getOrElse(dim) { emptyList() }.toMutableSet()
                for (simplex in getSimplices(dim + 1)) {
                    for (face in simplex.faceList) {
                        resultAsSet.add(face)
                    }
                }
                val result: List<Simplex<Vertex>> = resultAsSet.distinct()
                simplices[dim] = result
                return result
            }
            return SimplicialComplex(::getSimplices)
        }

        public fun <Vertex : Comparable<Vertex>> generatedBy(
            generatingSimplices: List<Simplex<Vertex>>
        ): SimplicialComplex<Vertex> {
            val generatingSimplicesAsMap = generatingSimplices.groupBy { simplex -> simplex.dim }
            return SimplicialComplex.generatedBy(generatingSimplicesAsMap)
        }
    }
}

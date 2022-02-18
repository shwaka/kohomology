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
import com.github.shwaka.kohomology.vectsp.BasisName

public class Simplex<Vertex : Comparable<Vertex>>(vertices: List<Vertex>) : BasisName {
    public val vertices: List<Vertex> = vertices.sorted()

    public val dim: Int = vertices.size

    public fun face(i: Int): Simplex<Vertex> {
        return Simplex(this.vertices.filterIndexed { index, _ -> index != i })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Simplex<*>

        if (vertices != other.vertices) return false

        return true
    }

    override fun hashCode(): Int {
        return vertices.hashCode()
    }

    override fun toString(): String {
        val verticesString = this.vertices.joinToString(",")
        return "<$verticesString>"
    }
}

public class SimplicialComplex<Vertex : Comparable<Vertex>>(
    public val getSimplices: (dim: Int) -> List<Simplex<Vertex>>,
) {
    private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>getGVectorSpace(
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
        return DGVectorSpace(gVectorSpace, differential, matrixSpace)
    }

    public fun eulerCharacteristic(): Int {
        val maxDim = this.getSimplices(0).size - 1
        return (0..maxDim).map { dim -> this.getSimplices(dim).size * (-1).pow(dim) }.sum()
    }
}

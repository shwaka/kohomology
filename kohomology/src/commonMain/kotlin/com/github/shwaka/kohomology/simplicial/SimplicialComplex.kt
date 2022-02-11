package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.dg.DGVectorSpace
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorSpace
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public class Simplex<Vertex : Comparable<Vertex>>(vertices: List<Vertex>) : BasisName {
    public val vertices = vertices.sorted()

    public val dim: Int = vertices.size

    public fun face(i: Int): Simplex<Vertex> {
        return Simplex(this.vertices.drop(i))
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
}

public class SimplicialComplex<Vertex : Comparable<Vertex>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val getSimplices: (dim: Int) -> List<Simplex<Vertex>>,
    public val matrixSpace: MatrixSpace<S, V, M>,
) {
    private val gVectorSpace: GVectorSpace<IntDegree, Simplex<Vertex>, S, V> by lazy {
        GVectorSpace.fromBasisNames(
            this.matrixSpace.numVectorSpace,
            name = "ChainComplexFromSimplicialComplex",
        ) { degree -> this.getSimplices(-degree) }
    }

    private val differential: GLinearMap<IntDegree, Simplex<Vertex>, Simplex<Vertex>, S, V, M> by lazy {
        GLinearMap.fromGVectors(this.gVectorSpace, this.gVectorSpace, 1, this.matrixSpace, "differential") { degree ->
            val dim = -degree.value
            this.getSimplices(dim).map { simplex ->
                val terms = (0..dim).map { simplex.face(it) }.map {
                    this.gVectorSpace.fromBasisName(it, degree.value + 1)
                }
                this.gVectorSpace.context.run {
                    terms.sum(degree.value + 1)
                }
            }
        }
    }

    public val dgVectorSpace: DGVectorSpace<IntDegree, Simplex<Vertex>, S, V, M> by lazy {
        DGVectorSpace(this.gVectorSpace, this.differential, this.matrixSpace)
    }
}

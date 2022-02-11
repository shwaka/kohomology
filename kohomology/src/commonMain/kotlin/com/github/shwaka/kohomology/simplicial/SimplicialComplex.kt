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

public class SimplicialComplex<Vertex : Comparable<Vertex>, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val matrixSpace: MatrixSpace<S, V, M>,
    public val getSimplices: (dim: Int) -> List<Simplex<Vertex>>,
) {
    private val gVectorSpace: GVectorSpace<IntDegree, Simplex<Vertex>, S, V> by lazy {
        GVectorSpace.fromBasisNames(
            this.matrixSpace.numVectorSpace,
            name = "ChainComplexFromSimplicialComplex",
        ) { degree -> this.getSimplices(-degree) }
    }

    private val differential: GLinearMap<IntDegree, Simplex<Vertex>, Simplex<Vertex>, S, V, M> by lazy {
        GLinearMap.fromGVectors(this.gVectorSpace, this.gVectorSpace, 1, this.matrixSpace, "differential") { degree ->
            if (degree.isZero()) {
                // This is necessary since <x>.face(0) = <> (i.e. augmented)
                List(this.getSimplices(0).size) {
                    this.gVectorSpace.getZero(1)
                }
            } else {
                val dim = -degree.value
                val gVectorSpace = this.gVectorSpace
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

    public val dgVectorSpace: DGVectorSpace<IntDegree, Simplex<Vertex>, S, V, M> by lazy {
        DGVectorSpace(this.gVectorSpace, this.differential, this.matrixSpace)
    }
}

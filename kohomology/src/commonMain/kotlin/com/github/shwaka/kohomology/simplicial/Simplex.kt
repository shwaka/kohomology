package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.vectsp.BasisName

public class Simplex<Vertex : Comparable<Vertex>>
private constructor(public val vertices: List<Vertex>) : BasisName {
    public val dim: Int = vertices.size - 1

    public val faceList: List<Simplex<Vertex>> by lazy {
        (0..this.dim).map { i -> this.face(i) }
    }

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

    public companion object {
        public fun <Vertex : Comparable<Vertex>> fromSorted(vertices: List<Vertex>): Simplex<Vertex> {
            return Simplex(vertices)
        }

        public fun <Vertex : Comparable<Vertex>> fromUnsorted(vertices: List<Vertex>): Simplex<Vertex> {
            return Simplex(vertices.sorted())
        }
    }
}

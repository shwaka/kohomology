package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.util.ComparablePair
import com.github.shwaka.kohomology.util.directProductOf

private fun <Vertex1 : Comparable<Vertex1>, Vertex2 : Comparable<Vertex2>> productOf(
    simplex1: Simplex<Vertex1>,
    simplex2: Simplex<Vertex2>,
): Simplex<ComparablePair<Vertex1, Vertex2>> {
    return Simplex.fromUnsorted(
        directProductOf(simplex1.vertices, simplex2.vertices)
    )
}

public fun <Vertex1 : Comparable<Vertex1>, Vertex2 : Comparable<Vertex2>> productOf(
    simplicialComplex1: SimplicialComplex<Vertex1>,
    simplicialComplex2: SimplicialComplex<Vertex2>,
): SimplicialComplex<ComparablePair<Vertex1, Vertex2>> {
    val maximalFaces1: List<Simplex<Vertex1>> = simplicialComplex1.allMaximalFaces.values.flatten()
    val maximalFaces2: List<Simplex<Vertex2>> = simplicialComplex2.allMaximalFaces.values.flatten()
    val generatingSimplices: List<Simplex<ComparablePair<Vertex1, Vertex2>>> =
        maximalFaces1.flatMap { face1 ->
            maximalFaces2.map { face2 -> productOf(face1, face2) }
        }
    return SimplicialComplex.generatedBy(generatingSimplices)
}

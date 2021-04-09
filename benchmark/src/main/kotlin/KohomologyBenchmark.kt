package test

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KohomologyBenchmark {
    @Benchmark
    fun freeLoopSpaceOfEvenSphere(): String {
        val sphereDim = 4
        val indeterminateList = listOf(
            Indeterminate("x", sphereDim),
            Indeterminate("y", sphereDim * 2 - 1)
        )
        val matrixSpace = DenseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

        var result = ""
        for (degree in 0 until 20) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }
}

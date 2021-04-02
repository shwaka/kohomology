import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.specific.BigRationalField
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverBigRational
import kotlinx.browser.document

fun main() {
    BigRationalField.withContext {
        myprint(one / two + one / three)
    }
    numVectorTest()
    cohomologyTest()
}

fun myprint(obj: Any) {
    val text = obj.toString()
    println("[myprint] $text")
    val root = document.getElementById("root") ?: throw Exception("root not found!")
    val p = document.createElement("p")
    p.textContent = text
    root.appendChild(p)
}

fun numVectorTest() {
    val vectorSpace = DenseNumVectorSpaceOverBigRational
    vectorSpace.withContext {
        val v = vectorSpace.fromValues(one, zero)
        myprint("2 * (1, 0) = ${two * v}")
    }
}

fun cohomologyTest() {
    val matrixSpace = DenseMatrixSpaceOverBigRational
    val indeterminateList = listOf(
        Indeterminate("a", 2),
        Indeterminate("b", 2),
        Indeterminate("x", 3),
        Indeterminate("y", 3),
        Indeterminate("z", 3),
    )
    val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, _, _, _) ->
        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))
    }
    val (a, b, x, y, z) = freeDGAlgebra.gAlgebra.generatorList
    freeDGAlgebra.withDGAlgebraContext {
        myprint(d(x * y))
        myprint(d(x * y * z))
    }
    for (n in 0 until 12) {
        val basis = freeDGAlgebra.cohomology()[n].getBasis()
        myprint("H^$n = Q$basis")
    }
}

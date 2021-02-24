import com.github.shwaka.kohomology.field.BigRational
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import kotlinx.browser.document

fun main() {
    BigRationalField.withContext {
        myprint(BigRational(1, 2) + BigRational(1, 3))
    }
    numVectorTest()
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
    val vectorSpace = DenseNumVectorSpace.from(BigRationalField)
    vectorSpace.withContext {
        val v = vectorSpace.fromValues(one, zero)
        myprint("2 * (1, 0) = ${two * v}")
    }
}

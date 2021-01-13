import com.github.shwaka.kohomology.field.BigRational
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.times
import kotlinx.browser.document

fun main() {
    myprint(BigRational(1, 2) + BigRational(1, 3))
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
    val zero = BigRationalField.zero
    val one = BigRationalField.one
    val two = BigRationalField.fromInt(2)
    val vectorSpace = DenseNumVectorSpace.from(BigRationalField, 2)
    val v = vectorSpace.get(one, zero)
    myprint("2 * (1, 0) = ${two * v}")
}

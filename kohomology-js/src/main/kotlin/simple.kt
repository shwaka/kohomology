import com.github.shwaka.kohomology.specific.BigRationalField
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverBigRational
import kotlinx.browser.document

fun main() {
    BigRationalField.withContext {
        myprint(one / two + one / three)
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
    val vectorSpace = DenseNumVectorSpaceOverBigRational
    vectorSpace.withContext {
        val v = vectorSpace.fromValues(one, zero)
        myprint("2 * (1, 0) = ${two * v}")
    }
}

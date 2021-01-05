import com.github.shwaka.kohomology.field.BigRational
import kotlinx.browser.document

fun main() {
    console.log("Hello, ${greet()}")
    myprint("hoge")
    myprint("fuga")
    myprint(BigRational(1, 2) + BigRational(1, 3))
}

fun myprint(obj: Any) {
    val text = obj.toString()
    println("[myprint] $text")
    val root = document.getElementById("root") ?: throw Exception("root not found!")
    val p = document.createElement("p")
    p.textContent = text
    root.appendChild(p)
}

fun greet() = "world"

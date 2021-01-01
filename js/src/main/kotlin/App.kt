import kotlinx.browser.document

import com.github.shwaka.kohomology.field.IntRational

fun main() {
    println("Hello world!")
    println(IntRational(1, 2) + IntRational(1, 3))
    println(IntRational(1, 3) + IntRational(-2, 6))
    document.bgColor = "cyan"
}

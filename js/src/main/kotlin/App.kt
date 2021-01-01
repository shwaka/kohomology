import kotlinx.browser.document

data class Rational(val numerator: Int, val denominator: Int) {
    operator fun plus(other: Rational): Rational {
        val numerator = this.numerator * other.denominator + other.numerator * this.denominator
        val denominator = this.denominator * other.denominator
        return Rational(numerator, denominator)
    }

    override fun toString(): String {
        if (this.numerator == 0) {
            return "0"
        } else if (this.denominator == 1) {
            return this.numerator.toString()
        } else {
            return "${this.numerator}/${this.denominator}"
        }
    }
}

fun main() {
    println("Hello world!")
    println(Rational(1, 2) + Rational(1, 3))
    println(Rational(1, 3) + Rational(-2, 6))
    document.bgColor = "cyan"
}

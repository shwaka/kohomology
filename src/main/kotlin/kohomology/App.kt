package kohomology

data class Rational(val numerator: Int, val denominator: Int) : Scalar<Rational> {
    override val field = RationalField
    override operator fun plus(other: Rational): Rational {
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

    override fun unwrap(): Rational {
        return this
    }
}

object RationalField : Field<Rational> {
    override fun wrap(a: Rational): Scalar<Rational> {
        return a
    }
}

interface Scalar<S> {
    operator fun plus(other: S): S
    fun unwrap(): S
    val field: Field<S>
}

interface Field<S> {
    fun wrap(a: S): Scalar<S>
}

fun <S> add(a: Scalar<S>, b: Scalar<S>): Scalar<S> {
    return a.field.wrap(a + b.unwrap())
}


fun main(args: Array<String>) {
    println("Hello world!")
    println(Rational(1, 2) + Rational(1, 3))
    println(Rational(1, 3) + Rational(-2, 6))

    val a = Rational(1, 2)
    val b = Rational(1, 3)
    println(add(a, b))
}

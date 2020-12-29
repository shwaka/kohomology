package com.github.shwaka.kohomology.field

data class Rational(val numerator: Int, val denominator: Int) : Scalar<Rational> {
    override val field = RationalField
    override operator fun plus(other: Rational): Rational {
        val numerator = this.numerator * other.denominator + other.numerator * this.denominator
        val denominator = this.denominator * other.denominator
        return Rational(numerator, denominator)
    }
    override operator fun times(other: Rational): Rational {
        return Rational(this.numerator * other.numerator, this.denominator * other.denominator)
    }

    override fun toString(): String {
        return when {
            this.numerator == 0 -> {
                "0"
            }
            this.denominator == 1 -> {
                this.numerator.toString()
            }
            else -> {
                "${this.numerator}/${this.denominator}"
            }
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
    override fun fromInteger(n: Int): Rational {
        return Rational(n, 1)
    }
}

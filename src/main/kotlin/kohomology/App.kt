package kohomology

interface Scalar<S> {
    operator fun plus(other: S): S
    operator fun plus(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this + other.unwrap())
    }
    fun unwrap(): S
    val field: Field<S>
}

interface Field<S> {
    fun wrap(a: S): Scalar<S>
    fun fromInteger(n: Int): Scalar<S>
}

fun <S> add(a: Scalar<S>, b: Scalar<S>): Scalar<S> {
    // return a.field.wrap(a + b.unwrap())
    return a + b
}

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
    override fun fromInteger(n: Int): Rational {
        return Rational(n, 1)
    }
}

data class IntModp(val value: Int, val p: Int) : Scalar<IntModp> {
    // 色々とチェックした方が良さそう
    // p > 0
    override val field: Field<IntModp>
    init {
        // 毎回オブジェクト生成するのは無駄
        this.field = Fp(this.p)
    }
    override operator fun plus(other: IntModp): IntModp {
        if (this.p != other.p) {
            throw Error("different characteristic!")
        }
        return IntModp(this.value + other.value, this.p)
    }
    override fun unwrap(): IntModp {
        return this
    }
    override fun toString(): String {
        return "${this.value % this.p} mod ${this.p}"
    }
}

data class Fp(val p: Int) : Field<IntModp> {
    override fun wrap(a: IntModp): Scalar<IntModp> {
        return a
    }
    override fun fromInteger(n: Int): IntModp {
        return IntModp(n, p)
    }
}

fun main(args: Array<String>) {
    println("Hello world!")
    println(Rational(1, 2) + Rational(1, 3))
    println(Rational(1, 3) + Rational(-2, 6))

    val a = Rational(1, 2)
    val b = RationalField.fromInteger(2)
    println(add(a, b))

    val p = 5
    val c = IntModp(3, p)
    val d = IntModp(1, p)
    println(add(c, d))
    println(add(c, d + Fp(p).fromInteger(1)))
}

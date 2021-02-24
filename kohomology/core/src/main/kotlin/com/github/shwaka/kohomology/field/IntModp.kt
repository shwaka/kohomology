package com.github.shwaka.kohomology.field

class IntModp(value: Int, p: Int) : Scalar<IntModp> {
    val value: Int = value.positiveRem(p)
    val p: Int = p

    override val field: Field<IntModp> = Fp.get(this.p)

    override fun toString(): String {
        return "${this.value.positiveRem(this.p)} mod ${this.p}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as IntModp

        if (this.value != other.value) return false
        if (this.p != other.p) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.value
        result = 31 * result + this.p
        return result
    }
}
class Fp private constructor(val p: Int) : Field<IntModp> {
    companion object {
        private val cache: MutableMap<Int, Fp> = mutableMapOf()
        fun get(p: Int): Fp {
            return this.cache.getOrPut(p, { if (p.isPrime()) Fp(p) else throw ArithmeticException("$p is not prime") })
        }
    }

    override val scalarContext: ScalarContext<IntModp> = ScalarContext(this)

    override val field = this

    override val zero
        get() = IntModp(0, this.p)
    override val one
        get() = IntModp(1, this.p)

    override fun add(a: IntModp, b: IntModp): IntModp {
        if (a.p != b.p) {
            throw Exception("[Error] different characteristic: ${a.p} and ${b.p}")
        }
        return IntModp(a.value + b.value, this.p)
    }

    override fun subtract(a: IntModp, b: IntModp): IntModp {
        if (a.p != b.p) {
            throw Exception("[Error] different characteristic: ${a.p} and ${b.p}")
        }
        return IntModp(a.value - b.value, this.p)
    }

    override fun multiply(a: IntModp, b: IntModp): IntModp {
        if (a.p != b.p) {
            throw Exception("[Error] different characteristic: ${a.p} and ${b.p}")
        }
        return IntModp(a.value * b.value, this.p)
    }

    override fun divide(a: IntModp, b: IntModp): IntModp {
        if (a.p != b.p) {
            throw Exception("[Error] different characteristic: ${a.p} and ${b.p}")
        }
        val bInv = this.invModp(b)
        return IntModp(a.value * bInv.value, this.p)
    }

    private fun invModp(a: IntModp): IntModp {
        if (a == IntModp(0, a.p)) throw ArithmeticException("division by zero (IntModp(0, ${this.p}))")
        // TODO: Int として pow した後に modulo するのは重い
        return IntModp(a.value.pow(this.p - 2).positiveRem(this.p), this.p)
    }

    override fun fromInt(n: Int): IntModp {
        return IntModp(n, p)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Fp

        if (this.p != other.p) return false

        return true
    }

    override fun hashCode(): Int {
        return this.p
    }

    override fun toString(): String {
        return "F_${this.p}"
    }
}

val F2 = Fp.get(2)
val F3 = Fp.get(3)
val F5 = Fp.get(5)
val F7 = Fp.get(7)

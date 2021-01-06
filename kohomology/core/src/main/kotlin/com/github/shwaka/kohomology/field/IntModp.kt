package com.github.shwaka.kohomology.field

class IntModp(value: Int, p: Int) : Scalar<IntModp> {
    val value: Int = value % p
    val p: Int = p
    override val field: Field<IntModp> = Fp.get(this.p)
    override operator fun plus(other: IntModp): IntModp {
        if (this.p != other.p) {
            throw Exception("[Error] different characteristic: ${this.p} and ${other.p}")
        }
        return IntModp(this.value + other.value, this.p)
    }
    override operator fun times(other: IntModp): IntModp {
        if (this.p != other.p) {
            throw Exception("[Error] different characteristic: ${this.p} and ${other.p}")
        }
        return IntModp(this.value * other.value, this.p)
    }
    override operator fun div(other: IntModp): IntModp {
        return this * other.inv()
    }
    override fun inv(): IntModp {
        if (this == IntModp(0, this.p)) throw ArithmeticException("division by zero (IntModp(0, ${this.p}))")
        // TODO: Int として pow した後に modulo するのは重い
        return IntModp(this.value.pow(this.p - 2) % this.p, this.p)
    }
    override fun unwrap(): IntModp {
        return this
    }
    override fun toString(): String {
        return "${this.value % this.p} mod ${this.p}"
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
    override fun wrap(a: IntModp): Scalar<IntModp> {
        return a
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

    override val ZERO
        get() = IntModp(0, this.p)
    override val ONE
        get() = IntModp(1, this.p)
}

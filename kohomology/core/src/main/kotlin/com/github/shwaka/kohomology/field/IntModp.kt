package com.github.shwaka.kohomology.field

data class IntModp(val value: Int, val p: Int) : Scalar<IntModp> {
    // 色々とチェックした方が良さそう
    // p > 0
    override val field: Field<IntModp>
    init {
        this.field = Fp.get(this.p)
    }
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
        throw NotImplementedError("Not implemented")
    }
    override fun unwrap(): IntModp {
        return this
    }
    override fun toString(): String {
        return "${this.value % this.p} mod ${this.p}"
    }
}

data class Fp private constructor(val p: Int) : Field<IntModp> {
    companion object {
        private val cache: MutableMap<Int, Fp> = mutableMapOf()
        fun get(p: Int): Fp {
            return this.cache.getOrPut(p, { Fp(p) })
        }
    }
    override fun wrap(a: IntModp): Scalar<IntModp> {
        return a
    }
    override fun fromInt(n: Int): IntModp {
        return IntModp(n, p)
    }
    override val ZERO
        get() = IntModp(0, this.p)
    override val ONE
        get() = IntModp(1, this.p)
}

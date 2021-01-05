package com.github.shwaka.kohomology.field

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

data class Fp(val p: Int) : Field<IntModp> {
    override fun wrap(a: IntModp): Scalar<IntModp> {
        return a
    }
    override fun fromInt(n: Int): IntModp {
        return IntModp(n, p)
    }
    override val ZERO = TODO("Not yet implemented")
    override val ONE = TODO("Not yet implemented")
}

package com.github.shwaka.kohomology.field

import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext

class IntModp(value: Int, override val field: Fp) : Scalar<IntModp> {
    val value: Int = value.positiveRem(field.p)
    val p: Int = field.p

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

    override fun add(a: IntModp, b: IntModp): IntModp {
        if (a.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${a.p} for $a does not match the context (p=${this.p})")
        if (b.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${b.p} for $b does not match the context (p=${this.p})")
        return IntModp(a.value + b.value, this)
    }

    override fun subtract(a: IntModp, b: IntModp): IntModp {
        if (a.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${a.p} for $a does not match the context (p=${this.p})")
        if (b.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${b.p} for $b does not match the context (p=${this.p})")
        return IntModp(a.value - b.value, this)
    }

    override fun multiply(a: IntModp, b: IntModp): IntModp {
        if (a.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${a.p} for $a does not match the context (p=${this.p})")
        if (b.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${b.p} for $b does not match the context (p=${this.p})")
        return IntModp(a.value * b.value, this)
    }

    override fun divide(a: IntModp, b: IntModp): IntModp {
        if (a.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${a.p} for $a does not match the context (p=${this.p})")
        if (b.p != this.p)
            throw ArithmeticException("[Error] the characteristic ${b.p} for $b does not match the context (p=${this.p})")
        val bInv = this.invModp(b)
        return IntModp(a.value * bInv.value, this)
    }

    private fun invModp(a: IntModp): IntModp {
        if (a == IntModp(0, this))
            throw ArithmeticException("division by zero (IntModp(0, ${this.p}))")
        // TODO: Int として pow した後に modulo するのは重い
        return IntModp(a.value.pow(this.p - 2).positiveRem(this.p), this)
    }

    override fun fromInt(n: Int): IntModp {
        return IntModp(n, this)
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
val DenseNumVectorSpaceOverF2 = DenseNumVectorSpace.from(F2)
val DenseMatrixSpaceOverF2 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF2)

val F3 = Fp.get(3)
val DenseNumVectorSpaceOverF3 = DenseNumVectorSpace.from(F3)
val DenseMatrixSpaceOverF3 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF3)

val F5 = Fp.get(5)
val DenseNumVectorSpaceOverF5 = DenseNumVectorSpace.from(F5)
val DenseMatrixSpaceOverF5 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF5)

val F7 = Fp.get(7)
val DenseNumVectorSpaceOverF7 = DenseNumVectorSpace.from(F7)
val DenseMatrixSpaceOverF7 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF7)

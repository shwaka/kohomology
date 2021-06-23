package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.github.shwaka.kohomology.util.isPrime
import com.github.shwaka.kohomology.util.positiveRem
import com.github.shwaka.kohomology.util.pow
import com.github.shwaka.kohomology.vectsp.PrintConfig

class IntModp(value: Int, val characteristic: Int) : Scalar {
    val value: Int = value.positiveRem(characteristic)

    override fun isZero(): Boolean {
        return this.value == 0
    }

    override fun isPrintedPositively(): Boolean = true

    override fun toString(printConfig: PrintConfig, withSign: Boolean): String {
        return if (withSign) {
            this.toString()
        } else {
            this.toStringWithoutSign()
        }
    }

    private fun toStringWithoutSign(): String = this.toString()

    override fun toString(): String {
        return "${this.value.positiveRem(this.characteristic)} mod ${this.characteristic}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as IntModp

        if (this.value != other.value) return false
        if (this.characteristic != other.characteristic) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.value
        result = 31 * result + this.characteristic
        return result
    }
}
class Fp private constructor(override val characteristic: Int) : Field<IntModp> {
    companion object {
        private val cache: MutableMap<Int, Fp> = mutableMapOf()
        fun get(p: Int): Fp {
            return this.cache.getOrPut(p, { if (p.isPrime()) Fp(p) else throw ArithmeticException("$p is not prime") })
        }
    }

    override val context: ScalarContext<IntModp> = ScalarContext(this)

    override val field = this

    override fun contains(scalar: IntModp): Boolean {
        return this.characteristic == scalar.characteristic
    }

    override fun add(a: IntModp, b: IntModp): IntModp {
        if (a.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${a.characteristic} for $a does not match the context (p=${this.characteristic})")
        if (b.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${b.characteristic} for $b does not match the context (p=${this.characteristic})")
        return IntModp(a.value + b.value, this.characteristic)
    }

    override fun subtract(a: IntModp, b: IntModp): IntModp {
        if (a.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${a.characteristic} for $a does not match the context (p=${this.characteristic})")
        if (b.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${b.characteristic} for $b does not match the context (p=${this.characteristic})")
        return IntModp(a.value - b.value, this.characteristic)
    }

    override fun multiply(a: IntModp, b: IntModp): IntModp {
        if (a.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${a.characteristic} for $a does not match the context (p=${this.characteristic})")
        if (b.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${b.characteristic} for $b does not match the context (p=${this.characteristic})")
        return IntModp(a.value * b.value, this.characteristic)
    }

    override fun divide(a: IntModp, b: IntModp): IntModp {
        if (a.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${a.characteristic} for $a does not match the context (p=${this.characteristic})")
        if (b.characteristic != this.characteristic)
            throw ArithmeticException("[Error] the characteristic ${b.characteristic} for $b does not match the context (p=${this.characteristic})")
        val bInv = this.invModp(b)
        return IntModp(a.value * bInv.value, this.characteristic)
    }

    override fun unaryMinusOf(scalar: IntModp): IntModp {
        return IntModp(-scalar.value, this.characteristic)
    }

    private fun invModp(a: IntModp): IntModp {
        if (a == IntModp(0, this.characteristic))
            throw ArithmeticException("division by zero (IntModp(0, ${this.characteristic}))")
        // TODO: Int として pow した後に modulo するのは重い
        return IntModp(a.value.pow(this.characteristic - 2).positiveRem(this.characteristic), this.characteristic)
    }

    override fun fromInt(n: Int): IntModp {
        return IntModp(n, this.characteristic)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Fp

        if (this.characteristic != other.characteristic) return false

        return true
    }

    override fun hashCode(): Int {
        return this.characteristic
    }

    override fun toString(): String {
        return "F_${this.characteristic}"
    }
}

val F2 = Fp.get(2)
val DenseNumVectorSpaceOverF2 = DenseNumVectorSpace.from(F2)
val DenseMatrixSpaceOverF2 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF2)
val SparseNumVectorSpaceOverF2 = SparseNumVectorSpace.from(F2)
val SparseMatrixSpaceOverF2 = SparseMatrixSpace.from(SparseNumVectorSpaceOverF2)

val F3 = Fp.get(3)
val DenseNumVectorSpaceOverF3 = DenseNumVectorSpace.from(F3)
val DenseMatrixSpaceOverF3 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF3)
val SparseNumVectorSpaceOverF3 = SparseNumVectorSpace.from(F3)
val SparseMatrixSpaceOverF3 = SparseMatrixSpace.from(SparseNumVectorSpaceOverF3)

val F5 = Fp.get(5)
val DenseNumVectorSpaceOverF5 = DenseNumVectorSpace.from(F5)
val DenseMatrixSpaceOverF5 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF5)
val SparseNumVectorSpaceOverF5 = SparseNumVectorSpace.from(F5)
val SparseMatrixSpaceOverF5 = SparseMatrixSpace.from(SparseNumVectorSpaceOverF5)

val F7 = Fp.get(7)
val DenseNumVectorSpaceOverF7 = DenseNumVectorSpace.from(F7)
val DenseMatrixSpaceOverF7 = DenseMatrixSpace.from(DenseNumVectorSpaceOverF7)
val SparseNumVectorSpaceOverF7 = SparseNumVectorSpace.from(F7)
val SparseMatrixSpaceOverF7 = SparseMatrixSpace.from(SparseNumVectorSpaceOverF7)

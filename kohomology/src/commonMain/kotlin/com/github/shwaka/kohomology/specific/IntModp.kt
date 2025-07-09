package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.FiniteField
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.isPrime

public class IntModp(value: Int, public val characteristic: Int) : Scalar {
    public val value: Int = value.mod(characteristic)

    override fun isZero(): Boolean {
        return this.value == 0
    }

    override fun isOne(): Boolean {
        return this.value == 1
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
        return this.value.mod(this.characteristic).toString()
        // return "${this.value.mod(this.characteristic)} mod ${this.characteristic}"
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

public class Fp private constructor(override val characteristic: Int) : FiniteField<IntModp> {
    public companion object {
        private val cache: MutableMap<Int, Fp> = mutableMapOf()
        public fun get(p: Int): Fp {
            return this.cache.getOrPut(p) {
                if (p.isPrime())
                    Fp(p)
                else
                    throw ArithmeticException("$p is not prime")
            }
        }
    }

    override val context: ScalarContext<IntModp> = ScalarContext(this)

    override val order: Int = characteristic
    override val elements: List<IntModp> by lazy {
        (0 until this.characteristic).map { this.fromInt(it) }
    }

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

    private val invModpCache: List<IntModp> by lazy {
        (0 until this.characteristic).map { i ->
            IntModp(
                this.powModp(i, this.characteristic - 2),
                this.characteristic,
            )
        }
    }

    private fun invModp(a: IntModp): IntModp {
        if (a == IntModp(0, this.characteristic))
            throw ArithmeticException("division by zero (IntModp(0, ${this.characteristic}))")
        return this.invModpCache[a.value]
    }

    private fun powModp(a: Int, exponent: Int): Int {
        return when {
            exponent == 0 -> 1
            exponent == 1 -> a
            exponent > 1 -> {
                val half = this.powModp(a, exponent / 2)
                val rem = if (exponent % 2 == 1) a else 1
                (half * half * rem).mod(this.characteristic)
            }
            exponent < 0 -> throw Exception("Negative exponent ($exponent) is not supported.")
            else -> throw Exception("This can't happen!")
        }
    }

    override fun fromInt(n: Int): IntModp {
        return IntModp(n, this.characteristic)
    }

    override val zero: IntModp = this.fromInt(0)
    override val one: IntModp = this.fromInt(1)
    override val two: IntModp = this.fromInt(2)
    override val three: IntModp = this.fromInt(3)
    override val four: IntModp = this.fromInt(4)
    override val five: IntModp = this.fromInt(5)

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

/*
for p in 2 3 5 7; do
    cat << EOS
public val F$p: Fp = Fp.get($p)
public val DenseNumVectorSpaceOverF$p: DenseNumVectorSpace<IntModp> = DenseNumVectorSpace.from(F$p)
public val DenseMatrixSpaceOverF$p: DenseMatrixSpace<IntModp> = DenseMatrixSpace.from(DenseNumVectorSpaceOverF$p)
public val SparseNumVectorSpaceOverF$p: SparseNumVectorSpace<IntModp> = SparseNumVectorSpace.from(F$p)
public val SparseMatrixSpaceOverF$p: SparseMatrixSpace<IntModp> = SparseMatrixSpace.from(SparseNumVectorSpaceOverF$p)

EOS
done
 */

public val F2: Fp = Fp.get(2)
public val DenseNumVectorSpaceOverF2: DenseNumVectorSpace<IntModp> = DenseNumVectorSpace.from(F2)
public val DenseMatrixSpaceOverF2: DenseMatrixSpace<IntModp> = DenseMatrixSpace.from(DenseNumVectorSpaceOverF2)
public val SparseNumVectorSpaceOverF2: SparseNumVectorSpace<IntModp> = SparseNumVectorSpace.from(F2)
public val SparseMatrixSpaceOverF2: SparseMatrixSpace<IntModp> = SparseMatrixSpace.from(SparseNumVectorSpaceOverF2)

public val F3: Fp = Fp.get(3)
public val DenseNumVectorSpaceOverF3: DenseNumVectorSpace<IntModp> = DenseNumVectorSpace.from(F3)
public val DenseMatrixSpaceOverF3: DenseMatrixSpace<IntModp> = DenseMatrixSpace.from(DenseNumVectorSpaceOverF3)
public val SparseNumVectorSpaceOverF3: SparseNumVectorSpace<IntModp> = SparseNumVectorSpace.from(F3)
public val SparseMatrixSpaceOverF3: SparseMatrixSpace<IntModp> = SparseMatrixSpace.from(SparseNumVectorSpaceOverF3)

public val F5: Fp = Fp.get(5)
public val DenseNumVectorSpaceOverF5: DenseNumVectorSpace<IntModp> = DenseNumVectorSpace.from(F5)
public val DenseMatrixSpaceOverF5: DenseMatrixSpace<IntModp> = DenseMatrixSpace.from(DenseNumVectorSpaceOverF5)
public val SparseNumVectorSpaceOverF5: SparseNumVectorSpace<IntModp> = SparseNumVectorSpace.from(F5)
public val SparseMatrixSpaceOverF5: SparseMatrixSpace<IntModp> = SparseMatrixSpace.from(SparseNumVectorSpaceOverF5)

public val F7: Fp = Fp.get(7)
public val DenseNumVectorSpaceOverF7: DenseNumVectorSpace<IntModp> = DenseNumVectorSpace.from(F7)
public val DenseMatrixSpaceOverF7: DenseMatrixSpace<IntModp> = DenseMatrixSpace.from(DenseNumVectorSpaceOverF7)
public val SparseNumVectorSpaceOverF7: SparseNumVectorSpace<IntModp> = SparseNumVectorSpace.from(F7)
public val SparseMatrixSpaceOverF7: SparseMatrixSpace<IntModp> = SparseMatrixSpace.from(SparseNumVectorSpaceOverF7)

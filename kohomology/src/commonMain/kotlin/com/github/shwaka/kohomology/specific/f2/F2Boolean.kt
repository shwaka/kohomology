package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.FiniteField
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.ScalarContextImpl
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.github.shwaka.kohomology.util.PrintConfig
import kotlin.jvm.JvmInline

@JvmInline
public value class IntMod2Boolean private constructor(public val value: Boolean) : Scalar {
    override fun isZero(): Boolean {
        return !this.value
    }

    override fun isOne(): Boolean {
        return this.value
    }

    override fun isPrintedPositively(): Boolean {
        return true
    }

    override fun toString(printConfig: PrintConfig, withSign: Boolean): String {
        return this.toString()
    }

    override fun toString(): String {
        return if (this.value) {
            "1"
        } else {
            "0"
        }
    }

    public companion object {
        private val zero: IntMod2Boolean = IntMod2Boolean(false)
        private val one: IntMod2Boolean = IntMod2Boolean(true)

        public operator fun invoke(value: Boolean): IntMod2Boolean {
            return if (value) {
                this.one
            } else {
                this.zero
            }
        }
    }
}

public object F2Boolean : FiniteField<IntMod2Boolean> {
    override val context: ScalarContext<IntMod2Boolean> = ScalarContextImpl(this)

    override val characteristic: Int = 2

    override val order: Int = 2
    override val elements: List<IntMod2Boolean> = listOf(
        IntMod2Boolean(false),
        IntMod2Boolean(true),
    )

    override fun contains(scalar: IntMod2Boolean): Boolean {
        return true
    }

    override fun add(a: IntMod2Boolean, b: IntMod2Boolean): IntMod2Boolean {
        return IntMod2Boolean(a.value xor b.value)
    }

    override fun subtract(a: IntMod2Boolean, b: IntMod2Boolean): IntMod2Boolean {
        return IntMod2Boolean(a.value xor b.value)
    }

    override fun multiply(a: IntMod2Boolean, b: IntMod2Boolean): IntMod2Boolean {
        return IntMod2Boolean(a.value && b.value)
    }

    override fun divide(a: IntMod2Boolean, b: IntMod2Boolean): IntMod2Boolean {
        if (!b.value) {
            throw ArithmeticException("division by zero")
        }
        return a
    }

    override fun unaryMinusOf(scalar: IntMod2Boolean): IntMod2Boolean {
        return scalar
    }

    override fun fromInt(n: Int): IntMod2Boolean {
        return IntMod2Boolean(n.mod(2) == 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): IntMod2Boolean {
        if (denominator == 0) {
            throw ArithmeticException("division by zero")
        }
        return this.fromInt(numerator)
    }

    override val zero: IntMod2Boolean = this.fromInt(0)
    override val one: IntMod2Boolean = this.fromInt(1)
    override val two: IntMod2Boolean = this.fromInt(2)
    override val three: IntMod2Boolean = this.fromInt(3)
    override val four: IntMod2Boolean = this.fromInt(4)
    override val five: IntMod2Boolean = this.fromInt(5)
}

public val SparseNumVectorSpaceOverF2Boolean: SparseNumVectorSpace<IntMod2Boolean> = SparseNumVectorSpace.from(F2Boolean)
public val SparseMatrixSpaceOverF2Boolean: SparseMatrixSpace<IntMod2Boolean> = SparseMatrixSpace.from(SparseNumVectorSpaceOverF2Boolean)

// These should be defined here (not in SetNumVector.kt and SetMatrix.kt)
// to confirm the order of initialization.
public val SetNumVectorSpaceOverF2Boolean: SetNumVectorSpace<IntMod2Boolean> =
    SetNumVectorSpace.from(F2Boolean)
public val SetMatrixSpaceOverF2Boolean: SetMatrixSpace<IntMod2Boolean> =
    SetMatrixSpace.from(SetNumVectorSpaceOverF2Boolean)

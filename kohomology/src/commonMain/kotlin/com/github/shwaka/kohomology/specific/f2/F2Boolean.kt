package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.ScalarContextImpl
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.github.shwaka.kohomology.util.PrintConfig
import kotlin.jvm.JvmInline

@JvmInline
public value class IntMod2Boolean(public val value: Boolean) : Scalar {
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
}

public object F2Boolean : Field<IntMod2Boolean> {
    override val context: ScalarContext<IntMod2Boolean> = ScalarContextImpl(this)

    override val characteristic: Int = 2

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
            throw Exception("Division by zero")
        }
        return a
    }

    override fun unaryMinusOf(scalar: IntMod2Boolean): IntMod2Boolean {
        return scalar
    }

    override fun fromInt(n: Int): IntMod2Boolean {
        return IntMod2Boolean(n.mod(2) == 1)
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

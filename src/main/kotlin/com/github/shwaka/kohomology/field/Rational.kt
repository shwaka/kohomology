package com.github.shwaka.kohomology.field

interface RationalScalar<S> : Scalar<S>

interface RationalField<S> : Field<S> {
    fun fromIntPair(numerator: Int, denominator: Int): Scalar<S>
}

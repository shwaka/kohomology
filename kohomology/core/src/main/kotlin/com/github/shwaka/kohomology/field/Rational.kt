package com.github.shwaka.kohomology.field

interface RationalScalar<S : Scalar<S>> : Scalar<S>

interface RationalField<S : Scalar<S>> : Field<S> {
    fun fromIntPair(numerator: Int, denominator: Int): S
}

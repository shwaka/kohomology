package com.github.shwaka.kohomology.field

interface RationalScalar<S : RationalScalar<S>> : Scalar<S>

interface RationalField<S : RationalScalar<S>> : Field<S> {
    fun fromIntPair(numerator: Int, denominator: Int): S
}

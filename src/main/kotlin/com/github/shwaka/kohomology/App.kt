package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Rational
import com.github.shwaka.kohomology.field.RationalField
import com.github.shwaka.kohomology.field.IntModp
import com.github.shwaka.kohomology.field.Fp

fun <S> add(a: Scalar<S>, b: Scalar<S>): Scalar<S> {
    // return a.field.wrap(a + b.unwrap())
    return a + b
}

fun <S> test(a: Scalar<S>, b: Scalar<S>) {
    println("------------------------------")
    println("test: a=${a}, b=${b}")
    println("a + b = ${a + b}")
    println("a - b = ${a - b}")
    println("a * b = ${a * b}")
    println("------------------------------")
}


fun main() {
    println("Hello world!")
    println(Rational(1, 2) + Rational(1, 3))
    println(Rational(1, 3) + Rational(-2, 6))

    val a = Rational(1, 2)
    val b = RationalField.fromInteger(2)
    println(add(a, b))
    test(a, b)

    val p = 5
    val c = IntModp(3, p)
    val d = IntModp(1, p)
    println(add(c, d))
    println(add(c, d + Fp(p).fromInteger(1)))
    test(c, d)
}

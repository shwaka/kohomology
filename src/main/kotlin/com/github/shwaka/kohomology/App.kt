package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.Fp
import com.github.shwaka.kohomology.field.IntModp
import com.github.shwaka.kohomology.field.IntRational
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.Scalar

fun <S> add(a: Scalar<S>, b: Scalar<S>): Scalar<S> {
    // return a.field.wrap(a + b.unwrap())
    return a + b
}

fun <S> test(a: Scalar<S>, b: Scalar<S>) {
    println("------------------------------")
    println("test: a=$a, b=$b")
    println("a + b = ${a + b}")
    println("a - b = ${a - b}")
    println("a * b = ${a * b}")
    println("------------------------------")
}

fun main() {
    println("Hello world!")
    println(IntRational(1, 2) + IntRational(1, 3))
    println(IntRational(1, 3) + IntRational(-2, 6))

    val a = IntRational(1, 2)
    val b = IntRationalField.fromInteger(2)
    println(add(a, b))
    test(a, b)

    val p = 5
    val c = IntModp(3, p)
    val d = IntModp(1, p)
    println(add(c, d))
    println(add(c, d + Fp(p).fromInteger(1)))
    test(c, d)
}

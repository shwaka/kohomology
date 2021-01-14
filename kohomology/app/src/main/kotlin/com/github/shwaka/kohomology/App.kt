package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.Fp
import com.github.shwaka.kohomology.field.IntModp
import com.github.shwaka.kohomology.field.IntRational
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.Scalar

fun <S : Scalar<S>> add(a: S, b: S): S {
    // return a.field.wrap(a + b.unwrap())
    return a + b
}

fun <S : Scalar<S>> test(a: S, b: S) {
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
    val b = IntRationalField.fromInt(2)
    println(add(a, b))
    test(a, b)

    val p = 5
    val c = IntModp(3, p)
    val d = IntModp(1, p)
    println(add(c, d))
    println(add(c, d + Fp.get(p).fromInt(1)))
    test(c, d)
}

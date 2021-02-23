package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Fp
import com.github.shwaka.kohomology.field.IntModp
import com.github.shwaka.kohomology.field.IntRational
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.Scalar

fun <S : Scalar<S>> add(field: Field<S>, a: S, b: S): S {
    // return a.field.wrap(a + b.unwrap())
    return field.withContext { a + b }
}

fun <S : Scalar<S>> test(field: Field<S>, a: S, b: S) {
    field.withContext {
        println("------------------------------")
        println("test: a=$a, b=$b")
        println("a + b = ${a + b}")
        println("a - b = ${a - b}")
        println("a * b = ${a * b}")
        println("------------------------------")
    }
}

fun main() {
    println("Hello world!")
    IntRationalField.withContext {
        println(IntRational(1, 2) + IntRational(1, 3))
        println(IntRational(1, 3) + IntRational(-2, 6))
    }

    val a = IntRational(1, 2)
    val b = IntRationalField.fromInt(2)
    println(add(IntRationalField, a, b))
    test(IntRationalField, a, b)

    val p = 5
    val field = Fp.get(p)
    val c = IntModp(3, p)
    val d = IntModp(1, p)
    println(add(field, c, d))
    field.withContext {
        println(add(field, c, d + field.fromInt(1)))
    }
    test(field, c, d)
}

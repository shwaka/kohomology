package kohomology

import kohomology.field.Scalar
import kohomology.field.Field
import kohomology.field.Rational
import kohomology.field.RationalField
import kohomology.field.IntModp
import kohomology.field.Fp

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

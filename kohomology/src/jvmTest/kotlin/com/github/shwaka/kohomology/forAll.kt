package com.github.shwaka.kohomology

import io.kotest.inspectors.forAll

fun IntRange.forAll(fn: (Int) -> Unit) {
    this.toList().forAll(fn)
}
